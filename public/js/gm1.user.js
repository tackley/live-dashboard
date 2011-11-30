// ==UserScript==
// @include	   http://www.guardian.co.uk/*
// @name           Show Guardian Link Hits
// @namespace      http://www.guardian.co.uk/usage
// @description    Show's Live Usage information on all guardian links
// @version         1.1
// ==/UserScript==

var $ = unsafeWindow.jQuery;

// add our stylesheet
$('head').append('<link rel="stylesheet" href="http://gnmfasteragain.int.gnl:5000/assets/stylesheets/gm-stats.css" type="text/css" />');

// create "hits" elements for each g.co.uk link
$('a[href^="http://www.guardian.co.uk"]').each(function(i, node) {
  var targetUrl = node.href.split("?")[0].split("#")[0];
  $(node).before('<span class=greasy-hits data-href="' + targetUrl + '"></span>');
});


function updateStats() {
	$.getJSON("http://gnmfasteragain.int.gnl:5000/api/counts?callback=?", function(result) {
	  $('span.greasy-hits').each(function() {
	    var targetUrl = $(this).data('href');
	    var count = result[targetUrl];
	    if (count) {
	      $(this).removeClass("greasy-hits-zero").text(count);
	      if (count[0] === "0" || count === "trace")
            { $(this).removeClass("greasy-hits-high"); }
          else
            { $(this).addClass("greasy-hits-high"); }
	    } else {
	      $(this).removeClass("greasy-hits-high").addClass("greasy-hits-zero").text("0");
	    }
	  });
	});
	window.setTimeout(updateStats, 5000);
}

updateStats();

/*
This is my rewrite in Coffescript; however it doesn't work because greasemonkey requires the
metadata at the top of the file (see http://wiki.greasespot.net/Metadata_Block), and
coffeescript wraps the script :(

###
// ==UserScript==
// @include	   http://www.guardian.co.uk/*
// @name           Show Guardian Link Hits
// @namespace      http://www.guardian.co.uk/usage
// @description    Shows Live Usage information on all guardian links
// ==/UserScript==`
###

$ = unsafeWindow.jQuery;

# add our stylesheet
$('head').append('<link rel="stylesheet" href="http://gnmfasteragain.int.gnl:5000/assets/stylesheets/gm-stats.css" type="text/css" />');

# create "hits" elements for each g.co.uk link
$('a[href^="http://www.guardian.co.uk"]').each ->
  targetUrl = $(this).href.split("?")[0].split("#")[0]
  $(this).before('<span class=greasy-hits data-href="' + targetUrl + '"></span>')


updateStats = ->
    $.getJSON("http://gnmfasteragain.int.gnl:5000/api/counts?callback=?", (result) ->
        $('span.greasy-hits').each ->
            targetUrl = $(this).data('href')
            count = result[targetUrl]
            if (count)
                $(this).removeClass("greasy-hits-zero").text(count)
                if (count[0] == "0")
                    $(this).removeClass("greasy-hits-high")
                else
                    $(this).addClass("greasy-hits-high")
            else
                $(this).removeClass("greasy-hits-high").addClass("greasy-hits-zero").text("0")
        window.setTimeout(updateStats, 5000)
    )

updateStats()

 */


