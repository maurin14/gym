document.addEventListener("DOMContentLoaded", function () {
    loadClasses();

    const params = new URLSearchParams(window.location.search);
    const classId = params.get("classId");

    if (classId !== null) {
        setTimeout(() => {
            document.getElementById("classId").value = classId;
        }, 500);
    }
});

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

function saveClientAttendance() {
    const attendance = {
        client: {
            userId: 4
        },
        gymClass: {
            idClass: document.getElementById("classId").value
        },
        attendanceDate: document.getElementById("attendanceDate").value,
        attendanceStatus: "Presente",
        observation: document.getElementById("observation").value
    };

    fetch("/attendances", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(attendance)
    }).then(() => {
        alert("Asistencia registrada correctamente");
        window.location.href = "/client/attendances";
    });
}