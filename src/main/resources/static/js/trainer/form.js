document.addEventListener("DOMContentLoaded", function () {
    loadTrainers();

    const parts = window.location.pathname.split("/");
    const idClass = parts[parts.length - 1];

    if (!isNaN(idClass)) {
        loadClass(idClass);
    }
});

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

function loadClass(idClass) {
    fetch("/classes/" + idClass)
        .then(response => response.json())
        .then(gymClass => {
            document.getElementById("formTitle").innerText = "Modificar Clase";
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
    }).then(() => {
        alert("Clase guardada correctamente");
        window.location.href = "/trainer/classes";
    });
}