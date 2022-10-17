$(document).ready(function(){
		$("#buttonCancel").on("click",function()
				{
					window.location=moduleURL;
				});
		$("#fileImage").change(function(){
			if(!checkFileSize(this)){
				return;
			}
			showImageThumbnail(this);
		});
		
	});
	
	function showImageThumbnail(fileInput)
	{
		var file=fileInput.files[0];
		var reader = new FileReader();
		reader.onload=function(e){
			$("#thumbnail").attr("src",e.target.result);
		};
		
		reader.readAsDataURL(file);
	}
	
	function checkFileSize(fileInput){
		
		fileSize=fileInput.files[0].size;
		//alert(fileSize);
		if(fileSize > MAX_FILE_SIZE){
			fileInput.setCustomValidity("You must choose an image less than "+MAX_FILE_SIZE+" bytes");
			fileInput.reportValidity();
			return false;
		}
		else
		{
			fileInput.setCustomValidity("");
			return true;
		}
		
	}
	