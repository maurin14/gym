let branchSubmitting = false;

function branchMessage(key, fallback) {
    return (window.branchMessages && window.branchMessages[key]) || fallback;
}

function confirmSave() {
    if (branchSubmitting) {
        return;
    }

    confirmAdminAction({
        title: branchMessage("saveTitle", "Guardar sucursal?"),
        confirmText: branchMessage("saveConfirm", "Guardar")
    }).then((result) => {
        if (result.isConfirmed && !branchSubmitting) {
            branchSubmitting = true;
            showAdminLoading(branchMessage("saveLoading", "Guardando..."));
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
        title: branchMessage("deleteTitle", "Eliminar sucursal?"),
        text: branchMessage("deleteText", "Esta accion no se puede deshacer."),
        confirmText: branchMessage("deleteConfirm", "Eliminar"),
        icon: "warning"
    }).then((result) => {
        if (result.isConfirmed) {
            showAdminLoading(branchMessage("deleteLoading", "Eliminando..."));
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
        title: branchMessage("statusTitle", "Cambiar estado?"),
        confirmText: branchMessage("yes", "Si")
    }).then((result) => {
        if (result.isConfirmed) {
            showAdminLoading(branchMessage("processing", "Procesando..."));
            window.location.href = "/admin/branches/status/" + id;
        }
    });
}

function confirmCancelBranch() {
    confirmAdminAction({
        title: branchMessage("cancelTitle", "Cancelar cambios?"),
        text: branchMessage("cancelText", "Los cambios no guardados se perderan."),
        confirmText: branchMessage("yes", "Si"),
        icon: "warning"
    }).then((result) => {
        if (result.isConfirmed) {
            window.location.href = "/admin/branches";
        }
    });
}
