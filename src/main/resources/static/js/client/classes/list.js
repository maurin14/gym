document.addEventListener("DOMContentLoaded", function () {
    loadAttendances();
});

function loadAttendances() {
    fetch("/attendances")
        .then(response => response.json())
        .then(data => {
            const tbody = document.getElementById("attendancesTableBody");
            tbody.innerHTML = "";

            data.forEach(attendance => {
                tbody.innerHTML += `
                    <tr>
                        <td>${attendance.gymClass.classType}</td>
                        <td>${attendance.attendanceDate}</td>
                        <td><span class="badge status-active">${attendance.attendanceStatus}</span></td>
                        <td>${attendance.observation}</td>
                        <td>${attendance.registerDate}</td>
                    </tr>
                `;
            });
        });
}