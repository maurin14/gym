let currentPage = 1;
let totalPages = 1;
const rowsPerPage = 4;

document.addEventListener("DOMContentLoaded", function () {
    loadAttendances();
});

function loadAttendances() {
    fetch("/client/attendances/page?page=" + (currentPage - 1) + "&size=" + rowsPerPage)
        .then(response => response.json())
        .then(data => {
            totalPages = data.totalPages;
            currentPage = data.currentPage;
            showAttendances(data.attendances);
            showPagination();
        })
        .catch(() => showSystemError("No se pudo cargar."));
}

function showAttendances(attendances) {
    const tbody = document.getElementById("attendancesTableBody");
    tbody.innerHTML = "";

    if (!attendances || attendances.length === 0) {
        tbody.innerHTML = `
            <tr>
                <td colspan="5" class="empty-message">No hay asistencias registradas.</td>
            </tr>
        `;
        return;
    }

    attendances.forEach(attendance => {

        const statusClass = getAttendanceStatusClass(attendance.attendanceStatus);

        tbody.innerHTML += `
            <tr>
                <td>${attendance.classType || ""}</td>
                <td>${formatDate(attendance.attendanceDate)}</td>

                <td>
                    <span class="badge ${statusClass}">
                        ${attendance.attendanceStatus || ""}
                    </span>
                </td>

                <td>${attendance.observation || ""}</td>
                <td>${formatDate(attendance.registerDate)}</td>
            </tr>
        `;
    });
}

function getAttendanceStatusClass(status) {

    const statusText = status ? status.toLowerCase() : "";

    if (statusText === "ausente") {
        return "status-inactive";
    }

    if (statusText === "tarde") {
        return "status-pending";
    }

    return "status-active";
}

function formatDate(dateValue) {

    if (!dateValue) {
        return "";
    }

    const parts = String(dateValue).split("-");

    if (parts.length !== 3) {
        return dateValue;
    }

    return parts[2] + "-" + parts[1] + "-" + parts[0];
}

function showPagination() {
    const pagination = document.getElementById("attendancesPagination");
    pagination.innerHTML = "";

    const visualTotalPages = Math.max(totalPages, 1);

    pagination.appendChild(createPageLink("Anterior", Math.max(currentPage - 1, 1), currentPage === 1));

    for (let page = 1; page <= visualTotalPages; page++) {
        const link = createPageLink(page, page, false);
        if (page === currentPage) {
            link.classList.add("active");
        }
        pagination.appendChild(link);
    }

    pagination.appendChild(createPageLink("Siguiente", Math.min(currentPage + 1, visualTotalPages), currentPage >= visualTotalPages));
}

function createPageLink(text, page, disabled) {
    const link = document.createElement("button");
    link.type = "button";
    link.className = "pagination-link";
    link.textContent = text;
    link.disabled = Boolean(disabled);

    if (disabled) {
        link.classList.add("disabled");
    }

    link.addEventListener("click", function () {
        if (!disabled && page >= 1 && page <= totalPages && page !== currentPage) {
            currentPage = page;
            loadAttendances();
        }
    });

    return link;
}