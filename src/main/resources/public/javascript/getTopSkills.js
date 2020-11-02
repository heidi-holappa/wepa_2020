// This class is only for testing javaScript. It was not used in the project

var urlTopSkills = contextRoot + "getTopSkills/" + userid
var urlOtherSkills = contextRoot + "getOtherSkills/" + userid
var urlEndorse = contextRoot + "endorseJs/" 


var httpGetTopSkills = new XMLHttpRequest()
var httpGetOtherSkills = new XMLHttpRequest()
var httpPostEndorse = new XMLHttpRequest()
 
httpGetTopSkills.onreadystatechange = function () {
    if (this.readyState != 4 || this.status != 200) {
        return
    }
    
    var topSkills = JSON.parse(this.responseText)
 
    var topSkillsView = "Top three skills <br />"
    
    for (var i = 0; i < topSkills.length; i++) {
        var skillN = topSkills[i]
        topSkillsView += "<li>" + skillN.skill + " - Endorsements: " + skillN.endorsements + " " + "<button type=\"button\" class=\"btn btn-primary\" onclick=\"laheta(" + skillN.id + ")\" value=\"Endorse\">Endorse</button></li>"
        
    }
    
    document.getElementById("topSkills").innerHTML = topSkillsView
 
}

httpGetOtherSkills.onreadystatechange = function() {
    if (this.readyState !=4 || this.status != 200) {
        return
    }
    
    var otherSkills = JSON.parse (this.responseText)
    
    var otherSkillsView = "Other skills: <br />"
    
    for (var i = 0; i < otherSkills.length; i++) {
        var otherN = otherSkills[i]
        otherSkillsView += "<li>" + otherN.skill + " - Endorsements: " + otherN.endorsements  + " " + "<button type=\"button\" class=\"btn btn-primary\" onclick=\"laheta(" + otherN.id + ")\" value=\"Endorse\">Endorse</button></li>"
        
    }
    
    document.getElementById("otherSkills").innerHTML = otherSkillsView
    
}
 

window.onload = function (){
    console.log("Metodi window.onload käynnistyi")
    httpGetTopSkills.open("GET", urlTopSkills)
    httpGetTopSkills.send()    
    httpGetOtherSkills.open("GET", urlOtherSkills)
    httpGetOtherSkills.send()
};

function updateSkills(){
    console.log("Metodi updateSkills käynnistyi")
    httpGetTopSkills.open("GET", urlTopSkills, true)
    httpGetTopSkills.send()    
    httpGetOtherSkills.open("GET", urlOtherSkills, true)
    httpGetOtherSkills.send()
};


function laheta(id) {
    
    console.log("method laheta(id) is running. Variable id = " + id)
    console.log("url + id: " + (urlEndorse + id))

    
    httpPostEndorse.open("POST", urlEndorse + id, true)
    httpPostEndorse.send()
//    httpGETendorse.setRequestHeader("Content-Type", "application/json")
//    httpPOSTendorse.send(JSON.stringify(data))
    updateSkills()
    
}

httpPostEndorse.onreadystatechange = function() {
    
    if (this.readyState != 4) {
        return
    }
     
}

//topSkillsView += "<li>" + skillN.skill + " - Endorsements: " + skillN.endorsements + " " + "<span th:unless=\"${modify =='true'}\"><a th:href=\"@{/endorse/{id}(id=${" + skillN.id + "})}\">Endorse</a></span>"