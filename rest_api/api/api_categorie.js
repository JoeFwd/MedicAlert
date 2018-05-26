var express = require('express');
var tables = require('../tables');
var connection = require('../connection');
var utils = require('./utils');
var auth = require('./authentification');
var config = require('./config');
var jwt = require('jsonwebtoken');

var categorieRouter = express.Router();
var attr = tables.tables.medicamentCategorie.attr;

categorieRouter.get('', auth.ensureToken, function(req, res) {
	/*jwt.verify(req.token, config.secret, function(err, jwtdata){
        if(err){
            res.sendStatus(403);
        } else {*/
            var sql = 'SELECT * FROM ' + tables.tables.medicamentCategorie.nom;
            connection.query(sql, [req.body.categorie], function(err, result){
                if(err){
                    console.error(err);
                    res.statusCode = 500;
                    return res.json({
                        errors: ['N\'a pas pu récupérer toutes les catégories'],
                        succes:false
                    });
                }
                res.statusCode = 200;
                res.json(result);
            });
	//}});
});

module.exports.router = categorieRouter;