let currentPage = 1;
let totalPages = 1;

const rowsPerPage = 5;
const classBasePath = window.location.pathname.startsWith("/admin/classes")
        ? "/admin/classes"
        : "/trainer/classes";

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

        });
}

function showClasses(classes) {

    const tbody = document.getElementById("classesTableBody");
    tbody.innerHTML = "";

    classes.forEach(gymClass => {

        tbody.innerHTML += `
            <tr>
                <td>${gymClass.classType}</td>
                <td>${gymClass.trainerName}</td>
                <td>${gymClass.classDate}</td>
                <td>${gymClass.startTime}</td>
                <td>${gymClass.endTime}</td>
                <td>${gymClass.duration} min</td>
                <td>${gymClass.maxCapacity}</td>
                <td>${gymClass.enrolledCount}</td>
                <td>${gymClass.difficultyLevel}</td>
                <td>${gymClass.description}</td>

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
            </tr>
        `;
    });
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
        .then(() => {
            showAdminSuccess("Clase eliminada.").then(loadClasses);
        });
    });
}
