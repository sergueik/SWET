var addAttribute = function(element, attributeName, attributeValue) {
    if(attributeName.includes('xpath')){
        attributeName = "xpath";
    }
        try{
            element.setAttribute(attributeName, attributeValue);
        }
        catch(err){
            return;
        }
    }
var removeAttribute = function(element, attributeName, onChange) {
    try{
        attributeName = oldAttribute;
        element.removeAttribute(attributeName);
        element.style.outline= "";
    }catch(err){
        return;
    }   
}

var oldNodes = [];
var oldAttribute = "";
var allNodes = [];
var highlightElements = function(xpathOrCss, xpath, onChange) {
    var elements;
    try{
        if(xpathOrCss.includes("xpath")){
            elements = document.evaluate(xpath, document, null, XPathResult.ORDERED_NODE_SNAPSHOT_TYPE, null);  //xpath
        }else{
            elements = document.querySelectorAll(xpath); //css
        }
    }catch(err){
        if(xpath) {
            chrome.runtime.sendMessage({ count: "wrongXpath" });
        }else {
            chrome.runtime.sendMessage({ count: "blank" });
        }
        for (var i = 0; i < oldNodes.length; i++) {
                removeAttribute(oldNodes[i], xpathOrCss, onChange);
        }
        oldNodes = [];
        allNodes = [];
        return;
    }

    var  totalMatchFound, node;
    if(xpathOrCss.includes("xpath")){   
        totalMatchFound = elements.snapshotLength;  //xpath
    }else{
        totalMatchFound = elements.length;  //css
    }
    for (var i = 0; i < oldNodes.length; i++) {
        removeAttribute(oldNodes[i], xpathOrCss, onChange);
    }
    oldNodes = [];
    allNodes = [];
    
    chrome.runtime.sendMessage({ count: totalMatchFound });

    for (var i = 0; i < totalMatchFound; i++) {
        if(xpathOrCss.includes("xpath")){
             node = elements.snapshotItem(i); //xpath
        }else{
            node = elements[i]; //css
        }
        if(i===0 && !(xpath==="/" || xpath==="." || xpath==="/." || xpath==="//." || xpath==="//..")){
            node.scrollIntoViewIfNeeded();
        }
        oldNodes.push(node);
        oldAttribute = xpathOrCss;
        addAttribute(node, xpathOrCss, i+1 );
        allNodes.push(node.outerHTML);

    }
    chrome.runtime.sendMessage({ count: allNodes });
};

chrome.runtime.onMessage.addListener(function(message, sender, sendResponse) {
    this.tempXpath = ""; //resetting it
    this.indexes = [];
    this.matchIndex = [];
    if (message.xpath || message.xpath === "") {
        if(!message.xpath[1]){
            message.name = 'xpath';
        }else if(message.xpath[1].charAt(0).includes("/") || message.xpath[1].charAt(0).includes("(") || message.xpath[1].substr(0,2).includes('./')){
            message.name = 'xpath';
        }else{
            message.name = 'css';
        }
        highlightElements(message.name, message.xpath[1], message.xpath[2]);
    }
    if (message.name === "xpath") {
        var ele = document.querySelector('[xpath="' + message.index +'"]');
        if(ele){
            ele.style.outline= "2px dotted orangered";
            ele.scrollIntoViewIfNeeded();    
        }    
    }
    if (message.name === "xpath-remove") {
        var ele = document.querySelector('[xpath="' + message.index +'"]');
        if(ele){
            ele.style.outline= "";
        }
    }
    if (message.name === "css") {
        var ele = document.querySelector('[css="' + message.index +'"]');
        if(ele){
            ele.style.outline= "2px dotted orangered";
            ele.scrollIntoViewIfNeeded();    
        }    
    }
    if (message.name === "css-remove") {
        var ele = document.querySelector('[css="' + message.index +'"]');
        if(ele){
            ele.style.outline= "";
        }
    }
});

function generateAbsXpath(element) {
    if(!element){
        return "element is inside iframe & it is not supported by ChroPath currently. Please write xpath manually.";  
    }
    if (element.tagName.toLowerCase()==='html')
        return '/html[1]';
    if (element.tagName.toLowerCase()==='body')
        return '/html[1]/body[1]';

    var ix= 0;
    var siblings= element.parentNode.childNodes;
    for (var i= 0; i<siblings.length; i++) {
        var sibling= siblings[i];
        if (sibling===element){
            if(element.tagName.toLowerCase().includes('svg')){
                var absXpath = generateAbsXpath(element.parentNode)+'/'+'*';
                return absXpath;
            }else{
                var absXpath = generateAbsXpath(element.parentNode)+'/'+element.tagName.toLowerCase()+'['+(ix+1)+']';
                if(absXpath.includes("/*/")){
                    absXpath = "It might be child of iframe & it is not supported currently.";
                }
                return absXpath;
            }
        }
        if (sibling.nodeType===1 && sibling.tagName.toLowerCase()===element.tagName.toLowerCase()){
            ix++;
        }
    }
}

