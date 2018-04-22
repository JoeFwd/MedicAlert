var connection = require('./connection');
var tables = require('./tables');

var express = require('express');
var bodyParser = require('body-parser');
var app = express();
var port = process.env.PORT || 8080;

// To parse JSON : req.body in json:
app.use(bodyParser.json({type: 'application/json'}));
// To parse form data:
app.use(bodyParser.urlencoded({
  extended: true
}));

tables.createAllTables();

app.get('/', function(req, res) {
  res.send('Our first route is working.:)');
});

/*Requête pour la table Médicaments*/

function hasOnlyNumbers(cip7){
	return (cip7.match(/^[0-9]+$/))?true:false;
}

function isEmptyObject(obj){
	for(var prop in obj) {
		if(obj.hasOwnProperty(prop)){
			return false;
		}
	}
	return true;
}

/*sql: requête; data: tableau contenant les données correspondant aux paramètres de la requête HTTP.
req, res, next sont les arguments qui doivent donnés au handler de la fonction de routage (express.Router().*).*/
function middlewareHandler(sql, data, req, res, next){
	connection.query(sql, data, function(err, result){
		if(err){
			console.error(err);
			res.statusCode = 500;
			return res.json({
				errors: ['Le médicament ne peut être récupérer']
			});
		}
		if(result.length === 0){
			res.statusCode = 404;
			return res.json({
				errors: ['Médicament not found']
			});
		}
		req.medicament = result;
		next();
	});
}

function findMedicamentByCip7(req, res, next){
	var sql = 'SELECT * FROM ' + tables.medicamentsTable + ' WHERE cip7 = ?;';
	var data = [req.params.cip7];
	if(!hasOnlyNumbers(req.params.cip7)){
		res.statusCode = 400;
		return res.json({
			errors: ['Invalid cip7']
		});		
	}
	middlewareHandler(sql, data, req, res, next);
}

function findMedicamentByCis(req, res, next){
	var sql = 'SELECT * FROM ' + tables.medicamentsTable + ' WHERE cis = ?;';
	var data = [req.params.cis];
	if(!hasOnlyNumbers(req.params.cis)){
		res.statusCode = 400;
		return res.json({
			errors: ['Invalid cis']
		});		
	}
	middlewareHandler(sql, data, req, res, next);
}

function findMedicamentContainingNom(req, res, next){
	var sql = 'SELECT * FROM ' + tables.medicamentsTable + ' WHERE nom LIKE ?';
	var containsNom = '%' + req.params.nom + '%';
	var data = [containsNom];
		sql+=' ORDER BY nom';
	if(req.params.limit){
		if(!hasOnlyNumbers(req.params.limit)){
			res.statusCode = 400;
			return res.json({
				errors: ['Invalid limit']
			});		
		}
		var limit=Number(req.params.limit);
		if(limit < 1){
			res.statusCode = 400;
			return res.json({
				errors: ['Invalid limit value']
			});		
		}		
		sql+=' LIMIT ' + connection.escape(limit);
	}
	sql+=";"
	console.log(data);
	console.log('sql: ' + sql);
	middlewareHandler(sql, data, req, res, next);	
}

function checkPatchReqValidity(req, res, next){
	/*Check la validite de la valeur du cip7*/
	if(!hasOnlyNumbers(req.params.cip7)){
		res.statusCode = 400;
		return res.json({
			errors: ['Invalid cip7']
		});		
	}
	
	if(isEmptyObject(req.body)){
		res.statusCode = 200;
		return res.json({
			message: ['Nothing updated']
		});	
	}
	
	var sql = 'SELECT * FROM ' + tables.medicamentsTable + ' WHERE cip7 = ?;';
	var data = [req.params.cip7];	
	middlewareHandler(sql, data, req, res, next);
}

var medicamentRouter = express.Router();
medicamentRouter.get('/cip7/:cip7', findMedicamentByCip7, function(req, res) {
	console.log(req.body);
	res.statusCode = 200;
	res.json(req.medicament);
});
medicamentRouter.get('/cis/:cis', findMedicamentByCis, function(req, res) {
	res.statusCode = 200;
	res.json(req.medicament);
});
medicamentRouter.get('/nom/:nom/:limit?', findMedicamentContainingNom, function(req, res) {
	res.statusCode = 200;
	res.json(req.medicament);
});
medicamentRouter.post('/', function(req, res) {
	var sql = 'INSERT INTO ' + tables.medicamentsTable + ' (cip7, cis, nom, date_peremption, quantite) VALUES (?, ?, ?, ?, ?);';
	var data = [
		req.body.cip7,
		req.body.cis,
		req.body.nom,
		req.body.date_peremption,
		req.body.quantite 
	];
	var cip7 = req.body.cip7;
	/*Evite les injections sql*/
	connection.query(sql, data, function(err, result){
		if(err){
			console.error(err);
			res.statusCode = 500;
			return res.json({
				errors: ['La création du médicament a échouée'] 
			});
		}
		var sql = 'SELECT * FROM ' + tables.medicamentsTable + ' WHERE cip7 = ?;';
		connection.query(sql, [cip7], function(err, result){
			if(err){
				console.error(err);
				res.statusCode = 500;
				return res.json({
					errors: ['Le médicament est introuvable après sa création']
				});
			}
			res.statusCode = 201;
			res.json(result);
		});
	});
});
medicamentRouter.patch('/:cip7', checkPatchReqValidity, function(req, res) {
	/*ecriture de la requête de modification*/
	var sql = 'UPDATE ' + tables.medicamentsTable + " SET ";
	var data = [];
	for(var key in req.body){
		if(!key.match(/^(cis)?(nom)?(date_peremption)?(quantite)?$/)){ /*On doit vérifier si l'attribut donné par le client est valide*/
			res.statusCode = 400;
			return res.json({
				errors: ['Invalid key ' + key]
			});		
		}
		sql += key + '=?,';
		data.push(req.body[key]);
	}
	sql = sql.substring(0, sql.length - 1);
	sql += ' WHERE cip7 = ' + connection.escape(Number(req.params.cip7)) + ';';
	console.log(sql);
	
	/*requête de modification executée*/
	connection.query(sql, data, function(err, result){
		if(err){
			console.error(err);
			res.statusCode = 500;
			return res.json({
				errors: ['Le médicament n\'a pas pu être modifié']
			});
		}
		var sql = 'SELECT * FROM ' + tables.medicamentsTable + ' WHERE cip7 = ?;';
		connection.query(sql, [req.params.cip7], function(err, result){
			if(err){
				console.error(err);
				res.statusCode = 500;
				return res.json({
					errors: ['Le médicament est introuvable après sa modification']
				});
			}
			res.statusCode = 201;
			res.json(result);
		});
	});
});

medicamentRouter.delete('/:cip7', findMedicamentByCip7, function(req, res) {
	var sql = 'DELETE FROM ' + tables.medicamentsTable + ' WHERE cip7 = ?;';
	var data = [req.params.cip7];
	connection.query(sql, [req.params.cip7], function(err, result){
		if(err){
			console.error(err);
			res.statusCode = 500;
			return res.json({
				errors: ['Le médicament n\'a pas pu être supprimé']
			});
		}
		res.statusCode = 200;
		res.json({
			errors: ['Le médicament a bien été supprimé']
		});
	});
});
app.use('/medicaments', medicamentRouter);

app.listen(port, function() {
	console.log('Listening on port ' + port);
});
