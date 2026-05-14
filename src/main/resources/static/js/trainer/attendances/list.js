let currentPage = 1;
let totalPages = 1;

const rowsPerPage = 5;

document.addEventListener("DOMContentLoaded", function () {
    loadAttendances();
});

function loadAttendances() {

    fetch("/attendances/page?page=" + (currentPage - 1) + "&size=" + rowsPerPage)
        .then(response => response.json())
        .then(data => {

            totalPages = data.totalPages;

            showAttendances(data.attendances);
            showPagination();

        });
}

function showAttendances(attendances) {

    const tbody = document.getElementById("attendancesTableBody");
    tbody.innerHTML = "";

    attendances.forEach(attendance => {

        tbody.innerHTML += `
            <tr>
                <td>${attendance.clientName}</td>
                <td>${attendance.classType}</td>
                <td>${attendance.attendanceDate}</td>
                <td>
                    <span class="badge status-active">
                        ${attendance.attendanceStatus}
                    </span>
                </td>
                <td>${attendance.observation}</td>
                <td>${attendance.registerDate}</td>
                <td class="actions">
                    <a class="btn-primary"
                       href="/trainer/attendances/form/${attendance.idAttendance}">
                        Editar
                    </a>

                    <button type="button"
                            class="btn-danger"
                            onclick="deleteAttendance(${attendance.idAttendance})">
                        Eliminar
                    </button>
                </td>
            </tr>
        `;
    });
}

function showPagination() {

    if (totalPages === 0) {
        totalPages = 1;
    }

    document.getElementById("pageInfo").innerText =
            "Página " + currentPage + " de " + totalPages;

    document.getElementById("prevBtn").style.display =
            currentPage === 1 ? "none" : "inline-block";

    document.getElementById("nextBtn").style.display =
            currentPage === totalPages ? "none" : "inline-block";
}

function nextPage() {

    if (currentPage < totalPages) {
        currentPage++;
        loadAttendances();
    }
}

function previousPage() {

    if (currentPage > 1) {
        currentPage--;
        loadAttendances();
    }
}

function deleteAttendance(idAttendance) {

    fetch("/attendances/" + idAttendance, {
        method: "DELETE"
    })
    .then(() => {
        alert("Asistencia eliminada correctamente");
        loadAttendances();
    });
}