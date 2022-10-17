dropdownBrands = $("#brand");
dropdownCategories = $("#category");
	
	$(document).ready(function(){
		
		$("#shortDescription").richText();
		$("#fullDescription").richText();
		
		dropdownBrands.change(function(){
			dropdownCategories.empty();
			getCategories();
		});	
		
		getCategoriesForNewForm();
		
		
		

	});
	
function  getCategoriesForNewForm(){
	catIdField = $("#categoryId");
	editMode = false;
	
	if(catIdField.length){
		editMode=true;
	}
	
	if(!editMode){
		getCategories;
	}
	
}
	
	
	function getCategories(){
		brandId = dropdownBrands.val();
		url=brandmoduleURL + "/" + brandId + "/categories";
		
		$.get(url,function(responseJson){
			$.each(responseJson,function(index,category){
				$("<option>").val(category.id).text(category.name).appendTo(dropdownCategories);
			});
		});
	}
	
	function checkUnique(form){
		prodID = $("#id").val();
		prodName = $("#name").val();
		csrfValue = $("input[name='_csrf']").val();
		
	
		
		params={id : prodID , name : prodName , _csrf : csrfValue};
		
		$.post(checkUniqueurl,params,function(response){
			if(response=="OK"){
				form.submit();
			}
			else if(response == "Duplicate"){
				showModalDialog("Warning","There is another brand with the same name "+prodName);
			}
			else{
				showModalDialog("Error","Unknown response from the Server");
			}
			}).fail(function(){
				showModalDialog("Error","You could not connect to the Server");
			}); 
			return false;
		}
	
		
		function showModalDialog(title,message){
			$("#modalTitle").text(title);
			$("#modalBody").text(message);
			$("#modalDialog").modal();
		}