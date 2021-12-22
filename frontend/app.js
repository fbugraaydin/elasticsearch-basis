$(document).ready(function () {
  var formattedOutput = new Intl.NumberFormat('tr-TR', {
    style: 'currency',
    currency: 'TRY',
    minimumFractionDigits: 2,
  });

  $.ajax('http://localhost:8080/products', {
      success: function (data, status, xhr) {
        printProducts(data);
      }
  });

  $("#search-button").click(function () {
    const query = $("#search").val()
    $.ajax('http://localhost:8080/products/search?query=' + query, {
      success: function (data, status, xhr) {
        printProducts(data);
      }
    });
  });

  $("#filter-button").click(function () {
    const minPrice = $("#min-price").val()
    const maxPrice = $("#max-price").val()
    const title = $("#title").val()
    const sortOrder = $("#sort-order").val()
    const request = {
      "sortOrder": sortOrder
    }
    if(minPrice != null && minPrice != "" && !Number.isNaN(minPrice)){
      request.minPrice = parseInt(minPrice)
    }
    if(maxPrice != null && maxPrice != "" && !Number.isNaN(minPrice)){
      request.maxPrice = parseInt(maxPrice)
    }
    if(title != null && title != ""){
      request.title = title
    }
    
    $.ajax({
      type: 'POST',
      url: 'http://localhost:8080/products/filter',
      data: JSON.stringify(request),
      success: function(data) { 
        printProducts(data) 
      },
      contentType: "application/json",
      dataType: 'json'
    });

  });

  getProductDetail = function(id){
    $("#products-content").hide()
    $("#product-detail-content").show()
    $.ajax('http://localhost:8080/products/' + id, {
      success: function (data, status, xhr) {
        $("#product-detail-content").show()
        $("#card-detail-title").text(data.title)
        $("#card-detail-price").text(formattedOutput.format(data.price))
        $("#card-detail-seller").text(data.seller)
        $("#card-detail-rate").text(data.rate)
      }
    });
  }

  var printProducts = function (data) {
    $("#product-detail-content").hide()
    $("#products-content").show()
    $('#products-content').empty()
    data.forEach(element => {
      const price = formattedOutput.format(element.price)
      const product =
        '<div onclick="getProductDetail('+ element.id +')" class="card mb-3" style="max-width: 540px; padding-top:10px; padding-bottom:10px;">' +
          '<div class="row g-0">' +
            '<div class="col-md-4">' +
              '<image src="' + element.image + '" class="img-fluid rounded-start">' +
            '</div>' +
            '<div class="col-md-8">' +
              '<div class="card-body">' +
                '<h5 class="card-title">' + element.title + '</h5>' +
                '<b class="card-text">' + price + '</b>' +
              '</div>' +
            '</div>' +
          '</div>' +
        '</div>';

      $('#products-content').append(product);
    });
  }


});