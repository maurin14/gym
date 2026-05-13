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
                <td>${gymClass.difficultyLevel}</td>
                <td>${gymClass.description}</td>

                <td>
                    <a class="btn-primary"
                       href="/client/attendances/form?classId=${gymClass.idClass}">
                        Registrar
                    </a>
                </td>
            </tr>
        `;
    });
}

function showPagination() {

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