const attendanceBasePath = window.attendanceBasePath ||
        (window.location.pathname.startsWith("/admin/attendances")
                ? "/admin/attendances"
                : "/trainer/attendances");

let attendanceSaving = false;

// i18n dinámico
const i18n = window.i18n || {
    selectClient: "Select a client",
    selectClass: "Select a class",
    attendanceRequired: "This field is required",
    statusRequired: "Select a valid option",
    incompleteData: "Please complete the required fields",
    savingAttendance: "Saving attendance...",
    attendanceSaved: "Attendance saved successfully",
    attendanceUpdated: "Attendance updated successfully",
    loadClientsError: "Could not load clients.",
    loadClassesError: "Could not load classes.",
    loadAttendanceError: "Could not load attendance.",
    saveError: "Could not save attendance"
};

document.addEventListener("DOMContentLoaded", function () {
    Promise.all([
        loadClients(),
        loadClasses()
    ]).then(() => {
        const idAttendance = getAttendanceIdFromPath();
        if (idAttendance) {
            loadAttendanceForEdit(idAttendance);
        }
    });
});

function loadClients() {
    return fetch("/attendances/clients")
        .then(res => res.json())
        .then(data => {
            const clientSelect = document.getElementById("clientId");
            clientSelect.innerHTML = `<option value="">${i18n.selectClient}</option>`;
            data.filter(c => c.status && c.status.toLowerCase() === "active")
                .forEach(c => {
                    clientSelect.innerHTML += `<option value="${c.userId}">${c.fullName}</option>`;
                });
        })
        .catch(() => showAdminError("Error", i18n.loadClientsError));
}

function loadClasses() {
    return fetch("/classes")
        .then(res => res.json())
        .then(data => {
            const classSelect = document.getElementById("classId");
            classSelect.innerHTML = `<option value="">${i18n.selectClass}</option>`;
            data.forEach(c => {
                classSelect.innerHTML += `
                    <option value="${c.idClass}" data-date="${c.classDate}">
                        ${c.classType} - ${c.branchName || ""}
                        - ${c.classDate}
                    </option>
                `;
            });
        })
        .catch(() => showAdminError("Error", i18n.loadClassesError));
}

function loadAttendanceForEdit(idAttendance) {
    fetch("/attendances/edit/" + idAttendance)
        .then(res => {
            if (!res.ok) throw new Error();
            return res.json();
        })
        .then(att => {
            document.getElementById("idAttendance").value = att.idAttendance || "";
            const title = document.getElementById("formTitle");
            if (title) title.textContent = i18n.attendanceUpdated;
            setInputValue("clientId", att.clientId || "");
            setInputValue("classId", att.classId || "");
            setInputValue("attendanceDate", att.attendanceDate || "");
            setInputValue("attendanceStatus", att.attendanceStatus || "Presente");
            setInputValue("observation", att.observation || "");
        })
        .catch(() => showAdminError("Error", i18n.loadAttendanceError));
}

function setInputValue(id, value) {
    const el = document.getElementById(id);
    if (el) el.value = value;
}

function getAttendanceIdFromPath() {
    const match = window.location.pathname.match(/\/form\/(\d+)$/);
    return match ? match[1] : "";
}

function setAttendanceDate() {
    const classSelect = document.getElementById("classId");
    const selected = classSelect.options[classSelect.selectedIndex];
    if (selected) {
        const date = selected.getAttribute("data-date");
        if (date) document.getElementById("attendanceDate").value = date;
    }
}

function saveAttendance() {
    if (attendanceSaving) return;

    clearAttendanceErrors();

    const idAttendance = document.getElementById("idAttendance").value;
    const clientId = document.getElementById("clientId").value;
    const classId = document.getElementById("classId").value;
    const attendanceDate = document.getElementById("attendanceDate").value;
    const attendanceStatus = document.getElementById("attendanceStatus").value;
    const observation = document.getElementById("observation").value.trim();

    let hasErrors = false;
    if (clientId === "") { showAttendanceError("clientId", i18n.selectClient); hasErrors = true; }
    if (classId === "") { showAttendanceError("classId", i18n.selectClass); hasErrors = true; }
    if (attendanceDate === "") { showAttendanceError("attendanceDate", i18n.attendanceRequired); hasErrors = true; }
    if (attendanceStatus === "") { showAttendanceError("attendanceStatus", i18n.statusRequired); hasErrors = true; }

    if (hasErrors) {
        showAdminError(i18n.incompleteData, i18n.incompleteData);
        return;
    }

    const attendance = {
        client: { userId: parseInt(clientId) },
        gymClass: { idClass: parseInt(classId) },
        attendanceDate,
        attendanceStatus,
        observation
    };

    const url = idAttendance !== "" ? "/attendances/" + idAttendance : "/attendances";
    const method = idAttendance !== "" ? "PUT" : "POST";

    attendanceSaving = true;
    showAdminLoading(i18n.savingAttendance);

    fetch(url, {
        method,
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(attendance)
    })
    .then(res => {
        if (!res.ok) throw new Error();
        return res.json();
    })
    .then(() => {
        showAdminSuccess(
            idAttendance !== "" ? i18n.attendanceUpdated : i18n.attendanceSaved
        ).then(() => window.location.href = attendanceBasePath);
    })
    .catch(() => {
        attendanceSaving = false;
        showAdminError("Error", i18n.saveError);
    });
}

function clearAttendanceErrors() {
    document.querySelectorAll(".field-error").forEach(e => e.textContent = "");
    document.querySelectorAll(".invalid").forEach(f => f.classList.remove("invalid"));
}

function showAttendanceError(field, message) {
    const error = document.getElementById(field + "Error");
    const fieldEl = document.getElementById(field);
    if (error) error.textContent = message;
    if (fieldEl) fieldEl.classList.add("invalid");
}