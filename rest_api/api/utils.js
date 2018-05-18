function hasOnlyNumbers(str){
	return (str.match(/^[0-9]+$/))?true:false;
}

module.exports.hasOnlyNumbers = hasOnlyNumbers;

function isEmptyObject(obj){
	for(var prop in obj) {
		if(obj.hasOwnProperty(prop)){
			return false;
		}
	}
	return true;
}

module.exports.isEmptyObject = isEmptyObject;

function removeIdAttribute(obj){
    var newObj = {};
    for(var key in obj){
        if(key.localeCompare("id") != 0)
            newObj[key] = obj[key];
    }
    return newObj;
}

module.exports.removeIdAttribute = removeIdAttribute;