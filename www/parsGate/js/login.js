var token, desc;
function get(a) {
    return document.getElementById(a)
}
function onWinLoad() {
    document.getElementById("ChangePasswordSub").style.display = "none";
    showLogin()
    get("username").select()
}
function keypress2(c) {
    var d = get("username2");
    var a = c;
    var b = true;
    var e;
    if ("which" in a) {
        e = a.which
    } else {
        if ("keyCode" in a) {
            e = a.keyCode
        } else {
            if ("keyCode" in window.event) {
                e = window.event.keyCode
            } else {
                if ("which" in window.event) {
                    e = a.which
                } else {
                    alert("the browser don't support")
                }
            }
        }
    }
    if (e == 13) {
        // return onClick2()
        document.getElementById("Register").click();
    }
    return true
}

function keypress(c) {
    var d = get("username");
    var a = c;
    var b = true;
    var e;
    if ("which" in a) {
        e = a.which
    } else {
        if ("keyCode" in a) {
            e = a.keyCode
        } else {
            if ("keyCode" in window.event) {
                e = window.event.keyCode
            } else {
                if ("which" in window.event) {
                    e = a.which
                } else {
                    alert("the browser don't support")
                }
            }
        }
    }
    if (e == 13) {
        return onClick()
    }
    return true
}
function keypress2(c) {
    var d = get("username");
    var a = c;
    var b = true;
    var e;
    if ("which" in a) {
        e = a.which
    } else {
        if ("keyCode" in a) {
            e = a.keyCode
        } else {
            if ("keyCode" in window.event) {
                e = window.event.keyCode
            } else {
                if ("which" in window.event) {
                    e = a.which
                } else {
                    alert("the browser don't support")
                }
            }
        }
    }
    if (e == 13) {
        // return onClick2()
        document.getElementById("Register").click();
    }
    return true
}
function GetXmlHttpObject() {
    var a = null;
    try {
        a = new XMLHttpRequest()
    } catch (b) {
        try {
            a = new ActiveXObject("Msxml2.XMLHTTP")
        } catch (b) {
            a = new ActiveXObject("Microsoft.XMLHTTP")
        }
    }
    return a
}
function onClick(obj) {
    var d = get("username");
    var c = d.value;
    var a = get("password");
    var b = a.value;
    var e = "";
    var g = "", f = "";
    if (validate(c) === false) {
        e = "Please enter the username"
    }
    if (validate(b) === false) {
        e += e ? " and password" : "Please enter the password"
    }
    if (e) {

        get("err-label").innerHTML = e + ".";
        return false
    }
    c = encode(c);
    b = encode(b);
    c = encryptedString(key, c);
    b = encryptedString(key, b);
    if (obj)
        refresh(c, b, obj.value);
    else
        refresh(c, b, "Register");
    stateChanged()
}