var tempXpath="";
var indexes = []; 
var matchIndex = [];
var containsFlag = false;

function isInsideIframe (node) {
    var child = true;
    var frameOrNot = node.ownerDocument;
    while(child){
        try{
            var temp = frameOrNot.ownerDocument;
            frameOrNot = temp;
        }catch(err){
            child = false;
        }
    }
    return frameOrNot !== document;

}

function formRelXpath(element) {

    if(!element){
        return "element is inside iframe & it is not supported by ChroPath currently. Please write xpath manually.";  
    }

    var innerText = element.textContent.trim().slice(0,50);
    var tagName = element.tagName.toLowerCase();
    
    if(tagName.includes("style") || tagName.includes("script")){
        return "This is "+tagName+" tag. For "+tagName+" tag, no need to write selector. :P";  
    }
    if(tagName.includes('svg')){
        tagName = "*";
    }
    
    if(innerText.includes("'")){
        innerText = innerText.split('  ')[innerText.split('  ').length-1];
        containsText = '[contains(text(),"'+innerText+'")]';
        equalsText = '[text()="'+innerText+'"]';
    }else{
        innerText = innerText.split('  ')[innerText.split('  ').length-1];
        containsText = "[contains(text(),'"+innerText+"')]";
        equalsText = "[text()='"+innerText+"']";
    }
    if (tagName.includes('html')){
        return '/html'+this.tempXpath;
    }
    var attr="";
    var attrValue="";
    var listOfAttr = {};
    if (element.id!==''){
        this.tempXpath = '//'+tagName+"[@id='"+element.id+"']"+this.tempXpath;
        var totalMatch = document.evaluate(this.tempXpath, document, null, XPathResult.ORDERED_NODE_SNAPSHOT_TYPE, null).snapshotLength;
        if(totalMatch===1){
            return this.tempXpath;
        }else{
            this.tempXpath = this.tempXpath;
        }
    }else if(element.attributes.length!=0){
        if(!attrValue){
            for(var i=0; i<element.attributes.length; i++){
                attr = element.attributes[i].name;
                attrValue = element.attributes[i].nodeValue;
                if (attrValue!=null && attrValue!="" && !attr.includes("style") && !attr.includes("xpath")){
                    listOfAttr[attr] = attrValue;
                }    
            }
        }
        if("placeholder" in listOfAttr){
            attr = "placeholder";
            attrValue = listOfAttr[attr];
        }else if("title" in listOfAttr){
            attr = "title";
            attrValue = listOfAttr[attr];
        }else if("value" in listOfAttr){
            attr = "value";
            attrValue = listOfAttr[attr];
        }else if("name" in listOfAttr){
            attr = "name";
            attrValue = listOfAttr[attr];
        }else if("type" in listOfAttr){
            attr = "type";
            attrValue = listOfAttr[attr];
        }else if("class" in listOfAttr){
            attr = "class";
            attrValue = listOfAttr[attr];
        }else{
            attr = Object.keys(listOfAttr)[0];
            attrValue = listOfAttr[attr];
        }
        if(attrValue!=null && attrValue!="" && !attr.includes("xpath")){
            var xpathWithoutAttribute = this.tempXpath;
            var xpathWithAttribute = "";
            if(attrValue.includes('  ')){
               attrValue = attrValue.split('  ')[attrValue.split('  ').length-1];
               containsFlag = true;
            }
            if(attrValue.includes("'")){
                if(attrValue.charAt(0)===" " || attrValue.charAt(attrValue.length-1)===" " || containsFlag){
                   xpathWithAttribute = '//'+tagName+'[contains(@'+attr+',"'+attrValue.trim()+'")]'+this.tempXpath;
                }else{
                   xpathWithAttribute = '//'+tagName+'[@'+attr+'="'+attrValue+'"]'+this.tempXpath;
                }
            }else{
                if(attrValue.charAt(0)===" " || attrValue.charAt(attrValue.length-1)===" " || containsFlag){
                   xpathWithAttribute = '//'+tagName+"[contains(@"+attr+",'"+attrValue.trim()+"')]"+this.tempXpath;
                }else{
                   xpathWithAttribute = '//'+tagName+"[@"+attr+"='"+attrValue+"']"+this.tempXpath;
                }
            }
            var totalMatch = document.evaluate(xpathWithAttribute, document, null, XPathResult.ORDERED_NODE_SNAPSHOT_TYPE, null).snapshotLength;
            if(totalMatch===1){
                if((xpathWithAttribute.includes('@href') || xpathWithAttribute.includes('@src')) && innerText){
                    var containsXpath = '//'+tagName+containsText;
                    var totalMatch = document.evaluate(containsXpath, document, null, XPathResult.ORDERED_NODE_SNAPSHOT_TYPE, null).snapshotLength;
                    if(totalMatch===0){
                        var equalsXpath = '//'+tagName+equalsText;
                        var totalMatch = document.evaluate(equalsXpath, document, null, XPathResult.ORDERED_NODE_SNAPSHOT_TYPE, null).snapshotLength;
                        if(totalMatch===1){
                            return equalsXpath;
                        }
                    }else if(totalMatch===1){
                        return containsXpath;
                    }
                }
                return xpathWithAttribute;
            }else if(innerText && element.getElementsByTagName('*').length===0){

                var containsXpath = '//'+tagName+containsText;
                var totalMatch = document.evaluate(containsXpath, document, null, XPathResult.ORDERED_NODE_SNAPSHOT_TYPE, null).snapshotLength;
                if(totalMatch===0){
                    var equalsXpath = '//'+tagName+equalsText;
                    var totalMatch = document.evaluate(equalsXpath, document, null, XPathResult.ORDERED_NODE_SNAPSHOT_TYPE, null).snapshotLength;
                    if(totalMatch===1){
                        return equalsXpath;
                    }
                }else if(totalMatch===1){
                     return containsXpath;
                }else{
                    containsXpath = xpathWithAttribute+containsText;
                    totalMatch = document.evaluate(containsXpath, document, null, XPathResult.ORDERED_NODE_SNAPSHOT_TYPE, null).snapshotLength;
                    if(totalMatch===0){
                        var equalsXpath = xpathWithAttribute+equalsText;
                        var totalMatch = document.evaluate(equalsXpath, document, null, XPathResult.ORDERED_NODE_SNAPSHOT_TYPE, null).snapshotLength;
                        if(totalMatch===1){
                            return equalsXpath;
                        }
                    }else if(totalMatch===1){
                        return containsXpath;
                    }else if(attrValue.includes('/') || innerText.includes('/')){
                        if(attrValue.includes('/')){
                            containsXpath = xpathWithoutAttribute+containsText;
                        }
                        if(innerText.includes('/')){
                            containsXpath = containsXpath.replace(containsText,"");
                        }
                        this.tempXpath = containsXpath;
                    }else{
                        this.tempXpath = containsXpath;
                    }
                }
            }else{
                this.tempXpath = xpathWithAttribute;
                if(attrValue.includes('/')){
                   this.tempXpath = "//"+tagName+xpathWithoutAttribute;
                }
            }
        }else if(innerText && element.getElementsByTagName('*').length===0){

                var containsXpath = '//'+tagName+containsText;
                var totalMatch = document.evaluate(containsXpath, document, null, XPathResult.ORDERED_NODE_SNAPSHOT_TYPE, null).snapshotLength;
                if(totalMatch===0){
                    var equalsXpath = '//'+tagName+equalsText;
                    var totalMatch = document.evaluate(equalsXpath, document, null, XPathResult.ORDERED_NODE_SNAPSHOT_TYPE, null).snapshotLength;
                    if(totalMatch===1){
                        return equalsXpath;
                    }
                }else if(totalMatch===1){
                     return containsXpath;
                }
                this.tempXpath = containsXpath;

        }else if((attrValue==null || attrValue=="" || attr.includes("xpath"))){
            this.tempXpath = "//"+tagName+this.tempXpath;
        }     
    }else if(attrValue=="" && innerText && element.getElementsByTagName('*').length===0 && !tagName.includes("script")){

        var containsXpath = '//'+tagName+containsText+this.tempXpath;
        var totalMatch = document.evaluate(containsXpath, document, null, XPathResult.ORDERED_NODE_SNAPSHOT_TYPE, null).snapshotLength;
        if(totalMatch===0){
            this.tempXpath = '//'+tagName+equalsText+this.tempXpath;
            var totalMatch = document.evaluate(this.tempXpath, document, null, XPathResult.ORDERED_NODE_SNAPSHOT_TYPE, null).snapshotLength;
            if(totalMatch===1){
                return this.tempXpath;
            }
        }else if(totalMatch===1){
            return containsXpath;
        }else{
            this.tempXpath = containsXpath;
        } 
    }else{
        this.tempXpath = "//"+tagName+this.tempXpath;
    }

    var ix= 0;
    
    var siblings= element.parentNode.childNodes;
    for (var i= 0; i<siblings.length; i++) {
        var sibling= siblings[i];
        if (sibling===element){
                indexes.push(ix+1);            
                this.tempXpath = formRelXpath(element.parentNode);
                if(!this.tempXpath.includes("/")){
                    return this.tempXpath;
                }else{
                    var totalMatch = document.evaluate(this.tempXpath, document, null, XPathResult.ORDERED_NODE_SNAPSHOT_TYPE, null).snapshotLength;
                    if(totalMatch===1){
                        return this.tempXpath;
                    }else{
                        this.tempXpath = "/"+this.tempXpath.replace(/\/\/+/g, '/');
                        var regSlas = /\/+/g;
                        var regBarces = /[^[\]]+(?=])/g; ////this is to get content inside all []
                        while ((match = regSlas.exec(this.tempXpath)) != null) {
                           matchIndex.push(match.index);
                        }
                        for(var j=0; j<indexes.length; j++){
                            if(j===0){
                                var lastTag = this.tempXpath.slice(matchIndex[matchIndex.length-1]);
                                if((match = regBarces.exec(lastTag)) != null){
                                    lastTag = lastTag.replace(regBarces,indexes[j]).split("]")[0]+"]";
                                    this.tempXpath = this.tempXpath.slice(0, matchIndex[matchIndex.length-1])+lastTag;
                                }else{
                                    this.tempXpath = this.tempXpath+"["+indexes[j]+"]";
                                }
                            }else{
                                var lastTag = this.tempXpath.slice(matchIndex[matchIndex.length-(j+1)],matchIndex[matchIndex.length-(j)]);
                                if((match = regBarces.exec(lastTag)) != null){
                                    lastTag = lastTag.replace(regBarces,indexes[j]);
                                    this.tempXpath = this.tempXpath.slice(0, matchIndex[matchIndex.length-(j+1)])+lastTag+this.tempXpath.slice(matchIndex[matchIndex.length-j]);
                                }else{
                                    this.tempXpath = this.tempXpath.slice(0, matchIndex[matchIndex.length-j]) +"["+indexes[j]+"]"+ this.tempXpath.slice(matchIndex[matchIndex.length-j]);
                                }
                            }
                            var totalMatch = document.evaluate(this.tempXpath, document, null, XPathResult.ORDERED_NODE_SNAPSHOT_TYPE, null).snapshotLength;
                            if(totalMatch===1){
                                var regSlashContent = /([a-zA-Z])([^/]*)/g; //this regex is different for Chrome
                                var length = this.tempXpath.match(regSlashContent).length;
                                for(var k=j+1; k<length-1; k++){
                                    var lastTag = this.tempXpath.match(/\/([^\/]+)\/?$/)[1];
                                    var arr = this.tempXpath.match(regSlashContent);
                                    arr.splice(length-k,1,'/');
                                    var relXpath="";
                                    for(var i=0; i< arr.length; i++){
                                        if(arr[i]){
                                            relXpath = relXpath+"/"+arr[i];
                                        }else{
                                            relXpath = relXpath+"//"+arr[i];
                                        }
                                    }
                                    relXpath = (relXpath+"/"+lastTag).replace(/\/\/+/g, '//'); //replace more than 2 forward slashes to double slash
                                    relXpath = relXpath.replace(/\/\/+/g, '/'); //replace double forward slashes to single slash
                                    relXpath = relXpath.replace(/\/+/g,"//"); //replace double forward slashes to single slash
                                    var totalMatch = document.evaluate(relXpath, document, null, XPathResult.ORDERED_NODE_SNAPSHOT_TYPE, null).snapshotLength;
                                    if(totalMatch===1){
                                        this.tempXpath = relXpath;
                                    }
                                }
                                return this.tempXpath.replace('//html','');
                            }
                        }
                    }
                }                
        }
        if (sibling.nodeType===1 && sibling.tagName.toLowerCase()===element.tagName.toLowerCase()){
            ix++;
        }
    }
}

