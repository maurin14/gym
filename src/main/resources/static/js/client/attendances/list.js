document.addEventListener("DOMContentLoaded", function () {
    loadAttendances();
});

function loadAttendances() {

    fetch("/client/attendances/data")
        .then(response => response.json())
        .then(data => {

            const tbody = document.getElementById("attendancesTableBody");
            tbody.innerHTML = "";

            data.forEach(attendance => {

                tbody.innerHTML += `
                    <tr>
                        <td>${attendance.gymClass ? attendance.gymClass.classType : "Sin clase"}</td>
                        <td>${attendance.attendanceDate}</td>
                        <td>${attendance.attendanceStatus}</td>
                        <td>${attendance.observation}</td>
                        <td>${attendance.registerDate}</td>
                    </tr>
                `;
            });
        });
}