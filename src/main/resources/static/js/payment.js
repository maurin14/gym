let paymentSubmitting = false;

function loadPayments(page) {
    const status = document.getElementById("filterStatus") ? document.getElementById("filterStatus").value : "";
    const paymentMethod = document.getElementById("filterPaymentMethod") ? document.getElementById("filterPaymentMethod").value : "";
    const branchId = document.getElementById("filterBranch") ? document.getElementById("filterBranch").value : "";

    const xhttp = new XMLHttpRequest();

    xhttp.onreadystatechange = function () {
        if (xhttp.readyState === 4 && xhttp.status === 200) {
            document.getElementById("payment-table-container").innerHTML = xhttp.responseText;
        }
    };

    const params = "page=" + encodeURIComponent(page)
            + "&status=" + encodeURIComponent(status)
            + "&paymentMethod=" + encodeURIComponent(paymentMethod)
            + "&branchId=" + encodeURIComponent(branchId);

    xhttp.open("GET", "/admin/payments/ajax/list?" + params, true);
    xhttp.send();
}

function filterPayments() {
    loadPayments(0);
}

function changePaymentPage(element) {
    loadPayments(element.dataset.page);
}

function confirmSavePayment() {
    if (paymentSubmitting) {
        return;
    }

    Swal.fire({
        title: "¿Desea guardar el pago?",
        icon: "question",
        showCancelButton: true,
        confirmButtonText: "Guardar",
        cancelButtonText: "Cancelar"
    }).then((result) => {
        if (result.isConfirmed) {
            paymentSubmitting = true;
            document.getElementById("paymentForm").submit();
        }
    });
}

function confirmChangePaymentStatus(element) {
    const id = element.dataset.id;

    Swal.fire({
        title: "¿Desea cambiar el estado del pago?",
        text: "El estado cambiará entre Pagado, Pendiente y Anulado.",
        icon: "question",
        showCancelButton: true,
        confirmButtonText: "Sí, cambiar",
        cancelButtonText: "Cancelar"
    }).then((result) => {
        if (result.isConfirmed) {
            window.location.href = "/admin/payments/status/" + id;
        }
    });
}

function confirmDeletePayment(element) {
    const id = element.dataset.id;

    Swal.fire({
        title: "¿Está seguro de eliminar este pago?",
        icon: "warning",
        showCancelButton: true,
        confirmButtonText: "Eliminar",
        cancelButtonText: "Cancelar"
    }).then((result) => {
        if (result.isConfirmed) {
            window.location.href = "/admin/payments/delete/" + id;
        }
    });
}

function confirmCancelPayment() {
    Swal.fire({
        title: "¿Cancelar?",
        text: "Los cambios no guardados se perderán.",
        icon: "warning",
        showCancelButton: true,
        confirmButtonText: "Sí, cancelar",
        cancelButtonText: "Seguir editando"
    }).then((result) => {
        if (result.isConfirmed) {
            window.location.href = "/admin/payments";
        }
    });
}
