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
var patient = require('./api/api_patient');
var patientRouter = patient.router;
var auth = require('./api/authentification');
var authRouter = auth.router;
var categorie = require('./api/api_categorie');
var categorieRouter = categorie.router;
var aideSoignant = require('./api/api_aide_soignant');
var aideSoignantRouter = aideSoignant.router;
var traitement = require('./api/api_traitement');
var traitementRouter = traitement.router;
var rendezVous = require('./api/api_rendez_vous');
var rendezVousRouter = rendezVous.router;
var firebase = require('./api/api_firebase');
var firebaseRouter = firebase.router;


var errHandler = function(err) {
    console.log(err);
}

/*var createTablePromise = tables.createAllTables();
createTablePromise.then(function(resolve, reject){
    var fillMedicamentCategorieTablePromise = tables.fillMedicamentCategorieTable();
    return fillMedicamentCategorieTablePromise;
}, errHandler)
.then(function(resolve, reject){
    var stringQueries = index.insertMedicamentsQueries();
    return stringQueries;
}, errHandler)
.then(function(stringQueries){
    if(stringQueries == null) console.error("Couldn't retrieve inserts queries");
    var queries = stringQueries.split('\n');
    for(var i=0; i<queries.length; i++){
        connection.query(queries[i], [], function(err, response){
            if(err){
                errHandler(err);
            }
        });
    }
});*/


app.use('/medicaments', medicamentRouter);
app.use('/patients', patientRouter);
app.use('', authRouter);
app.use('/categorie', categorieRouter);
app.use('/aide_soignants', aideSoignantRouter);
app.use('/traitements', traitementRouter);
app.use('/rendez_vous', rendezVousRouter);
app.use('/firebase_tokens', firebaseRouter);

var server = app.listen(port, function() {
	console.log('Listening on port ' + port);
});

module.exports = server;
