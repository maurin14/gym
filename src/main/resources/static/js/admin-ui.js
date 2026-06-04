function systemSwalOptions(options) {
    return Object.assign({
        width: "360px",
        padding: "1rem",
        confirmButtonColor: "#111827",
        cancelButtonColor: "#6b7280"
    }, options || {});
}

function systemMessage(key, fallback) {
    return (window.systemMessages && window.systemMessages[key]) || fallback;
}

function confirmSystemAction(options) {
    options = options || {};

    if (!window.Swal) {
        return Promise.resolve({ isConfirmed: window.confirm(options.title || systemMessage("confirmTitle", "Save changes?")) });
    }

    return Swal.fire(systemSwalOptions({
        title: options.title || systemMessage("confirmTitle", "Save changes?"),
        text: options.text || "",
        icon: options.icon || "question",
        showCancelButton: true,
        confirmButtonText: options.confirmText || options.confirmButtonText || systemMessage("confirmButton", "Yes"),
        cancelButtonText: options.cancelText || options.cancelButtonText || systemMessage("cancelButton", "Cancel")
    }));
}

function showSystemLoading(title, text) {
    if (!window.Swal) {
        return Promise.resolve();
    }

    return Swal.fire(systemSwalOptions({
        title: title || systemMessage("loadingTitle", "Processing..."),
        text: text || systemMessage("loadingText", "Please wait."),
        allowOutsideClick: false,
        allowEscapeKey: false,
        didOpen: function () {
            Swal.showLoading();
        }
    }));
}

function showSystemSuccess(title, text) {
    if (!window.Swal) {
        return Promise.resolve();
    }

    return Swal.fire(systemSwalOptions({
        title: title || systemMessage("successTitle", "Saved."),
        text: text || "",
        icon: "success",
        confirmButtonText: systemMessage("okButton", "OK")
    }));
}

function showSystemError(title, text) {
    if (!window.Swal) {
        return Promise.resolve();
    }

    return Swal.fire(systemSwalOptions({
        title: title || systemMessage("errorTitle", "Could not save."),
        text: text || "",
        icon: "error",
        confirmButtonText: systemMessage("okButton", "OK")
    }));
}

const adminSwalOptions = systemSwalOptions;
const confirmAdminAction = confirmSystemAction;
const showAdminLoading = showSystemLoading;
const showAdminSuccess = showSystemSuccess;
const showAdminError = showSystemError;

function clearSystemTransientState() {
    document.body.classList.remove('modal-open', 'drawer-open', 'sidebar-open', 'menu-open', 'swal2-shown', 'swal2-height-auto');

    if (document.body.style.overflow === 'hidden') {
        document.body.style.overflow = '';
    }

    if (window.Swal && Swal.isVisible && Swal.isVisible()) {
        Swal.close();
    }
}

window.addEventListener('pageshow', function (event) {
    if (event.persisted) {
        clearSystemTransientState();
    }
});

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
                confirmText: form.dataset.adminConfirmText || form.dataset.systemConfirmText || systemMessage("saveButton", "Save")
            }).then(function (result) {
                if (!result.isConfirmed || submitting) {
                    return;
                }

                submitting = true;
                showSystemLoading(form.dataset.adminLoading || form.dataset.systemLoading || systemMessage("loadingTitle", "Processing..."));
                form.submit();
            });
        });
    });
});
