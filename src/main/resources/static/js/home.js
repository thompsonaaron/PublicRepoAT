$(document).ready(function () {
    // alert("Javascript file loaded successfully");
    // HH does PM, hh does AM as default
    var todayAM = moment().format('YYYY-MM-DDThh:mm');
    var todayPM = moment().format('YYYY-MM-DDTHH:mm');

    if (todayPM.substring(11, 13) < 12) {
        $('#workoutDate').val(todayAM);
    } else {
        $('#workoutDate').val(todayPM);
    }

});

function addSplit() {

    // Container <div> where dynamic content will be placed
    var timeContainer = $('#timeContainer');
    // var hourContainer = $('#hourContainer');
    // var minuteContainer = $('#minuteContainer');
    // var secondContainer = $('#secondContainer');
    // var closeContainer = $('#closeBtnRow');

    var length = $(timeContainer).children().length;
    length = parseInt(length);
    var increment = parseInt(1);
    var newLength = length + increment;

    var containerDiv = document.createElement("div");
    containerDiv.className = "row col-md-12";
    containerDiv.id = "container" + newLength;

    var minutes = document.createElement("input");
    minutes.type = "number";
    minutes.name = "minutes";
    minutes.min = "0";
    minutes.max = "59";
    minutes.placeholder = "min";
    minutes.className = "timeRow form-control col-md-4";
    minutes.id = "min" + newLength;

    var seconds = document.createElement("input");
    seconds.type = "number";
    seconds.name = "seconds";
    seconds.min = "0";
    seconds.max = "59";
    seconds.placeholder = "sec";
    seconds.className = "timeRow form-control col-md-4";
    seconds.id = "sec" + newLength;

    var closeSpan = document.createElement('span');
    closeSpan.innerHTML = 'x';
    closeSpan.className = "closebtn col-md-4";
    closeSpan.id = "closebtn" + newLength;
    closeSpan.onclick = function () {
        // removes the latest seconds and minutes boxes
        $("#" + containerDiv.id).remove();
    };

    containerDiv.append(minutes, seconds, closeSpan);

    timeContainer.append(containerDiv);
    // timeContainer.append(seconds);
    // minuteContainer.append("</br/>");
    // secondContainer.append('&nbsp;')
    // timeContainer.append(closeSpan);
    // closeContainer.append("<br/>");
    minutes.focus();

    // unable to get it to scroll to the newly created split input location
    //     scrollTo = $('#comments');

    // container.animate({
    //     scrollTop: scrollTo.offset().top - container.offset().top + container.scrollTop()
    // });
}

function convertDuration(totalSeconds) {
    var sec_num = parseInt(totalSeconds, 10);
    var hours = Math.floor(sec_num / 3600);
    var minutes = Math.floor((sec_num - (hours * 3600)) / 60);
    var seconds = sec_num - (hours * 3600) - (minutes * 60);

    if (hours < 10) { hours = "0" + hours; }
    if (minutes < 10) { minutes = "0" + minutes; }
    if (seconds < 10) { seconds = "0" + seconds; }
    return hours + ':' + minutes + ':' + seconds;
}

function deleteTeammateConfirmation(firstName, lastName, userId) {

    let confirmed = confirm("Are you sure that you want to remove" + " " + firstName + " " + lastName + " " + "from your team?");
    if (confirmed) {
        $.ajax({
            type: 'POST',
            url: 'localhost8080/removeTeammate?userId=' + 'userId',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
            datatype: 'json',
            success: function (deck) {
                alert("User successfully removed from your team");
            },
            error: function (request, error) {
                alert("Failed to remove user from your team");
            }
        })
    }
}
