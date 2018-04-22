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

function lookForMedicamentByCip13 (req, res, next){
	var cip13 = req.params.cip13;
	var sql = 'SELECT * FROM ' + tables.medicamentsTable + ' WHERE cip13 = ?';
	connection.query(sql, [cip13], function(err, result){
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

function lookForMedicamentByCis (req, res, next){
	var cis = req.params.cis;
	var sql = 'SELECT * FROM ' + tables.medicamentsTable + ' WHERE cis = ?';
	connection.query(sql, [cis], function(err, results){
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
		req.medicaments = results;
		next();	
	});
}

var medicamentRouter = express.Router();
medicamentRouter.get('/:cip13', function(req, res) {
	var cip13 = req.query.cip13;
	var sql = 'SELECT * FROM ' + tables.medicamentsTable + ' WHERE cip13 = ?';
	connection.query(sql, [cip13], function(err, result){
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
		res.statusCode = 200;
		res.json(result);
	});
});
medicamentRouter.get('/:cis', lookForMedicamentByCis, function(req, res) {
	res.json(req.medicaments);
});
medicamentRouter.get('/:nom', function(req, res) {});
medicamentRouter.post('/', function(req, res) {
	var sql = 'INSERT INTO ' + tables.medicamentsTable + ' (cip13, cis, nom, date_peremption, quantite) VALUES (?, ?, ?, ?, ?);';
	var data = [
		req.body.cip13,
		req.body.cis,
		req.body.nom,
		req.body.date_peremption,
		req.body.quantite 
	];
	var cip13 = req.body.cip13;
	/*Evite les injections sql*/
	connection.query(sql, data, function(err, result){
		if(err){
			console.error(err);
			res.statusCode = 500;
			return res.json({
				errors: ['La création du médicament a échouée'] 
			});
		}
		var sql = 'SELECT * FROM ' + tables.medicamentsTable + ' WHERE cip13 = ?';
		connection.query(sql, [cip13], function(err, result){
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
medicamentRouter.patch('/:cip13', function(req, res) {});
medicamentRouter.delete('/:cip13', function(req, res) {});
app.use('/medicaments', medicamentRouter);

app.listen(port, function() {
	console.log('Listening on port ' + port);
});
