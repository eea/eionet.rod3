var isChanged = false;
var selects = new Array();
var clist=new Array();

function mvValues(selFrom, selTo, unit) {
	var i, count = 0;
	var optsLen;
	var newVal, newText;

	isChanged = true;

	var selected = new Array();

	for (i = 0; i < selFrom.length; ++i) {
		if (selFrom[i].selected) {
			selected[count++] = i;

			newVal = selFrom[i].value;
			var pos = newVal.indexOf(':');
			if (pos > 0) {
				newVal = newVal.substr(0, pos);
			}

			newText = selFrom[i].text;
			pos = newText.indexOf('[');
			if (pos > 1) {
				newText = newText.substr(0, pos - 1); // strip leading space as well
			}
			if (unit != null && unit.text.length > 0) {
				newVal = newVal + ':' + unit.value;
				newText = newText + ' [' + unit.text + ']';
			}

			var opt = document.createElement("option");
			selTo.add(opt);
			opt.text = newText;
			opt.value = newVal;
		}
	}

	// remove from
	count = 0;
	for (i = selected.length-1; i >= 0; --i) {
		selFrom[selected[i]] = null;
	}
}

function addClientValuesSel(selFrom, selTo){
	for (i = 0; i < selTo.length; ++i) {
		if (selFrom.value == selTo[i].value){
			alert("No client can be participating both 'Report to' and 'Other clients using this reporting'." + '\n' + "To report it formally you must first remove it from list-box of the 'Others clients using this reporting'.");
			return false;
		}
	}
	return true;
}

function addClientValues(selFrom, selTo) {
	var i, j, count = 0;
	var optsLen;
	var newVal, newText;

	isChanged = true;
	
	var selected = new Array();

	for (i = 0; i < selFrom.length; ++i) {
		if (selFrom[i].selected) {
			var exists;
			exists = false;
			for (j = 0;j < selTo.length; ++j) {
				var s,x;
				s = new String(selTo[j].value);
				u = new String("");

/*				if (s.indexOf(":") > -1) {
					u = s.substr(s.indexOf(":")+1);
					s = s.substring(0,s.indexOf(":"));
				} */

					if (s.valueOf() == selFrom[i].value) {
						exists = true;
						break;
					}
			}
			if (!exists) {
				var existsClientReport = document.f.report_to.value;
				selected[count++] = i;

				newVal = selFrom[i].value;
				
				if (newVal != existsClientReport){

					var pos = newVal.indexOf(':');
					/*if (pos > 0) {
						newVal = newVal.substr(0, pos);
					} */
	
					newText = selFrom[i].text;
					pos = newText.indexOf('[');
					if (pos > 1) {
						newText = newText.substr(0, pos - 1); // strip leading space as well
					}
					/*if (unit != null && unit.text.length > 0) {
						newVal = newVal + ':' + unit.value;
						newText = newText + ' [' + unit.text + ']';
					} */
	
					// add to
					var opt = document.createElement("option");
					selTo.add(opt);
					opt.text = newText;
					opt.value = newVal;
				}else{
					alert("No client can be participating both 'Report to' and 'Other clients using this reporting'." + '\n' + "To report it formally you must first remove it from list-box of the 'Report to'.");
				}
			}
		}
	}
}

function delValues(selFrom) {
	for (i = selFrom.length-1; i >= 0; --i) {
		if (selFrom[i].selected) {
			selFrom[i] = null;
		}
	}
}

function inclSelect(sel1, sel2) {
	var pair = new Array(2);
	pair[0] = sel1;
	pair[1] = sel2;
	selects[selects.length] = pair;
}

// select all options of the specified selects
function selectOptions() {
	for (i = 0; i < selects.length; ++i) {
		selValues(selects[i][0]);
	}
}

function selValues(sel) {
	for (i = 0; i < sel.length; ++i) {
		sel[i].selected = true;
	}
}

function changed() {
	isChanged = true;
}

