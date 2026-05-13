let classes = [];
let currentPage = 1;

const rowsPerPage = 5;

document.addEventListener("DOMContentLoaded", function () {

    loadClasses();

});

function loadClasses() {

    fetch("/classes")
        .then(response => response.json())
        .then(data => {

            classes = data;
            currentPage = 1;

            showClasses();
            showPagination();

        });

}

function showClasses() {

    const tbody =
            document.getElementById("classesTableBody");

    tbody.innerHTML = "";

    const start = (currentPage - 1) * rowsPerPage;
    const end = start + rowsPerPage;

    const pageClasses = classes.slice(start, end);

    pageClasses.forEach(gymClass => {

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

    const totalPages = Math.ceil(classes.length / rowsPerPage);

    document.getElementById("pageInfo").innerText =
            "Página " + currentPage + " de " + totalPages;

    document.getElementById("prevBtn").style.display =
            currentPage === 1 ? "none" : "inline-block";

    document.getElementById("nextBtn").style.display =
            currentPage === totalPages ? "none" : "inline-block";
}

function nextPage() {

    const totalPages = Math.ceil(classes.length / rowsPerPage);

    if (currentPage < totalPages) {

        currentPage++;

        showClasses();
        showPagination();

    }
}

function previousPage() {

    if (currentPage > 1) {

        currentPage--;

        showClasses();
        showPagination();

    }
}