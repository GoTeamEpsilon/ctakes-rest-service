$(document).ready(function() {
     $('#fire').click(function() {
        try {
            $.ajax({
				url: "http://localhost:8080/ctakes-web-rest/service/analyze",
				type: "POST",
				crossDomain: true,
				cache: false,
				async: true,
				data: "Patient is having cancer",
				error: function(xhr, statusText, error) {
					 navigator.notification.alert(ajaxErrorMsg);
				},
				success: function(response, statusText, xhr) {					
					alert("Success - " + JSON.stringify(response));
				}
			})
        } catch (err) {
            alert("Error - " + err.message);
        }

    });
});
