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
                        <td>${gymClass.enrolledCount}</td>
                        <td>${gymClass.difficultyLevel}</td>
                        <td>${gymClass.description}</td>
                        <td class="actions">
                            <a class="btn-primary" href="/trainer/classes/form/${gymClass.idClass}">Editar</a>
                            <button type="button" class="btn-danger" onclick="deleteClass(${gymClass.idClass})">Eliminar</button>
                        </td>
                    </tr>
                `;
            });
        });
}

function deleteClass(idClass) {
    if (confirm("¿Desea eliminar esta clase?")) {
        fetch("/classes/" + idClass, {
            method: "DELETE"
        }).then(() => {
            alert("Clase eliminada correctamente");
            loadClasses();
        });
    }
}