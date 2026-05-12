document.addEventListener("DOMContentLoaded", function () {
    loadClasses();
});

function loadClasses() {
    fetch("/classes")
        .then(response => response.json())
        .then(data => {
            const tbody = document.getElementById("classesTableBody");
            tbody.innerHTML = "";

            data.forEach(gymClass => {
                tbody.innerHTML += `
                    <tr>
                        <td>${gymClass.classType}</td>
                        <td>${gymClass.trainer.fullName}</td>
                        <td>${gymClass.classDate}</td>
                        <td>${gymClass.startTime}</td>
                        <td>${gymClass.endTime}</td>
                        <td>${gymClass.duration} min</td>
                        <td>${gymClass.maxCapacity}</td>
                        <td>${gymClass.difficultyLevel}</td>
                        <td>${gymClass.description}</td>
                        <td>
                            <a class="btn-primary" href="/client/attendances/form?classId=${gymClass.idClass}">
                                Registrar
                            </a>
                        </td>
                    </tr>
                `;
            });
        });
}