'use strict';

var singleUploadForm = document.querySelector('#singleUploadForm');
var singleFileUploadInput = document.querySelector('#singleFileUploadInput');
var singleFileUploadError = document.querySelector('#singleFileUploadError');
var singleFileUploadSuccess = document.querySelector('#singleFileUploadSuccess');

function uploadSingleFile(file) {
    var formData = new FormData();
    formData.append("file", file);

    var xhr = new XMLHttpRequest();
    xhr.open("POST", "/uploadFile");

    xhr.onload = function() {
        console.log(xhr.responseText);
        var response = JSON.parse(xhr.responseText);
        if(xhr.status == 200) {
            singleFileUploadError.style.display = "none";
            singleFileUploadSuccess.innerHTML = "<p>File Uploaded Successfully.</p>";
            singleFileUploadSuccess.style.display = "block";
        } else {
            singleFileUploadSuccess.style.display = "none";
            singleFileUploadError.innerHTML = (response && response.message) || "Some Error Occurred";
        }
    }
    xhr.send(formData);

    var xhr2 = new XMLHttpRequest();
    xhr2.open ("POST", "/verifyJar");

    xhr2.onload = function()  {
        if (xhr2.responseText == "merge")    {
            verifySignature.style.display = "none";
            verifySignature.innerHTML = "<p> Signature is OK </p>";
            verifySignature.style.display = "block";
        }   else    {
            verifySignature.style.display = "none";
            verifySignature.innerHTML = "<p>" + xhr2.responseText +  "</p>";
            verifySignature.style.display = "block";
        }
    }
    xhr2.send(formData);
}

singleUploadForm.addEventListener('submit', function(event){
 if(document.getElementById('CaptchaEnterJar').value == document.getElementById('randomfieldJar').value ) {
    var files = singleFileUploadInput.files;
    if(files.length === 0) {
        singleFileUploadError.innerHTML = "Please select a file";
        singleFileUploadError.style.display = "block";
    }
    uploadSingleFile(files[0]);
    event.preventDefault();
    ChangeCaptchaJar();
    } else {
                alert('Please re-check the captcha'); // The alert message that'll be displayed when the user enters a wrong Captcha
            }

}, true);