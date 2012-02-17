jQuery ->
  setVisibility = ->
    $(".tag-list").toggle($("#tags-toggle").is(":checked"))
    $(".trail-text").toggle($("#trail-text-toggle").is(":checked"))

  refreshData = ->
    $('[data-ajax-refresh]').each ->
      $(this).load $(this).data("ajax-refresh"), ->
        setVisibility()

  $("input[type='checkbox']").click -> setVisibility()

  setInterval refreshData, 2500