function changedReporting() {

	var first = document.forms['f'].elements['firstReporting'];
	var freq = document.forms['f'].elements['reportFreqMonths'];
	var next = document.forms['f'].elements['nextDeadline'];
	var to = document.forms['f'].elements['validTo'];
	var terminate = document.forms['f'].elements['terminate'];
	var next2 = document.forms['f'].elements['nextDeadline2'];

	changed();

	var utcMonth;
	next.value = "";
	next2.value = "";
	terminate.value = "N";

	fiDate = checkDateSimple(first);
	toDate = checkDateSimple(to);
	
	fx = checkNumberSimple(freq);

	// Provide default if to value not given
	//
	if(fiDate != false && toDate == false)
		to.value = "31/12/9999";

	// Check that date fields have valid values
	//
	if(fiDate == false || toDate == false)
		return;

	// Calculate value for TERMINATE
	//
	var currDate = new Date();
	currDate.setUTCMinutes(currDate.getUTCMinutes() - currDate.getTimezoneOffset());
	var toDat = new Date(toDate.yx, parseInt(toDate.mx, 10)-1, toDate.dx, 23, 59, 59);
	toDat.setUTCMinutes(toDat.getUTCMinutes() - toDat.getTimezoneOffset());
	if(currDate.getTime() > toDat.getTime())
		terminate.value = "Y";

	// Check that frequency field has valid value
	//
	if(fx == -1)
		return;

	// If non-repeating, use First Reporting for Next Due Date
	//
	if(fx == 0) {
		next.value = first.value;
		return;
	}

	// Repeating date
	//
	currDate.setUTCDate(currDate.getUTCDate() - (fx * 3));  // Buffer
	var repDate = new Date(fiDate.yx, parseInt(fiDate.mx, 10)-1, fiDate.dx, 20);
	repDate.setUTCMinutes(repDate.getUTCMinutes() - repDate.getTimezoneOffset());
	if(fiDate.dx < 28) {
		while(repDate.getTime() < currDate.getTime() && repDate.getTime() < toDat.getTime())
			repDate.setUTCMonth(repDate.getUTCMonth() + fx);
		if(repDate.getTime() > toDat.getTime())
			repDate.setUTCMonth(repDate.getUTCMonth() - fx);
	}
	else {
		repDate.setUTCDate(repDate.getUTCDate() - 3);
		while(repDate.getTime() < currDate.getTime() && repDate.getTime() < toDat.getTime())
			repDate.setUTCMonth(repDate.getUTCMonth() + fx);
		if(repDate.getTime() > toDat.getTime())
			repDate.setUTCMonth(repDate.getUTCMonth() - fx);
		var rewindDate = new Date(repDate.getTime()); // Save for check below
		utcMonth = repDate.getUTCMonth();
		var day = 0;
		if(utcMonth == 0){ day = 31; }
		else if(utcMonth == 1){ day = 28; }
		else if(utcMonth == 2){ day = 31; }
		else if(utcMonth == 3){ day = 30; }
		else if(utcMonth == 4){ day = 31; }
		else if(utcMonth == 5){ day = 30; }
		else if(utcMonth == 6){ day = 31; }
		else if(utcMonth == 7){ day = 31; }
		else if(utcMonth == 8){ day = 30; }
		else if(utcMonth == 9){ day = 31; }
		else if(utcMonth == 10){ day = 30; }
		else if(utcMonth == 11){ day = 31; }

		if(day >= fiDate.dx){
			repDate.setUTCDate(fiDate.dx);
		} else {
			while(utcMonth == repDate.getUTCMonth()){
				repDate.setUTCDate(repDate.getUTCDate() + 1);
			}
			repDate.setUTCDate(repDate.getUTCDate() - 1);
		}
		// If we went over Valid To date, rewind and repeat
		//
		if(repDate.getTime() > toDat.getTime()) {
			repDate.setTime(rewindDate.getTime());
			repDate.setUTCMonth(repDate.getUTCMonth() - fx);
			utcMonth = repDate.getUTCMonth();
			while(utcMonth == repDate.getUTCMonth()){
				repDate.setUTCDate(repDate.getUTCDate() + 1);
			}
			repDate.setUTCDate(repDate.getUTCDate() - 1);
		}
	}
	next.value = ddmmyyyyDate(repDate);

	// Calculate value for TERMINATE
	//
	if(repDate.getTime() < currDate.getTime())
		terminate.value = "Y";

	// Deadline after the next
	//
	if(repDate.getUTCDate() < 28)
		repDate.setUTCMonth(repDate.getUTCMonth() + fx);
	else {
		repDate.setUTCDate(repDate.getUTCDate() - 3);
		repDate.setUTCMonth(repDate.getUTCMonth() + fx);
		utcMonth = repDate.getUTCMonth();
		while(utcMonth == repDate.getUTCMonth())
			repDate.setUTCDate(repDate.getUTCDate() + 1);
		repDate.setUTCDate(repDate.getUTCDate() - 1);
	}
	if(repDate.getTime() < toDat.getTime())
		next2.value = ddmmyyyyDate(repDate);
}