function generateRelXpath(element) {
    let relXpath = formRelXpath(element);
    let doubleForwardSlash = /\/\/+/g; //regex to find double forward slash
    let numOfDoubleForwardSlash = 0;
    try{
        numOfDoubleForwardSlash = relXpath.match(doubleForwardSlash).length;    
    }catch(err){ }
    if(numOfDoubleForwardSlash>1 && relXpath.includes('[') && !relXpath.includes('@href') && !relXpath.includes('@src')){
        relXpath = optimizeXpath(relXpath);
    }
    return relXpath;
}

function optimizeXpath(xpath) {
    let xpathDiv = xpath.split("//");
    let leng = xpathDiv.length;
    var regBarces = /[^[\]]+(?=])/g; //this is to get content inside all []
    let bracesContentArr = xpath.match(regBarces);
    let startOptimizingFromHere = 1;
    for(let j=bracesContentArr.length-1; j>0; j--){
        startOptimizingFromHere++;
        if(bracesContentArr[j].length>3){
            startOptimizingFromHere = startOptimizingFromHere;
            break;
        }
    }
    let tempXpath = xpath.split("//"+xpathDiv[leng-startOptimizingFromHere])[1];
    let totalMatch = 0;
    try{
        totalMatch = document.evaluate(tempXpath, document, null, XPathResult.ORDERED_NODE_SNAPSHOT_TYPE, null).snapshotLength;
    }catch(err){
        return xpath;
    }
    if(totalMatch===1){
        return tempXpath;
    }
    for(let i=leng-startOptimizingFromHere; i>0; i--){
        let temp = xpath.replace("//"+xpathDiv[i],"");
        try{
            totalMatch = document.evaluate(temp, document, null, XPathResult.ORDERED_NODE_SNAPSHOT_TYPE, null).snapshotLength;
            if(totalMatch===1){
                xpath = temp;
            }
        }catch(err){
            return xpath;
        }
    } 
    return xpath;
}


