var express = require('express');
var tables = require('../tables');
var connection = require('../connection');
var utils = require('./utils');
var auth = require('./authentification');
var config = require('./config');
var jwt = require('jsonwebtoken');

const genericSelectMedicaments = 'SELECT * FROM ' + tables.tables.medicaments.nom;

function invalidCip13Response(cip13, res){
	if(!utils.hasOnlyNumbers(cip13)){
		res.statusCode = 400;
		return res.json({
			errors: ['Invalid cip13'],
			invalidKey : [cip13]
		});
	}
	return null;
}

/*sql: requête; data: tableau contenant les données correspondant aux paramètres de la requête HTTP.
req, res, next sont les arguments qui doivent être donnés au handler de la fonction de routage (express.Router().*).*/
function checkExistenceofMedicament(sql, data, req, res, next){
	connection.query(sql, data, function(err, result){
		if(err){
			console.error(err);
			res.statusCode = 500;
			return res.json({
				errors: ['Le médicament ne peut être récupéré']
			});
		}
		if(result.length === 0){
			res.statusCode = 404;
			return res.json({
				errors: ['Médicament not found'],
				notFound: true
			});
		}
		req.medicament = result;
		next();
	});
}

function checkCip13Validity(req, res, next){
	var invalidCip13Res = invalidCip13Response(req.params.cip13, res);
	if(invalidCip13Res != null) return invalidCip13Res;
	next();
}

function checkExistenceOfMedicamentWithCipAsParam(req, res, next){
	var sql = genericSelectMedicaments + ' WHERE cip13 = ?;';
	var data = [req.params.cip13];
	checkExistenceofMedicament(sql, data, req, res, next);
}

function checkCip13Validity(req, res, next){
	var sql = genericSelectMedicaments + ' WHERE cip13 = ?;';
	var data = [req.params.cip13];
	var invalidCip13Res = invalidCip13Response(req.params.cip13, res);
	if(invalidCip13Res != null) return invalidCip13Res;
	next();
}

function findMedicamentContainingNom(req, res, next){ //Cas d'erreur non testé
	var sql = genericSelectMedicaments + ' WHERE nom LIKE ?';
	var containsNom = '%' + req.params.nom + '%';
	var data = [containsNom];
		sql+=' ORDER BY nom';
	if(req.params.limit){
		if(!utils.hasOnlyNumbers(req.params.limit)){
			res.statusCode = 400;
			return res.json({
				errors: ['A limit can only be a number'],
				invalidParamLimit : req.params.limit
			});
		}
		var limit=Number(req.params.limit);
		if(limit < 1){
			res.statusCode = 400;
			return res.json({
				errors: ['A limit has to be greater than 0'],
				invalidParamLimit : req.params.limit
			});
		}
		sql+=' LIMIT ' + connection.escape(limit);
	}
	sql+=";"
	checkExistenceofMedicament(sql, data, req, res, next);
}

/*Vérifie si toutes les clés de req.body correspond aux attributs de la table Médicament et renvoie une réponse d'erreur si l'une d'entre elle n'est pas correcte sinon null.*/
function validKeys(req, res){
    var attr = tables.tables.medicaments.attr;
    var str = '^';
    var invalidKeys = [];
    for(var i=0; i<attr.length; i++){
        str += '(' + attr[i] + ')?'
    }
    str += '$';
    regex = new RegExp(str);
    for(var key in req.body){
        if(!key.match(regex)){ /*On doit vérifier si l'attribut donné par le client est valide*/
            invalidKeys.push(key);
        }
    }
    if(invalidKeys.length > 0){
            res.statusCode = 400;
            return res.json({
                errors: ['Invalid key'],
                invalidKey: invalidKeys
            });
    }
    return null;
}

function checkPostReqBodyValidity(req, res, next){
    var attr = tables.tables.medicaments.attr;
    if(Object.keys(req.body).length != attr.length){
            res.statusCode = 400;
            var error = 'req.body needs to be of type : {';
            for(var i=0; i<attr.length; i++){
                error += attr[i] + ' : ... '
            }
            error += '}';
            return res.json({
                errors: [error],
                invalidNumberOfParams : Math.abs(Object.keys(req.body).length - attr.length)
            });
    }

    var validKeysResponse = validKeys(req, res);
    if(validKeysResponse != null) return validKeysResponse;

	/*Check la validite de la valeur du cip13*/
	var invalidCip13Res = invalidCip13Response(req.body.cip13, res);
	if(invalidCip13Res != null) return invalidCip13Res;

    next();
}

