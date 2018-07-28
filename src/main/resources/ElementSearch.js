(function() {
  var SWD_Page_Recorder, addStyle, bye, createCommand, dbg, getInputElementsByTypeAndValue, getPageXY, getCssSelectorOF, getElementId, getPathTo, handler, hello, prev, preventEvent, pseudoGuid, rightClickHandler, say;

  var ELEMENT_NODE = 1;
  say = function(context) {
    if (typeof console !== 'undefined' && console !== null) {
      return console.log(context);
    }
  };

  dbg = function(context) {
    if (typeof console !== 'undefined' && console !== null) {
      return console.log('DBG:' + context);
    }
  };

  hello = function(context) {
    return dbg('(begin): ' + context);
  };

  bye = function(context) {
    return dbg('(end): ' + context);
  };

  pseudoGuid = function() {
    var result;
    hello('pseudoGuid');
    result = 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx';
    result = result.replace(/[xy]/g, function(re_match) {
      var random_value, replacement;
      random_value = Math.random() * 16 | 0;
      replacement = re_match === 'x' ? random_value : random_value & 0x3 | 0x8;
      return replacement.toString(16);
    });
    bye('pseudoGuid');
    return result;
  };

  getInputElementsByTypeAndValue = function(inputType, inputValue) {
    var allDocumentInputElements, inputElement, result, _i, _len;
    hello('getInputElementsByTypeAndValue');
    allDocumentInputElements = document.getElementsByTagName('input');
    result = new Array();
    for (_i = 0, _len = allDocumentInputElements.length; _i < _len; _i++) {
      inputElement = allDocumentInputElements[_i];
      if (inputElement.type === inputType && inputElement.value === inputValue) {
        result.push(inputElement);
      }
    }
    bye('getInputElementsByTypeAndValue');
    return result;
  };

    // http://stackoverflow.com/questions/6743912/get-the-pure-text-without-html-element-by-javascript
		getText = function(element, addSpaces) {
      var i, result, text, child;
      hello('getText ' + element.tagName);
      if (element.childNodes && element.childNodes > 1 ) {
        result = '';
        for (i = 0; i < element.childNodes.length; i++) {
            child = element.childNodes[i];
            text = null;
            // NOTE we only collapsing child node values when there is more than one child
            if (child.elementType === 1) {
                text = getText(child, addSpaces);
            } else if (child.elementType === 3) {
                text = child.elementValue;
            }
            if (text) {
                if (addSpaces && /\S$/.test(result) && /^\S/.test(text)) text = ' ' + text;
                result += text;
            }
        }
      } else {
        result = element.innerText || element.textContent || '';
      }
      result = result.replace(/\r?\n/g, ' ').replace(/\s+/g, ' ' ).replace(/^\s+/, '' ).replace(/\s+$/, '' )
      bye('getText result: ' + result);
      return result;
		};

    getElementId = function(element) {
         var selector = '';
         hello('getElementId ' + element.tagName);

         if (element instanceof Element && element.nodeType === ELEMENT_NODE && element.id) {
             selector = element.id;
         }
         bye('getElementId');
         return selector;
     };

      // The initial version code of getCssSelectorOF was partially borrowed from chromium project:
      // https://chromium.googlesource.com/chromium/src.git
     getCssSelectorOF = function(element) {
       hello('getCssSelectorOF ' + element.tagName);
       var specialAttributesArray = ['href','src','title','alt','name', 'value', 'type', 'action', 'onclick'];
       if (!(element instanceof Element))
         return;
       var path = [];
       while (element.nodeType === ELEMENT_NODE) {
         var selector = element.nodeName.toLowerCase();
         if (element.id && path.length != 0) {
           if (element.id.indexOf('-') > -1) {
             selector += '[id = "' + element.id + '"]';
           } else {
             selector += '#' + element.id;
           }
           path.unshift(selector);
           break;
         } else if (element.className ) {
           var attr = element.className;
           // ignore className attributes with special characters
           if (attr.indexOf('(') == -1 /* && attr.indexOf('*') == -1 && attr.indexOf('.') == -1 */) {
             selector += '.' + attr.replace(/^\s+/,'').replace(/\s+$/,'').replace(/\s+/g, '.');
           }
         } else {
           var element_sibling = element;
           var sibling_cnt = 1;
           while (element_sibling = element_sibling.previousElementSibling) {
             if (element_sibling.nodeName.toLowerCase() == selector)
               sibling_cnt++;
           }
           if (sibling_cnt != 1)
             selector += ':nth-of-type(' + sibling_cnt + ')';
          }
          var arrayLength = specialAttributesArray.length;
          var attribute_conditions_postfix = [];
          var attribute_condition = '';
          var prefix = '';
          // refactored to look similarly with getCssSelectorOF and getPathTo
          for (var i = 0; i < arrayLength; i++) {
            specialAttribute = specialAttributesArray[i];
            attribute_condition = probeAttribute(element,specialAttribute, prefix);
            if (attribute_condition) {
              hello('Found attribute condition:' + attribute_condition);
              attribute_conditions_postfix.push(attribute_condition);
            }
          }
          if (attribute_conditions_postfix.length > 0 ) {
            for (var i = 0; i < attribute_conditions_postfix.length; i++) {
              selector += '[ ' + attribute_conditions_postfix[i] + ' ]';
            }
            hello('Finished building postfix: ' + element.tagName + ' ' + selector);
          }
         path.unshift(selector);
         element = element.parentNode;
       }
       bye('getCssSelectorOF');
       return path.join(' > ');
     };

    // prefix : '@' for xpath, empty for css.
    probeAttribute = function(element,attributeName, prefix){
      if (element.hasAttribute(attributeName)) {
        return prefix + attributeName + ' = "' + element.getAttribute(attributeName) + '"';
      } else {
        return null;
      }
    }
    getPathTo = function(element, depth) {
        depth = depth + 1;
        var element_sibling, siblingTagName, siblings, cnt, sibling_count;
        var specialAttributesArray = ['href','src','title','alt','name', 'value', 'type', 'action', 'onclick'];
        var postfixConditions = [];
        var elementTagName = element.tagName.toLowerCase();
        hello('getPathTo ' +  elementTagName );
        if (element.id != '' /* and */ && depth != 1) {
            return 'id("' + element.id + '")';
            // alternative :
            // return '*[@id="' + element.id + '"]';
        } else if (element.name && document.getElementsByName(element.name).length === 1) {
            return '//' + elementTagName + '[@name="' + element.name + '"]';
        } else if (element === document.body) {
          return '/html/' + elementTagName;
        }
        var attribute_condition = '';
        var attribute_conditions_postfix = [];
        var arrayLength = specialAttributesArray.length;
        var prefix = '@';
        for (var i = 0; i < arrayLength; i++) {
          specialAttribute = specialAttributesArray[i];
          attribute_condition = probeAttribute(element,specialAttribute,prefix);
          if (attribute_condition) {
            hello('Found attribute condition:' + attribute_condition);
            attribute_conditions_postfix.push(attribute_condition);
          }
        }

        if (attribute_conditions_postfix.length > 0 ) {
          postfix = '[ '+ attribute_conditions_postfix.join(' and ') + ' ]';
          hello('Finished building postfix: ' + elementTagName + postfix);
          return ( getPathTo(element.parentNode, depth) + '/' +  elementTagName + postfix );
        }

        sibling_count = 0;
        siblings = element.parentNode.childNodes;
        siblings_length = siblings.length;
        for (cnt = 0; cnt < siblings_length; cnt++) {
          var element_sibling = siblings[cnt];
          if (element_sibling.nodeType !== ELEMENT_NODE) { // not ELEMENT_NODE
            continue;
          }
          if (element_sibling === element) {
            return getPathTo(element.parentNode, depth) + '/' + elementTagName + '[' + (sibling_count + 1) + ']';
          }
          if (element_sibling.nodeType === 1 && element_sibling.tagName.toLowerCase() === elementTagName) {
            sibling_count++;
          }
        }
        return bye('getPathTo ' + elementTagName);
    };

    // simplified
    // ng12Hybrid detection not supported yet
    // not functional yet
    testForAngular = function() {
      hello('testForAngular' );
      var isAngular =  (window.angular && window.angular.resumeBootstrap) ?
        true: false;
        return bye('testForAngular ' + isAngular);
    };

    getProtractorLocators = function(element) {
      hello('getProtractorLocators' );
      var specialAttributesArray = ['ng-repeat','ng-binding','ng-model','ng-option'];
      var elementTagName = element.tagName.toLowerCase();
      var attribute_postfix = [];
      var postfix = '';
      var arrayLength = specialAttributesArray.length;
      // if (testForAngular()) {
        for (var i = 0; i < arrayLength; i++) {
          specialAttribute = specialAttributesArray[i];
          postfix = probeAttribute(element,specialAttribute,'@');
          if (postfix) {
            hello('Found postfix:' + postfix);
            attribute_postfix.push(postfix);
          }
        }
      // }
      bye('getProtractorLocators ' +  attribute_postfix.join(','));
    };


    getPageXY = function(element) {
    var x, y;
    hello('getPageXY');
    x = 0;
    y = 0;
    while (element) {
      x += element.offsetLeft;
      y += element.offsetTop;
      element = element.offsetParent;
    }
    bye('getPageXY');
    return [x, y];
  };

  createCommand = function(jsonData) {
    var myJSONText;
    hello('createCommand');
    myJSONText = JSON.stringify(jsonData, null, 2);
    document.swdpr_command = myJSONText;
    return bye('createCommand ' + myJSONText);
  };

  addStyle = function(css) {
    var head, style;
    hello('addStyle');
    head = document.getElementsByTagName('head')[0];
    style = document.createElement('style');
    style.type = 'text/css';
    if (style.styleSheet) {
      style.styleSheet.cssText = css;
    } else {
      style.appendChild(document.createTextNode(css));
    }
    head.appendChild(style);
    return bye('addStyle');
  };

  preventEvent = function(event) {
    hello('preventEvent');
    if (event.preventDefault) {
      event.preventDefault();
    }
    event.returnValue = false;
    if (event.stopPropagation) {
      event.stopPropagation();
    } else {
      event.cancelBubble = true;
    }
    bye('preventEvent');
    return false;
  };

  prev = void 0;

  document.Swd_prevActiveElement = void 0;

  handler = function(event) {
    hello('handler');
    if (document.SWD_Page_Recorder == null) {
      return;
    }
    if (event.target === document.body || prev === event.target) {
      return;
    }
    if (prev) {
      prev.className = prev.className.replace(/\s?\bhighlight\b/, '');
      prev = void 0;
    }
    if (event.target && event.ctrlKey) {
      prev = event.target;
      prev.className += ' highlight';
    }
    return bye('handler');
  };

  rightClickHandler = function(event) {
    var jsonData, body, eventPreventingResult, mxy, root, target, txy, xpath, css_selector, id, elementText, tagName;
    hello("rightClickHandler");
    if (document.SWD_Page_Recorder == null) {
      return;
    }
    if (event.ctrlKey) {
      if (event == null) {
        event = window.event;
      }
      target = 'target' in event ? event.target : event.srcElement;
      root = document.compatMode === 'CSS1Compat' ? document.documentElement : document.body;
      mxy = [event.clientX + root.scrollLeft, event.clientY + root.scrollTop];
      xpath = getPathTo(target, 0);
      txy = getPageXY(target);
      css_selector = getCssSelectorOF(target);
      var dummy = getProtractorLocators(target);
      id = getElementId(target);
      elementText = getText(target, true);
      tagName = target.tagName;
      body = document.getElementsByTagName('body')[0];
      jsonData = {
        'Command': 'GetXPathFromElement',
        'Caller': 'EventListener : mousedown',
        'CommandId': pseudoGuid(),
        'CssSelector': css_selector,
        'ElementId': id,
        'XPathValue': xpath,
        'ElementText': elementText,
        'ElementTagName': tagName,
      };
      createCommand(jsonData);
      depth = 0;
      document.SWD_Page_Recorder.showPos(event, xpath, css_selector, id, elementText, tagName );
      eventPreventingResult = preventEvent(event);
      bye('rightClickHandler');
      return eventPreventingResult;
    }
  };

  SWD_Page_Recorder = (function() {
    function SWD_Page_Recorder() {}

    SWD_Page_Recorder.prototype.getMainWinElement = function() {
      return document.getElementById('SwdPR_PopUp');
    };

    SWD_Page_Recorder.prototype.displaySwdForm = function(x, y) {
      var el;
      hello('displaySwdForm');
      el = this.getMainWinElement();
      el.style.background = 'white';
      el.style.position = 'absolute';
      el.style.left = x + 'px';
      el.style.top = y + 'px';
      el.style.display = 'block';
      el.style.border = '3px solid black';
      el.style.padding = '5px 5px 5px 5px';
      el.style.zIndex = 2147483647;
      return bye('displaySwdForm');
    };

    SWD_Page_Recorder.prototype.showPos = function(event, xpath, css_selector, id, elementText, tagName ) {
      var x, y;
      hello('showPos');
      if (window.event) {
        x = window.event.clientX + document.documentElement.scrollLeft + document.body.scrollLeft;
        y = window.event.clientY + document.documentElement.scrollTop + document.body.scrollTop;
      } else {
        x = event.clientX + window.scrollX;
        y = event.clientY + window.scrollY;
      }
      x -= 2;
      y -= 2;
      y = y + 15;
      this.displaySwdForm(x, y);
      document.getElementById('SwdPR_PopUp_XPathLocator').innerHTML = xpath;
      document.getElementById('SwdPR_PopUp_CssSelector').innerHTML = css_selector;
      document.getElementById('SwdPR_PopUp_ElementId').innerHTML = id;
      document.getElementById('SwdPR_PopUp_ElementGUID').innerHTML = pseudoGuid();
      document.getElementById('SwdPR_PopUp_CodeIDText').value = '';
      document.getElementById('SwdPR_PopUp_ElementText').innerHTML = elementText;
      document.getElementById('SwdPR_PopUp_ElementTagName').innerHTML = tagName;
      say(x + ';' + y);
      return bye('showPos');
    };

    SWD_Page_Recorder.prototype.closeForm = function() {
      return document.getElementById('SwdPR_PopUp').style.display = 'none';
    };

    SWD_Page_Recorder.prototype.createElementForm = function() {
      var closeClickHandler, element;
      hello('createElementForm');
      element = document.createElement('div');
      element.id = 'SwdPR_PopUp';
      if (document.body != null) {
        document.body.appendChild(element);
      } else {
        say("createElementForm Failed to inject element SwdPR_PopUp. The document has no body");
      }
      // TODO: fix the id
      closeClickHandler = "";
            element.innerHTML = '\
            <form name="SWDForm" id="SWDForm"> \
              <table id="SWDTable">\
                <tr>\
                  <td>Code identifier</td>\
                  <td>\
                    <div id="SwdPR_PopUp_Element_Name">\
                      <span id="SwdPR_PopUp_CodeID">\
                        <input type="text" id="SwdPR_PopUp_CodeIDText"/>\
                      </span>\
                      <span id="SwdPR_PopUp_CodeClose"/>\
                      <span id="SwdPR_PopUp_CloseButton" onclick="document.SWD_Page_Recorder.closeForm()">\
                        <svg width="10" height="10"><circle cx="5" cy="5" r="4" stroke="red" stroke-width="1" fill="pink" />X</svg>\
                      </span>\
                    </div>\
                  </td>\
                </tr>\
                <tr>\
                  <td>\
                    <input type="radio" name="ElementSelectedBy" id="ElementId"/>\
                    <label for="ElementId">Id:</label>\
                  </td>\
                  <td>\
                    <span id="SwdPR_PopUp_ElementId">Element Id</span>\
                  </td>\
                </tr>\
                <tr>\
                  <td>\
                    <label>GUID:</label>\
                  </td>\
                  <td>\
                    <span id="SwdPR_PopUp_ElementGUID">Element GUID</span>\
                  </td>\
                </tr>\
                <tr>\
                  <td>\
                    <input type="radio" name="ElementSelectedBy" id="ElementXPath"/>\
                    <label for="ElementXPath">XPath:</label>\
                  </td>\
                  <td>\
                    <span id="SwdPR_PopUp_XPathLocator">Element XPath</span>\
                  </td>\
                </tr>\
                <tr>\
                  <td>\
                    <input type="radio" name="ElementSelectedBy" id="ElementCssSelector" checked="checked"/>\
                    <label for="ElementCssSelector">Css:</label>\
                  </td>\
                  <td>\
                    <span id="SwdPR_PopUp_CssSelector">Element Css</span>\
                  </td>\
                </tr>\
                <tr>\
                  <td>\
                    <input type="radio" name = "ElementSelectedBy" id="ElementText"/>\
                    <label for="ElementText">Text:</label>\
                  </td>\
                  <td>\
                    <span id="SwdPR_PopUp_ElementText">Element Text</span>\
                  </td>\
                </tr>\
                <tr>\
                  <td>\
                    <input type="radio" name = "ElementSelectedBy" id="ElementTagName" disabled="disabled"/>\
                    <label for="ElementTagName">TagName:</label>\
                  </td>\
                  <td>\
                    <span id="SwdPR_PopUp_ElementTagName">Element TagName</span>\
                  </td>\
                </tr>\
              </table>\
              <input type="button" value="Add element" onclick="document.SWD_Page_Recorder.addElement()"/>\
              </form>';
      return bye("createElementForm");
    };

    SWD_Page_Recorder.prototype.addElement = function() {

    var codeIDTextElement = document.getElementById('SwdPR_PopUp_CodeIDText');
      hello('addElement ' + codeIDTextElement.value );
      var htmlIdElement = document.getElementById('SwdPR_PopUp_ElementId');
      var cssSelectorElement = document.getElementById('SwdPR_PopUp_CssSelector');
      var xPathLocatorElement = document.getElementById('SwdPR_PopUp_XPathLocator');
      var elementTextElement = document.getElementById('SwdPR_PopUp_ElementText');
      var elementTagName = document.getElementById('SwdPR_PopUp_ElementTagName');
      var elementSelectedBy = 'ElementCssSelector';

      var radios = document.SWDForm.ElementSelectedBy;
      if (radios){ // this code is unstable
        for (i=0; i < radios.length; i++) {
          if (radios[i].checked) {
            elementSelectedBy = radios[i].getAttribute('id');
            // hello(radios[i].getAttribute('id') + ' you got a value');
          }
        }
      }
      hello('ElementSelectedBy : ' + elementSelectedBy);
      var JsonData = {
        'Command': 'AddElement',
        'Caller': 'addElement',
        'CommandId': pseudoGuid(),
        'ElementSelectedBy': elementSelectedBy,
        'ElementCodeName': codeIDTextElement.value,
        'ElementId': (htmlIdElement.hasChildNodes()) ? htmlIdElement.firstChild.nodeValue:'',
        'ElementCssSelector': (cssSelectorElement.hasChildNodes())?cssSelectorElement.firstChild.nodeValue : '',
        'ElementXPath': (xPathLocatorElement.hasChildNodes())? xPathLocatorElement.firstChild.nodeValue : '',
        'ElementText': (elementTextElement.hasChildNodes())? elementTextElement.firstChild.nodeValue: '',
        'ElementTagName': (elementTagName.hasChildNodes())? elementTagName.firstChild.nodeValue: '',
      };
      createCommand(JsonData);
      return bye('addElement ' + codeIDTextElement.value + '>');
    };

    return SWD_Page_Recorder;

  })();

 addStyle('.highlight { background-color:silver !important}');
 addStyle('table#SWDTable { background-color:white; border-collapse:collapse; } table#SWDTable,table#SWDTable th, table#SWDTable td { font-family: Verdana, Arial; font-size: 10pt; padding-left:10pt; padding-right:10pt; border-bottom: 1px solid black; }');
 addStyle('input#SwdPR_PopUp_CodeIDText { display:table-cell; width:95%; }');
 addStyle('span#SwdPR_PopUp_CloseButton { display:table-cell; -moz-border-radius: 4px; -webkit-border-radius: 4px; -o-border-radius: 4px; border-radius: 4px; border: 1px solid #ccc; color: white; background-color: #980000; cursor: pointer; font-size: 10pt; padding: 0px 2px; font-weight: bold; position: absolute; right: 3px; top: 8px; }');

  /*
  addStyle "span#SwdPR_PopUp_CloseButton {
              display:table-cell;
              width:10px;
              border: 2px solid #c2c2c2;
              padding: 1px 5px;
              top: -20px;
              background-color: #980000;
              border-radius: 20px;
              font-size: 15px;
              font-weight: bold;
              color: white;text-decoration: none; cursor:pointer;
            }"
  */


  addStyle("div#SwdPR_PopUp {             display:none;           }           div#SwdPR_PopUp_Element_Name {             display:table;             width: 100%;           }");

  /*
      Important!
      It wont work if the document has no body, such as top frameset pages.
  */


  if (document.body != null) {
    if (document.body.addEventListener) {
      document.body.addEventListener('mouseover', handler, false);
      document.addEventListener('contextmenu', rightClickHandler, false);
    } else if (document.body.attachEvent) {
      document.body.attachEvent('mouseover', function(e) {
        return handler(e || window.event);
      });
      document.body.attachEvent('oncontextmenu', function(e) {
        return rightClickHandler(e || window.event);
      });
    } else {
      document.body.onmouseover = handler;
      document.body.onmouseover = rightClickHandler;
    }
    document.SWD_Page_Recorder = new SWD_Page_Recorder();
    document.SWD_Page_Recorder.createElementForm();
  } else {
    say("Document has no body tag... Injecting empty SWD");
    document.SWD_Page_Recorder = "STUB. Document has no body tag :(";
  }

}).call(this);

