const attendanceBasePath = window.attendanceBasePath || (window.location.pathname.startsWith("/admin/attendance")
        ? "/admin/attendance"
        : "/trainer/attendances");

let attendanceSaving = false;

document.addEventListener("DOMContentLoaded", function () {
    Promise.all([loadClients(), loadClasses()]).then(loadAttendanceForEdit);
});

function loadClients() {
    return fetch("/attendances/clients")
        .then(response => response.json())
        .then(data => {
            const clientSelect = document.getElementById("clientId");

            clientSelect.innerHTML =
                    '<option value="">Seleccione un cliente</option>';

            data
                .filter(client => client.status === "active")
                .forEach(client => {
                    clientSelect.innerHTML += `
                        <option value="${client.userId}">
                            ${client.fullName}
                        </option>
                    `;
                });
        });
}

function loadClasses() {
    return fetch("/classes")
        .then(response => response.json())
        .then(data => {
            const classSelect = document.getElementById("classId");

            classSelect.innerHTML =
                    '<option value="">Seleccione una clase</option>';

            data.forEach(gymClass => {
                classSelect.innerHTML += `
                    <option value="${gymClass.idClass}" data-date="${gymClass.classDate}">
                        ${gymClass.classType} - ${gymClass.branchName || "Sin sucursal"} - ${gymClass.classDate}
                    </option>
                `;
            });
        });
}

function loadAttendanceForEdit() {
    const idAttendance = getAttendanceIdFromPath();

    if (!idAttendance) {
        return;
    }

    fetch("/attendances/" + idAttendance)
        .then(response => response.json())
        .then(attendance => {
            document.getElementById("idAttendance").value = attendance.idAttendance || "";

            const title = document.getElementById("formTitle");
            if (title) {
                title.textContent = "Editar asistencia";
            }

            if (attendance.client) {
                document.getElementById("clientId").value = attendance.client.userId;
            }

            if (attendance.gymClass) {
                document.getElementById("classId").value = attendance.gymClass.idClass;
            }

            document.getElementById("attendanceDate").value = attendance.attendanceDate || "";
            document.getElementById("attendanceStatus").value = attendance.attendanceStatus || "Presente";
            document.getElementById("observation").value = attendance.observation || "";
        });
}

function getAttendanceIdFromPath() {
    const match = window.location.pathname.match(/\/form\/(\d+)$/);
    return match ? match[1] : "";
}

function setAttendanceDate() {
    const classSelect = document.getElementById("classId");
    const selectedOption = classSelect.options[classSelect.selectedIndex];

    if (selectedOption) {
        document.getElementById("attendanceDate").value =
                selectedOption.getAttribute("data-date");
    }
}

function saveAttendance() {
    if (attendanceSaving) {
        return;
    }

    const idAttendance = document.getElementById("idAttendance").value;
    const clientId = document.getElementById("clientId").value;
    const classId = document.getElementById("classId").value;
    const attendanceDate = document.getElementById("attendanceDate").value;
    const attendanceStatus = document.getElementById("attendanceStatus").value;
    const observation = document.getElementById("observation").value.trim();

    if (clientId === "" || classId === "" || attendanceDate === "" || attendanceStatus === "") {
        clearAttendanceErrors();
        if (clientId === "") showAttendanceError("clientId", "Seleccione una opcion.");
        if (classId === "") showAttendanceError("classId", "Seleccione una opcion.");
        if (attendanceDate === "") showAttendanceError("attendanceDate", "La fecha es obligatoria.");
        if (attendanceStatus === "") showAttendanceError("attendanceStatus", "Seleccione una opcion.");
        showAdminError("Revise los datos.");
        return;
    }

    const attendance = {
        client: {
            userId: clientId
        },
        gymClass: {
            idClass: classId
        },
        attendanceDate: attendanceDate,
        attendanceStatus: attendanceStatus,
        observation: observation
    };

    const url = idAttendance ? "/attendances/" + idAttendance : "/attendances";
    const method = idAttendance ? "PUT" : "POST";

    attendanceSaving = true;

    showAdminLoading("Guardando...");

    fetch(url, {
        method: method,
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(attendance)
    }).then(response => {
        if (!response.ok) {
            throw new Error("No se pudo guardar la asistencia.");
        }

        showAdminSuccess(idAttendance ? "Actualizado." : "Guardado.").then(() => {
            window.location.href = attendanceBasePath;
        });
    }).catch(error => {
        attendanceSaving = false;
        showAdminError("No se pudo guardar.", error.message || "Revise los datos.");
    });
}

function clearAttendanceErrors() {
    document.querySelectorAll(".field-error").forEach(error => {
        error.textContent = "";
    });
    document.querySelectorAll(".invalid").forEach(field => {
        field.classList.remove("invalid");
    });
}

function showAttendanceError(field, message) {
    const error = document.getElementById(field + "Error");
    const fieldElement = document.getElementById(field);

    if (error) {
        error.textContent = message;
    }

    if (fieldElement) {
        fieldElement.classList.add("invalid");
    }
}
