let currentPage = 1;
let totalPages = 1;
const rowsPerPage = 5;

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
        tbody.innerHTML += `
            <tr>
                <td>${attendance.classType || ""}</td>
                <td>${attendance.attendanceDate || ""}</td>
                <td>${attendance.attendanceStatus || ""}</td>
                <td>${attendance.observation || ""}</td>
                <td>${attendance.registerDate || ""}</td>
            </tr>
        `;
    });
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
