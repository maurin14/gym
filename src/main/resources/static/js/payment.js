let paymentSubmitting = false;

function confirmSavePayment() {
    if (paymentSubmitting) {
        return;
    }

    confirmAdminAction({
        title: "Guardar pago?",
        confirmText: "Guardar"
    }).then((result) => {
        if (result.isConfirmed && !paymentSubmitting) {
            paymentSubmitting = true;
            showAdminLoading("Guardando...");
            document.getElementById("paymentForm").submit();
        }
    });
}

function confirmChangePaymentStatus(element) {
    const id = element.dataset.id;

    confirmAdminAction({
        title: "Cambiar estado?",
        text: "El estado cambiara entre Pagado, Pendiente y Anulado.",
        confirmText: "Si"
    }).then((result) => {
        if (result.isConfirmed) {
            showAdminLoading("Procesando...");
            window.location.href = "/admin/payments/status/" + id;
        }
    });
}

function confirmDeletePayment(element) {
    const id = element.dataset.id;

    confirmAdminAction({
        title: "Eliminar pago?",
        text: "Esta accion no se puede deshacer.",
        confirmText: "Eliminar",
        icon: "warning"
    }).then((result) => {
        if (result.isConfirmed) {
            showAdminLoading("Eliminando...");
            window.location.href = "/admin/payments/delete/" + id;
        }
    });
}

function confirmCancelPayment() {
    confirmAdminAction({
        title: "Cancelar cambios?",
        text: "Los cambios no guardados se perderan.",
        confirmText: "Si",
        icon: "warning"
    }).then((result) => {
        if (result.isConfirmed) {
            window.location.href = "/admin/payments";
        }
    });
}
