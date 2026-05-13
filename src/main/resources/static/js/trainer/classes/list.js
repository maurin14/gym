document.addEventListener("DOMContentLoaded", function () {
    loadClasses();
});

function loadClasses() {

    fetch("/trainer/classes")
        .then(response => response.json())
        .then(data => {

            const tbody = document.getElementById("classesTableBody");
            tbody.innerHTML = "";

            data.forEach(gymClass => {

                tbody.innerHTML += `
                    <tr>
                        <td>${gymClass.classType}</td>
                        <td>${gymClass.description}</td>
                        <td>${gymClass.startTime}</td>
                        <td>${gymClass.endTime}</td>
                        <td>${gymClass.capacity}</td>
                        <td>${gymClass.difficulty}</td>
                        <td>${gymClass.instructor.fullName}</td>
                        <td>${gymClass.registerDate}</td>

                        <td class="actions">

                            <a class="btn-primary"
                               href="/trainer/classes/form/${gymClass.idClass}">
                               Editar
                            </a>

                            <button type="button"
                                    class="btn-danger"
                                    onclick="deleteClass(${gymClass.idClass})">
                                Eliminar
                            </button>

                        </td>
                    </tr>
                `;
            });
        });
}

function deleteClass(idClass) {

    if (confirm("¿Desea eliminar esta clase?")) {

        fetch("/trainer/classes/" + idClass, {
            method: "DELETE"
        })
        .then(() => {

            alert("Clase eliminada correctamente");
            loadClasses();

        });
    }
}