function getNodename(element) {
    var name = "", className;
    if (element.classList.length) {
        name = [element.tagName.toLowerCase()];
        className = element.className.trim();
        className = className.replace(/  +/g, ' ');
        name.push(className.split(" ").join("."));
        name = name.join(".")
    }
    return name;
}

function getChildNumber(node) {
    var classes = {}, i, firstClass, uniqueClasses;
    var parentNode = node.parentNode, childrenLen;
    childrenLen= parentNode.children.length;
    for (i = 0; i < childrenLen; i++) {
        if (parentNode.children[i].classList.length) {
            firstClass = parentNode.children[i].classList[0];
            if (!classes[firstClass]) {
                classes[firstClass] = [parentNode.children[i]]
            } else {
                classes[firstClass].push(parentNode.children[i])
            }
        }
    }
    uniqueClasses = Object.keys(classes).length || -1;
    var obj = {
        childIndex : -1,
        childLen : childrenLen
    }


    if (classes[Object.keys(classes)[0]] === childrenLen) {
        obj.childIndex = Array.prototype.indexOf.call(classes[node.classList[0]], node);
        obj.childLen = classes[Object.keys(classes)[0]].length;
        return obj
    } else if (uniqueClasses && uniqueClasses!== -1  && uniqueClasses !== childrenLen) {
        obj.childIndex = Array.prototype.indexOf.call(parentNode.children, node);
        obj.childLen = classes[Object.keys(classes)[0]].length;
        return obj
    } else if(uniqueClasses === -1){
        obj.childIndex = Array.prototype.indexOf.call(parentNode.children, node);
        obj.childLen = childrenLen;
        return obj
    }else{
        return obj
    }
}


