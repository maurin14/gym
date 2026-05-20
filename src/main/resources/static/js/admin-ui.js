function adminSwalOptions(options) {
    return Object.assign({
        width: "360px",
        padding: "1rem",
        confirmButtonColor: "#111827",
        cancelButtonColor: "#6b7280"
    }, options || {});
}

function confirmAdminAction(options) {
    return Swal.fire(adminSwalOptions({
        title: options.title || "Confirmar accion",
        text: options.text || "",
        icon: options.icon || "question",
        showCancelButton: true,
        confirmButtonText: options.confirmText || "Si",
        cancelButtonText: options.cancelText || "Cancelar"
    }));
}

function showAdminLoading(title, text) {
    return Swal.fire(adminSwalOptions({
        title: title || "Procesando...",
        text: text || "Espere un momento.",
        icon: "info",
        allowOutsideClick: false,
        allowEscapeKey: false,
        didOpen: function () {
            Swal.showLoading();
        }
    }));
}

function showAdminSuccess(title, text) {
    return Swal.fire(adminSwalOptions({
        title: title || "Listo.",
        text: text || "",
        icon: "success",
        confirmButtonText: "OK"
    }));
}

function showAdminError(title, text) {
    return Swal.fire(adminSwalOptions({
        title: title || "No se pudo completar.",
        text: text || "",
        icon: "error",
        confirmButtonText: "OK"
    }));
}

document.addEventListener("DOMContentLoaded", function () {
    document.querySelectorAll("form[data-admin-confirm]").forEach(function (form) {
        let submitting = false;

        form.addEventListener("submit", function (event) {
            if (submitting) {
                return;
            }

            event.preventDefault();

            confirmAdminAction({
                title: form.dataset.adminConfirm,
                text: form.dataset.adminText || "",
                confirmText: form.dataset.adminConfirmText || "Guardar"
            }).then(function (result) {
                if (!result.isConfirmed || submitting) {
                    return;
                }

                submitting = true;
                showAdminLoading(form.dataset.adminLoading || "Guardando...");
                form.submit();
            });
        });
    });
});
