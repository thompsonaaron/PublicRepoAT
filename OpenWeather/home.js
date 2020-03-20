// alert("this is a test");

function getWeather() {
    $.ajax({
        type: 'GET',
        // 'https://cors-anywhere.herokuapp.com/api.openweathermap.org/data/2.5/weather?zip=55126&units=imperial&appid=4119a2091f62ee87ab99ba2fb9d193a6'
        url: 'https://cors-anywhere.herokuapp.com/api.openweathermap.org/data/2.5/weather?zip=' + $('#Zipcode').val() + '&units=' + $('#units').val() + '&appid=4119a2091f62ee87ab99ba2fb9d193a6',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        },
        dataType: 'json',
        success: function (weather) {
            clearWeather();

            var weatherdiv = $('#weatherdiv');

            var cityName = '<p>Current Conditions in ' + ' ';
            cityName += weather.name;
            $('#cityHeader').append(cityName);

            var src = 'http://openweathermap.org/img/wn/';
            showImage(src + weather.weather[0].icon + '@2x.png', '#firstColumnA', 100, 100);

            // data is coming in as an array
            var mainWeather = '<p>';
            mainWeather += weather.weather[0].main + ': ' + weather.weather[0].description + '</p>';
            $('#firstColumnB').append('</br>' + mainWeather);


            var degrees = '';
            if ($('#units').val() == "Imperial") {
                degrees = ' F';
            } else if ($('#units').val() == "Metric") {
                degrees = ' C'
            };
            var tempData = '<p>';
            tempData += 'Temperature: ' + weather.main.temp + degrees + '</br>';
            tempData += 'Humidity: ' + weather.main.humidity + '% ' + '</br>';
            tempData += 'Wind: ' + weather.wind.speed + ' miles per hour</p>';
            $('#secondColumn').append(tempData)
        },
        error: function (jqXHR, textStatus, errorThrown) {
            alert("FAILURE!");
        }

    })

};

function clearWeather() {
    $('#cityHeader').empty();
    $('#firstColumnA').empty();
    $('#firstColumnB').empty();
    $('#secondColumn').empty();
}

function showImage(src, location, width, height, alt) {
    var img = document.createElement("img");
    img.src = src;
    img.width = width;
    img.height = height;
    img.alt = alt;

    $(location).append(img);
    '#firstColumnA'
}

function getForecast() {
    $.ajax({
        type: 'GET',
        // https://cors-anywhere.herokuapp.com/api.openweathermap.org/data/2.5/forecast?zip={zip code},{country code}&appid={your api key}
        url: 'https://cors-anywhere.herokuapp.com/api.openweathermap.org/data/2.5/forecast?zip=' + $('#Zipcode').val() + '&units=' + $('#units').val() + '&appid=4119a2091f62ee87ab99ba2fb9d193a6',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        },
        dataType: 'json',
        success: function (forecast) {

            clearFiveDay();

            var fiveDayText = '<p>Five Day Forecast </p>';
            $('#fivedayheader').append(fiveDayText);

            // var date1 = forecast.list[0].dt_txt;
            var date2 = forecast.list[1].dt_txt;
            var date3 = forecast.list[9].dt_txt;
            var date4 = forecast.list[17].dt_txt;
            var date5 = forecast.list[25].dt_txt;
            var date6 = forecast.list[33].dt_txt;
            $('#5day1').append(dateFormatter(date2) + '</br>');
            $('#5day2').append(dateFormatter(date3) + '</br>');
            $('#5day3').append(dateFormatter(date4) + '</br>');
            $('#5day4').append(dateFormatter(date5) + '</br>');
            $('#5day5').append(dateFormatter(date6) + '</br>');


            for (var day = 0; day < 5; day++) {
                var minTemp = 1000;
                var maxTemp = -1000;


                // where a chunk is a 3 hour period, starting at index of one (the next day)
                for (var chunk = 0; chunk < 8; chunk++) {
                    var index = day * 8 + chunk + 1;
                    // gonna take the icon image from the icon code from the middle of the day reading
                    if (chunk == 5){
                        var icon = forecast.list[index].weather[0].icon;
                    }

                    if (index > 39){
                        break;
                    } else {
                        var chunkMax = forecast.list[index].main.temp_max;
                        var chunkMin = forecast.list[index].main.temp_min;

                        if (chunkMax > maxTemp) {
                            maxTemp = chunkMax;
                        } if (chunkMin < minTemp) {
                            minTemp = chunkMin;
                        }
                    }

                    
                }
                var dayplus = day + 1;
                var source = 'http://openweathermap.org/img/wn/' + icon + '@2x.png';
                showImage(source, '#5day' + dayplus, 100, 100);
                $('#5day' + dayplus).append('</br>' + 'H: ' + maxTemp + ' L:' + minTemp);


                // $('#5day' + dayplus).append('<img src=' + source + 'height="50px"' + 'width="50px">');
            }
        }

    })
}

function dateFormatter(date) {
    var currentDateTime = new Date(date);
    const months = ["JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC"];
    var formattedDate = currentDateTime.getDate() + "-" + months[currentDateTime.getMonth()];
    return formattedDate;
}

function clearFiveDay() {
    $('#5day1').empty();
    $('#5day2').empty();
    $('#5day3').empty();
    $('#5day4').empty();
    $('#5day5').empty();
    $('#fivedayheader').empty();
}