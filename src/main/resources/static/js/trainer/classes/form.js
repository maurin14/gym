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

            trainerSelect.innerHTML =
                    '<option value="">Seleccione un entrenador</option>';

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

            if (gymClass.trainer !== null) {
                document.getElementById("trainerId").value = gymClass.trainer.userId;
            }

            document.getElementById("enrolledCount").value = gymClass.enrolledCount;
            document.getElementById("difficultyLevel").value = gymClass.difficultyLevel;
            document.getElementById("description").value = gymClass.description;
        });
}

function saveClass() {

    const classType = document.getElementById("classType").value.trim();
    const classDate = document.getElementById("classDate").value;
    const startTime = document.getElementById("startTime").value;
    const endTime = document.getElementById("endTime").value;
    const maxCapacity = document.getElementById("maxCapacity").value;
    const trainerId = document.getElementById("trainerId").value;
    const enrolledCount = document.getElementById("enrolledCount").value;
    const difficultyLevel = document.getElementById("difficultyLevel").value;
    const description = document.getElementById("description").value.trim();

    if (classType === "" || classDate === "" || startTime === "" || endTime === "" ||
            maxCapacity === "" || trainerId === "" || enrolledCount === "" ||
            difficultyLevel === "" || description === "") {
        alert("Debe completar todos los campos.");
        return;
    }

    if (parseInt(maxCapacity) <= 0) {
        alert("La capacidad máxima debe ser mayor a 0.");
        return;
    }

    if (parseInt(enrolledCount) < 0) {
        alert("La cantidad de inscritos no puede ser negativa.");
        return;
    }

    if (parseInt(enrolledCount) > parseInt(maxCapacity)) {
        alert("La cantidad de inscritos no puede ser mayor que la capacidad máxima.");
        return;
    }

    if (startTime >= endTime) {
        alert("La hora final debe ser mayor que la hora de inicio.");
        return;
    }

    const idClass = document.getElementById("idClass").value;

    const gymClass = {
        classType: classType,
        classDate: classDate,
        startTime: startTime,
        endTime: endTime,
        maxCapacity: maxCapacity,
        trainer: {
            userId: trainerId
        },
        enrolledCount: enrolledCount,
        difficultyLevel: difficultyLevel,
        description: description
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