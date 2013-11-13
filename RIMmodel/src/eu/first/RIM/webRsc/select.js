function SelectColumn(index, tableId) {
	var columnText = 'You selected:\n\n';
	var columnSelector = '#' + tableId + ' tbody > tr > td:nth-child('
			+ (index + 1) + ')';
	var cells = $(columnSelector);

	// clear existing selections
	if (window.getSelection) { // all browsers, except IE before version 9
		window.getSelection().removeAllRanges();
	}

	if (document.createRange) {
		cells.each(function(i, cell) {
			var rangeObj = document.createRange();
			rangeObj.selectNodeContents(cell);
			window.getSelection().addRange(rangeObj);
			columnText = columnText + '\n' + rangeObj.toString();
		});

	} else { // Internet Explorer before version 9
		cells.each(function(i, cell) {
			var rangeObj = document.body.createTextRange();
			rangeObj.moveToElementText(cell);
			rangeObj.select();
			columnText = columnText + '\n' + rangeObj.toString();
		});
	}
	return false;
}

$(document).ready(function() {
	$("#loadpage").hide();
	$('#sent thead th').each(function(index) {
		$(this).click(function() {
			SelectColumn(index, 'sent');
		});
	});

	$('#fixed thead th').each(function(index) {
		$(this).click(function() {
			SelectColumn(index, 'fixed');
		});
	});

	$('#demoTbl thead th').each(function(index) {
		$(this).click(function() {
			SelectColumn(index, 'demoTbl');
		});
	});

	$('#qual thead th').each(function(index) {
		$(this).click(function() {
			SelectColumn(index, 'qual');
		});
	});

	$('#perf thead th').each(function(index) {
		$(this).click(function() {
			SelectColumn(index, 'perf');
		});
	});

	$('#mismatch thead th').each(function(index) {
		$(this).click(function() {
			SelectColumn(index, 'mismatch');
		});
	});
	$('#cust thead th').each(function(index) {
		$(this).click(function() {
			SelectColumn(index, 'cust');
		});
	});
	$('#prod thead th').each(function(index) {
		$(this).click(function() {
			SelectColumn(index, 'prod');
		});
	});
});

function selectAllComplex() {
	var selF;
	var selQ;
	var selS;
	var selP;
	var selM;
	var selC;
	var selPr;
	if (document.selection) {
		selF = document.body.createTextRange();
		selQ = document.body.createTextRange();
		selS = document.body.createTextRange();
		selP = document.body.createTextRange();
		selM = document.body.createTextRange();
		selC = document.body.createTextRange();
		selPr = document.body.createTextRange();

		selF.moveToElementText(document.getElementById('fixed'));
		selQ.moveToElementText(document.getElementById('qual'));
		selS.moveToElementText(document.getElementById('sent'));
		selP.moveToElementText(document.getElementById('perf'));
		selM.moveToElementText(document.getElementById('mismatch'));
		selC.moveToElementText(document.getElementById('cust'));
		selPr.moveToElementText(document.getElementById('prod'));

		selF.select();
		selQ.select();
		selS.select();
		selP.select();
		selM.select();
		selC.select();
		selPr.select();
	} else {
		selF = document.createRange();
		selQ = document.createRange();
		selS = document.createRange();
		selP = document.createRange();
		selM = document.createRange();
		selC = document.createRange();
		selPr = document.createRange();

		selF.setStartBefore(document.getElementById('fixed'));
		selF.setEndAfter(document.getElementById('fixed'));
		selQ.setStartBefore(document.getElementById('qual'));
		selQ.setEndAfter(document.getElementById('qual'));
		selS.setStartBefore(document.getElementById('sent'));
		selS.setEndAfter(document.getElementById('sent'));
		selP.setStartBefore(document.getElementById('perf'));
		selP.setEndAfter(document.getElementById('perf'));
		selM.setStartBefore(document.getElementById('mismatch'));
		selM.setEndAfter(document.getElementById('mismatch'));
		selC.setStartBefore(document.getElementById('cust'));
		selC.setEndAfter(document.getElementById('cust'));
		selPr.setStartBefore(document.getElementById('prod'));
		selPr.setEndAfter(document.getElementById('prod'));

		window.getSelection().addRange(selF);
		window.getSelection().addRange(selQ);
		window.getSelection().addRange(selS);
		window.getSelection().addRange(selP);
		window.getSelection().addRange(selM);
		window.getSelection().addRange(selC);
		window.getSelection().addRange(selPr);
	}
}
function selectAllSimple() {
	var sel;
	if (document.selection) {
		sel = document.body.createTextRange();
		sel.moveToElementText(document.getElementById('demoTbl'));
		sel.select();
	} else {
		sel = document.createRange();
		sel.setStartBefore(document.getElementById('demoTbl'));
		sel.setEndAfter(document.getElementById('demoTbl'));
		window.getSelection().addRange(sel);
	}
}
