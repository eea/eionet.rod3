function helpFunction(helpID){
	
	 var URLHelp = document.getElementById('hrefHelpModal').value;
	 
	$.ajax({
		url : URLHelp,
		data : {
			helpId : helpID
		},
		success : function(responseText) {
			$("#title-modal").attr('title', responseText.title);
			$("#help-text").html(responseText.text);
			$('#title-modal').dialog({
				minWidth: 600,
				minHeight: 200
			});
		}
	});
}