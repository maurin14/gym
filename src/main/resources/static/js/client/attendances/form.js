document.addEventListener("DOMContentLoaded", function () {
    loadClasses();
});

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

            const params = new URLSearchParams(window.location.search);
            const classId = params.get("classId");

            if (classId !== null) {
                classSelect.value = classId;
                setAttendanceDate();
            }
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

function saveClientAttendance() {

    const classId = document.getElementById("classId").value;
    const attendanceDate = document.getElementById("attendanceDate").value;
    const observation = document.getElementById("observation").value.trim();

    if (classId === "") {
        Swal.fire({
            icon: "warning",
            title: "Seleccione una clase",
            text: "Debe seleccionar una clase para registrar la asistencia.",
            confirmButtonColor: "#d97818"
        });
        return;
    }

    if (attendanceDate === "") {
        Swal.fire({
            icon: "warning",
            title: "Fecha requerida",
            text: "La fecha de asistencia no puede estar vacia.",
            confirmButtonColor: "#d97818"
        });
        return;
    }

    const attendance = {
        gymClass: {
            idClass: classId
        },
        attendanceDate: attendanceDate,
        attendanceStatus: "Presente",
        observation: observation
    };

    fetch("/attendances", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(attendance)
    }).then(() => {
        Swal.fire({
            icon: "success",
            title: "Asistencia registrada",
            text: "Tu asistencia se registro correctamente.",
            confirmButtonColor: "#d97818"
        }).then(() => {
            window.location.href = "/client/attendances";
        });
    });
}
