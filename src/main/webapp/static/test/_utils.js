var TestUtils = {
		// Similar to `$.load`, but return the object with itself.
		loadHtmlObj : function(urlAndSelector){
			var url = urlAndSelector.split(" ")[0],
				selector = urlAndSelector.substring(url.length),
				result;

			jQuery.ajax({
				async: false,
				url: url,  // '../../downloader.html',
				success: function(data) {
					// http://stackoverflow.com/questions/25281457/the-right-way-parse-html-to-jquery-object
					var response = $('<html/>').html(data);
					result = selector ? response.find(selector) : response;
				},
			});

			return result;
		},

}