document.addEventListener("DOMContentLoaded", function () {
    loadClients();
    loadClasses();

    const parts = window.location.pathname.split("/");
    const idAttendance = parts[parts.length - 1];

    if (!isNaN(idAttendance)) {
        loadAttendance(idAttendance);
    }
});

function loadClients() {
    fetch("/attendances/clients")
        .then(response => response.json())
        .then(data => {
            const clientSelect = document.getElementById("clientId");
            clientSelect.innerHTML = '<option value="">Seleccione un cliente</option>';

            data.forEach(client => {
                clientSelect.innerHTML += `
                    <option value="${client.userId}">
                        ${client.fullName}
                    </option>
                `;
            });
        });
}

function loadClasses() {
    fetch("/classes")
        .then(response => response.json())
        .then(data => {
            const classSelect = document.getElementById("classId");
            classSelect.innerHTML = '<option value="">Seleccione una clase</option>';

            data.forEach(gymClass => {
                classSelect.innerHTML += `
                    <option value="${gymClass.idClass}">
                        ${gymClass.classType} - ${gymClass.classDate}
                    </option>
                `;
            });
        });
}

function loadAttendance(idAttendance) {
    fetch("/attendances/" + idAttendance)
        .then(response => response.json())
        .then(attendance => {
            document.getElementById("formTitle").innerText = "Modificar Asistencia";
            document.getElementById("idAttendance").value = attendance.idAttendance;
            document.getElementById("clientId").value = attendance.client.userId;
            document.getElementById("classId").value = attendance.gymClass.idClass;
            document.getElementById("attendanceDate").value = attendance.attendanceDate;
            document.getElementById("attendanceStatus").value = attendance.attendanceStatus;
            document.getElementById("observation").value = attendance.observation;
        });
}

function saveAttendance() {
    const idAttendance = document.getElementById("idAttendance").value;

    const attendance = {
        client: {
            userId: document.getElementById("clientId").value
        },
        gymClass: {
            idClass: document.getElementById("classId").value
        },
        attendanceDate: document.getElementById("attendanceDate").value,
        attendanceStatus: document.getElementById("attendanceStatus").value,
        observation: document.getElementById("observation").value
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
        alert("Asistencia guardada correctamente");
        window.location.href = "/trainer/attendances";
    });
}