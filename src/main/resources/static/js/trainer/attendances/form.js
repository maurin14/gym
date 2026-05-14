document.addEventListener("DOMContentLoaded", function () {
    loadClients();
    loadClasses();
});

function loadClients() {

    fetch("/attendances/clients")
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

    fetch("/classes")
        .then(response => response.json())
        .then(data => {

            const classSelect = document.getElementById("classId");

            classSelect.innerHTML =
                    '<option value="">Seleccione una clase</option>';

            data.forEach(gymClass => {

                classSelect.innerHTML += `
                    <option value="${gymClass.idClass}" data-date="${gymClass.classDate}">
                        ${gymClass.classType} - ${gymClass.classDate}
                    </option>
                `;
            });
        });
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

    const clientId = document.getElementById("clientId").value;
    const classId = document.getElementById("classId").value;
    const attendanceDate = document.getElementById("attendanceDate").value;
    const attendanceStatus = document.getElementById("attendanceStatus").value;
    const observation = document.getElementById("observation").value.trim();

    if (clientId === "" || classId === "" || attendanceDate === "" || attendanceStatus === "") {
        alert("Debe completar todos los campos obligatorios.");
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

    fetch("/attendances", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(attendance)
    }).then(() => {
        alert("Asistencia guardada correctamente");
        window.location.href = "/trainer/attendances";
    });
}