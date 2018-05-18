var express = require('express');
var tables = require('../tables');
var connection = require('../connection');
var utils = require('./utils');
var bcrypt = require('bcrypt');
const saltRounds = 10;


var patientRouter = express.Router();
var attr = tables.tables.patients.attr;

//const dateNaissanceYYYYMMDD = 'convert(varchar, ?, 23)'; /*DATE_FORMAT(date_peremption, "%Y-%m-%d") AS date_peremption,               req.body.date_peremption, Format : YYYY-MM-DD*/
const genericSelectPatients = 'SELECT ' + attr[0] + ', ' + attr[1] + ', ' + attr[2] + ', ' + attr[3] + ', ' + 'DATE_FORMAT(' + attr[4] + ', "%Y-%m-%d") AS '  + attr[4] + ' FROM ' + tables.tables.patients.nom;

function responseForDuplicateEmails(req, res, next){
	sql = 'SELECT * FROM ' + tables.tables.patients.nom + ' WHERE email = ?;';

    connection.query(sql, [req.body[attr[0]]], function(err, response){
        if(err){
            console.error(err);
            res.statusCode = 500;
            return res.json({
                errors: ['La création du patient a échouée']
            });
        }
        if(response.length > 0){
            res.statusCode = 400;
            return res.json({
                errors: ["Duplicate email"],
                duplicate: req.body[attr[0]]
            });
        }
        next();
    });
}

patientRouter.post('/', responseForDuplicateEmails, function(req, res) {
	sql = 'INSERT INTO ' + tables.tables.patients.nom + ' ('+attr[0]+', '+attr[1]+', '+attr[2]+', '+attr[3]+', '+attr[4]+') VALUES (?,?,?,?,?);';

    bcrypt.hash(req.body[attr[1]], saltRounds, function(err, hash) {
        data = [
            req.body[attr[0]],
            hash,
            req.body[attr[2]],
            req.body[attr[3]],
            req.body[attr[4]]
        ];
        connection.query(sql, data, function(err, response){
            if(err){
                console.error(err);
                res.statusCode = 500;
                return res.json({
                    errors: ['La création du patient a échouée']
                });
            }
            res.statusCode = 201;
            return res.json({
                succes: "Patient crée avec succès"
            });
        });
    });

		/*var sql = genericSelectMedicaments + ' WHERE cip13 = ?;';
		connection.query(sql, [req.body.cip13], function(err, result){
			if(err){
				console.error(err);
				res.statusCode = 500;
				return res.json({
					errors: ['Le médicament est introuvable après sa création']
				});
			}
			res.statusCode = 201;
			res.json(removeIdAttribute(result[0]));
		});*/
});

patientRouter.patch('/:email', responseForDuplicateEmails, function(req, res) {
    if(utils.isEmptyObject(req.body)){
        res.statusCode = 200;
        return res.json({
            message: ['Aucune mise à jour']
        });
    }

	var sql = 'UPDATE ' + tables.tables.patients.nom + " SET ";
	var data = [];
    bcrypt.hash(req.body[attr[1]], saltRounds, function(err, hash) {
        for(var key in req.body){
            sql += key + '=?,';
            console.log(key);
            if(key.localeCompare(attr[1]) == 0){
                data.push(hash);
                console.log('pw hashed');
            }
            else
                data.push(req.body[key]);
        }

        sql = sql.substring(0, sql.length - 1);
        sql += ' WHERE ' + attr[0] + ' = ' + connection.escape(req.params[attr[0]]) + ';';

        connection.query(sql, data, function(err, result){
            if(err){
                console.error(err);
                res.statusCode = 500;
                return res.json({
                    errors: ['Le patient n\'a pas pu être modifié']
                });
            }
            sql = 'SELECT * FROM ' + tables.tables.patients.nom + ' WHERE email = ?;';
            connection.query(sql, [req.params.email], function(err, result){
                if(err){
                    console.error(err);
                    res.statusCode = 500;
                    return res.json({
                        errors: ['Le patient est introuvable après sa modification']
                    });
                }
                res.statusCode = 200;
                res.json(utils.removeIdAttribute(result[0]));
            });
        });
    });
});

/*patientRouter.delete('/:cip13', findMedicamentBycip13, function(req, res) {
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
});*/

module.exports = patientRouter;