function checkPatchReqBodyValidity(req, res, next){
    var attr = tables.tables.medicaments.attr;
    if(Object.keys(req.body).length > attr.length){
            res.statusCode = 400;
            var error = 'Too many parameters : accepted are ';
            for(var i=0; i<attr.length; i++){
                error += attr[i] + ' : ... '
            }
            error += '}';
            return res.json({
                errors: [error],
                invalidNumberOfParams : Object.keys(req.body).length - attr.length
            });
    }
    var validKeysResponse = validKeys(req, res);
    if(validKeysResponse != null) return validKeysResponse;

	/*Check la validite de la valeur du cip13*/
	var invalidCip13Res = invalidCip13Response(req.params.cip13, res);
	if(invalidCip13Res != null) return invalidCip13Res;

    next();
}

function duplicateMedicamentResponse(req, res, next){
    var sql = genericSelectMedicaments + ' WHERE cip13 = ?;';
	connection.query(sql, [req.body.cip13], function(err, result){
		if(err){
			console.error(err);
			res.statusCode = 500;
			res.json({
				errors: ['La création du médicament a échouée']
			});
			return;
		}
        else if(result.length > 0){
            res.statusCode = 400;
            res.json({
                errors: ['Le code cip donné existe déjà. Les doublons sont interdits'],
                duplicate: req.body.cip13
            });
            return;
        }
        next();
	});
}


var medicamentRouter = express.Router();
medicamentRouter.get('/formePharma/:formePharma', auth.ensureToken, function(req, res) {
	/*jwt.verify(req.token, config.secret, function(err, jwtdata){
        if(err){
            res.sendStatus(403);
        } else {*/
            sql = 'SELECT * FROM ' + tables.tables.medicaments.nom +' WHERE formePharma = ?;';
            connection.query(sql, [req.params.formePharma], function(err, result){
                if(err){
                    console.error(err);
                    res.statusCode = 500;
                    return res.json({
                        errors: ['La requête a a échouée'],
                        succes : false
                    });
                }
                res.statusCode = 200;
                res.json(result.map(utils.removeIdAttribute));
            });
	//}});
});

/*Should remove this and allow the id to be sent to all other get medicament requests*/
medicamentRouter.get('/get_id/:cip13', auth.ensureToken, checkCip13Validity, checkExistenceOfMedicamentWithCipAsParam, function(req, res) {
	/*jwt.verify(req.token, config.secret, function(err, jwtdata){
        if(err){
            res.sendStatus(403);
        } else {*/
            var sql =  "SELECT id FROM " + tables.tables.medicaments.nom +" WHERE cip13 = ?;"
            connection.query(sql, [req.params.cip13], function(err, result){
                if(err){
                    console.error(err);
                    res.statusCode = 500;
                    return res.json({
                        errors: ['Le médicament est introuvable'],
                        id : -1
                    });
                }
                res.statusCode = 200;
                return res.json(result[0]);
            });
	//}});
});

medicamentRouter.get('/cip13/:cip13', auth.ensureToken, checkCip13Validity, checkExistenceOfMedicamentWithCipAsParam, function(req, res) {
	/*jwt.verify(req.token, config.secret, function(err, jwtdata){
        if(err){
            res.sendStatus(403);
        } else {*/
            res.statusCode = 200;
            res.json(utils.removeIdAttribute(req.medicament[0]));
	//}});
});

medicamentRouter.get('/id/:id', auth.ensureToken, function(req, res, next){
        checkExistenceofMedicament(sql = 'SELECT * FROM ' + tables.tables.medicaments.nom +' WHERE id = ?;', [req.params.id], req, res, next);
    }, function(req, res) {
        /*jwt.verify(req.token, config.secret, function(err, jwtdata){
            if(err){
                res.sendStatus(403);
            } else {*/
                res.statusCode = 200;
                res.json(req.medicament[0]);
        //}});
});

