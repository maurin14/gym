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
                        <td>${attendance.client.fullName}</td>
                        <td>${attendance.gymClass.classType}</td>
                        <td>${attendance.attendanceDate}</td>
                        <td><span class="badge status-active">${attendance.attendanceStatus}</span></td>
                        <td>${attendance.observation}</td>
                        <td>${attendance.registerDate}</td>
                        <td class="actions">
                            <a class="btn-primary" href="/trainer/attendances/form/${attendance.idAttendance}">Editar</a>
                            <button type="button" class="btn-danger" onclick="deleteAttendance(${attendance.idAttendance})">Eliminar</button>
                        </td>
                    </tr>
                `;
            });
        });
}

function deleteAttendance(idAttendance) {
    if (confirm("¿Desea eliminar esta asistencia?")) {
        fetch("/attendances/" + idAttendance, {
            method: "DELETE"
        }).then(() => {
            alert("Asistencia eliminada correctamente");
            loadAttendances();
        });
    }
}