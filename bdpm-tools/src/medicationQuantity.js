const muliplierReducer = (accumulator, currentValue) => accumulator + currentValue;

function getNumberAtEndOfString(str){
	var isDecimal=false;
	var index=str.length;
	while(index > 0){
		if(index<str.length && !isDecimal && str.charAt(index-1) == ','){
			isDecimal=true;
			index--;
			continue;
		}
		if(str.charAt(index-1).match(/[0-9]/))
			index--;
		else break;
	}
	var number = str.slice(index, str.length).replace(',', '.');
	if(number.localeCompare('') == 0)
		return null;
	else
		return Number(number);
}

function getNumberAtBeginningOfString(str){
	var index=0;
	var isDecimal=false;
	while(index < str.length){
		if(index>0 && !isDecimal && str.charAt(index) == ','){
			isDecimal=true;
			index++;
			continue;
		}
		if(str.charAt(index).match(/[0-9]/))
			index++;
		else break;
	}
	var number = str.slice(0, index).replace(',', '.');
	if(number.localeCompare('') == 0)
		return null;
	else
		return Number(number);
}
var total = 0;

function removeDatesFromLibelle(libelle, sep){
	var sepLibelle = libelle.split(sep);
	if(sepLibelle.length < 2)
		return libelle;
	for(var i=0; i<sepLibelle.length; i++)
		if(sepLibelle[i].localeCompare('') == 0) return libelle;

	var str=sepLibelle[0];
	var index=str.length;
	while(index > 0){
		if(str.charAt(index-1).match(/[0-9]/)){
			index--;
		}
		else break;
	}
	sepLibelle[0] = str.slice(0, index);
	
	for(var i=1; i<sepLibelle.length; i++){
		index=0;
		str = sepLibelle[i];
		while(index < str.length){
			if(str.charAt(index).match(/[0-9]/)){
				index++;
			}
			else break;
		}
		sepLibelle[i] = str.slice(index, str.length);
	}
	return sepLibelle.join(sep);
}

/*const plaquettesStr = "plaquette(s)";
const plaquetteStr = "plaquette";
const geluleStr = "gélule";
const gelulesStr = "gélule(s)";
const comprimeStr = "comprimé";
const comprimesStr = "comprimé(s)"

function parseLibelle(libelle){
	while(libelle.localeCompare('') != 0){
		var elt = libelle.split(' ')[0];
		console.log(elt);
		if(elt.match(/^[0-9]+$/))
			return elt;
		if(elt.localeCompare(plaquettesStr))
			return plaquettesStr;
		if(elt.localeCompare(plaquetteStr))
			return plaquetteStr;
		if(elt.localeCompare(geluleStr))
			return geluleStr;
		if(elt.localeCompare(gelulesStr))
			return gelulesStr;
		if(elt.localeCompare(comprimeStr))
			return comprimeStr;
		if(elt.localeCompare(comprimesStr))
			return comprimesStr;
		libelle.splice(0, 1).join(' ');
	}
}

function consommeLibelle(libelle){
	return libelle.splice(0, 1);
}

function head(libelle, quantity){
	var num=1;
	var elt = parseLibelle(libelle);
	if(elt.match(/^[0-9]+$/))
		num=Number(elt);
	if(elt.localeCompare(plaquettesStr))
		return num * plaquettes(libelle, quantity);
	
	console.log("Error head");
	return null;
}

function plaquettes(libelle, quantity){
	var elt = parseLibelle(libelle);
	if()
}*/

module.exports.getQuantityOfLibelle = function (libelle){
	libelle = removeDatesFromLibelle(libelle, '/');
	libelle = removeDatesFromLibelle(libelle, '-');
	//console.log(total);
	/*En unité*/
	if(libelle.includes('plaquette(s)')){
		/*var numPlaquette = getNumberAtBeginningOfString(libelle);
		if(numPlaquette == null)
			numPlaquette=1;*/
		var numbers = libelle.match(/\d+/g);
		total++;
		return (numbers == null)?1:numbers.map(Number).reduce(muliplierReducer);
	}
	if(libelle.localeCompare('sachet(s)-dose(s) de 5 gélule(s)') == 0){
		total++;
		return 5;
	}
	if(libelle.includes('pilulier(s)')){
		total++;
		var numbers = libelle.match(/\d+/g);
		return (numbers == null)?1:numbers.map(Number).reduce(muliplierReducer);
	}
	var splitLibelle = libelle.split(' ');
	if(splitLibelle.length > 1){
		if(splitLibelle[0].match(/\d+/g) && splitLibelle[1].localeCompare('sachet(s)-dose(s)') == 0){
			total++;
			return Number(splitLibelle[0]);
		}
	}
	if(splitLibelle.length > 1){
		if(splitLibelle[0].match(/\d+/g) && splitLibelle[1].localeCompare('sachet(s)') == 0){
			total++;
			return Number(splitLibelle[0]);
		}
	}
	splitLibelle = libelle.split('dispositif(s) en sachet(s)');
	if(splitLibelle.length > 1){
		if(splitLibelle[0].match(/\d+/g)){
			total++;
			return Number(splitLibelle[0].trim());
		}
	}
	
	/*En ml*/
	if(libelle.localeCompare("2 poche(s) polyoléfine bicompartimenté(e)(s)/à 2 compartiments suremballée(s)/surpochée(s) de 250 ml (petit compartiment A) + 4750 ml (grand compartiment B)") == 0){total++; return 5250;}
	if(libelle.match(/poche\(s\)/)){
		var numPoches = getNumberAtEndOfString(libelle);
		if(numPoches == null)
			numPoches=1;
		splitLibelle = libelle.split('ml');
		if(splitLibelle.length > 1){
			var i=0, number;
			while((number = getNumberAtEndOfString(splitLibelle[i].trim())) == null){i++; if(i>=splitLibelle.length) break;}
			if(number != null){
				total++;
				return numPoches * number;
			}
		}
		splitLibelle = libelle.split('l');
		if(splitLibelle.length > 1){
			var i=0, number;
			while((number = getNumberAtEndOfString(splitLibelle[i].trim())) == null){i++; if(i>=splitLibelle.length) break;}
			if(number != null){
				total++;
				return numPoches * number * 1000;
			}
		}
	}
	if(libelle.match(/flacon\(s\)/)){
		var numFlacon = getNumberAtBeginningOfString(libelle);
		if(numFlacon == null)
			numFlacon=1;
		splitLibelle=libelle.split('gélule');
		if(splitLibelle.length > 1){
			var i=0, number;
			while((number = getNumberAtEndOfString(splitLibelle[i].trim())) == null){i++; if(i>=splitLibelle.length) break;}
			if(number != null){
				total++;
				return numFlacon * number;
			}			
		}
		splitLibelle=libelle.split('comprimé');
		if(splitLibelle.length > 1){
			var i=0, number;
			while((number = getNumberAtEndOfString(splitLibelle[i].trim())) == null){i++; if(i>=splitLibelle.length) break;}
			if(number != null){
				total++;
				return numFlacon * number;
			}			
		}
		/*splitLibelle=libelle.split('ml');
		if(splitLibelle.length > 1){
			var tmp = splitLibelle[0].split(' ');
			if(tmp.length > 1){
				if(tmp[0].match(/^[0-9]+$/g)){
					tmp.splice(0, 1);
				}
				splitLibelle[0] = tmp.join(' ');
			}
			console.log(splitLibelle[0]);
			//console.log(splitLibelle[0].match(/[0-9]/g).length);
		}*/
		
		
	
	}
	return -1;
}
