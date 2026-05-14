document.addEventListener("DOMContentLoaded", function () {
    const parts = window.location.pathname.split("/");
    const idClass = parts[parts.length - 1];

    loadTrainers().then(() => {
        if (!isNaN(idClass)) {
            loadClass(idClass);
        }
    });
});

function loadTrainers() {
    return fetch("/classes/trainers")
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
            document.getElementById("formTitle").innerText = "Modificar clase";
            document.getElementById("idClass").value = gymClass.idClass;
            document.getElementById("classType").value = gymClass.classType;
            document.getElementById("classDate").value = gymClass.classDate;
            document.getElementById("startTime").value = gymClass.startTime;
            document.getElementById("endTime").value = gymClass.endTime;
            document.getElementById("maxCapacity").value = gymClass.maxCapacity;

            if (gymClass.trainer !== null) {
                document.getElementById("trainerId").value = gymClass.trainer.userId;
            }

            document.getElementById("enrolledCount").value = gymClass.enrolledCount;
            document.getElementById("difficultyLevel").value = gymClass.difficultyLevel;
            document.getElementById("description").value = gymClass.description;
        });
}

function saveClass() {
    clearClassErrors();

    const classType = document.getElementById("classType").value.trim();
    const classDate = document.getElementById("classDate").value;
    const startTime = document.getElementById("startTime").value;
    const endTime = document.getElementById("endTime").value;
    const maxCapacity = document.getElementById("maxCapacity").value;
    const trainerId = document.getElementById("trainerId").value;
    const enrolledCount = document.getElementById("enrolledCount").value;
    const difficultyLevel = document.getElementById("difficultyLevel").value;
    const description = document.getElementById("description").value.trim();
    const clientErrors = {};

    if (classType === "") clientErrors.classType = "Este campo es obligatorio.";
    if (classDate === "") clientErrors.classDate = "La fecha es obligatoria.";
    if (startTime === "") clientErrors.startTime = "Este campo es obligatorio.";
    if (endTime === "") clientErrors.endTime = "Este campo es obligatorio.";
    if (maxCapacity === "") clientErrors.maxCapacity = "Este campo es obligatorio.";
    if (trainerId === "") clientErrors.trainerId = "Seleccione una opcion.";
    if (difficultyLevel === "") clientErrors.difficultyLevel = "Seleccione una opcion.";
    if (description === "") clientErrors.description = "Este campo es obligatorio.";

    if (Object.keys(clientErrors).length > 0) {
        showClassErrors(clientErrors);
        return;
    }

    const gymClass = {
        classType: classType,
        classDate: classDate,
        startTime: startTime,
        endTime: endTime,
        maxCapacity: parseInt(maxCapacity),
        trainer: trainerId === "" ? null : {
            userId: parseInt(trainerId)
        },
        enrolledCount: enrolledCount === "" ? 0 : parseInt(enrolledCount),
        difficultyLevel: difficultyLevel,
        description: description
    };

    let url = "/classes";
    let method = "POST";
    const idClass = document.getElementById("idClass").value;

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
    .then(data => {
        if (!data.success) {
            showClassErrors(data.errors || {});
            return;
        }

        alert("Clase guardada correctamente");
        window.location.href = "/trainer/classes";
    })
    .catch(() => showClassError("form", "No se pudo guardar. Intente nuevamente."));
}

function clearClassErrors() {
    document.querySelectorAll(".error-message").forEach(error => {
        error.textContent = "";
    });
}

function showClassErrors(errors) {
    Object.keys(errors).forEach(field => {
        showClassError(field, errors[field]);
    });
}

function showClassError(field, message) {
    const error = document.getElementById(field + "Error");

    if (error) {
        error.textContent = message;
        return;
    }

    alert(message);
}