medicamentRouter.get('/nom/:nom/:limit?', auth.ensureToken, findMedicamentContainingNom, function(req, res) {
	/*jwt.verify(req.token, config.secret, function(err, data){
        if(err){
            res.sendStatus(403);
        } else {*/
            res.statusCode = 200;
            res.json(req.medicament.map(utils.removeIdAttribute));
	//}});
});
medicamentRouter.post('/', auth.ensureToken, checkPostReqBodyValidity, duplicateMedicamentResponse, function(req, res) {
	/*jwt.verify(req.token, config.secret, function(err, jwtdata){
    if(err){
        res.sendStatus(403);
    } else {*/
        sql = 'INSERT INTO ' + tables.tables.medicaments.nom + ' (cip13, nom, formePharma) VALUES (?, ?, ?);';
        var data = [
            req.body.cip13,
            req.body.nom,
            req.body.formePharma
        ];
        /*Evite les injections sql*/
        connection.query(sql, data, function(err, result){
            if(err){
                console.error(err);
                res.statusCode = 500;
                return res.json({
                    errors: ['La création du médicament a échouée']
                });
            }
            var sql = genericSelectMedicaments + ' WHERE cip13 = ?;';
            connection.query(sql, [req.body.cip13], function(err, result){
                if(err){
                    console.error(err);
                    res.statusCode = 500;
                    return res.json({
                        errors: ['Le médicament est introuvable après sa création']
                    });
                }
                res.statusCode = 201;
                res.json(utils.removeIdAttribute(result[0]));
            });
        });
	//}});
});

medicamentRouter.patch('/:cip13', auth.ensureToken, checkCip13Validity, checkPatchReqBodyValidity, checkExistenceOfMedicamentWithCipAsParam, duplicateMedicamentResponse, function(req, res) {
	/*jwt.verify(req.token, config.secret, function(err, jwtdata){
        if(err){
            res.sendStatus(403);
        } else {*/
            if(utils.isEmptyObject(req.body)){
                res.statusCode = 200;
                return res.json({
                    succes: ['Aucune mise à jour']
                });
            }

            /*ecriture de la requête de modification*/
            var sql = 'UPDATE ' + tables.tables.medicaments.nom + " SET ";
            var data = [];
            for(var key in req.body){
                sql += key + '=?,';
                data.push(req.body[key]);
            }
            sql = sql.substring(0, sql.length - 1);
            sql += ' WHERE cip13 = ' + connection.escape(req.params.cip13) + ';';

            /*requête de modification executée*/
            connection.query(sql, data, function(err, result){
                if(err){
                    console.error(err);
                    res.statusCode = 500;
                    return res.json({
                        errors: ['Le médicament n\'a pas pu être modifié']
                    });
                }
                var sql = genericSelectMedicaments + ' WHERE cip13 = ?;';
                connection.query(sql, [req.body.cip13], function(err, result){
                    if(err){
                        console.error(err);
                        res.statusCode = 500;
                        return res.json({
                            errors: ['Le médicament est introuvable après sa modification']
                        });
                    }
                    res.statusCode = 200;
                    return res.json(utils.removeIdAttribute(result[0]));
                });
            });
	//}});
});

medicamentRouter.delete('/:cip13', auth.ensureToken, checkCip13Validity, checkExistenceOfMedicamentWithCipAsParam, function(req, res) {
	/*jwt.verify(req.token, config.secret, function(err, jwtdata){
        if(err){
            res.sendStatus(403);
        } else {*/
            var sql = 'DELETE FROM ' + tables.tables.medicaments.nom + ' WHERE cip13 = ?;';
            connection.query(sql, [req.params.cip13], function(err, result){
                if(err){
                    console.error(err);
                    res.statusCode = 500;
                    return res.json({
                        errors: ['Le médicament n\'a pas pu être supprimé']
                    });
                }
                res.statusCode = 200;
                res.json({
                    succes: ['Le médicament a bien été supprimé']
                });
            });
    //}});
});

module.exports = medicamentRouter;