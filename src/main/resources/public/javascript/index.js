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






