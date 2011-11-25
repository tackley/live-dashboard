// ==UserScript==
// @include	   http://www.guardian.co.uk/*
// @name           Show Guardian Link Hits
// @namespace      http://www.guardian.co.uk/usage
// @description    Show's Live Usage information on all guardian links
// ==/UserScript==

var jQuery = unsafeWindow.jQuery;

jQuery.getJSON("http://gnmfasteragain.int.gnl:5000/api/counts?callback=?", function(result) {
  jQuery('.greasy-hits').remove()
  jQuery('a.link-text').each(function(i, node) {
    console.log(node);
    if (result[node.href] != undefined) {
      jQuery(node).before("<span class=greasy-hits style='position: absolute; z-index: 9000; background-color: #62CFFC;font-size: 10pt;border-radius: 3px;color: white;padding: 1px 3px 2px; text-indent: 0'>" + result[node.href] +"</span>");
    }
  });
});

