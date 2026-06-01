let currentPage = 1;
let totalPages = 1;
const rowsPerPage = 5;

document.addEventListener("DOMContentLoaded", function () {
    loadClasses();
});

function loadClasses() {
    fetch("/classes/page?page=" + (currentPage - 1) + "&size=" + rowsPerPage)
        .then(response => response.json())
        .then(data => {
            totalPages = data.totalPages;
            currentPage = data.currentPage;
            showClasses(data.classes);
            showPagination();
        })
        .catch(() => showSystemError("No se pudo cargar."));
}

function showClasses(classes) {
    const tbody = document.getElementById("classesTableBody");
    tbody.innerHTML = "";

    if (!classes || classes.length === 0) {
        tbody.innerHTML = `
            <tr>
                <td colspan="11" class="empty-message">No hay clases disponibles.</td>
            </tr>
        `;
        return;
    }

    classes.forEach(gymClass => {
        tbody.innerHTML += `
            <tr>
                <td>${gymClass.classType || ""}</td>
                <td>${gymClass.trainerName || ""}</td>
                <td>${gymClass.branchName || "Sin sucursal"}</td>
                <td>${gymClass.classDate || ""}</td>
                <td>${gymClass.startTime || ""}</td>
                <td>${gymClass.endTime || ""}</td>
                <td>${gymClass.duration || 0} min</td>
                <td>${gymClass.maxCapacity || ""}</td>
                <td>${gymClass.difficultyLevel || ""}</td>
                <td>${gymClass.description || ""}</td>
                <td>
                    <a class="btn-primary table-action"
                       href="/client/attendances/form?classId=${gymClass.idClass}">
                        Registrar
                    </a>
                </td>
            </tr>
        `;
    });
}

function showPagination() {
    const pagination = document.getElementById("classesPagination");
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
            loadClasses();
        }
    });

    return link;
}
