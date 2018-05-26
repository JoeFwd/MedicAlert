var express = require('express');
var tables = require('../tables');
var connection = require('../connection');
var utils = require('./utils');
var bcrypt = require('bcrypt');
var auth = require('./authentification');
var config = require('./config');
var jwt = require('jsonwebtoken');


var aideSoignantRouter = express.Router();
var attr = tables.tables.aideSoignants.attr;

//const dateNaissanceYYYYMMDD = 'convert(varchar, ?, 23)'; /*DATE_FORMAT(date_peremption, "%Y-%m-%d") AS date_peremption,               req.body.date_peremption, Format : YYYY-MM-DD*/
const genericSelectAideSoignants = 'SELECT ' + attr[0] + ', ' + attr[1] + ', ' + attr[2] + ', ' + attr[3] + ', ' + 'DATE_FORMAT(' + attr[4] + ', "%Y-%m-%d") AS '  + attr[4] + ' FROM ' + tables.tables.aideSoignants.nom;

function responseForDuplicateEmails(req, res, next){
    var sql = genericSelectAideSoignants + ' WHERE email = ?;';
	connection.query(sql, [req.body.email], function(err, result){
		if(err){
			console.error(err);
			res.statusCode = 500;
			res.json({
				errors: ['La création du paitient a échouée']
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

aideSoignantRouter.get('/nom_complet', auth.ensureToken, function(req, res) {
	/*jwt.verify(req.token, config.secret, function(err, jwtdata){
        if(err){
            res.sendStatus(403);
        } else {*/
            var sql = 'SELECT * FROM ' + tables.tables.aideSoignants.nom + ';';
            connection.query(sql, function(error, response){
                if(error){
                    console.error(error);
                    res.statusCode = 500;
                    return res.json({
                        errors: ['Retourner tous les aide-soignants a échoué'],
                        succes : false
                    });
                }
                res.statusCode = 200;
                return res.json(response.map(function(a){
                    return {
                        id : a.id,
                        prenom : a.prenom,
                        nom : a.nom
                    };
                }));
            });
    //}});
});

aideSoignantRouter.post('/', auth.ensureToken, responseForDuplicateEmails, function(req, res) {
	/*jwt.verify(req.token, config.secret, function(err, jwtdata){
        if(err){
            res.sendStatus(403);
        } else {*/
            var sql = 'INSERT INTO ' + tables.tables.aideSoignants.nom + ' (';
            var values = "";

            for(var i=0; i<tables.tables.aideSoignants.attr.length; i++){
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
                            errors: ['La création de l\'aide-soignant a échouée'],
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

aideSoignantRouter.patch('/:email', auth.ensureToken, responseForDuplicateEmails, function(req, res) {
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

            var sql = 'UPDATE ' + tables.tables.aideSoignants.nom + " SET ";
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
                            errors: ['L\' aide-soignant n\'a pas pu être modifié'],
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

module.exports.router = aideSoignantRouter;