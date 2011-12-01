// ==UserScript==
// @include	   http://www.guardian.co.uk/*
// @include	   http://www.guardiannews.com/*
// @name           Show Guardian Link Hits
// @namespace      http://www.guardian.co.uk/usage
// @description    Show's Live Usage information on all guardian links
// @version         1.2
// ==/UserScript==
console.log("Starting GreasyCounter");
// a function that loads jQuery and calls a callback function when jQuery has finished loading
function addJQuery(callback) {
  var script = document.createElement("script");
  script.setAttribute("src", "http://ajax.googleapis.com/ajax/libs/jquery/1.7.1/jquery.min.js");
  script.addEventListener('load', function() {
    var script = document.createElement("script");
    script.textContent = "(" + callback.toString() + ")();";
    document.body.appendChild(script);
  }, false);
  document.body.appendChild(script);
}

// the guts of this userscript
function main() {
    var statsServer="http://gnmfasteragain.int.gnl:5000";
    // var statsServer="http://localhost:9000";
    // add our stylesheet
    $('head').append('<link rel="stylesheet" href="'+statsServer+'/assets/stylesheets/gm-stats.css" type="text/css" />');

    // create "hits" elements for each g.co.uk link
    $('a[href^="http://www.guardian.co.uk"]').each(function(i, node) {
      var targetUrl = node.href.split("?")[0].split("#")[0];
      $(node).before('<span class=greasy-hits data-href="' + targetUrl + '"></span>');
    });


    function updateStats() {
        console.log("Updating stats");
    	$.getJSON(statsServer+"/api/counts?callback=?", function(result) {
            // console.log(result);
    	  $('span.greasy-hits').each(function(i, elem) {
    	    var targetUrl = $(elem).data('href');
    	    var count = result[targetUrl];
            // console.log("i: "+i+" elem:"+elem+" href "+targetUrl+" = "+count);
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
}

// load jQuery and execute the main function
addJQuery(main);var $ = unsafeWindow.jQuery;


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


