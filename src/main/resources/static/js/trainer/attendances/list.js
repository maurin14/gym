let currentPage = 1;
let totalPages = 1;

const rowsPerPage = 5;
const attendanceBasePath = window.attendanceBasePath || (window.location.pathname.startsWith("/admin/attendance")
        ? "/admin/attendance"
        : "/trainer/attendances");
const canDeleteAttendances = Boolean(window.canDeleteAttendances);

document.addEventListener("DOMContentLoaded", function () {
    loadAttendances();
});

function loadAttendances() {

    fetch("/attendances/page?page=" + (currentPage - 1) + "&size=" + rowsPerPage)
        .then(response => response.json())
        .then(data => {

            totalPages = data.totalPages;
            currentPage = data.currentPage;

            showAttendances(data.attendances);
            showPagination();

        });
}

function showAttendances(attendances) {

    const tbody = document.getElementById("attendancesTableBody");
    tbody.innerHTML = "";

    if (!attendances || attendances.length === 0) {
        tbody.innerHTML = `
            <tr>
                <td colspan="8" class="empty-message">
                    No hay asistencias registradas.
                </td>
            </tr>
        `;
        return;
    }

    attendances.forEach(attendance => {

        tbody.innerHTML += `
            <tr>
                <td>${attendance.clientName}</td>
                <td>${attendance.classType}</td>
                <td>${attendance.branchName || "Sin sucursal"}</td>
                <td>${attendance.attendanceDate}</td>
                <td>
                    <span class="badge status-active">
                        ${attendance.attendanceStatus}
                    </span>
                </td>
                <td>${attendance.observation}</td>
                <td>${attendance.registerDate}</td>
                <td>
                    <div class="actions">
                        <a class="btn-primary"
                           href="${attendanceBasePath}/form/${attendance.idAttendance}">
                            Editar
                        </a>

                        ${canDeleteAttendances ? `
                        <button type="button"
                                class="btn-danger"
                                onclick="deleteAttendance(${attendance.idAttendance})">
                            Eliminar
                        </button>
                        ` : ""}
                    </div>
                </td>
            </tr>
        `;
    });
}

function showPagination() {

    const pagination = document.getElementById("attendancesPagination");
    pagination.innerHTML = "";

    const visualTotalPages = Math.max(totalPages, 1);

    pagination.appendChild(createPageButton(
            "Anterior",
            Math.max(currentPage - 1, 1),
            currentPage === 1 ? "btn-secondary disabled" : "btn-secondary",
            currentPage === 1
    ));

    for (let page = 1; page <= visualTotalPages; page++) {
        pagination.appendChild(createPageButton(
                page,
                page,
                page === currentPage ? "btn-primary page-active" : "btn-secondary"
        ));
    }

    pagination.appendChild(createPageButton(
            "Siguiente",
            Math.min(currentPage + 1, visualTotalPages),
            currentPage >= visualTotalPages ? "btn-secondary disabled" : "btn-secondary",
            currentPage >= visualTotalPages
    ));
}

function createPageButton(text, page, className, disabled) {
    const button = document.createElement("button");
    button.type = "button";
    button.className = className;
    button.textContent = text;
    button.dataset.page = page;
    button.disabled = Boolean(disabled);
    button.addEventListener("click", function () {
        changeAttendancePage(this);
    });
    return button;
}

function changeAttendancePage(element) {
    const page = parseInt(element.dataset.page, 10);
    if (page >= 1 && page <= totalPages && page !== currentPage) {
        currentPage = page;
        loadAttendances();
    }
}

function deleteAttendance(idAttendance) {
    if (!canDeleteAttendances) {
        return;
    }

    confirmAdminAction({
        title: "Eliminar asistencia?",
        text: "Esta accion no se puede deshacer.",
        confirmText: "Eliminar",
        icon: "warning"
    }).then((result) => {
        if (!result.isConfirmed) {
            return;
        }

        showAdminLoading("Eliminando...");

        fetch("/attendances/" + idAttendance, {
            method: "DELETE"
        })
        .then(response => {
            if (!response.ok) {
                throw new Error("No se pudo eliminar.");
            }
            showAdminSuccess("Eliminado.").then(loadAttendances);
        })
        .catch(error => {
            showAdminError(error.message || "No se pudo eliminar.");
        });
    });
}
