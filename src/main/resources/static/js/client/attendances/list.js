document.addEventListener("DOMContentLoaded", function () {
    loadAttendances();
});

function loadAttendances() {

    fetch("/client/attendances/data")
        .then(response => response.json())
        .then(data => {

            const tbody =
                    document.getElementById("attendancesTableBody");

            tbody.innerHTML = "";

            if (data.length === 0) {
                tbody.innerHTML = `
                    <tr>
                        <td colspan="5" class="empty-message">No hay asistencias registradas.</td>
                    </tr>
                `;
                return;
            }

            data.forEach(attendance => {

                tbody.innerHTML += `
                    <tr>

                        <td>${attendance.classType}</td>

                        <td>${attendance.attendanceDate}</td>

                        <td>${attendance.attendanceStatus}</td>

                        <td>${attendance.observation}</td>

                        <td>${attendance.registerDate}</td>

                    </tr>
                `;
            });

        });
}
