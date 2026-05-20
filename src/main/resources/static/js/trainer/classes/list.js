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

            totalPages = data.totalPages || 1;

            showClasses(data.classes);
            showPagination();

        })
        .catch(() => {
            Swal.fire({
                icon: "error",
                title: "Error",
                text: "No se pudieron cargar las clases.",
                confirmButtonColor: "#d97818"
            });
        });
}

function showClasses(classes) {

    const tbody = document.getElementById("classesTableBody");
    tbody.innerHTML = "";

    if (!classes || classes.length === 0) {
        tbody.innerHTML = `
            <tr>
                <td colspan="11" class="empty-message">
                    No hay clases registradas.
                </td>
            </tr>
        `;
        return;
    }

    classes.forEach(gymClass => {

        tbody.innerHTML += `
            <tr>
                <td>${gymClass.classType || ""}</td>
                <td>${gymClass.trainerName || "Sin entrenador"}</td>
                <td>${gymClass.classDate || ""}</td>
                <td>${gymClass.startTime || ""}</td>
                <td>${gymClass.endTime || ""}</td>
                <td>${gymClass.duration || 0} min</td>
                <td>${gymClass.maxCapacity || 0}</td>
                <td>${gymClass.enrolledCount || 0}</td>
                <td>${gymClass.difficultyLevel || ""}</td>
                <td>${gymClass.description || ""}</td>

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
        loadClasses();
    }
}

function previousPage() {

    if (currentPage > 1) {
        currentPage--;
        loadClasses();
    }
}

function deleteClass(idClass) {

    Swal.fire({
        title: "¿Eliminar clase?",
        text: "Esta acción eliminará la clase seleccionada.",
        icon: "warning",
        showCancelButton: true,
        confirmButtonColor: "#d97818",
        cancelButtonColor: "#6c757d",
        confirmButtonText: "Sí, eliminar",
        cancelButtonText: "Cancelar"
    }).then((result) => {

        if (result.isConfirmed) {

            fetch("/classes/" + idClass, {
                method: "DELETE"
            })
            .then(response => {

                if (!response.ok) {
                    throw new Error("No se pudo eliminar la clase.");
                }

                Swal.fire({
                    icon: "success",
                    title: "Clase eliminada",
                    text: "La clase se eliminó correctamente.",
                    confirmButtonColor: "#d97818"
                }).then(() => {

                    if (currentPage > 1) {
                        currentPage--;
                    }

                    loadClasses();
                });
            })
            .catch(() => {
                Swal.fire({
                    icon: "error",
                    title: "Error",
                    text: "No se pudo eliminar la clase.",
                    confirmButtonColor: "#d97818"
                });
            });
        }
    });
}