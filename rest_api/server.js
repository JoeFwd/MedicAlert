/*sudo /etc/init.d/mysql start*/

var connection = require('./connection');
var tables = require('./tables');

var express = require('express');
var bodyParser = require('body-parser');
var app = express();
var port = process.env.PORT || 8080;

// To parse JSON : req.body in json:
app.use(bodyParser.json({type: 'application/json'}));
// To parse form data:
app.use(bodyParser.urlencoded({
  extended: true
}));

var index = require('./index');
var medicamentRouter = require('./api/api_medicament');
var patientRouter = require('./api/api_patient');

tables.createAllTables();

async function fillMedicamentsTable(){
    var test = await index.insertMedicamentsQueries();
    if(test == null) return;
    var queries = test.split('\n');
    for(var i=0; i<queries.length; i++){
        await connection.query(queries[i], [], function(err, response){
            if(err){
                console.error(err);
                return;
            }
        });
    }
}

//fillMedicamentsTable();

app.use('/medicaments', medicamentRouter);
app.use('/patients', patientRouter);

var server = app.listen(port, function() {
	console.log('Listening on port ' + port);
});

module.exports = server;
