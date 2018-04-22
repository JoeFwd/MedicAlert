var connection = require('./connection');

const medicamentsTable = "Medicaments";
exports.medicamentsTable = medicamentsTable;

const createTableMedicaments = 
	'CREATE TABLE IF NOT EXISTS ' + medicamentsTable + '('
	+ 'cip7 INT PRIMARY KEY,'
	+ ' cis VARCHAR(8) NOT NULL,'
	+ ' nom VARCHAR(100) NOT NULL,'
	+ ' date_peremption DATETIME NOT NULL,'
	+ ' quantite INT'
	+ ')';

function notifyCreatedTable(result, table){
	if(result['warningCount'] == 0) console.log('Table ' + table + ' crée');
	else if(result['warningCount'] == 1) console.log('Table ' + table + ' existe');
	else console.log('Unknown table warning');
}

exports.createAllTables = function (){
	connection.query(createTableMedicaments, function(err, result){
		if(err){
			console.error(err);
			return;
		}
		notifyCreatedTable(result, medicamentsTable);
	});
}
/*UPDATE Medicaments
SET cis='6721345'
WHERE id = 2; 

UPDATE Medicaments
SET cis='7721345'
WHERE id = 1; */