function checkDateSimple(field) {
	rVal = new Object;
	var s = field.value;

	if(s.length == 0) {
		clearwarning(field);
		return false;
	}

	var re = /([0-9]{2})\/([0-9]{2})\/([0-9]{4})/;

	if(s.length != 10 || !re.test(s)) {
		setwarning(field, "Invalid date format. Date needs to be in dd/mm/yyyy format.");
		field.value = "31/12/9999";
		return false;
	}
	rVal.dx = RegExp.$1;
	rVal.mx = RegExp.$2;
	rVal.yx = RegExp.$3;

	if(rVal.mx < 1 || rVal.mx > 12) {
		setwarning(field, "Invalid date.");
		field.value = "31/12/9999";
		return false;
	}
	else if ((rVal.dx < 1) ||
				((rVal.mx == 1 || rVal.mx == 3 || rVal.mx == 5 || rVal.mx == 7 || rVal.mx == 8 || rVal.mx == 10 || rVal.mx == 12) && rVal.dx > 31) ||
				((rVal.mx == 4 || rVal.mx == 6 || rVal.mx == 9 || rVal.mx == 11) && rVal.dx > 30) ||
				(rVal.mx == 2 && rVal.dx > 29)) {
		setwarning(field, "Invalid date.");
		field.value = "31/12/9999";
		return false;
	}
	clearwarning(field);
	return rVal;
}

function checkNumberSimple(field) {
	var s = field.value;

	if(s.length == 0) {
		clearwarning(field);
		return -1;
	}

	var fx = parseInt(field.value);
	if(isNaN(fx) || fx < 0) {
		setwarning(field, "Invalid reporting frequency. Positive, whole number expected.");
		return -1;
	}
	clearwarning(field);
	return fx;
}

function ddmmyyyyDate(dat) {
	var s;

	if(dat.getUTCDate() >= 10)
		s = dat.getUTCDate();
	else
		s = "0" + dat.getUTCDate();

	s += "/";
	if(dat.getUTCMonth() >= 9)
		s += parseInt(dat.getUTCMonth() + 1);
	else
		s += "0" + parseInt(dat.getUTCMonth() + 1);

	s += "/" + dat.getUTCFullYear();

	return s;
}

function addValues(selFrom, selTo, unit,clist,volSelTo) {
	var i, j, count = 0;
	var optsLen;
	var newVal, newText;

	isChanged = true;

	var selected = new Array();

	eu28 = { AT:1,BE:1,BG:1,HR:1,CY:1,CZ:1,
		 DE:1,DK:1,EE:1,ES:1,FI:1,FR:1,GB:1,GR:1,
		 HU:1,IE:1,IT:1,LT:1,LU:1,LV:1,MT:1,NL:1,
		 PL:1,PT:1,RO:1,SE:1,SI:1,SK:1 };

	for (k = 0; k < selFrom.length; ++k) {
		if (selFrom[k].selected) {
			if(selFrom[k].value == '0'){
				if(clist != null){
					for (z = 0; z < clist.length; z++) {
						s = clist[z].split(":");
						pvalue = s[0];
						ptext = s[1];
						ptype = s[2];
						ptwoletter = s[3];
						if (ptype.valueOf() == 'C' && ptwoletter.valueOf() in eu28) {
							for (q = 0; q < selFrom.length; ++q) {
								if(selFrom[q].value == pvalue.valueOf()){
									if (!selFrom[q].selected) {
										selFrom[q].selected = true;
									}
								}
							}
						}
					}
				}
				selFrom[k].selected = false;
			}
		}
	}
	for (i = 0; i < selFrom.length; ++i) {
		
		if (selFrom[i].selected) {

			var exists;
			exists = false;
			existsVolunt = false;
			for (j = 0;j < selTo.length; ++j) {
				var x;
				u = new String(selTo[j].value);
				if (u.valueOf() == selFrom[i].value) {
					exists = true;
					break;
				}
			}

			for (z = 0;z < volSelTo.length; ++z) {
				var st,id;
				id = new String(volSelTo[z].value);
				if (id.valueOf() == selFrom[i].value) {
					existsVolunt = true;
					break;
				}
			}
			if (!exists) {
				if (!existsVolunt) {
					selected[count++] = i;

					newVal = selFrom[i].value;
					var pos = newVal.indexOf(':');
					if (pos > 0) {
						newVal = newVal.substr(0, pos);
					}

					newText = selFrom[i].text;
					pos = newText.indexOf('[');
					if (pos > 1) {
						newText = newText.substr(0, pos - 1); // strip leading space as well
					}
					if (unit != null && unit.text.length > 0) {
						newVal = newVal + ':' + unit.value;
						newText = newText + ' [' + unit.text + ']';
					}

					// add to
					var opt = document.createElement("option");
					selTo.add(opt);
					opt.text = newText;
					opt.value = newVal;
				} else {
					alert("No country can be participating both formally and voluntarily." + '\n' + "To report it formally you must first remove it from voluntarily reporting list-box.")
				}
			}
		}
	}
}

