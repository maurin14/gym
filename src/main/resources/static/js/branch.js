let branchSubmitting = false;

function confirmSave() {
    if (branchSubmitting) {
        return;
    }

    confirmAdminAction({
        title: "Guardar sucursal?",
        confirmText: "Guardar"
    }).then((result) => {
        if (result.isConfirmed && !branchSubmitting) {
            branchSubmitting = true;
            showAdminLoading("Guardando...");
            document.getElementById("branchForm").submit();
        }
    });
}

function confirmSaveBranch() {
    confirmSave();
}

function confirmDeleteBranch(element) {
    let id = element.dataset.id;

    confirmAdminAction({
        title: "Eliminar sucursal?",
        text: "Esta accion no se puede deshacer.",
        confirmText: "Eliminar",
        icon: "warning"
    }).then((result) => {
        if (result.isConfirmed) {
            showAdminLoading("Eliminando...");
            window.location.href = "/admin/branches/delete/" + id;
        }
    });
}

function confirmChangeBranchStatus(element) {
    confirmToggleBranch(element);
}

function confirmToggleBranch(element) {
    let id = element.dataset.id;

    confirmAdminAction({
        title: "Cambiar estado?",
        confirmText: "Si"
    }).then((result) => {
        if (result.isConfirmed) {
            showAdminLoading("Procesando...");
            window.location.href = "/admin/branches/status/" + id;
        }
    });
}

function confirmCancelBranch() {
    confirmAdminAction({
        title: "Cancelar cambios?",
        text: "Los cambios no guardados se perderan.",
        confirmText: "Si",
        icon: "warning"
    }).then((result) => {
        if (result.isConfirmed) {
            window.location.href = "/admin/branches";
        }
    });
}