function onClickChangePassword(obj) {
    var d = get("username");
    var c = d.value;
    var a = get("password");
    var b = a.value;
    var e = "";
    var g = "", f = "";
    if (validate(c) === false) {
        e = "Please enter the username"
    }
    if (validate(b) === false) {
        e += e ? " and password" : "Please enter the password"
    }
    if (e) {

        get("err-label").innerHTML = e + ".";
        return false
    }
    c = encode(c);
    b = encode(b);
    c = encryptedString(key, c);
    b = encryptedString(key, b);
    refresh(c, b, "ChangePasswordVerify");
    stateChanged()
}
function onClick2() {
    var uname = get("username2");
    var unameval = uname.value;
    var oldpass = get("oldpassword");
    var oldpassval = oldpass.value;
    var newpass = get("newpassword");
    var newpassval = newpass.value;
    var confpass = get("confpassword");
    var confpassval = confpass.value;
    var e = "";
    if (validate(unameval) === false) {
        e = "Please enter the username"
    }
    if (validate(oldpassval) === false) {
        e += e ? " and old password" : "Please enter old password"
    }

    if (validate(newpassval) === false) {
        e += e ? " and new password" : "Please enter new password"
    }
    if (validate(confpassval) === false) {
        e += e ? " and confirm password" : "Please enter confirm password"
    }
    if (newpassval != confpassval) {
        e += e ? " and check confirm match" : "Password doesn't match"
    }
    if (e) {

        get("err-label").innerHTML = e + ".";
        return false
    }
    unameval = encode(unameval);
    oldpassval = encode(oldpassval);
    newpassval = encode(newpassval);
    confpassval = encode(confpassval);

    unameval = encryptedString(key, unameval);
    oldpassval = encryptedString(key, oldpassval);
    newpassval = encryptedString(key, newpassval);
    confpassval = encryptedString(key, confpassval);
    refresh2(unameval, oldpassval, newpassval, confpassval);
    stateChanged()
}
function onLoading(a) {
    get("username").disabled = a;
    get("password").disabled = a;
    get("Register").disabled = a;
    get("Unregister").disabled = a;
    get("ViewAccount").disabled = a;
    get("ChangePassword").disabled = a;
    if (!a) {
        get("username").select()
    } else {
        get("err-label").innerHTML = ""
    }
    get("loading").style.visibility = !a ? "hidden" : "visible"
}
function refresh(c, b, g) {
    xmlHttp = GetXmlHttpObject();
    if (xmlHttp == null) {
        alert("Your browser does not support AJAX.\nPlease update your browser.");
        return
    }
    if (g != "View Account")
        onLoading(true);
    var referer = (document.location + '').split('Referer=');
    if (referer.length == 1)
        var a = "command=" + g + "&name=" + c + "&word=" + b;
    else {
        var ref = referer[1];
        for (i = 2; i
        < referer.length; i++) {
            ref += "Referer=" + referer[i];
        }
        var wysiwyg_clean = encodeURIComponent(ref);
        var a = "command=" + g + "&name=" + c + "&word=" + b + "&referer=" + wysiwyg_clean;
    }
    url = "tr_auth_cnfd41d.html?" + a;
    xmlHttp.onreadystatechange = stateChanged;
    xmlHttp.open("GET", url, true);
    xmlHttp.send()
}
function refresh2(uname, oldpass, newpass, confpass) {
    xmlHttp = GetXmlHttpObject();
    if (xmlHttp == null) {
        alert("Your browser does not support AJAX.\nPlease update your browser.");
        return
    }
    onLoading(true);

    var a = "command=Change Password&user=" + uname + "&oldPass=" + oldpass + "&newPass=" + newpass + "&confPass=" + confpass;
    url = "change_pass_cnfd41d.html?" + a;
    xmlHttp.onreadystatechange = stateChanged;
    xmlHttp.open("GET", url, true);
    xmlHttp.send()
}
function showChangePass() {
    document.getElementById("err-label").innerHTML = "";
    document.getElementById("newpassword").value = "";
    document.getElementById("confpassword").value = "";
    document.getElementById("username2").value = document.getElementById("username").value;
    document.getElementById("oldpassword").value = document.getElementById("password").value;
    document.getElementById("username2-cnt").style.display = "block";
    document.getElementById("oldpassword-cnt").style.display = "block";
    document.getElementById("newpassword-cnt").style.display = "block";
    document.getElementById("confpassword-cnt").style.display = "block";
    document.getElementById("ChangePasswordSub").style.display = "inline";
    document.getElementById("cancel").style.display = "inline";
    document.getElementById("Register").style.display = "none";
    document.getElementById("Unregister").style.display = "none";
    document.getElementById("ViewAccount").style.display = "none";
    document.getElementById("username-cnt").style.display = "none";
    document.getElementById("password-cnt").style.display = "none";
    document.getElementById("ChangePassword").style.display = "none";
    get("loading").style.visibility = "hidden";


}
function showLogin() {
    document.getElementById("err-label").innerHTML = "";
    document.getElementById("password").value = "";
    document.getElementById("username2-cnt").style.display = "none";
    document.getElementById("oldpassword-cnt").style.display = "none";
    document.getElementById("newpassword-cnt").style.display = "none";
    document.getElementById("confpassword-cnt").style.display = "none";
    document.getElementById("ChangePasswordSub").style.display = "none";
    document.getElementById("cancel").style.display = "none";
    document.getElementById("Register").style.display = "inline";
    document.getElementById("Unregister").style.display = "inline";
    document.getElementById("ViewAccount").style.display = "inline";
    document.getElementById("ChangePassword").style.display = changepassDisplay;
    document.getElementById("password-cnt").style.display = "block";
    document.getElementById("username-cnt").style.display = "block";
}

function stateChanged() {

    if (xmlHttp.readyState == 4 || xmlHttp.readyState == "complete") {
        var resp = eval("(" + xmlHttp.responseText + ")");
        if (resp.success == true) {
            if (resp.account != undefined) {
                jiji = resp.account;
                try {
                    $('#lg-account').html(jiji)
                } catch (error) {
                }
                $.colorbox({inline: true, href: "#lg-account"});
            }


            else if (!resp.redirect) {
                if (resp.desc && resp.desc != "") {
//    showLogin();
                    showChangePass(false);
                    get("err-label").innerHTML = resp.desc;
                }
                else if (resp.success_msg != "") {
                    showLogin();
                    get("err-label").innerHTML = resp.success_msg;
                    onLoading(false);
                }
            }
            else {
                get("err-label").innerHTML = resp.success_msg;
                document.location.replace(resp.location)
            }
        } else {
            onLoading(false);
            if (resp.desc && resp.desc != "")
                get("err-label").innerHTML = resp.fail_msg + "</br>" + resp.desc;
            else
                get("err-label").innerHTML = resp.fail_msg + "</br>";
        }
    }
}
function validate(a) {
    return a !== ""
};
