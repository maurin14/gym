const attendanceBasePath = window.attendanceBasePath ||
        (window.location.pathname.startsWith("/admin/attendances")
                ? "/admin/attendances"
                : "/trainer/attendances");

let attendanceSaving = false;

document.addEventListener("DOMContentLoaded", function () {

    const idAttendance = getAttendanceIdFromPath();

    if (idAttendance) {
        loadAttendanceForEdit(idAttendance);
    }

    Promise.all([
        loadClients(),
        loadClasses()
    ]).then(() => {
        if (idAttendance) {
            loadAttendanceForEdit(idAttendance);
        }
    });

});

function loadClients() {

    return fetch("/attendances/clients")
            .then(response => response.json())
            .then(data => {

                const clientSelect =
                        document.getElementById("clientId");

                clientSelect.innerHTML =
                        '<option value="">Seleccione un cliente</option>';

                data
                        .filter(client =>
                            client.status &&
                                    client.status.toLowerCase() === "active"
                        )
                        .forEach(client => {

                            clientSelect.innerHTML += `
                        <option value="${client.userId}">
                            ${client.fullName}
                        </option>
                    `;
                        });
            })
            .catch(() => {
                showAdminError(
                        "Error",
                        "No se pudieron cargar los clientes."
                );
            });
}

function loadClasses() {

    return fetch("/classes")
            .then(response => response.json())
            .then(data => {

                const classSelect =
                        document.getElementById("classId");

                classSelect.innerHTML =
                        '<option value="">Seleccione una clase</option>';

                data.forEach(gymClass => {

                    classSelect.innerHTML += `
                    <option value="${gymClass.idClass}"
                            data-date="${gymClass.classDate}">
                        ${gymClass.classType}
                        - ${gymClass.branchName || "Sin sucursal"}
                        - ${gymClass.classDate}
                    </option>
                `;
                });
            })
            .catch(() => {
                showAdminError(
                        "Error",
                        "No se pudieron cargar las clases."
                );
            });
}

function loadAttendanceForEdit(idAttendance) {

    fetch("/attendances/edit/" + idAttendance)
            .then(response => {

                if (!response.ok) {
                    throw new Error();
                }

                return response.json();
            })
            .then(attendance => {

                document.getElementById("idAttendance").value =
                        attendance.idAttendance || "";

                const title =
                        document.getElementById("formTitle");

                if (title) {
                    title.textContent =
                            "Editar asistencia";
                }

                setInputValue("clientId", attendance.clientId || "");
                setInputValue("classId", attendance.classId || "");
                setInputValue("attendanceDate", attendance.attendanceDate || "");
                setInputValue("attendanceStatus", attendance.attendanceStatus || "Presente");
                setInputValue("observation", attendance.observation || "");

            })
            .catch(() => {
                showAdminError(
                        "Error",
                        "No se pudo cargar la asistencia."
                );
            });
}

function setInputValue(id, value) {

    const element =
            document.getElementById(id);

    if (element) {
        element.value = value;
    }
}

function getAttendanceIdFromPath() {

    const match =
            window.location.pathname.match(/\/form\/(\d+)$/);

    return match ? match[1] : "";
}

function setAttendanceDate() {

    const classSelect =
            document.getElementById("classId");

    const selectedOption =
            classSelect.options[classSelect.selectedIndex];

    if (selectedOption) {

        const date =
                selectedOption.getAttribute("data-date");

        if (date) {
            document.getElementById("attendanceDate").value =
                    date;
        }
    }
}

function saveAttendance() {

    if (attendanceSaving) {
        return;
    }

    clearAttendanceErrors();

    const idAttendance =
            document.getElementById("idAttendance").value;

    const clientId =
            document.getElementById("clientId").value;

    const classId =
            document.getElementById("classId").value;

    const attendanceDate =
            document.getElementById("attendanceDate").value;

    const attendanceStatus =
            document.getElementById("attendanceStatus").value;

    const observation =
            document.getElementById("observation").value.trim();

    let hasErrors = false;

    if (clientId === "") {
        showAttendanceError("clientId", "Seleccione un cliente.");
        hasErrors = true;
    }

    if (classId === "") {
        showAttendanceError("classId", "Seleccione una clase.");
        hasErrors = true;
    }

    if (attendanceDate === "") {
        showAttendanceError("attendanceDate", "La fecha es obligatoria.");
        hasErrors = true;
    }

    if (attendanceStatus === "") {
        showAttendanceError("attendanceStatus", "Seleccione un estado.");
        hasErrors = true;
    }

    if (hasErrors) {
        showAdminError(
                "Datos incompletos",
                "Debe completar los campos obligatorios."
        );
        return;
    }

    const attendance = {

        client: {
            userId: parseInt(clientId)
        },

        gymClass: {
            idClass: parseInt(classId)
        },

        attendanceDate: attendanceDate,
        attendanceStatus: attendanceStatus,
        observation: observation || ""
    };

    const url = idAttendance !== ""
            ? "/attendances/" + idAttendance
            : "/attendances";

    const method = idAttendance !== ""
            ? "PUT"
            : "POST";

    attendanceSaving = true;

    showAdminLoading("Guardando asistencia...");

    fetch(url, {

        method: method,

        headers: {
            "Content-Type": "application/json"
        },

        body: JSON.stringify(attendance)

    })
            .then(response => {

                if (!response.ok) {
                    throw new Error();
                }

                return response.json();
            })
            .then(() => {

                showAdminSuccess(
                        idAttendance !== ""
                        ? "Asistencia actualizada correctamente."
                        : "Asistencia registrada correctamente."
                )
                        .then(() => {
                            window.location.href =
                                    attendanceBasePath;
                        });
            })
            .catch(() => {

                attendanceSaving = false;

                showAdminError(
                        "Error",
                        "No se pudo guardar la asistencia."
                );
            });
}

function clearAttendanceErrors() {

    document.querySelectorAll(".field-error")
            .forEach(error => {
                error.textContent = "";
            });

    document.querySelectorAll(".invalid")
            .forEach(field => {
                field.classList.remove("invalid");
            });
}

function showAttendanceError(field, message) {

    const error =
            document.getElementById(field + "Error");

    const fieldElement =
            document.getElementById(field);

    if (error) {
        error.textContent = message;
    }

    if (fieldElement) {
        fieldElement.classList.add("invalid");
    }
}