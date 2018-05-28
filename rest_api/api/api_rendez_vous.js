var express = require('express');
var tables = require('../tables');
var connection = require('../connection');
var utils = require('./utils');
var auth = require('./authentification');
var config = require('./config');
var jwt = require('jsonwebtoken');

var rendezVousRouter = express.Router();
var attr = tables.tables.rendezVous.attr;

rendezVousRouter.post('/', auth.ensureToken, function(req, res) {
	/*jwt.verify(req.token, config.secret, function(err, jwtdata){
        if(err){
            res.sendStatus(403);
        } else {*/
        var sql = 'INSERT INTO ' + tables.tables.rendezVous.nom + ' (';
        var values = "";

        for(var i=0; i<attr.length; i++){
            sql+= attr[i] + ",";
            values += '?,';
        }
        sql = sql.substring(0, sql.length - 1) + ') VALUES (' + values.substring(0, values.length-1) + ");";

        data = [
            req.body[attr[0]],
            req.body[attr[1]],
            req.body[attr[2]]
        ];

        console.log(req.body[attr[2]]);
        connection.query(sql, data, function(error, response){
            if(error){
                console.error(error);
                res.statusCode = 500;
                return res.json({
                    errors: ['La création du rendez-vous a échouée'],
                    succes : false
                });
            }
            res.statusCode = 201;
            return res.json({
                succes: true
            });
        });
    //}});
});

rendezVousRouter.get('/aide_soignant/:id_aide_soignant', auth.ensureToken, function(req, res) {
	/*jwt.verify(req.token, config.secret, function(err, jwtdata){
        if(err){
            res.sendStatus(403);
        } else {*/

            var sql = "select R.date_rdv, P.nom, P.prenom FROM " + tables.tables.patients.nom + " P, " + tables.tables.rendezVous.nom + " R WHERE P.id = R.id_patient AND P.id_aide_soignant = ?;";
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

rendezVousRouter.get('/patient/:id_patient', auth.ensureToken, function(req, res) {
	/*jwt.verify(req.token, config.secret, function(err, jwtdata){
        if(err){
            res.sendStatus(403);
        } else {*/
            var sql = "select R.date_rdv, A.nom, A.prenom FROM " + tables.tables.aideSoignants.nom + " A, " + tables.tables.rendezVous.nom + " R WHERE A.id = R.id_aide_soignant AND R.id_patient = ?;";
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
                res.json(response);
            });
    //}});
});

module.exports.router = rendezVousRouter;