var express = require('express');
var tables = require('../tables');
var connection = require('../connection');
var utils = require('./utils');
var auth = require('./authentification');
var config = require('./config');
var jwt = require('jsonwebtoken');

firebaseRouter = express.Router();

firebaseRouter.post('/', auth.ensureToken, function(req, res) {
	/*jwt.verify(req.token, config.secret, function(err, jwtdata){
    if(err){
        res.sendStatus(403);
    } else {*/
        sql = 'INSERT INTO ' + tables.tables.firebaseTokens.nom + ' (id_patient, token) VALUES (?, ?);';

        connection.query(sql, [req.body.id_patient, req.body.token], function(err, result){
            if(err){
                console.error(err);
                res.statusCode = 500;
                return res.json({
                    errors: ['La requête a échoué'],
                    succes : false
                });
            }
            res.statusCode = 201;
            res.json({succes : true});
        });
	//}});
});

firebaseRouter.get('/token/:id_patient', auth.ensureToken, function(req, res) {
	/*jwt.verify(req.token, config.secret, function(err, jwtdata){
        if(err){
            res.sendStatus(403);
        } else {*/
            var sql = 'SELECT token from ' + tables.tables.firebaseTokens.nom + ' WHERE id_patient = ?;';
            connection.query(sql, [req.params.id_patient], function(err, response){
                if(err){
                    console.error(err);
                    res.statusCode = 500;
                    return res.json({
                        errors: ['La requête a échoué'],
                        succes: false
                    });
                }
                res.statusCode = 200;
                res.json(response[0]);
            });
    //}});
});

firebaseRouter.patch('/token/:id_patient', auth.ensureToken, function(req, res) {
	/*jwt.verify(req.token, config.secret, function(err, jwtdata){
        if(err){
            res.sendStatus(403);
        } else {*/
            var sql = 'UPDATE ' + tables.tables.firebaseTokens.nom + ' SET token = ? WHERE id_patient = ?;';
            connection.query(sql, [req.body.token, req.params.id_patient], function(err, response){
                if(err){
                    console.error(err);
                    res.statusCode = 500;
                    return res.json({
                        errors: ['La requête a échoué'],
                        succes: false
                    });
                }
                res.statusCode = 200;
                res.json({succes: true});
            });
    //}});
});

module.exports.router = firebaseRouter;