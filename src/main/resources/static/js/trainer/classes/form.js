let classSaving = false;

document.addEventListener("DOMContentLoaded", function () {

    const idClass = getClassIdFromPath();

    if (idClass !== null) {
        loadClass(idClass);
    }

    loadBranches().then(() => {

        const branchSelect = document.getElementById("branchId");

        if (branchSelect) {
            branchSelect.addEventListener("change", loadTrainers);
        }

        loadTrainers().then(() => {

            if (idClass !== null) {
                loadClass(idClass);
            }
        });
    });
});

function getClassIdFromPath() {
    const match = window.location.pathname.match(/\/(?:form|edit)\/(\d+)$/);
    return match ? match[1] : null;
}

function getClassesListPath() {
    return window.classBasePath || (window.location.pathname.startsWith("/admin/classes")
            ? "/admin/classes"
            : "/trainer/classes");
}

function loadTrainers() {

    const branchId = document.getElementById("branchId")
            ? document.getElementById("branchId").value
            : "";

    const url = branchId
            ? "/classes/trainers?branchId=" + encodeURIComponent(branchId)
            : "/classes/trainers";

    return fetch(url)
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

function loadBranches() {

    return fetch("/classes/branches")
            .then(response => response.json())
            .then(data => {

                const branchSelect = document.getElementById("branchId");

                branchSelect.innerHTML =
                        '<option value="">Seleccione una sucursal</option>';

                data.forEach(branch => {

                    branchSelect.innerHTML += `
                    <option value="${branch.id}">
                        ${branch.name}
                    </option>
                `;
                });
            });
}

function loadClass(idClass) {

    fetch("/classes/" + idClass)
            .then(response => response.json())
            .then(gymClass => {

                if (gymClass.success === false) {
                    showClassError(
                            "form",
                            gymClass.message || "No se pudo cargar la clase."
                            );
                    return;
                }

                document.getElementById("formTitle").innerText =
                        "Editar clase";

                setInputValue("idClass", gymClass.idClass);
                setInputValue("classType", gymClass.classType || "");
                setInputValue("classDate", normalizeDate(gymClass.classDate));
                setInputValue("startTime", normalizeTime(gymClass.startTime));
                setInputValue("endTime", normalizeTime(gymClass.endTime));
                setInputValue("maxCapacity", gymClass.maxCapacity || "");
                setInputValue("enrolledCount", gymClass.enrolledCount || 0);
                setInputValue("description", gymClass.description || "");

                setInputValue("branchId", gymClass.branchId || "");
                setSelectValue("difficultyLevel", gymClass.difficultyLevel);

                loadTrainers().then(() => {
                    setInputValue("trainerId", gymClass.trainerId || "");
                });
            })
            .catch(() => {
                showClassError(
                        "form",
                        "No se pudo cargar la clase."
                        );
            });
}

function setInputValue(id, value) {

    const element = document.getElementById(id);

    if (element) {
        element.value = value;
    }
}

function normalizeDate(value) {
    return value ? String(value).substring(0, 10) : "";
}

function normalizeTime(value) {
    return value ? String(value).substring(0, 5) : "";
}

function setSelectValue(selectId, value) {

    const select = document.getElementById(selectId);

    if (!select) {
        return;
    }

    if (value === null || value === undefined || value === "") {
        select.value = "";
        return;
    }

    const exists = Array.from(select.options)
            .some(option => option.value === value);

    if (!exists) {
        select.add(new Option(value, value));
    }

    select.value = value;
}

function saveClass() {

    if (classSaving) {
        return;
    }

    clearClassErrors();

    const classType = document.getElementById("classType").value.trim();
    const classDate = document.getElementById("classDate").value;
    const startTime = document.getElementById("startTime").value;
    const endTime = document.getElementById("endTime").value;
    const maxCapacity = document.getElementById("maxCapacity").value;
    const trainerId = document.getElementById("trainerId").value;
    const branchId = document.getElementById("branchId").value;
    const enrolledCount = document.getElementById("enrolledCount").value;
    const difficultyLevel = document.getElementById("difficultyLevel").value;
    const description = document.getElementById("description").value.trim();

    const clientErrors = {};

    if (classType === "") {
        clientErrors.classType = "Este campo es obligatorio.";
    }

    if (classDate === "") {
        clientErrors.classDate = "La fecha es obligatoria.";
    }

    if (startTime === "") {
        clientErrors.startTime = "Este campo es obligatorio.";
    }

    if (endTime === "") {
        clientErrors.endTime = "Este campo es obligatorio.";
    }

    if (maxCapacity === "") {
        clientErrors.maxCapacity = "Este campo es obligatorio.";
    }

    if (trainerId === "") {
        clientErrors.trainerId = "Seleccione una opción.";
    }

    if (difficultyLevel === "") {
        clientErrors.difficultyLevel = "Seleccione una opción.";
    }

    if (branchId === "") {
        clientErrors.branchId = "Seleccione una sucursal.";
    }

    if (description === "") {
        clientErrors.description = "Este campo es obligatorio.";
    }

    if (Object.keys(clientErrors).length > 0) {
        showClassErrors(clientErrors);
        showClassError("form", "Revise los campos marcados.");
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
        branch: branchId === "" ? null : {
            id: parseInt(branchId)
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

    classSaving = true;

    showAdminLoading("Guardando...");

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
                    showClassErrors(data.fieldErrors || data.errors || {});
                    showClassError(
                            "form",
                            data.message || "Revise los campos marcados."
                            );
                    classSaving = false;
                    return;
                }

                showAdminSuccess(
                        idClass !== ""
                        ? "Actualizado."
                        : "Guardado."
                        ).then(() => {
                    window.location.href = getClassesListPath();
                });
            })
            .catch(() => {
                classSaving = false;
                showAdminError("No se pudo guardar.", "Revise los datos.");
                showClassError("form", "No se pudo guardar.");
            });
}

function clearClassErrors() {

    document.querySelectorAll(".field-error, .error-message")
            .forEach(error => {
                error.textContent = "";
            });

    document.querySelectorAll(".invalid")
            .forEach(field => {
                field.classList.remove("invalid");
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

        const fieldElement = document.getElementById(field);

        if (fieldElement) {
            fieldElement.classList.add("invalid");
        }

        return;
    }

    showAdminError(message);
}