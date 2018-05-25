process.env.NODE_ENV = 'test';
let chai = require('chai');
let chaiHttp = require('chai-http');
let server = require('../server');
let connection = require('../connection');
let tables = require('../tables');
let config = require('../api/config');
let bcrypt = require('bcrypt');
let jwt = require('jsonwebtoken');
let should = chai.should();
chai.use(chaiHttp);

const token = jwt.sign({
      id: 1,
      email: "someone@adress.fr",
      prenom: "bob",
      nom: "bobo",
      date_naissance: "02/02/2002"
}, config.secret);

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
            .set('authorization', token)
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
            .set('authorization', token)
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
            .set('authorization', token)
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
            .set('authorization', token)
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
            .set('authorization', token)
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
            .set('authorization', token)
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
            .set('authorization', token)
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
            .set('authorization', token)
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
            .set('authorization', token)
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
            .set('authorization', token)
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
            .set('authorization', token)
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
        .set('authorization', token)
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
            .set('authorization', token)
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
			.set('authorization', token)
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
            .set('authorization', token)
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
            .set('authorization', token)
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
            .set('authorization', token)
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
            .set('authorization', token)
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
            .set('authorization', token)
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
	sql = 'INSERT INTO ' + tables.tables.patients.nom + ' ('+attr[0]+', '+attr[1]+', '+attr[2]+', '+attr[3]+', '+attr[4]+') VALUES (?,?,?,?,?);';
    bcrypt.hash(patient[attr[1]], config.saltRounds, function(err, hash) {
        data = [
            patient[attr[0]],
            hash,
            patient[attr[2]],
            patient[attr[3]],
            patient[attr[4]]
        ];
        connection.query(sql, data, function(erro, response){
            if(erro){
                console.error("erreur " + erro);
            }
        });
    });
}

function supprimerPatient(patient){
    connection.query('DELETE FROM ' + tables.tables.patients.nom + ' WHERE email = ?', [patient.email], function(err, result){
        if(err) console.error(err);
    });
}

function ajouterAideSoignant(aideSoignant){
    var attr = tables.tables.aideSoignants.attr;
    var sql = 'INSERT INTO ' + tables.tables.aideSoignants.nom + ' (';
    var values = "";

    for(var i=0; i<tables.tables.aideSoignants.attr.length; i++){
        sql+= attr[i] + ",";
        values += '?,';
    }
    sql = sql.substring(0, sql.length - 1) + ') VALUES (' + values.substring(0, values.length-1) + ");";


    bcrypt.hash(aideSoignant[attr[1]], config.saltRounds, function(err, hash) {
        data = [
            aideSoignant[attr[0]],
            hash,
            aideSoignant[attr[2]],
            aideSoignant[attr[3]],
            aideSoignant[attr[4]],
            aideSoignant[attr[5]]
        ];
        connection.query(sql, data, function(erro, response){
            if(erro){
                console.error("erreur ajout aide soignant" + erro);
            }
        });
    });
}

function supprimerAideSoignant(aideSoignant){
    connection.query('DELETE FROM ' + tables.tables.aideSoignants.nom + ' WHERE email = ?', [aideSoignant.email], function(err, result){
        if(err) console.error(err);
    });
}

describe('POST patient', function () {
    let aideSoignant= {
        email: 'dummy@aidesoignant.com',
        password: 'plain_password',
        prenom: 'Marc',
        nom: 'March',
        date_naissance:'1989-01-09',
        adresse:"Palais Longchamps"
    };

    let patient = {
        email: 'dummy@adress.com',
        password: 'plain_password',
        prenom: 'Marc',
        nom: 'March',
        date_naissance:'1989-01-09',
        aide_soignant:1
    };
	before(function () {
        ajouterAideSoignant(aideSoignant);
	});

	after(function (done) {
        supprimerAideSoignant(aideSoignant);
        done();
	});
    it('Cela devrait ajouter un patient', function (done) {
        let autre_patient = {
            email: 'another@api.com',
            password: 'plain_password',
            prenom: 'Corentin',
            nom: 'Boucher',
            date_naissance:'1991-11-02',
            aide_soignant:1
        };
        chai.request(server)
            .post('/patients')
            .set('authorization', token)
            .send(autre_patient)
            .end(function (err, res) {
                res.should.have.status(201);
                res.body.should.have.property('succes');
                res.body.succes.should.be.eql(true);
                supprimerPatient(autre_patient);
                done();
            });
    });
});

describe('PATCH patient', function () {
    let aideSoignant= {
        email: 'dummy@aidesoignant.com',
        password: 'plain_password',
        prenom: 'Marc',
        nom: 'March',
        date_naissance:'1989-01-09',
        adresse:"Palais Longchamps"
    };
	before(function () {
        ajouterAideSoignant(aideSoignant);
	});

	after(function (done) {
        supprimerAideSoignant(aideSoignant);
        done();
	});
	let patient = {
        email: 'test@api.com',
        password: 'great_password',
        prenom: 'Jean',
        nom: 'Damien',
        date_naissance:'1972-03-22',
        aide_soignant:1
    };
    let newPassword = "abcdef!";
    let newEmail = "new_email@adress.com";
    let newName = "Bob";
    let newAideSoignant = "2";
	beforeEach(function () {
	    ajouterPatient(patient);
	});
   it('Cela devrait modifier un patient', function (done) {
        chai.request(server)
            .patch('/patients/' + patient.email)
            .send({
                email : newEmail,
                password : newPassword,
                nom : newName,
                aide_soignant:newAideSoignant
            })
            .end(function (err, res) {
                res.should.have.status(200);
                res.body.should.have.property('succes');
                res.body.succes.should.be.eql(true);
                supprimerPatient({
                    email: newEmail,
                    password: newPassword,
                    prenom: patient.prenom,
                    nom: newName,
                    date_naissance:patient.date_naissance,
                    aide_soignant:newAideSoignant
                });
                done();
            });
    });
});

describe('/login', function () {
    after(function (done) {
        supprimerPatient(p);
        done();
    });

    let p = {
        email: 'test@adress.fr',
        password: 'plain_password',
        prenom: 'Corentin',
        nom: 'Boucher',
        date_naissance:'1991-11-02'
    };
    ajouterPatient(p);
    it('S\' identifer devrait marcher', function (done) {
        chai.request(server)
            .post('/login')
            .send({
                email: p.email,
                password: p.password
            })
            .end(function (err, res) {
                res.should.have.status(200);
                res.body.should.have.property('succes');
                res.body.should.have.property('token');
                res.body.succes.should.be.eql(true);
                done();
            });
    });

    it('S\' identifer avec un mauvais mot de passe ne devrait pas pouvoir marcher', function (done) {
        chai.request(server)
            .post('/login')
            .send({
                email: p.email,
                password: p.password + 1
            })
            .end(function (err, res) {
                res.should.have.status(400);
                res.body.should.have.property('succes');
                res.body.should.not.have.property('token');
                res.body.succes.should.be.eql(false);
                done();
            });
    });

    it('S\' identifer avec un email inexistant ne devrait pas pouvoir marcher', function (done) {
        chai.request(server)
            .post('/login')
            .send({
                email: p.email + "fail_email",
                password: p.password + 1
            })
            .end(function (err, res) {
                res.should.have.status(404);
                res.body.should.have.property('errors');
                res.body.should.have.property('notFound');
                res.body.notFound.should.be.eql(true);
                done();
            });
    });
});

});