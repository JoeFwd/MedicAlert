var express = require('express');
var tables = require('../tables');
var connection = require('../connection');
var config = require('./config');
var bcrypt = require('bcrypt');
var jwt = require('jsonwebtoken');

authRouter = express.Router();

authRouter.post('/patients/login', checkExistenceOfPatientEmail, function(req, res) {
	let email = 'email';
	let password = 'password';
    let sql = 'SELECT * FROM ' + tables.tables.patients.nom + ' WHERE email = ?;';

    connection.query(sql, [req.body[email]], function(err, result){
        if(err){
            console.error(err);
            res.statusCode = 500;
            return res.json({
                errors: ['Erreur'],
                succes: false
            });
        }
        bcrypt.compare(req.body[password], result[0].password, function(err, compareResult) {
            if(compareResult){
                res.statusCode = 200;
                res.json({
                    id: result[0].id,
                    token: jwt.sign({
                            email: result[0].email,
                            prenom: result[0].prenom,
                            nom: result[0].nom,
                    }, config.secret),
                    succes: true
                });
            } else {
                res.statusCode = 400;
                return res.json({
                    succes: false
                });
            }
        });
    });
});

authRouter.post('/aide_soignants/login', checkExistenceOfAideSoignantEmail, function(req, res) {
	let email = 'email';
	let password = 'password';
    let sql = 'SELECT * FROM ' + tables.tables.aideSoignants.nom + ' WHERE email = ?;';

    connection.query(sql, [req.body[email]], function(err, result){
        if(err){
            console.error(err);
            res.statusCode = 500;
            return res.json({
                errors: ['Erreur'],
                succes: false
            });
        }
        bcrypt.compare(req.body[password], result[0].password, function(err, compareResult) {
            if(compareResult){
                res.statusCode = 200;
                res.json({
                    id: result[0].id,
                    token: jwt.sign({
                            email: result[0].email,
                            prenom: result[0].prenom,
                            nom: result[0].nom,
                    }, config.secret),
                    succes: true
                });
            } else {
                res.statusCode = 400;
                return res.json({
                    succes: false
                });
            }
        });
    });
});

function checkExistenceOfPatientEmail(req, res, next){
    sql = 'SELECT * FROM ' + tables.tables.patients.nom + ' WHERE email = ?;';
	connection.query(sql, [req.body['email']], function(err, result){
		if(err){
			console.error(err);
			res.statusCode = 500;
			return res.json({
				errors: ['La requête a échoué'],
				succes : false
			});
		}
		if(result.length === 0){
			res.statusCode = 404;
			return res.json({
				errors: ['not found'],
				succes: false
			});
		}
		next();
	});
}

function checkExistenceOfAideSoignantEmail(req, res, next){
    sql = 'SELECT * FROM ' + tables.tables.aideSoignants.nom + ' WHERE email = ?;';
	connection.query(sql, [req.body['email']], function(err, result){
		if(err){
			console.error(err);
			res.statusCode = 500;
			return res.json({
				errors: ['La requête a échoué'],
				succes : false
			});
		}
		if(result.length === 0){
			res.statusCode = 404;
			return res.json({
				errors: ['not found'],
				succes: false
			});
		}
		next();
	});
}

function ensureToken(req, res, next){
    const authHeader = req.headers['authorization'];
    if(typeof authHeader !== 'undefined'){
        req.token = authHeader;
    } else {
        res.statusCode = 403;
    }
    next();
}

module.exports.ensureToken = ensureToken;
module.exports.router = authRouter;