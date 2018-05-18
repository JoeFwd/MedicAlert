var connection = require('./connection');

const tables = {
    medicaments : {nom : "Medicaments", attr : ['cip13', 'nom', 'formePharma']},
    patients : {nom : "Patients", attr : ['email', 'password', 'prenom', 'nom', 'date_naissance']}
};
exports.tables = tables;

const createTables = [
        'CREATE TABLE IF NOT EXISTS ' + tables.medicaments.nom + '('
        + 'id INT AUTO_INCREMENT PRIMARY KEY,'
        + tables.medicaments.attr[0] + ' VARCHAR(13) NOT NULL,'
        + tables.medicaments.attr[1] + ' VARCHAR(256) NOT NULL,'
        + tables.medicaments.attr[2] + ' VARCHAR(64) NOT NULL,'
        + 'CONSTRAINT UniqueCIP UNIQUE(' + tables.medicaments.attr[0] +')'
        + ')',

        'CREATE TABLE IF NOT EXISTS ' + tables.patients.nom + '('
        + 'id INT AUTO_INCREMENT PRIMARY KEY,'
        + tables.patients.attr[0] + ' VARCHAR(64) NOT NULL,'
        + tables.patients.attr[1] + ' VARCHAR(255) NOT NULL,'
        + tables.patients.attr[2] + ' VARCHAR(32) NOT NULL,'
        + tables.patients.attr[3] + ' VARCHAR(32) NOT NULL,'
        + tables.patients.attr[4] + ' DATETIME NOT NULL,'
        + ' CONSTRAINT UniqueEmail UNIQUE(' + tables.patients.attr[0] +')'
        + ')'
];

function notifyCreatedTable(result, table){
	if(result['warningCount'] == 0) console.log('Table ' + table + ' crée');
	else if(result['warningCount'] == 1) console.log('Table ' + table + ' existe');
	else console.log('Unknown table warning');
}

async function createAllTables(){
    for(var i=0; i<createTables.length; i++){
        await connection.query(createTables[i], function(err, result){
            if(err){
                console.error(err);
                return;
            }
            //notifyCreatedTable(result, medicamentsTable);
        });
    }
}

exports.createAllTables = createAllTables;