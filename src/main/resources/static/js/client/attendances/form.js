document.addEventListener("DOMContentLoaded", function () {
    loadClasses();
});

let clientAttendanceSaving = false;
const i18n = window.i18n || {
    selectClass: "Select a class",
    noBranch: "No branch",
    classRequired: "Select a class.",
    dateRequired: "The date is required.",
    reviewFields: "Please review the highlighted fields.",
    confirmTitle: "Save changes?",
    saveButton: "Save",
    loadingTitle: "Saving...",
    saveError: "Could not save attendance.",
    saved: "Attendance saved successfully.",
    confirmButton: "Accept" 
};

function loadClasses() {

    fetch("/classes")
        .then(response => response.json())
        .then(data => {

            const classSelect = document.getElementById("classId");

            classSelect.innerHTML =
                    `<option value="">${i18n.selectClass}</option>`;

            data.forEach(gymClass => {

                classSelect.innerHTML += `
                    <option value="${gymClass.idClass}" data-date="${gymClass.classDate}">
                        ${gymClass.classType} - ${gymClass.branchName || i18n.noBranch} - ${gymClass.classDate}
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
    if (clientAttendanceSaving) return;

    clearClientAttendanceErrors();

    const classId = document.getElementById("classId").value;
    const attendanceDate = document.getElementById("attendanceDate").value;
    const observation = document.getElementById("observation").value.trim();

    if (classId === "") {
        showClientAttendanceError("classId", i18n.classRequired);
        showSystemError(i18n.reviewFields);
        return;
    }

    if (attendanceDate === "") {
        showClientAttendanceError("attendanceDate", i18n.dateRequired);
        showSystemError(i18n.reviewFields);
        return;
    }

    fetch(`/attendances/check?classId=${classId}&userId=${window.userId}`)
        .then(res => res.json())
        .then(data => {
            if (data.exists) {
                Swal.fire({
                    icon: 'error',
                    title: i18n.attendanceDuplicate || "Already registered",
                    text: i18n.attendanceDuplicateMsg || "You are already registered for this class.",
                    showConfirmButton: true,
                    confirmButtonText: i18n.confirmButton
                });
                return;
            }

            const attendance = {
                gymClass: { idClass: parseInt(classId) },
                attendanceDate: attendanceDate,
                attendanceStatus: "Presente",
                observation: observation
            };

            clientAttendanceSaving = true;
            showSystemLoading(i18n.loadingTitle || "Saving...");

            fetch("/attendances", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(attendance)
            })
            .then(response => {
                if (!response.ok) throw new Error(i18n.saveError);
                return response.json();
            })
            .then(() => {
                clientAttendanceSaving = false;

                Swal.fire({
                    icon: 'success',
                    title: i18n.saved,                
                    showConfirmButton: true,
                    confirmButtonText: i18n.confirmButton
                }).then(() => {
                    window.location.href = "/client/attendances";
                });
            })
            .catch(error => {
                clientAttendanceSaving = false;
                showSystemError(error.message || i18n.saveError);
            });

        })
        .catch(() => {
            showSystemError(i18n.saveError);
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
