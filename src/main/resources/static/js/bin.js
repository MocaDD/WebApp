'use strict';

var multipleUploadForm = document.querySelector('#multipleUploadForm');
var multipleFileUploadInput = document.querySelector('#multipleFileUploadInput');
var multipleFileUploadError = document.querySelector('#multipleFileUploadError');
var multipleFileUploadSuccess = document.querySelector('#multipleFileUploadSuccess');

function uploadMultipleFiles(files) {
    var formData = new FormData();
    for(var index = 0; index < files.length; index++) {
        formData.append("files", files[index]);
    }

    var xhr = new XMLHttpRequest();
    xhr.open("POST", "/uploadMultipleFiles");

    xhr.onload = function() {
        console.log(xhr.responseText);
        var response = JSON.parse(xhr.responseText);
        if(xhr.status == 200) {
            multipleFileUploadError.style.display = "none";
            var content = "<p>All Files Uploaded Successfully</p>";
            multipleFileUploadSuccess.innerHTML = content;
            multipleFileUploadSuccess.style.display = "block";
        } else {
            multipleFileUploadSuccess.style.display = "none";
            multipleFileUploadError.innerHTML = (response && response.message) || "Some Error Occurred";
        }

        if (files.length != 2)  {
            multipleFileUploadError.style.display = "none";
                        var content = "<p>Please upload only the binary file and the signature</p>";
                        multipleFileUploadSuccess.innerHTML = content;
                        multipleFileUploadSuccess.style.display = "block";
                        verifySignature.style.display = "none";
        }

    }
    xhr.send(formData);

    var xhr2 = new XMLHttpRequest();
        if (files.length == 2) {
            xhr2.open ("POST", "/verifyBin");

            xhr2.onload = function()  {
                if (xhr2.responseText == "OK")    {
                    verifySignature.style.display = "none";
                    verifySignature.innerHTML = "<p> Signature is OK </p>";
                    verifySignature.style.display = "block";
                }   else if (xhr2.responseText == "Not OK")   {
                    var xhr3 = new XMLHttpRequest();
                    xhr3.open ("POST", "/verifyBin2");

                    xhr3.onload = function()    {
                        if (xhr3.responseText == "OK")  {
                             verifySignature.style.display = "none";
                             verifySignature.innerHTML = "<p> Signature is OK </p>";
                             verifySignature.style.display = "block";
                        } else  {
                             verifySignature.style.display = "none";
                             verifySignature.innerHTML = "<p> Signature is not OK  </p>";
                             verifySignature.style.display = "block";
                        }

                    }
                    xhr3.send(formData);
                }
            }
            xhr2.send(formData);
        }
}

multipleUploadForm.addEventListener('submit', function(event){
    var files = multipleFileUploadInput.files;
    if(files.length === 0) {
        multipleFileUploadError.innerHTML = "Please select at least one file";
        multipleFileUploadError.style.display = "block";
    }
    uploadMultipleFiles(files);
    event.preventDefault();
}, true);