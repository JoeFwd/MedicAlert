process.env.NODE_ENV = 'test';
let chai = require('chai');
let chaiHttp = require('chai-http');
let server = require('../server');
let connection = require('../connection');
let tables = require('../tables');
let should = chai.should();
chai.use(chaiHttp);

function data(){
	return [
		{
			cip7: '1463078',
			cis: '68476308',
			nom: 'Smecta 100',
			date_peremption: '2022-01-11',
			quantite: '100'
		},
		{
			cip7: '1463081',
			cis: '68475604',
			nom: 'Doliprane 1000',
			date_peremption: '2019-05-23',
			quantite: '20'	
		},
		{
			cip7: '1463079',
			cis: '68476308',
			nom: 'Smecta 300',
			date_peremption: '2022-01-11',
			quantite: '300'	
		},
		{
			cip7: '1463080',
			cis: '68476308',
			nom: 'Smecta 200',
			date_peremption: '2022-01-11',
			quantite: '200'	
		}
	];
}

describe('/POST medicament', function () {
	beforeEach(function () {
		connection.query('DELETE FROM ' + tables.medicamentsTable, function(err, result){
			if(err) console.error(err);
		});
	});

	after(function (done) {
		server.close(function (){
			connection.query('DELETE FROM ' + tables.medicamentsTable, function(err, result){
				if(err) console.error(err);
			});
			connection.end();
			done();
		});
	});
	
	let obj = {
		cip7: 1463078,
		cis: '68476308',
		nom: 'Smecta 100',
		date_peremption: '2022-01-11',
		quantite: 100
	};
	it('Cela devrait ajouter un medicament', function (done) {
		chai.request(server)
			.post('/medicaments')
			.send(obj)
			.end(function (err, res) {
				res.should.have.status(201);
				res.body.should.be.eql(obj);
			});
		done();
	});
});
