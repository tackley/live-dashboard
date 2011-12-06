jQuery ->

  deleteMoreThanTen = ->
    items = $('.search-terms p')
    items[10..].fadeOut()
    items[11..].remove()

  formatParams = (params) ->
    p = for key, value of params
      "#{key}=#{value}"
    p.join " "

  highestDt = 0

  setInterval ( ->
    $.ajax "/api/search",
      type: 'GET'
      dataType: 'jsonp'
      data: { since: highestDt }
      success: (data) ->
         for searchTerm in data
           highestDt = searchTerm.dt
           html = $("<p>#{searchTerm.q}<span class=other-terms>#{formatParams(searchTerm.otherParams)}</span></p>")
           $('.search-terms').prepend(html.hide())
           html.slideDown()

         deleteMoreThanTen()
  ), 1500

