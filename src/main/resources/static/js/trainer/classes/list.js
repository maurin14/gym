let currentPage = 1;
let totalPages = 1;

const rowsPerPage = 4;

const classBasePath = window.classBasePath || (window.location.pathname.startsWith("/admin/classes")
        ? "/admin/classes"
        : "/trainer/classes");

const canManageClasses = Boolean(window.canManageClasses);

document.addEventListener("DOMContentLoaded", function () {
    loadBranchesFilter();
    loadClasses();
});

function loadBranchesFilter() {

    const branchFilter = document.getElementById("branchFilter");

    if (!branchFilter) {
        return;
    }

    fetch("/classes/branches")
            .then(response => response.json())
            .then(data => {

                branchFilter.innerHTML =
                        '<option value="">Todas las sucursales</option>';

                data.forEach(branch => {

                    branchFilter.innerHTML += `
                        <option value="${branch.id}">
                            ${branch.name}
                        </option>
                    `;
                });
            });
}

function filterClassesByBranch() {
    currentPage = 1;
    loadClasses();
}

function loadClasses() {

    const branchFilter = document.getElementById("branchFilter");
    const branchId = branchFilter ? branchFilter.value : "";

    let url = "/classes/page?page=" + (currentPage - 1) + "&size=" + rowsPerPage;

    if (branchId !== "") {
        url += "&branchId=" + encodeURIComponent(branchId);
    }

    fetch(url)
            .then(response => response.json())
            .then(data => {

                totalPages = data.totalPages;
                currentPage = data.currentPage;

                showClasses(data.classes);
                showPagination();

            });
}

function showClasses(classes) {

    const tbody = document.getElementById("classesTableBody");
    tbody.innerHTML = "";

    if (!classes || classes.length === 0) {
        tbody.innerHTML = `
            <tr>
                <td colspan="${canManageClasses ? 13 : 12}" class="empty-message">
                    No hay clases registradas.
                </td>
            </tr>
        `;
        return;
    }

    classes.forEach(gymClass => {

        const statusClass = gymClass.status
                ? "status-active"
                : "status-inactive";

        const statusText = gymClass.status
                ? "Activa"
                : "Inactiva";

        const difficultyClass =
                getDifficultyClass(gymClass.difficultyLevel);

        tbody.innerHTML += `
            <tr>
                <td>${gymClass.classType}</td>
                <td>${gymClass.trainerName}</td>
                <td>${gymClass.branchName || "Sin sucursal"}</td>
                <td>${gymClass.classDate}</td>
                <td>${gymClass.startTime}</td>
                <td>${gymClass.endTime}</td>
                <td>${gymClass.duration} min</td>
                <td>${gymClass.maxCapacity}</td>
                <td>${gymClass.enrolledCount}</td>

                <td>
                    <span class="badge ${statusClass}">
                        ${statusText}
                    </span>
                </td>

                <td>
                    <span class="badge ${difficultyClass}">
                        ${gymClass.difficultyLevel}
                    </span>
                </td>

                <td>${gymClass.description}</td>

                ${canManageClasses ? `
                <td>
                    <div class="actions">
                        <a class="btn-primary"
                           href="${classBasePath}/edit/${gymClass.idClass}">
                            Editar
                        </a>

                        <button type="button"
                                class="btn-danger"
                                onclick="deleteClass(${gymClass.idClass})">
                            Eliminar
                        </button>
                    </div>
                </td>
                ` : ""}
            </tr>
        `;
    });
}

function getDifficultyClass(difficultyLevel) {

    const difficulty = difficultyLevel
            ? difficultyLevel.toLowerCase()
            : "";

    if (difficulty === "alto") {
        return "status-inactive";
    }

    if (difficulty === "medio") {
        return "status-pending";
    }

    return "status-active";
}

function showPagination() {

    const pagination = document.getElementById("classesPagination");
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
        changeClassPage(this);
    });
    return button;
}

function changeClassPage(element) {
    const page = parseInt(element.dataset.page, 10);
    if (page >= 1 && page <= totalPages && page !== currentPage) {
        currentPage = page;
        loadClasses();
    }
}

function deleteClass(idClass) {
    if (!canManageClasses) {
        return;
    }

    confirmAdminAction({
        title: "Eliminar clase?",
        text: "Esta accion no se puede deshacer.",
        confirmText: "Eliminar",
        icon: "warning"
    }).then((result) => {
        if (!result.isConfirmed) {
            return;
        }

        showAdminLoading("Eliminando...");

        fetch("/classes/" + idClass, {
            method: "DELETE"
        })
        .then(response => {
            if (!response.ok) {
                throw new Error("No se pudo eliminar.");
            }
            showAdminSuccess("Clase eliminada.").then(loadClasses);
        })
        .catch(error => {
            showAdminError(error.message || "No se pudo eliminar.");
        });
    });
}