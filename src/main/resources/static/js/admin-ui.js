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
        return Promise.resolve({
            isConfirmed: window.confirm(options.title || systemMessage("confirmTitle", "Save changes?"))
        });
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
        return;
    }

    Swal.fire(systemSwalOptions({
        title: title || systemMessage("loadingTitle", "Processing..."),
        text: text || systemMessage("loadingText", "Please wait."),
        allowOutsideClick: false,
        allowEscapeKey: false,
        showConfirmButton: false,
        didOpen: function () {
            Swal.showLoading();
        }
    }));
}

function showSystemSuccess(messageOrTitle, text) {
    if (!window.Swal) {
        return;
    }

    const hasText = typeof text !== 'undefined';

    Swal.fire(systemSwalOptions({
        icon: "success",
        title: hasText ? messageOrTitle : systemMessage("successTitle", "Saved."),
        text: hasText ? text : (messageOrTitle || ""),
        timer: 1800,
        showConfirmButton: false
    }));
}

function showSystemError(messageOrTitle, text) {
    if (!window.Swal) {
        return;
    }

    const hasText = typeof text !== 'undefined';

    Swal.fire(systemSwalOptions({
        icon: "error",
        title: hasText ? messageOrTitle : systemMessage("errorTitle", "Could not save."),
        text: hasText ? text : (messageOrTitle || ""),
        confirmButtonText: systemMessage("okButton", "OK")
    }));
}

const adminSwalOptions = systemSwalOptions;
const confirmAdminAction = confirmSystemAction;
const showAdminLoading = showSystemLoading;
const showAdminSuccess = showSystemSuccess;
const showAdminError = showSystemError;

document.addEventListener("DOMContentLoaded", function () {
    document.querySelectorAll('.module-menu').forEach(function (menu) {
        menu.querySelectorAll('a').forEach(function (link) {
            link.addEventListener('click', function () {
                menu.removeAttribute('open');
            });
        });
    });

    document.addEventListener('click', function (event) {
        document.querySelectorAll('.module-menu[open]').forEach(function (menu) {
            if (!menu.contains(event.target)) {
                menu.removeAttribute('open');
            }
        });
    });

    document.addEventListener('keydown', function (event) {
        if (event.key === 'Escape') {
            document.querySelectorAll('.module-menu[open]').forEach(function (menu) {
                menu.removeAttribute('open');
            });
        }
    });

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
                const submitButton = event.submitter || form.querySelector('button[type="submit"], input[type="submit"]');

                if (submitButton) {
                    submitButton.disabled = true;
                }

                try {
                    showSystemLoading(form.dataset.adminLoading || form.dataset.systemLoading || systemMessage("loadingTitle", "Processing..."));
                    form.submit();
                } catch (error) {
                    submitting = false;
                    if (submitButton) {
                        submitButton.disabled = false;
                    }
                    throw error;
                }
            });
        });
    });

    if (window.systemFlashMessages) {
        if (window.systemFlashMessages.success) {
            showSystemSuccess(window.systemFlashMessages.success);
        }

        if (window.systemFlashMessages.error) {
            showSystemError(window.systemFlashMessages.error);
        }
    }
});
