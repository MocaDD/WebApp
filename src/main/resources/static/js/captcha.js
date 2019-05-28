function ChangeCaptcha() {
    var chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXTZabcdefghiklmnopqrstuvwxyz";
    // You can include special characters by adding them to the string above, for eg: chars += "@#?<>";

    var string_length = 6; // This is the length of the Captcha

    var ChangeCaptcha = '';
    for (var i=0; i<string_length; i++) {
        var rnum = Math.floor(Math.random() * chars.length);
        ChangeCaptcha += chars.substring(rnum,rnum+1);
    }

    document.getElementById('randomfield').value = ChangeCaptcha; // Final step which changes the field value to the Captcha produced
}

function ChangeCaptchaJar() {
    var chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXTZabcdefghiklmnopqrstuvwxyz";
    // You can include special characters by adding them to the string above, for eg: chars += "@#?<>";

    var string_length = 6; // This is the length of the Captcha

    var ChangeCaptchaJar = '';
    for (var i=0; i<string_length; i++) {
        var rnum = Math.floor(Math.random() * chars.length);
        ChangeCaptchaJar += chars.substring(rnum,rnum+1);
    }

    document.getElementById('randomfieldJar').value = ChangeCaptchaJar; // Final step which changes the field value to the Captcha produced
}