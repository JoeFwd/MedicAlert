- Base de donnée utilisé : http://base-donnees-publique.medicaments.gouv.fr/docs/Contenu_et_format_des_fichiers_telechargeables_dans_la_BDM_v1.pdf
- Téléchargement des données brutes : http://base-donnees-publique.medicaments.gouv.fr/telechargement.php
- Utilisation de deux projets github : 
	- Télécharger et parser les données de la base de données de médicament du site du gouvernement : https://github.com/TinyMan/bdpm-tools 
	- Un scanner qui permet de lire de nombreux type de "barcodes" : https://github.com/dm77/barcodescanner

Pré-requis :
Android Studio 3
nodejs v8
sql-server

rest_api :
- npm install
- sudo /etc/init.d/mysql start
- node server.js