function delValues(selFrom) {
	for (i = selFrom.length-1; i >= 0; --i) {
		if (selFrom[i].selected) {
			selFrom[i] = null;
		}
	}
}

function fillclist(type,list,text) {
	var i,js;
	for (i = list.length; i > 0; --i)
		list.options[i] = null;

	var opt = document.createElement("option");
	//opt.text = "Choose a group";
	//opt.value = "-1";
	//list.options.add(opt);
	j = 1;
	for (i = 0; i < clist.length; i++) {
	  s = clist[i].split(":");
	  pvalue = s[0];
	  ptext = s[1];
	  ptype = s[2];
	  pismember = s[3];
	  if (ptype.valueOf() == type) {
		opt = document.createElement("option");
		opt.text = ptext.valueOf();
		opt.value = pvalue.valueOf();
		list.options.add(opt);
	  }
	}
	list.options[0] = null;
	list.options[0].selected = true;
}

function addFullValues(selFrom, selTo,clist,forSelTo) {
	var i, j, count = 0;
	var optsLen;
	var newVal, newText;

	isChanged = true;

	var selected = new Array();

	eu28 = { AT:1,BE:1,BG:1,HR:1,CY:1,CZ:1,
		 DE:1,DK:1,EE:1,ES:1,FI:1,FR:1,GB:1,GR:1,
		 HU:1,IE:1,IT:1,LT:1,LU:1,LV:1,MT:1,NL:1,
		 PL:1,PT:1,RO:1,SE:1,SI:1,SK:1 }

	for (k = 0; k < selFrom.length; ++k) {
		if (selFrom[k].selected) {
			var str,u;
			u = null;
			str = new String(selFrom[k].value);
			if(str != null && str.valueOf() == '0'){
				if(clist != null){
					for (z = 0; z < clist.length; z++) {
						s = clist[z].split(":");
						pvalue = s[0];
						ptext = s[1];
						ptype = s[2];
						ptwoletter = s[3];
						if (ptype.valueOf() == 'C' && ptwoletter.valueOf() in eu28) {
							val = pvalue.valueOf();
							for (q = 0; q < selFrom.length; ++q) {
								if(selFrom[q].value == val.valueOf()){
									if (!selFrom[q].selected) {
										selFrom[q].selected = true;
									}
								}
							}
						}
					}
				}
				selFrom[k].selected = false;
			}
		}
	}

	for (i = 0; i < selFrom.length; ++i) {
		if (selFrom[i].selected) {
			var st,id;
			id = new String(selFrom[i].value);

			var exists;
			exists = false;
			existsFormally = false;
			for (j = 0;j < selTo.length; ++j) {
				var s,x;
				s = new String(selTo[j].value);
				u = new String("");

/*				if (s.indexOf(":") > -1) {
					u = s.substr(s.indexOf(":")+1);
					s = s.substring(0,s.indexOf(":"));
				} */

					if (s.valueOf() == selFrom[i].value) {
						exists = true;
						break;
					}
			}
			for (z = 0;z < forSelTo.length; ++z) {
				if (forSelTo[z].value == id.valueOf()) {
					existsFormally = true;
					break;
				}
			}
			if (!exists) {
				if(!existsFormally){
					selected[count++] = i;

					newVal = selFrom[i].value;

					var pos = newVal.indexOf(':');
					/*if (pos > 0) {
						newVal = newVal.substr(0, pos);
					} */

					newText = selFrom[i].text;
					pos = newText.indexOf('[');
					if (pos > 1) {
						newText = newText.substr(0, pos - 1); // strip leading space as well
					}
					/*if (unit != null && unit.text.length > 0) {
						newVal = newVal + ':' + unit.value;
						newText = newText + ' [' + unit.text + ']';
					} */

					// add to
					var opt = document.createElement("option");
					selTo.add(opt);
					opt.text = newText;
					opt.value = newVal;
				} else {
					alert("No country can be participating both formally and voluntarily." + '\n' + "To report it voluntarily you must first remove it from formally reporting list-box.")
				}
			}
		}
	}
}

