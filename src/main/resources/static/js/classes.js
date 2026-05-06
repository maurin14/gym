document.addEventListener("DOMContentLoaded", function () {
    loadClasses();
    loadTrainers();
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
                            <button type="button" class="btn-primary" onclick="editClass(${gymClass.idClass})">Editar</button>
                            <button type="button" class="btn-danger" onclick="deleteClass(${gymClass.idClass})">Eliminar</button>
                        </td>
                    </tr>
                `;
            });
        });
}

function loadTrainers() {
    fetch("/classes/trainers")
        .then(response => response.json())
        .then(data => {
            const trainerSelect = document.getElementById("trainerId");
            trainerSelect.innerHTML = '<option value="">Seleccione un entrenador</option>';

            data.forEach(trainer => {
                trainerSelect.innerHTML += `
                    <option value="${trainer.userId}">
                        ${trainer.fullName}
                    </option>
                `;
            });
        });
}

function saveClass() {
    const idClass = document.getElementById("idClass").value;

    const gymClass = {
        classType: document.getElementById("classType").value,
        classDate: document.getElementById("classDate").value,
        startTime: document.getElementById("startTime").value,
        endTime: document.getElementById("endTime").value,
        maxCapacity: document.getElementById("maxCapacity").value,
        trainer: {
            userId: document.getElementById("trainerId").value
        },
        enrolledCount: document.getElementById("enrolledCount").value,
        difficultyLevel: document.getElementById("difficultyLevel").value,
        description: document.getElementById("description").value
    };

    let url = "/classes";
    let method = "POST";

    if (idClass !== "") {
        url = "/classes/" + idClass;
        method = "PUT";
    }

    fetch(url, {
        method: method,
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(gymClass)
    })
        .then(response => response.json())
        .then(() => {
            clearForm();
            loadClasses();
            alert("Clase guardada correctamente");
        });
}

function editClass(idClass) {
    fetch("/classes/" + idClass)
        .then(response => response.json())
        .then(gymClass => {
            document.getElementById("idClass").value = gymClass.idClass;
            document.getElementById("classType").value = gymClass.classType;
            document.getElementById("classDate").value = gymClass.classDate;
            document.getElementById("startTime").value = gymClass.startTime;
            document.getElementById("endTime").value = gymClass.endTime;
            document.getElementById("maxCapacity").value = gymClass.maxCapacity;
            document.getElementById("trainerId").value = gymClass.trainer.userId;
            document.getElementById("enrolledCount").value = gymClass.enrolledCount;
            document.getElementById("difficultyLevel").value = gymClass.difficultyLevel;
            document.getElementById("description").value = gymClass.description;
        });
}

function deleteClass(idClass) {
    if (confirm("¿Desea eliminar esta clase?")) {
        fetch("/classes/" + idClass, {
            method: "DELETE"
        })
            .then(() => {
                loadClasses();
                alert("Clase eliminada correctamente");
            });
    }
}

function clearForm() {
    document.getElementById("idClass").value = "";
    document.getElementById("classType").value = "";
    document.getElementById("classDate").value = "";
    document.getElementById("startTime").value = "";
    document.getElementById("endTime").value = "";
    document.getElementById("maxCapacity").value = "";
    document.getElementById("trainerId").value = "";
    document.getElementById("enrolledCount").value = "";
    document.getElementById("difficultyLevel").value = "Bajo";
    document.getElementById("description").value = "";
}