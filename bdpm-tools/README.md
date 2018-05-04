Télécharge les données depuis http://base-donnees-publique.medicaments.gouv.fr/telechargement.php et crée un fichier sql. Dans celui-ci, la table Medicaments est crée et ajoute tous les médicaments qui ont un code cip13 dans la table, plus précisement, son nom, son code cip13 et sa forme galénique. 

# Usage

```
cd bdpm-tools
npm install
node index.js
```

Github utilisé : https://github.com/TinyMan/bdpm-tools
