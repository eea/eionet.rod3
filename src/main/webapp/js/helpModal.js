function helpFunction(helpID){
	$.ajax({
		url : '../help.do',
		data : {
			helpId : helpID
		},
		success : function(responseText) {
			$("#title-modal").attr('title', responseText.title);
			$("#help-text").text(responseText.text);
			$('#title-modal').dialog();
		}
	});
}