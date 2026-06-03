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
        title: options.title || window.i18n.saveChanges,
        text: options.text || "",
        icon: options.icon || "question",
        showCancelButton: true,
        confirmButtonText: options.confirmText || window.i18n.yes,
        cancelButtonText: options.cancelText || window.i18n.cancel
    }));
}

function showSystemLoading(title, text) {
    return Swal.fire(systemSwalOptions({
        title: title || window.i18n.processing,
        text: text || window.i18n.saving,
        allowOutsideClick: false,
        allowEscapeKey: false,
        didOpen: function () {
            Swal.showLoading();
        }
    }));
}

function showSystemSuccess(title, text) {
    return Swal.fire(systemSwalOptions({
        title: title || window.i18n.success,
        text: text || "",
        icon: "success",
        confirmButtonText: window.i18n.accept
    }));
}

function showSystemError(title, text) {
    return Swal.fire(systemSwalOptions({
        title: title || window.i18n.error,
        text: text || "",
        icon: "error",
        confirmButtonText: window.i18n.accept
    }));
}

const adminSwalOptions = systemSwalOptions;
const confirmAdminAction = confirmSystemAction;
const showAdminLoading = showSystemLoading;
const showAdminSuccess = showSystemSuccess;
const showAdminError = showSystemError;

document.addEventListener("DOMContentLoaded", function () {

    document
        .querySelectorAll("form[data-admin-confirm], form[data-system-confirm]")
        .forEach(function (form) {

            let submitting = false;

            form.addEventListener("submit", function (event) {

                if (submitting) {
                    return;
                }

                event.preventDefault();

                confirmSystemAction({
                    title: form.dataset.adminConfirm || form.dataset.systemConfirm,
                    text: form.dataset.adminText || form.dataset.systemText || "",
                    confirmText:
                        form.dataset.adminConfirmText ||
                        form.dataset.systemConfirmText ||
                        window.i18n.save
                }).then(function (result) {

                    if (!result.isConfirmed || submitting) {
                        return;
                    }

                    submitting = true;

                    showSystemLoading(
                        form.dataset.adminLoading ||
                        form.dataset.systemLoading ||
                        window.i18n.processing
                    );

                    form.submit();
                });
            });
        });
}); extraña