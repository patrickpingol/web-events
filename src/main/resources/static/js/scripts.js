$(document).ready(function () {
    $('button[name="set-event"]').on('click', function () {
        var eventId = $('select[name="events"]').val()
        Cookies.set("EVENTID", eventId)
        window.location.replace('/')
    })

    $('button[name="unset-event"]').on('click', function () {
        Cookies.remove("EVENTID")
        window.location.replace('/')
    })

    $('button[name="create-event"]').on('click', function () {
        var eventName = $('input[name="event-name"]').val()
        var eventDate = $('input[name="event-date"]').val().replace('/', '-')
        var jsObject = {
            name: eventName,
            date: eventDate
        }
        var data = JSON.stringify(jsObject)
        console.log(data)
        $.ajax({
            url: '/api/event/create',
            method: 'POST',
            dataType: 'json',
            contentType: 'application/json',
            data: data,
            success: function () {
                window.location.replace('/')
            },
            error: function () {
                alert('ERROR')
            }
        })
    })

    $('button[name="upload-csv"]').on('click', function () {
        var formData = new FormData()
        formData.append('file', $('input[type=file]')[0].files[0])

        $.ajax({
            url: '/api/student/csv',
            method: 'POST',
            processData: false,
            contentType: false,
            data: formData,
            success: function () {
                window.location.replace('/import?message=success')
            },
            error: function () {
                window.location.replace('/import?message=error')
            }
        })
    })

    $('button[name="submit-attendance"]').on('click', function () {
        var studId = $('input[name="idnum"]').val()

        $.ajax({
            url: '/api/student/register?studentId=' + studId,
            method: 'POST',
            dataType:'json',
            success: function (data) {
                $('p[name="student-info"]').html(
                    data.message.id + ' | ' + data.message.lastName + ' ' + data.message.firstName + '<br />' +
                    data.message.college + ' | ' + data.message.course + ' | ' + data.message.status
                )
            },
            error: function (data) {
                alert('ERROR')
                $('p[name="student-info"]').html(''+data.message)
            }
        })
    })
});

function delEvent(eventId) {
    $.ajax({
        url: '/api/event/delete/' + eventId,
        method: 'DELETE',
        success: function () {
            window.location.reload()
        },
        error: function (data) {
            alert(data.message)
        }
    })
}