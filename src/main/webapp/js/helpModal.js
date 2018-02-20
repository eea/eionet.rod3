function helpFunction(helpID){
	
	 var URLHelp = document.getElementById('hrefHelpModal').value;
	 
	$.ajax({
		url : URLHelp,
		data : {
			helpId : helpID
		},
		success : function(responseText) {
			
			$("#title-modal").attr('title', "Rod - Help");
			//$(".ui-dialog-title").text(responseText.title);
			$("#help-text").html("<strong>" + responseText.title + "</strong><br/><br/>" + responseText.text);
			$('#title-modal').dialog({
				width: 390,
				minHeight: 200,
				position: [0]
			});
		}
	});
}