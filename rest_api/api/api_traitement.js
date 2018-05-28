var express = require('express');
var tables = require('../tables');
var connection = require('../connection');
var utils = require('./utils');
var auth = require('./authentification');
var config = require('./config');
var jwt = require('jsonwebtoken');

traitementRouter= express.Router();

traitementRouter.post('/', /*auth.ensureToken,*/ async function(req, res) {
	/*jwt.verify(req.token, config.secret, function(err, jwtdata){
        if(err){
            res.sendStatus(403);
        } else {*/
        new Promise(async function(resolve, reject) {
            var sql = 'INSERT INTO ' + tables.tables.traitements.nom + ' (';
            var values = "";
            var attr = tables.tables.traitements.attr;

            for(var i=0; i<attr.length; i++){
                sql+= attr[i] + ",";
                values += '?,';
            }
            sql = sql.substring(0, sql.length - 1) + ') VALUES (' + values.substring(0, values.length-1) + ");";

            var data = [];
            for(var i=0; i<tables.tables.traitements.attr.length; i++) data.push(req.body[attr[i]]);
            try {
                connection.query(sql, data, function(error, response){
                    if(error){
                        console.error(error);
                        reject(error);
                    } else {
                        return resolve(response.insertId);
                    }
                });
            } catch(e){
                 res.statusCode = 500;
                 res.json({
                     succes : false
                 });
            }
        }).then(async function(id_traitement){
            return new Promise(async function(resolve, reject) {
                var attr = tables.tables.traitementsMedicament.attr;
                var medicamentList= JSON.parse(req.body.medicamentList);

                var sql = 'INSERT INTO ' + tables.tables.traitementsMedicament.nom + ' (';
                var values = "";

                for(var i=0; i<attr.length; i++){
                    sql+= attr[i] + ",";
                    values += '?,';
                }
                sql = sql.substring(0, sql.length - 1) + ') VALUES (' + values.substring(0, values.length-1) + ");";
                for(var i=0; i<medicamentList.length; i++){
                    try {
                        await test(attr, sql, medicamentList[i], id_traitement, res);
                    } catch (e){
                        reject(e);
                    }
                }
                resolve();
            });
        }).then(function(){
            res.statusCode = 201;
            res.json({
                succes : true
            });
        }).catch((err) => {
                res.statusCode = 500;
                res.json({
                    succes : false
                });
        });
    //}});
});

async function test(attr, sql, med, id_traitement, res){
    return new Promise(function(resolve, reject) {
        connection.query(sql, [id_traitement, med[attr[1]], med[attr[2]], med[attr[3]]], function(err, response){
            if(err){
                reject(err);
                console.error(err);
            } else {
                resolve();
            }
        });
    });
}

traitementRouter.get('/patient/:id_patient', auth.ensureToken, function(req, res) {
	/*jwt.verify(req.token, config.secret, function(err, jwtdata){
        if(err){
            res.sendStatus(403);
        } else {*/
        /*nom, date_debut, duree_traitement, matin, apres_midi, soir*/
        new Promise(async function(resolve, reject) {
            var attr = tables.tables.traitements.attr;
            var sql = "select id, ";
            for(var i=0; i<attr.length; i++){
                if(attr[i].localeCompare("date_debut") == 0){
                    sql += " DATE_FORMAT(date_debut, \"%Y-%m-%d\") as date_debut,";
                } else {
                    sql += attr[i] + ",";
                }
            }
            sql = sql.substring(0, sql.length - 1);
            sql += " from " + tables.tables.traitements.nom + " where id_patient = ?;";
            connection.query(sql, [req.params.id_patient], function(err, response){
                if(err){
                    reject(err);
                }
                res.result = response;
                resolve(response);
            });
        }).then(async function(traitement){
            for(var i=0; i<traitement.length; i++){
                res.result[i].medicamentList = [];
                await new Promise(function(resolve, reject) {
                    var sql = "select M.*, T.id_traitement, T.id_medicament, T.dosage, DATE_FORMAT(T.date_peremption, \"%Y-%m-%d\") as date_peremption from " + tables.tables.traitementsMedicament.nom + " T, " + tables.tables.medicaments.nom + " M where T.id_traitement = ? AND M.id = T.id_medicament;";
                    connection.query(sql, [traitement[i].id], function(err, response){
                        if(err){
                            reject(err);
                        }
                        res.result[i].medicamentList = response;
                        resolve();
                    });
                });
            }
        }).then(function(){
            res.statusCode = 200;
            res.json(res.result);
        }).catch((err) => {
            console.error(err);
            res.statusCode = 500;
            return res.json({
                errors: ['La requête a échoué'],
                succes: false
            });
        });
    //}});
});

module.exports.router = traitementRouter;

