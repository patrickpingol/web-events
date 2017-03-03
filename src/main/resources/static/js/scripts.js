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
        submitAttendance();
    })

    $('input[name="idnum"]').keypress(function (event) {
        if (event.which == 13) {
            event.preventDefault()
            submitAttendance()
        }
    });

    $('button[name="submit-student"]').on('click', function () {
        var jsObject = {
            id: $('input[name="studentid"]').val(),
            lastName: $('input[name="lastname"]').val(),
            firstName: $('input[name="firstname"]').val(),
            college: $('select[name="college"]').val(),
            course: $('input[name="course"]').val()
        }
        var data = JSON.stringify(jsObject)

        $.ajax({
            url: '/api/student/create',
            method: 'POST',
            dataType: 'json',
            contentType: 'application/json',
            data: data,
            success: function () {
                window.location.replace('/student/register?message=success')
            },
            error: function () {
                window.location.replace('/student/register?message=error')
            }
        })
    })

    $('button[name="search-student"]').on('click', function () {
        var id = $('input[name="studentid"]').val()
        var lastName = $('input[name="lastname"]').val()
        var firstName = $('input[name="firstname"]').val()
        var college = $('select[name="college"]').val() != null ? $('select[name="college"]').val() : ''
        var course = $('input[name="course"]').val()

        $.ajax({
            url: '/api/student?id=' + id + '&lastName=' + lastName + '&firstName=' + firstName + '&college=' + college + '&course=' + course,
            method: 'GET',
            success: function (data) {
                var strData
                if (data.message.length > 0) {
                    for (var i = 0; i < data.message.length; i++) {
                        strData += '<tr>' +
                            '<td>' + data.message[i].id + '</td>' +
                            '<td>' + data.message[i].lastName + '</td>' +
                            '<td>' + data.message[i].firstName + '</td>' +
                            '<td>' + data.message[i].college + '</td>' +
                            '<td>' + data.message[i].course + '</td>' +
                            '</tr>'
                    }
                    $('tbody[name="data"]').html(strData)
                } else {
                    $('tbody[name="data"]').html('<tr><th colspan="5" class="text-center">No hits.</th></tr>')
                }
                $('span[name="res-count"]').html(data.message.length)
            },
            error: function (data) {
                console.log(data)
            }
        })
    })

    $('button[name="event-status"]').on('click', function () {
        var id = $('select[name="events"]').val()
        var status = $('select[name="status"]').val()

        $.ajax({
            url: '/api/event/' + id + '?status=' + status,
            method: 'GET',
            success: function (data) {
                var strData
                if (data.message.length > 0) {
                    for (var i = 0; i < data.message.length; i++) {
                        strData += '<tr>' +
                            '<td>' + data.message[i].id + '</td>' +
                            '<td>' + data.message[i].lastName + '</td>' +
                            '<td>' + data.message[i].firstName + '</td>' +
                            '<td>' + data.message[i].college + '</td>' +
                            '<td>' + data.message[i].course + '</td>' +
                            '<td>' + data.message[i].status + '</td>' +
                            '</tr>'
                    }
                    $('tbody[name="data"]').html(strData)
                } else {
                    $('tbody[name="data"]').html('<tr><th colspan="6" class="text-center">No hits.</th></tr>')
                }
                $('span[name="res-count"]').html(data.message.length)
            },
            error: function (data) {
                $('tbody[name="data"]').html('<tr><th colspan="6" class="text-center">' + data.responseJSON.message + '</th></tr>')
            }
        })
    })

    $('button[name="lottery-draw"]').on('click', function (event) {
        var id = Cookies.get("EVENTID")
        if (id == null || id == 'undefined') {
            $('div[name="winner"]').html(
                '<h1 class="display-3">No event is selected.</h1>'
            )
            return
        }
        $.ajax({
            url: '/api/lottery/draw?eventId=' + id,
            method: 'GET',
            success: function (data) {
                var randomNum = 1 + Math.floor(Math.random() * 4);
                $('div[name="winner"]').html(
                    '<img src="/images/loading/' + randomNum + '.gif" class="img-fluid">'
                )
                setTimeout(function () {
                    $('div[name="winner"]').html(
                        '<h2 class="display-3">' + data.message.firstName + ' ' + data.message.lastName + '</h2>' +
                        '<p class="lead">' + data.message.id + '</p>'
                    )
                }, 1500)
            },
            error: function (data) {
                $('div[name="winner"]').html(
                    '<h1 class="display-3">' + data.responseJSON.message + '</h1>'
                )
            }
        })
    })

    if ($('h1[name="count"]').length) {
        var id = Cookies.get("EVENTID")
        if (id == null || id == 'undefined') {
            $('h1[name="count"]').html('')
            $('span[name="count-label"]').html('No event selected.')
            return
        }
        setInterval(function () {
            $.ajax({
                url: '/api/live/' + id,
                method: 'GET',
                success: function (data) {
                    $('h1[name="count"]').html(
                        data.message
                    )
                    if (data.message > 1) {
                        $('span[name="count-label"]').html(
                            'ATTENDEES'
                        )
                    }
                },
                error: function (data) {
                    $('h1[name="count"]').html(
                        data.responseJSON.message
                    )
                }
            })
        }, 5000)
    }
});

$(function () {
    // Focus on load
    $('input[name="idnum"]').focus();
    // Force focus
    $('input[name="idnum"]').focusout(function () {
        $('input[name="idnum"]').focus();
    });
})

function submitAttendance() {
    var studId = $('input[name="idnum"]').val()
    $.ajax({
        url: '/api/student/register?studentId=' + studId,
        method: 'POST',
        dataType: 'json',
        success: function (data) {
            $('p[name="student-info"]').html(
                '<span class="display-3 h3">' + data.message.firstName + ' ' + data.message.lastName + '</span><br />' +
                data.message.id + '<br />' +
                data.message.college + ' | ' + data.message.course + ' | ' + data.message.status
            )
        },
        error: function (data) {
            var message = data.responseJSON.message
            if (message == "Cannot get property 'id' on null object")
                $('p[name="student-info"]').html('Invalid AUF Student ID.')
            else
                $('p[name="student-info"]').html(message)
        }
    })
    $('input[name="idnum"]').val('')
}

function showDeleteModal(eventId, eventName) {
    $('span[name="modal-message"]').html('Are you sure you want to delete ' + eventName + '?')
    $('span[name="deleteButton"]').html('<button type="button" onclick="delEvent(' + eventId + ')" class="btn btn-danger">ABSOLUTELY</button>')
    $('#deleteModal').modal('show')
}

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