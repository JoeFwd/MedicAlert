const converter = require('./converter');
const path = require('path');
const download = require('download');

const fs = require('fs')
const { promisify } = require('util')
const mkdir = promisify(fs.mkdir);

const bdpm_folder = 'bdpm';
const writeFile = promisify(fs.writeFile);

const files = {
	'CIS_CIP_bdpm': {
		name: 'Fichier des présentations',
		description: 'Il contient la liste des présentations (boîtes de médicaments) disponibles pour les médicaments présents dans le fichier décrit dans le paragraphe 3.1.',
		headers: [
			{ name: 'cis', type: 'string', pattern: /^\d{8}$/ },
			{ name: 'cip7', type: 'string', pattern: /^\d{7}$/ },
			'libelle',
			'statutAdministratif',
			'etatCommercialisation',
			{ name: 'dateDeclaration', type: 'date', toFormat: 'DD/MM/YYYY', format: 'DD/MM/YYYY' },
			{ name: 'cip13', type: 'string', pattern: /^\d{13}$/ },
			'agrementCollectivites',
			{ name: 'tauxRemboursement', type: 'array', sep: ';' },
			{ name: 'prix', type: 'float' },
			{ name: 'indicationsRemboursement', type: 'float' }
		]
	},
	'CIS_bdpm': {
		name: 'Fichier des spécialités',
		description: 'Il contient la liste des médicaments commercialisés ou en arrêt de commercialisation depuis moins de trois ans',
		headers: [
			{ name: 'cis', type: 'string', pattern: /^\d{8}$/ },
			'nom',
			'formePharma',
			{ name: 'voiesAdministration', type: 'array', sep: ';' },
			'statutAMM',
			'typeAMM',
			'etatCommercialisation',
			{ name: 'dateAMM', type: 'date', toFormat: 'DD/MM/YYYY', format: 'DD/MM/YYYY' },
			'statutBDM',
			'numAutorisation',
			{ name: 'titulaires', type: 'array', sep: ';' },
			{ name: 'surveillance', type: 'bool', deserializer: e => e === 'Oui' }
		]
	}
}

const json_formats = [
	{
		file: 'CIS_CIP_bdpm',
		fields: [
			'cis',
			'cip7',
			'cip13',
			'libelle',
			'statutAdministratif',
			'etatCommercialisation',
			'dateDeclaration',
			'agrementCollectivites',
			'prix',
			'indicationsRemboursement'
		],
	}, {
		file: 'CIS_bdpm',
		fields: [
			'cis',
			'nom',
			'formePharma',
			'statutAMM',
			'typeAMM',
			'etatCommercialisation',
			'dateAMM',
			'statutBDM',
			'numAutorisation',
			'surveillance'
		],
	}
]

function hasDesignation(des, array){
	for(var i=0; i<array.length; i++){
		if(array[i].localeCompare(des) == 0)
			return true;
	}
	return false;
}

const formePharmas = [
	"bâton",
	"capsule",
	"collyre",
	"comprimé",
	"crème",
	"dispositif",
	"emplâtre",
	"émulsion",
	"gel",
	"gélule",
	"gomme",
	"granule",
	"granulé",
	"lotion",
	"lyophilisat",
	"mousse",
	"ovule",
	"pastille",
	"pâte",
	"pommade",
	"poudre",
	"shampooing",
	"sirop",
	"solution",
	"suppositoire",
	"suspension",
	"vernis",
];
exports.formePharmas = formePharmas;

function parseformePharma(designation, formePharmas){
	for(var index=0; index<formePharmas.length; index++){
		if(designation.includes(formePharmas[index])){
			if(formePharmas[index].localeCompare("") == 0){
			
			}
			return formePharmas[index];
		}	
	}
	return "autres";
}

async function insertMedicamentsQueries() {
	try {
		try {
			await mkdir(path.join(__dirname, bdpm_folder));
		} catch (e) {
			if (e.code !== 'EEXIST') throw e;
		}
		const p = json_formats
			.map(async a => {
				console.log(`Downloading ${a.file} ...`)
				const url = `http://base-donnees-publique.medicaments.gouv.fr/telechargement.php?fichier=${a.file}.txt`;
				await download(url, path.join(__dirname, bdpm_folder), { filename: a.file + '.txt' });
				return a;
			})
			.map(async p => {
				const { file, req, fields, update, query, condition } = await p;
				console.log(`Parsing ${file} ...`);
				const filename = path.join(__dirname, bdpm_folder, file + '.txt');
				try {
					const res = await converter(filename, files[file].headers);
					return res;
				} catch (e) {
					console.error(`Erreur sur le fichier ${file} (${files[file].name}): `, e);
				}
			});
		
		var jsons = await Promise.all(p);
		var CIS_CIP_bdpm = jsons[0];
		var CIS_bdpm = jsons[1];
		var inserts = "";
		
		inserts+='CREATE TABLE IF NOT EXISTS Medicaments ('
		+ 'nom VARCHAR(255) NOT NULL,'
		+ ' cip13 VARCHAR(15) PRIMARY KEY,'
		+ ' formeGalenique VARCHAR(255) NOT NULL'
		+ ');\n';

		var regex = new RegExp("'", "g");
		for(var cis in CIS_bdpm){
			if(CIS_CIP_bdpm[cis]){
				inserts += "INSERT INTO Medicaments (cip13, nom, formePharma) VALUES (";
				inserts += '\'' + CIS_CIP_bdpm[cis].cip13.replace(regex, "''") + "'" + ", ";
				inserts += '\'' + CIS_bdpm[cis].nom.replace(regex, "''") + "'" + ", ";
				inserts += '\'' + parseformePharma(CIS_bdpm[cis].formePharma, formePharmas).replace(regex, "''") + "'";
				inserts += ");\n";
				inserts.replace(new RegExp(String.fromCharCode(9647), "g"), '');
                inserts.replace(new RegExp(String.fromCharCode(9634), "g"), '');
                inserts.replace(/\?\?H\?\?/g, '');
			}
		}

		inserts+="COMMIT;"
		await Promise.all([
			writeFile('insertMedicaments.sql', inserts)
		]);
		
		return inserts;
	} catch (e) {
		console.error(e);
		return null;
	}
}

module.exports.insertMedicamentsQueries = insertMedicamentsQueries;