const attendanceBasePath = window.location.pathname.startsWith("/admin/attendance")
        ? "/admin/attendance"
        : "/trainer/attendances";

document.addEventListener("DOMContentLoaded", function () {
    Promise.all([
        loadClients(),
        loadClasses()
    ]).then(() => {
        loadAttendanceForEdit();
    });
});

function loadClients() {
    return fetch("/attendances/clients")
        .then(response => response.json())
        .then(data => {
            const clientSelect = document.getElementById("clientId");

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
                    <option value="${gymClass.idClass}"
                            data-date="${gymClass.classDate}">
                        ${gymClass.classType} - ${gymClass.classDate}
                    </option>
                `;
            });
        });
}

function loadAttendanceForEdit() {
    const idAttendance = getAttendanceIdFromPath();

    if (idAttendance === "") {
        return;
    }

    fetch("/attendances/edit/" + idAttendance)
        .then(response => response.json())
        .then(attendance => {
            document.getElementById("idAttendance").value =
                    attendance.idAttendance;

            document.getElementById("formTitle").textContent =
                    "Editar Asistencia";

            document.getElementById("clientId").value =
                    attendance.clientId;

            document.getElementById("classId").value =
                    attendance.classId;

            document.getElementById("attendanceDate").value =
                    attendance.attendanceDate || "";

            document.getElementById("attendanceStatus").value =
                    attendance.attendanceStatus || "Presente";

            document.getElementById("observation").value =
                    attendance.observation || "";
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
        const date = selectedOption.getAttribute("data-date");

        if (date) {
            document.getElementById("attendanceDate").value = date;
        }
    }
}

function saveAttendance() {

    clearErrors();

    const idAttendance = document.getElementById("idAttendance").value;
    const clientId = document.getElementById("clientId").value;
    const classId = document.getElementById("classId").value;
    const attendanceDate = document.getElementById("attendanceDate").value;
    const attendanceStatus = document.getElementById("attendanceStatus").value;
    const observation = document.getElementById("observation").value.trim();

    let valid = true;

    if (clientId === "") {
        document.getElementById("clientError").innerText =
                "Seleccione un cliente";
        valid = false;
    }

    if (classId === "") {
        document.getElementById("classError").innerText =
                "Seleccione una clase";
        valid = false;
    }

    if (attendanceDate === "") {
        document.getElementById("dateError").innerText =
                "Seleccione una fecha";
        valid = false;
    }

    if (attendanceStatus === "") {
        document.getElementById("statusError").innerText =
                "Seleccione un estado";
        valid = false;
    }

    if (!valid) {
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

    let url = "/attendances";
    let method = "POST";

    if (idAttendance !== "") {
        url = "/attendances/" + idAttendance;
        method = "PUT";
    }

    fetch(url, {
        method: method,
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(attendance)
    }).then(() => {
        window.location.href = attendanceBasePath;
    });
}

function clearErrors() {
    document.getElementById("clientError").innerText = "";
    document.getElementById("classError").innerText = "";
    document.getElementById("dateError").innerText = "";
    document.getElementById("statusError").innerText = "";
}