let classSaving = false;

const i18n = window.i18n || {
    requiredField: "This field is required.",
    selectOption: "Select an option.",
    selectBranch: "Select a branch.",
    selectTrainer: "Select a trainer.",
    reviewFields: "Please review the highlighted fields.",
    saved: "Saved.",
    updated: "Updated.",
    saving: "Saving...",
    saveError: "Could not save.",
    formTitleAdd: "Add Class",
    formTitleEdit: "Edit Class",
    confirmButton: /*[[#{confirm.button}]]*/ "Accept",
    
};

document.addEventListener("DOMContentLoaded", function () {
    const idClass = getClassIdFromPath();

    loadBranches().then(() => {
        const branchSelect = document.getElementById("branchId");
        if (branchSelect) branchSelect.addEventListener("change", loadTrainers);

        if (idClass !== null) {
            loadClass(idClass);
        } else {
            loadTrainers();
        }
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
    const branchId = document.getElementById("branchId")?.value || "";
    const url = branchId ? "/classes/trainers?branchId=" + encodeURIComponent(branchId) : "/classes/trainers";

    return fetch(url)
        .then(response => response.json())
        .then(data => {
            const trainerSelect = document.getElementById("trainerId");
            trainerSelect.innerHTML = `<option value="">${i18n.selectTrainer}</option>`;
            data.forEach(trainer => {
                trainerSelect.innerHTML += `<option value="${trainer.userId}">${trainer.fullName}</option>`;
            });
        });
}

function loadBranches() {
    return fetch("/classes/branches")
        .then(response => response.json())
        .then(data => {
            const branchSelect = document.getElementById("branchId");
            branchSelect.innerHTML = `<option value="">${i18n.selectBranch}</option>`;
            data.forEach(branch => {
                branchSelect.innerHTML += `<option value="${branch.id}">${branch.name}</option>`;
            });
        });
}

function loadClass(idClass) {
    fetch("/classes/" + idClass)
        .then(response => response.json())
        .then(gymClass => {
            if (!gymClass || gymClass.success === false) {
                showClassError("form", gymClass?.message || i18n.reviewFields);
                return;
            }

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

            const formTitle = document.getElementById("formTitle");
            if (formTitle) formTitle.innerText = i18n.formTitleEdit;

            loadTrainers().then(() => {
                setInputValue("trainerId", gymClass.trainerId || "");
            });
        })
        .catch(() => {
            showClassError("form", i18n.reviewFields);
        });
}

function setInputValue(id, value) {
    const el = document.getElementById(id);
    if (el) el.value = value;
}

function normalizeDate(value) {
    return value ? String(value).substring(0, 10) : "";
}

function normalizeTime(value) {
    return value ? String(value).substring(0, 5) : "";
}

function setSelectValue(selectId, value) {
    const select = document.getElementById(selectId);
    if (!select) return;
    if (!value) { select.value = ""; return; }
    if (!Array.from(select.options).some(o => o.value === value)) select.add(new Option(value, value));
    select.value = value;
}

function saveClass() {
    if (classSaving) return;
    clearClassErrors();

    const idClass = document.getElementById("idClass").value;
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
    if (!classType) clientErrors.classType = i18n.requiredField;
    if (!classDate) clientErrors.classDate = i18n.requiredField;
    if (!startTime) clientErrors.startTime = i18n.requiredField;
    if (!endTime) clientErrors.endTime = i18n.requiredField;
    if (!maxCapacity) clientErrors.maxCapacity = i18n.requiredField;
    if (!trainerId) clientErrors.trainerId = i18n.selectTrainer;
    if (!branchId) clientErrors.branchId = i18n.selectBranch;
    if (!difficultyLevel) clientErrors.difficultyLevel = i18n.selectOption;
    if (!description) clientErrors.description = i18n.requiredField;

    if (Object.keys(clientErrors).length > 0) {
        showClassErrors(clientErrors);
        showClassError("form", i18n.reviewFields);
        return;
    }

    const gymClass = {
        classType,
        classDate,
        startTime,
        endTime,
        maxCapacity: parseInt(maxCapacity),
        trainer: trainerId === "" ? null : { userId: parseInt(trainerId) },
        branch: branchId === "" ? null : { id: parseInt(branchId) },
        enrolledCount: enrolledCount === "" ? 0 : parseInt(enrolledCount),
        difficultyLevel,
        description
    };

    const url = idClass !== "" ? "/classes/" + idClass : "/classes";
    const method = idClass !== "" ? "PUT" : "POST";

    classSaving = true;
    showAdminLoading(i18n.saving || "Saving...");

    fetch(url, {
        method,
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(gymClass)
    })
        .then(response => response.json())
        .then(data => {
            classSaving = false;
            if (!data.success) {
                showClassErrors(data.fieldErrors || data.errors || {});
                showClassError("form", data.message || i18n.reviewFields);
                return;
            }
                Swal.fire({
                    icon: 'success',
                    title: idClass !== "" ? i18n.updated : i18n.saved, 
                    showConfirmButton: true, 
                    confirmButtonText: i18n.confirmButton 
                }).then(() => {
                    window.location.href = getClassesListPath(); 
                });
        })
        .catch(() => {
            classSaving = false;
            showAdminError(i18n.saveError, i18n.reviewFields);
            showClassError("form", i18n.saveError);
        });
}

function clearClassErrors() {
    document.querySelectorAll(".field-error, .error-message").forEach(e => e.textContent = "");
    document.querySelectorAll(".invalid").forEach(f => f.classList.remove("invalid"));
}

function showClassErrors(errors) {
    Object.keys(errors).forEach(field => showClassError(field, errors[field]));
}

function showClassError(field, message) {
    const error = document.getElementById(field + "Error");
    if (error) {
        error.textContent = message;
        const fieldElement = document.getElementById(field);
        if (fieldElement) fieldElement.classList.add("invalid");
        return;
    }
    showAdminError(message);
}
