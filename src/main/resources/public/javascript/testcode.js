// This class is only for testing javaScript. It was not used in the project

var http = new XMLHttpRequest();

// This was for testing how to do the pop-up notification on index-view when new contact requests have arrived.
// This code was used in the project. 

window.addEventListener('DOMContentLoaded', () => {
    console.log('DOM fully loaded and parsed');
//    var friendRequests = document.getElementById(friendRequests).value;
    
    console.log('variable friendRequests: ' + friendRequests);
    
    if (friendRequests === true) {
    
        if (!document.cookie.split('; ').find(row => row.startsWith('show-alert'))) {
            if (confirm('Welcome back! You have new contact requests. Would you like to see them?')) {
              // goTo contacts
//              var url = "/contacts";
//              hae();
                window.location.href = '/contacts';
                console.log('goTo "/contacts".');
            } else {
              // Stay here
              console.log('Stay on index.');
            }
//            document.cookie = "show-alert=true; expires=Fri, 31 Dec 9999 23:59:59 GMT";
            document.cookie = "show-alert=true;";
        }
    
    }
});

//document.onload="showAlert()";


//function showAlert(friendRequests) {
//    
////    var friendRequests = document.getElementById(friendRequests).value;
//    
//    console.log('variable friendRequests: ' + friendRequests);
//    
//    if (friendRequests === true) {
//    
//        if (!document.cookie.split('; ').find(row => row.startsWith('show-alert'))) {
//            if (confirm('Welcome back! You have new contact requests. Would you like to see them?')) {
//              // goTo contacts
//              hae();
//              console.log('goTo "/contacts".');
//            } else {
//              // Stay here
//              console.log('Stay on index.');
//            }
//            document.cookie = "show-alert=true; expires=Fri, 31 Dec 9999 23:59:59 GMT";
//        }
//    
//    }
//    
//}



http.onreadystatechange = function() {
    if (this.readyState != 4 || this.status != 200) {
        return;
    }
    
}
    
function hae() {
    console.log("Running method hae()");
    http.open("GET", "/contacts");
    http.send();
}




// This is a tutorial on Cookies found in online educational material. 
//function doOnceCheck() {
//    if (!document.cookie.split('; ').find(row => row.startsWith('doSomethingOnlyOnce'))) {
//        alert("Do something here!");
//    document.cookie = "doSomethingOnlyOnce=true; expires=Fri, 31 Dec 9999 23:59:59 GMT";
//  }
//}