const attendanceBasePath = window.attendanceBasePath ||
        (window.location.pathname.startsWith("/admin/attendance")
                ? "/admin/attendance"
                : "/trainer/attendances");

const i18n = window.i18n || {
    selectClient: "Select a client",
    selectClass: "Select a class",
    noBranch: "No branch",
    errorTitle: "Error",
    clientsLoadError: "Could not load clients.",
    classesLoadError: "Could not load classes.",
    loadError: "Could not load attendance.",
    formTitleEdit: "Edit Attendance",
    clientRequired: "Select a client.",
    classRequired: "Select a class.",
    dateRequired: "The date is required.",
    statusRequired: "Select a status.",
    incompleteTitle: "Incomplete data",
    incompleteText: "Complete the required fields.",
    saving: "Saving attendance...",
    saved: "Attendance saved successfully.",
    updated: "Attendance updated successfully.",
    saveError: "Could not save attendance."
};

let attendanceSaving = false;

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
            .then(response => response.json())
            .then(data => {

                const clientSelect =
                        document.getElementById("clientId");

                clientSelect.innerHTML =
                        `<option value="">${i18n.selectClient}</option>`;

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
                        i18n.errorTitle,
                        i18n.clientsLoadError
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
                        `<option value="">${i18n.selectClass}</option>`;

                data.forEach(gymClass => {

                    classSelect.innerHTML += `
                    <option value="${gymClass.idClass}"
                            data-date="${gymClass.classDate}">
                        ${gymClass.classType}
                        - ${gymClass.branchName || i18n.noBranch}
                        - ${gymClass.classDate}
                    </option>
                `;
                });
            })
            .catch(() => {
                showAdminError(
                        i18n.errorTitle,
                        i18n.classesLoadError
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
                            i18n.formTitleEdit;
                }

                setInputValue("clientId", attendance.clientId || "");
                setInputValue("classId", attendance.classId || "");
                setInputValue("attendanceDate", attendance.attendanceDate || "");
                setInputValue("attendanceStatus", attendance.attendanceStatus || "Presente");
                setInputValue("observation", attendance.observation || "");

            })
            .catch(() => {
                showAdminError(
                        i18n.errorTitle,
                        i18n.loadError
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
        showAttendanceError("clientId", i18n.clientRequired);
        hasErrors = true;
    }

    if (classId === "") {
        showAttendanceError("classId", i18n.classRequired);
        hasErrors = true;
    }

    if (attendanceDate === "") {
        showAttendanceError("attendanceDate", i18n.dateRequired);
        hasErrors = true;
    }

    if (attendanceStatus === "") {
        showAttendanceError("attendanceStatus", i18n.statusRequired);
        hasErrors = true;
    }

    if (hasErrors) {
        showAdminError(
                i18n.incompleteTitle,
                i18n.incompleteText
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

    showAdminLoading(i18n.saving);

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
                        ? i18n.updated
                        : i18n.saved
                )
                        .then(() => {
                            window.location.href =
                                    attendanceBasePath;
                        });
            })
            .catch(() => {

                attendanceSaving = false;

                showAdminError(
                        i18n.errorTitle,
                        i18n.saveError
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
