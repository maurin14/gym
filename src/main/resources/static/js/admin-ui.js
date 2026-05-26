function systemSwalOptions(options) {
    return Object.assign({
        width: "360px",
        padding: "1rem",
        confirmButtonColor: "#111827",
        cancelButtonColor: "#6b7280"
    }, options || {});
}

function confirmSystemAction(options) {
    return Swal.fire(systemSwalOptions({
        title: options.title || "Guardar cambios?",
        text: options.text || "",
        icon: options.icon || "question",
        showCancelButton: true,
        confirmButtonText: options.confirmText || "Si",
        cancelButtonText: options.cancelText || "Cancelar"
    }));
}

function showSystemLoading(title, text) {
    return Swal.fire(systemSwalOptions({
        title: title || "Procesando...",
        text: text || "Espere un momento.",
        allowOutsideClick: false,
        allowEscapeKey: false,
        didOpen: function () {
            Swal.showLoading();
        }
    }));
}

function showSystemSuccess(title, text) {
    return Swal.fire(systemSwalOptions({
        title: title || "Guardado.",
        text: text || "",
        icon: "success",
        confirmButtonText: "OK"
    }));
}

function showSystemError(title, text) {
    return Swal.fire(systemSwalOptions({
        title: title || "No se pudo guardar.",
        text: text || "",
        icon: "error",
        confirmButtonText: "OK"
    }));
}

const adminSwalOptions = systemSwalOptions;
const confirmAdminAction = confirmSystemAction;
const showAdminLoading = showSystemLoading;
const showAdminSuccess = showSystemSuccess;
const showAdminError = showSystemError;

document.addEventListener("DOMContentLoaded", function () {
    document.querySelectorAll("form[data-admin-confirm], form[data-system-confirm]").forEach(function (form) {
        let submitting = false;

        form.addEventListener("submit", function (event) {
            if (submitting) {
                return;
            }

            event.preventDefault();

            confirmSystemAction({
                title: form.dataset.adminConfirm || form.dataset.systemConfirm,
                text: form.dataset.adminText || form.dataset.systemText || "",
                confirmText: form.dataset.adminConfirmText || form.dataset.systemConfirmText || "Guardar"
            }).then(function (result) {
                if (!result.isConfirmed || submitting) {
                    return;
                }

                submitting = true;
                showSystemLoading(form.dataset.adminLoading || form.dataset.systemLoading || "Procesando...");
                form.submit();
            });
        });
    });
});
