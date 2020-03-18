function validateForm(){
	var x = document.forms["Contact-Form"]["Name"].value;
	var y = document.forms["Contact-Form"]["Email"].value;
	var z = document.forms["Contact-Form"]["Phone"].value;
	
	var toReturn = true;
	
	if (x =="" || x==null || x < 1){
		alert("Name must be filled out");
		toReturn = false;
	}
	
	atpos = y.indexOf("@");
	dotpos = y.lastIndexOf(".");
	if (y =="" || y.length < 6 || atpos < 1 || dotpos - atpos < 2 ){
		alert("Please enter a valid email address");
		toReturn = false;
	}
	if (z =="" || z.length < 10){
		alert("Please enter your phone number (including area code)");
		toReturn = false;
	}

	return toReturn;
}

var form = document.getElementById("Contact-Form");
form.onsubmit = validateForm;