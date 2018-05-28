var express = require('express');
var tables = require('../tables');
var connection = require('../connection');
var utils = require('./utils');
var bcrypt = require('bcrypt');
var auth = require('./authentification');
var config = require('./config');
var jwt = require('jsonwebtoken');


var patientRouter = express.Router();
var attr = tables.tables.patients.attr;

//const dateNaissanceYYYYMMDD = 'convert(varchar, ?, 23)'; /*DATE_FORMAT(date_peremption, "%Y-%m-%d") AS date_peremption,               req.body.date_peremption, Format : YYYY-MM-DD*/
const genericSelectPatients = 'SELECT ' + attr[0] + ', ' + attr[1] + ', ' + attr[2] + ', ' + attr[3] + ', ' + 'DATE_FORMAT(' + attr[4] + ', "%Y-%m-%d") AS '  + attr[4] + ' FROM ' + tables.tables.patients.nom;

function responseForDuplicateEmails(req, res, next){
    var sql = genericSelectPatients + ' WHERE email = ?;';
	connection.query(sql, [req.body.email], function(err, result){
		if(err){
			console.error(err);
			res.statusCode = 500;
			res.json({
				errors: ['La création du paitient a échouée'],
				succes : false
			});
			return;
		}
        else if(result.length > 0){
            res.statusCode = 400;
            res.json({
                errors: ['L\'email donné existe déjà. Les doublons sont interdits'],
                duplicate: req.body.email
            });
            return;
        }
        next();
	});
}

patientRouter.post('/', auth.ensureToken, responseForDuplicateEmails, function(req, res) {
	/*jwt.verify(req.token, config.secret, function(err, jwtdata){
        if(err){
            res.sendStatus(403);
        } else {*/
            var sql = 'INSERT INTO ' + tables.tables.patients.nom + ' (';
            var values = "";

            for(var i=0; i<tables.tables.patients.attr.length; i++){
                sql+= attr[i] + ",";
                values += '?,';
            }
            sql = sql.substring(0, sql.length - 1) + ') VALUES (' + values.substring(0, values.length-1) + ");";

            bcrypt.hash(req.body[attr[1]], config.saltRounds, function(err, hash) {
                data = [
                    req.body[attr[0]],
                    hash,
                    req.body[attr[2]],
                    req.body[attr[3]],
                    req.body[attr[4]],
                    req.body[attr[5]]
                ];

                connection.query(sql, data, function(error, response){
                    if(error){
                        console.error(error);
                        res.statusCode = 500;
                        return res.json({
                            errors: ['La création du patient a échouée'],
                            succes : false
                        });
                    }
                    res.statusCode = 201;
                    return res.json({
                        succes: true
                    });
                });
            });
    //}});
});

patientRouter.patch('/:email', auth.ensureToken, responseForDuplicateEmails, function(req, res) {
	/*jwt.verify(req.token, config.secret, function(err, jwtdata){
        if(err){
            res.sendStatus(403);
        } else {*/
            if(utils.isEmptyObject(req.body)){
                res.statusCode = 200;
                return res.json({
                    message: ['Aucune mise à jour']
                });
            }

            var sql = 'UPDATE ' + tables.tables.patients.nom + " SET ";
            var data = [];
            bcrypt.hash(req.body[attr[1]], config.saltRounds, function(err, hash) {
                for(var key in req.body){
                    sql += key + '=?,';
                    if(key.localeCompare(attr[1]) == 0){
                        data.push(hash);
                    }
                    else
                        data.push(req.body[key]);
                }

                sql = sql.substring(0, sql.length - 1) + ' WHERE ' + attr[0] + ' = ' + connection.escape(req.params[attr[0]]) + ';';

                connection.query(sql, data, function(updateErr, updateResult){
                    if(updateErr){
                        console.error(updateErr);
                        res.statusCode = 500;
                        return res.json({
                            errors: ['Le patient n\'a pas pu être modifié'],
                            succes: false
                        });
                    }
                    res.statusCode = 200;
                    res.json({
                        succes: true
                    });
                });
            });
    //}});
});

patientRouter.get('/id_aide_soignant/:id_aide_soignant', auth.ensureToken, function(req, res) {
	/*jwt.verify(req.token, config.secret, function(err, jwtdata){
        if(err){
            res.sendStatus(403);
        } else {*/
            var sql = 'SELECT id, prenom, nom FROM ' + tables.tables.patients.nom + ' WHERE id_aide_soignant = ?;';
            connection.query(sql, [req.params.id_aide_soignant], function(err, response){
                if(err){
                    console.error(err);
                    res.statusCode = 500;
                    return res.json({
                        errors: ['La requête a échoué'],
                        succes: false
                    });
                }
                res.statusCode = 200;
                res.json(response);
            });
    //}});
});

/*patientRouter.delete('/:cip13', auth.ensureToken, findMedicamentBycip13, function(req, res) {
	jwt.verify(req.token, config.secret, function(err, jwtdata){
        if(err){
            res.sendStatus(403);
        } else {
            var sql = 'DELETE FROM ' + tables.tables.medicaments.nom + ' WHERE cip13 = ?;';
            var data = [req.params.cip13];
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
                    message: ['Le médicament a bien été supprimé']
                });
            });
        }
    });
});*/

module.exports.router = patientRouter;