function addValuesEnv(selFrom, selTo, unit) {
	var i, j, count = 0;
	var optsLen;
	var newVal, newText;

	isChanged = true;

	var selected = new Array();

	for (i = 0; i < selFrom.length; ++i) {
		if (selFrom[i].selected) {
			var exists;
			exists = false;
			for (j = 0;j < selTo.length; ++j) {
				var s,x;
				s = new String(selTo[j].value);
				u = new String("");
				if (s.indexOf(":") > -1) {
					u = s.substr(s.indexOf(":")+1);
					s = s.substring(0,s.indexOf(":"));
				}

				if (unit != null) {
					if ((s.valueOf() == selFrom[i].value) && (u.valueOf() == unit.value)) {
						exists = true;
						break;
					}
				}
				else
					if (s.valueOf() == selFrom[i].value) {
						exists = true;
						break;
					}
			}
			if (!exists) {
				selected[count++] = i;

				newVal = selFrom[i].value;
				var pos = newVal.indexOf(':');
				if (pos > 0) {
					newVal = newVal.substr(0, pos);
				}

				newText = selFrom[i].text;
				pos = newText.indexOf('[');
				if (pos > 1) {
					newText = newText.substr(0, pos - 1); // strip leading space as well
				}
				if (unit != null && unit.text.length > 0) {
					newVal = newVal + ':' + unit.value;
					newText = newText + ' [' + unit.text + ']';
				}

				// add to
				var opt = document.createElement("option");
				selTo.add(opt);
				opt.text = newText;
				opt.value = newVal;
			}
		}
	}
}

function checkStatus() {
	if (isChanged) {
		if(confirm("Save data?")==false)
			return false;
		var addBtn = document.getElementById("addBtn");
		var editBtn = document.getElementById("editBtn");
		if(addBtn)
			addBtn.click();
		else
			editBtn.click();
	}
}

//JavaScript based validation START

function clearwarning(field) {
	removeClassName(field, 'error');
	fid = field.id;
	if (fid) {
		msgobj = document.getElementById("error-" + fid);
		if (msgobj)
			msgobj.parentNode.removeChild(msgobj);
	}
}

function setwarning(field, msg) {
	addClassName(field, 'error');
	msgtext = document.createTextNode(msg);
	fid = field.id;
	if (fid) {
		msgobj = document.getElementById("error-" + fid);
		if (msgobj) {
			msgobj.replaceChild(msgtext, msgobj.firstChild);
			msgobj.appendChild(msgtext);
		} else {
			msgobj = document.createElement('div');
			msgobj.id = "error-" + fid;
			msgobj.className = "error-hint";
			msgobj.appendChild(msgtext);
			field.parentNode.appendChild(msgobj);
		}
	} else {
		alert(msg);
	}
	field.focus();
}

function checkMdText(field) {
	var s = field.value;
	if (s.length == 0) {
		setwarning(field, 'Field is mandatory');
	} else {
		clearwarning(field);
	}
}

function chkUrl(fld) {
	var s = fld.value;
	if ( s != "" &&  (s.substr(0,7) != "http://") && (s.substr(0,8) != "https://") && (s.substr(0,6) != "ftp://") )	{
		setwarning(fld, "Wrong URL format");
	} else {
		clearwarning(fld);
	}
}

function contReporting() {
	var val = document.getElementById("report_freq_months").value;
	if (val == "" || val == " ") {
		document.getElementById("contReporting").className = 'form-group displayYes';
	} else {
		document.getElementById("contReporting").className = 'form-group displayNone';
	}
}

function contReportingChanged(){
	var elem = document.getElementById("continousReporting");
	if(elem.checked){
		document.getElementById("next_reporting").value = 'Continuous reporting';
	} else {
		document.getElementById("next_reporting").value = '';
	}
}

//JavaScript based validation END
