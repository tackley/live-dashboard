jQuery ->
  setVisibility = ->
    console.log "setting visibility"
    $(".tag-list").toggle($("#tags-toggle").is(":checked"))


  $("#tags-toggle").click -> setVisibility()

  refreshData = ->
    $('[data-ajax-refresh]').each ->
      $(this).load $(this).data("ajax-refresh"), ->
        setVisibility()

  setInterval refreshData, 2500
