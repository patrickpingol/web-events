$(document).ready(function () {
    $('button[name="set-event"]').on('click', function () {
        var eventId = $('select[name="events"]').val()
        document.cookie = "EVENTID=" + eventId
        window.location.replace('/')
    })
});