process.env.NODE_ENV = 'test';
let chai = require('chai');
let chaiHttp = require('chai-http');
let server = require('../server');
let connection = require('../connection');
let tables = require('../tables');
let should = chai.should();
chai.use(chaiHttp);

function ajouterMedicament(medicament){
    var attr = tables.tables.medicaments.attr;
    var sql = 'INSERT INTO ' + tables.tables.medicaments.nom + ' (' + attr[0] + ', ' + attr[1] + ', ' + attr[2] + ') VALUES (?,?,?);'
    connection.query(sql, [medicament.cip13, medicament.nom, medicament.formePharma], function(err, result){
        if(err) console.error(err);
    });
}

function supprimerMedicament(medicament){
    connection.query('DELETE FROM ' + tables.tables.medicaments.nom + ' WHERE cip13 = ?', [medicament.cip13], function(err, result){
        if(err) console.error(err);
    });
}


describe('Tests for MedicAlert\'s API', function () {
    	after(function (done) {
    		server.close(function (){
    			connection.end();
    			done();
    		});
    	});

describe('GET medicament', function () {
    let objs =
    [{
        cip13: '1463079',
        nom: 'Smecta 100',
        formePharma: 'solution'
    },
    {
        cip13: '1463080',
        nom: 'Smecta 200',
        formePharma: 'solution'
    }];
	beforeEach(function () {
	    ajouterMedicament(objs[0]);
	    ajouterMedicament(objs[1]);
	});
	afterEach(function () {
	   supprimerMedicament(objs[0]);
	   supprimerMedicament(objs[1]);
	});
    it('Cela devrait récupérer un médicament avec son code cip', function (done) {
        chai.request(server)
            .get('/medicaments/cip13/' + objs[0].cip13)
            .end(function (err, res) {
                res.should.have.status(200);
                res.body.should.be.deep.eql(objs[0]);
                done();
            });
    });

    it('Récupérer un médicament avec un mauvais code cip devrait échouer', function (done) {
        let wrongCip13 = objs[0].cip13 + 'aaa';
        chai.request(server)
            .get('/medicaments/cip13/' + wrongCip13)
            .end(function (err, res) {
                res.should.have.status(400);
                res.body.should.have.property('errors');
                res.body.should.have.property('invalidKey');
                res.body.invalidKey.should.be.deep.eql([wrongCip13]);

                done();
            });
    });

    it('Cela devrait récupérer tous les médicaments qui contiennent le nom donné en paramètre dans leur nom complet', function (done) {
        var attr = tables.tables.medicaments.attr;
        chai.request(server)
            .get('/medicaments/nom/Smecta')
            .end(function (err, res) {
                res.should.have.status(200);
                res.body.should.be.a('array');
                res.body[0].should.be.deep.eql(objs[0]);
                res.body[1].should.be.deep.eql(objs[1]);
                done();
            });
    });

    it('Cela devrait récupérer les médicaments (limite donné en paramètre) qui contiennent le nom donné en paramètre dans leur nom complet', function (done) {
        var limit = 1;
        var attr = tables.tables.medicaments.attr;
        chai.request(server)
            .get('/medicaments/nom/Smecta/' + limit)
            .end(function (err, res) {
                res.should.have.status(200);
                res.body.should.be.a('array');
                res.body.should.have.length(1);
                res.body[0].should.be.deep.eql(objs[0]);
                done();
            });
    });
});

describe('DELETE medicament', function () {
    let obj = {
        cip13: '1463079',
        nom: 'Smecta 100',
        formePharma: 'solution'
    };

    it('Cela devrait pouvoir supprimer un médicament', function (done) {
        var attr = tables.tables.medicaments.attr;
        var sql = 'INSERT INTO ' + tables.tables.medicaments.nom + ' (' + attr[0] + ', ' + attr[1] + ', ' + attr[2] + ') VALUES (?,?,?);'
        connection.query(sql, [obj.cip13, obj.nom, obj.formePharma], function(err, result){
            if(err) console.error(err);
        });
        chai.request(server)
            .delete('/medicaments/' + obj.cip13)
            .end(function (err, res) {
                res.should.have.status(200);
                res.body.should.have.property('succes');
                done();
            });
    });

    it('Supprimer un médicament qui n\'existe pas devrait échouer', function (done) {
        var attr = tables.tables.medicaments.attr;
        chai.request(server)
            .delete('/medicaments/' + obj.cip13)
            .end(function (err, res) {
                res.should.have.status(404);
                res.body.should.have.property('errors');
                res.body.should.have.property('notFound');
                done();
            });
    });

    it('Supprimer un médicament qui avec un mauvais code cip devrait échouer', function (done) {
        var attr = tables.tables.medicaments.attr;
        let wrongCip13 = obj.cip13 + 'aaa';
        chai.request(server)
            .delete('/medicaments/' + wrongCip13)
            .end(function (err, res) {
                res.should.have.status(400);
                res.body.should.have.property('errors');
                res.body.should.have.property('invalidKey');
                res.body.invalidKey.should.be.deep.eql([wrongCip13]);
                done();
            });
    });
});

describe('PATCH medicament', function () {
    let obj = {
        cip13: '1463079',
        nom: 'Smecta 100',
        formePharma: 'solution'
    }
    it('Cela devrait modifier un médicament', function (done) {
        let med = {
            cip13: '16008699',
            nom: 'Smecta 200',
            formePharma: 'comprimé'
        };
        ajouterMedicament(obj);
        chai.request(server)
            .patch('/medicaments/' + obj.cip13)
            .send(med)
            .end(function (err, res) {
                res.should.have.status(200);
                res.body.should.be.deep.eql(med);
                supprimerMedicament(med);
                done();
            });
    });

    it('Une requête de modifition sans paramêtre ne devrait rien modifier', function (done) {
        ajouterMedicament(obj);
        chai.request(server)
            .patch('/medicaments/' + obj.cip13)
            .end(function (err, res) {
                res.should.have.status(200);
                res.body.should.be.a('object');
                res.body.should.have.property('succes');
                supprimerMedicament(obj);
                done();
            });
    });

    it('Une requête de modifition avec un cip qui n\'existe pas devrait échouer', function (done) {
        ajouterMedicament(obj);
        let cip = Number(obj.cip13) + 1
        chai.request(server)
            .patch('/medicaments/' + cip)
            .end(function (err, res) {
                res.should.have.status(404);
                res.body.should.have.property('errors');
                res.body.should.have.property('notFound');
                supprimerMedicament(obj);
                done();
            });
    });

    it('Une requête de modifition avec un mauvais code cip devrait échouer', function (done) {
        let wrongCip13 = 'testcip';
        chai.request(server)
            .patch('/medicaments/' + wrongCip13)
            .end(function (err, res) {
                res.should.have.status(400);
                res.body.should.have.property('errors');
                res.body.should.have.property('invalidKey');
                res.body.invalidKey.should.be.deep.eql([wrongCip13]);
                done();
            });
    });

    it('Trop de paramètres devrait faire échouer la requête', function (done) { /*A VOIR avec si on peut supprimer cip13*/
    let med = {
        cip13: '16008699',
        nom: 'Smecta 200',
        formePharma: 'comprimé',
        dummy: 'dummy'
    };
    ajouterMedicament(obj);
    chai.request(server)
        .patch('/medicaments/' + obj.cip13)
        .send(med)
        .end(function (err, res) {
            res.should.have.status(400);
            res.body.should.have.property('errors');
            res.body.should.have.property('invalidNumberOfParams');
            res.body.invalidNumberOfParams.should.be.deep.eql(1);
            supprimerMedicament(obj);
            done();
        });
    });

    it('Paramètres qui ne correspondent pas aux attributs de la table Médicament devrait faire échouer la requête', function (done) {
        var wrongKeys = ['nomdd', 'formeAleatoire'];
        let med = {};
        med[wrongKeys[0]] = 'newName';
        med[wrongKeys[1]] = 'comprimé';
        ajouterMedicament(obj);
        chai.request(server)
            .patch('/medicaments/' + obj.cip13)
            .send(med)
            .end(function (err, res) {
                res.should.have.status(400);
                res.body.should.have.property('errors');
                res.body.should.have.property('invalidKey');
                res.body.invalidKey.should.be.deep.eql(wrongKeys);
                supprimerMedicament(obj);
                done();
            });
    });
});

describe('POST medicament', function () {
    let obj = {
        cip13: '1463079',
        nom: 'Smecta 100',
        formePharma: 'solution'
    };
	beforeEach(function () {
        supprimerMedicament(obj);
	});

	after(function (done) {
            supprimerMedicament(obj);
			done();
	});

	it('Cela devrait ajouter un medicament', function (done) {
		chai.request(server)
			.post('/medicaments')
			.send(obj)
			.end(function (err, res) {
				res.should.have.status(201);
				res.body.should.be.deep.eql(obj);
				done();
			});
	});
    it('L\'ajout d\'un médicament avec des attributs manquants devrait echouer', function (done) {
        chai.request(server)
            .post('/medicaments')
            .send({
                cip13: '1463079',
            })
            .end(function (err, res) {
                res.should.have.status(400);
                res.body.should.have.property('errors');
                res.body.should.have.property('invalidNumberOfParams');
                res.body.invalidNumberOfParams.should.be.deep.eql(2);
                done();
            });
    });
    it('L\'ajout d\'un médicament avec trop d\'attributs devrait échouer', function (done) {
        chai.request(server)
            .post('/medicaments')
            .send({
                cip13: '1463079',
                nom: 'Smecta 100',
                formePharma: 'solution',
                dummy: 'dummy'
            })
            .end(function (err, res) {
                res.should.have.status(400);
                res.body.should.have.property('errors');
                res.body.should.have.property('invalidNumberOfParams');
                res.body.invalidNumberOfParams.should.be.deep.eql(1);
                done();
            });
    });

    it('L\'ajout d\'un médicament avec des attributs mal nommés devrait échouer', function (done) {
        var wrongKeys = ['cip1', 'nomdd', 'formeAleatoire'];
        let med = {};
        med[wrongKeys[0]] = '1456306';
        med[wrongKeys[1]] = 'newName';
        med[wrongKeys[2]] = 'comprimé';
        chai.request(server)
            .post('/medicaments/')
            .send(med)
            .end(function (err, res) {
                res.should.have.status(400);
                res.body.should.have.property('errors');
                res.body.should.have.property('invalidKey');
                res.body.invalidKey.should.be.deep.eql(wrongKeys);
                done();
            });
    });


    it('L\'ajout d\'un médicament avec un code cip13 qui comporte autre chose que des chiffres devrait échouer', function (done) {
        let wrongCip13 = '146lettre3079';
        chai.request(server)
            .post('/medicaments')
            .send({
                cip13: wrongCip13,
                nom: 'Smecta 100',
                formePharma: 'solution',
            })
            .end(function (err, res) {
                res.should.have.status(400);
                res.body.should.have.property('errors');
                res.body.should.have.property('invalidKey');
                res.body.invalidKey.should.be.deep.eql([wrongCip13]);
                done();
            });
    });

    it('L\'ajout d\'un médicament avec un code cip13 existant devrait échouer', function (done) {
        ajouterMedicament(obj);
        let med = {
            cip13: obj.cip13,
            nom: 'Smecta 300',
            formePharma: 'solution'
        };
        chai.request(server)
            .post('/medicaments')
            .send(med)
            .end(function (err, res) {
                res.should.have.status(400);
                res.body.should.have.property('errors');
                res.body.should.have.property('duplicate');
                res.body.duplicate.should.be.deep.eql(obj.cip13);
                done();
            });
    });
});

function ajouterPatient(patient){
    var attr = tables.tables.patients.attr;
    var sql = 'INSERT INTO ' + tables.tables.patients.nom + ' ('+attr[0]+', '+attr[1]+', '+attr[2]+', '+attr[3]+', '+attr[4]+') VALUES (?,?,?,?,?);';
    connection.query(sql, [patient[attr[0]], patient[attr[1]], patient[attr[2]], patient[attr[3]], patient[attr[4]]], function(err, result){
        if(err) console.error(err);
    });
}

function supprimerPatient(patient){
    connection.query('DELETE FROM ' + tables.tables.patients.nom + ' WHERE email = ?', [patient.email], function(err, result){
        if(err) console.error(err);
    });
}

describe('POST patient', function () {
	let patient = {
        email: 'test@api.com',
        password: 'great_password',
        prenom: 'Jean',
        nom: 'Damien',
        date_naissance:'1972-03-22'
    };
	beforeEach(function () {
        ajouterPatient(patient);
    });
	afterEach(function () {
	    supprimerPatient(patient);
	});
    it('Cela devrait ajouter un patient', function (done) {
        let autre_patient = {
            email: 'another@api.com',
            password: 'plain_password',
            prenom: 'Corentin',
            nom: 'Boucher',
            date_naissance:'1991-11-02'
        };
        chai.request(server)
            .post('/patients')
            .send(autre_patient)
            .end(function (err, res) {
                res.should.have.status(201);
                res.body.should.have.property('succes');
                supprimerPatient(autre_patient);
                done();
            });
    });

    it('Ajouter un nouveau patient avec un email déjà existant devrait échouer', function (done) {
        let autre_patient = {
            email: patient.email,
            password: 'plain_password',
            prenom: 'Corentin',
            nom: 'Boucher',
            date_naissance:'1991-11-02'
        };
        chai.request(server)
            .post('/patients')
            .send(autre_patient)
            .end(function (err, res) {
                res.should.have.status(400);
                res.body.should.have.property('errors');
                res.body.should.have.property('duplicate');
                res.body.duplicate.should.be.eql(autre_patient.email);
                supprimerPatient(autre_patient);
                done();
            });
    });
});

describe('PATCH patient', function () {
	let patient = {
        email: 'test@api.com',
        password: 'great_password',
        prenom: 'Jean',
        nom: 'Damien',
        date_naissance:'1972-03-22'
    };
    let newPassword = "abcdef!";
    let newEmail = "new_email@adress.com";
    let newName = "Bob";
	beforeEach(function () {
	    ajouterPatient(patient);
	});
	afterEach(function () {
	   supprimerPatient(patient);
	});
   it('Cela devrait modifier un patient', function (done) {
        chai.request(server)
            .patch('/patients/' + patient.email)
            .send({
                email : newEmail,
                password : newPassword,
                nom : newName
            })
            .end(function (err, res) {
                res.should.have.status(200);
                res.body.should.be.deep.eql({
                    cip13: obj.cip13,
                    nom : updatedNom,
                    formePharma: obj.formePharma
                });
                done();
            });
    });/*

    it('Une requête de modifition sans paramêtre ne devrait rien modifier', function (done) {
        chai.request(server)
            .patch('/patients/' + patient.email)
            .end(function (err, res) {
                res.should.have.status(200);
                res.body.should.be.a('object');
                res.body.should.have.property('message');
                done();
            });
    });

    it('Une requête de modifition avec un email qui n\'existe pas devrait échouer', function (done) {
        chai.request(server)
            .patch('/patients/' + newEmail)
            .end(function (err, res) {
                res.should.have.status(404);
                res.body.should.have.property('errors');
                done();
            });
    });

    /*it('Une requête de modifition avec un mauvais code cip devrait échouer', function (done) {
        let cip = 'testcip';
        chai.request(server)
            .patch('/medicaments/' + cip)
            .end(function (err, res) {
                res.should.have.status(400);
                res.body.should.have.property('errors');
                done();
            });
    });

    it('Trop de paramètres devrait faire échouer la requête', function (done) {
               let updatedNom = 'Smecta 200'
               chai.request(server)
                   .patch('/medicaments/' + obj.cip13)
                   .send({
                       cip13: obj.cip13,
                       nom : updatedNom,
                       formePharma: obj.formePharma
                   })
                   .end(function (err, res) {
                       res.should.have.status(400);
                       res.body.should.have.property('errors');
                       done();
                   });
           });

    it('Paramètres qui ne correspondent pas aux attributs de la table Médicament devrait faire échouer la requête', function (done) {
        let updatedNom = 'Smecta 200'
        chai.request(server)
            .patch('/medicaments/' + obj.cip13)
            .send({
                nom : updatedNom,
                formeAleatoire: obj.formePharma
            })
            .end(function (err, res) {
                res.should.have.status(400);
                res.body.should.have.property('errors');
                done();
            });
    });*/
});

});