function parents(element, _array) {
    var name, index;
    if (_array === undefined) {
        _array = [];
    }
    else {
        index = getChildNumber(element);
        name = getNodename(element);
        if (name) {
            if (index.childLen >= 1 && index.childIndex !== -1) {
                name += ":nth-child(" + ( index.childIndex + 1) + ")"
            }
            _array.push(name);
        }else if(_array.length <5){
            name = element.tagName.toLowerCase();
            if (index.childIndex !== -1) {
                name += ":nth-child(" + ( index.childIndex + 1) + ")"
            }
            _array.push(name);
        }
    }
    if (element.tagName !== 'BODY') return parents(element.parentNode, _array);
    else return _array;
}


function generateCSS(el) {
    if(!el){
        return "element is inside iframe & it is not supported by ChroPath currently. Please write CSS manually.";  
    }
    var tagName = el.tagName.toLowerCase();
    if(tagName.includes("style") || tagName.includes("script") || tagName.includes("svg")){
        return "This is "+tagName+" tag. For "+tagName+" tag, no need to write selector. :P";  
    }

    if (el.id!==''){
        return "#"+el.id;
    }

    if (!(el instanceof Element))
        return;
    var path = parents(el, []);
    path = path.reverse();
    var lastNode = path.slice(path.length -1, path.length);
    var _path = path.slice(0,path.length -1);
    if(_path.length!=0){
        return _path.join(" ") + " > "+ lastNode;
    }
    else{  //hack for body tag which is the 1st tag in html page
        return lastNode;
    }
}