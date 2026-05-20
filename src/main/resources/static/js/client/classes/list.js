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
                <td colspan="10" class="empty-message">
                    No hay clases disponibles.
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

    if (totalPages === 0) {
        totalPages = 1;
    }

    document.getElementById("pageInfo").innerText =
            "Página " + currentPage + " de " + totalPages;

    document.getElementById("prevBtn").style.display =
            currentPage === 1 ? "none" : "inline-flex";

    document.getElementById("nextBtn").style.display =
            currentPage === totalPages ? "none" : "inline-flex";
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