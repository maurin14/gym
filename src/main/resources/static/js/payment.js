let paymentSubmitting = false;

function paymentMessage(key, fallback) {
    return (window.paymentMessages && window.paymentMessages[key]) || fallback;
}

function confirmSavePayment() {
    if (paymentSubmitting) {
        return;
    }

    confirmAdminAction({
        title: paymentMessage("saveTitle", "Guardar pago?"),
        confirmText: paymentMessage("saveConfirm", "Guardar")
    }).then((result) => {
        if (result.isConfirmed && !paymentSubmitting) {
            paymentSubmitting = true;
            showAdminLoading(paymentMessage("saveLoading", "Guardando..."));
            document.getElementById("paymentForm").submit();
        }
    });
}

function confirmChangePaymentStatus(element) {
    const id = element.dataset.id;

    confirmAdminAction({
        title: paymentMessage("statusTitle", "Cambiar estado?"),
        text: paymentMessage("statusText", "El estado cambiara entre Pagado, Pendiente y Anulado."),
        confirmText: paymentMessage("yes", "Si")
    }).then((result) => {
        if (result.isConfirmed) {
            showAdminLoading(paymentMessage("processing", "Procesando..."));
            window.location.href = "/admin/payments/status/" + id;
        }
    });
}

function confirmDeletePayment(element) {
    const id = element.dataset.id;

    confirmAdminAction({
        title: paymentMessage("deleteTitle", "Eliminar pago?"),
        text: paymentMessage("deleteText", "Esta accion no se puede deshacer."),
        confirmText: paymentMessage("deleteConfirm", "Eliminar"),
        icon: "warning"
    }).then((result) => {
        if (result.isConfirmed) {
            showAdminLoading(paymentMessage("deleteLoading", "Eliminando..."));
            window.location.href = "/admin/payments/delete/" + id;
        }
    });
}

function confirmCancelPayment() {
    confirmAdminAction({
        title: paymentMessage("cancelTitle", "Cancelar cambios?"),
        text: paymentMessage("cancelText", "Los cambios no guardados se perderan."),
        confirmText: paymentMessage("yes", "Si"),
        icon: "warning"
    }).then((result) => {
        if (result.isConfirmed) {
            window.location.href = "/admin/payments";
        }
    });
}
