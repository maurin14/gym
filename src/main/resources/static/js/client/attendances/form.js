document.addEventListener("DOMContentLoaded", function () {
    loadClasses();
});

let clientAttendanceSaving = false;

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
    if (clientAttendanceSaving) {
        return;
    }

    const classId = document.getElementById("classId").value;
    const attendanceDate = document.getElementById("attendanceDate").value;
    const observation = document.getElementById("observation").value.trim();
    clearClientAttendanceErrors();

    if (classId === "") {
        showClientAttendanceError("classId", "Seleccione una clase.");
        showSystemError("Revise los datos.");
        return;
    }

    if (attendanceDate === "") {
        showClientAttendanceError("attendanceDate", "La fecha es obligatoria.");
        showSystemError("Revise los datos.");
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

    confirmSystemAction({
        title: "Guardar cambios?",
        confirmText: "Guardar"
    }).then(result => {
        if (!result.isConfirmed || clientAttendanceSaving) {
            return;
        }

        clientAttendanceSaving = true;
        showSystemLoading("Procesando...");

        fetch("/attendances", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(attendance)
        }).then(response => {
            if (!response.ok) {
                throw new Error("No se pudo guardar.");
            }

            showSystemSuccess("Guardado.").then(() => {
                window.location.href = "/client/attendances";
            });
        }).catch(error => {
            clientAttendanceSaving = false;
            showSystemError(error.message || "No se pudo guardar.");
        });
    });
}

function clearClientAttendanceErrors() {
    document.querySelectorAll(".field-error").forEach(error => {
        error.textContent = "";
    });
    document.querySelectorAll(".invalid").forEach(field => {
        field.classList.remove("invalid");
    });
}

function showClientAttendanceError(field, message) {
    const error = document.getElementById(field + "Error");
    const fieldElement = document.getElementById(field);

    if (error) {
        error.textContent = message;
    }

    if (fieldElement) {
        fieldElement.classList.add("invalid");
    }
}
