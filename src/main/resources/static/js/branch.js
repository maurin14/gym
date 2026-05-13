function loadBranches(page) {
    let name = document.getElementById("filterName").value;
    let active = document.getElementById("filterActive").value;
    let provinceElement = document.getElementById("filterProvince");
    let province = provinceElement ? provinceElement.value : "";

    let xhttp = new XMLHttpRequest();

    xhttp.onreadystatechange = function () {
        if (xhttp.readyState === 4 && xhttp.status === 200) {
            document.getElementById("branch-table-container").innerHTML = xhttp.responseText;
        }
    };

    let params = "page=" + encodeURIComponent(page)
            + "&name=" + encodeURIComponent(name)
            + "&active=" + encodeURIComponent(active)
            + "&province=" + encodeURIComponent(province);

    xhttp.open("GET", "/admin/branches/ajax/list?" + params, true);
    xhttp.send();
}

function filterBranches() {
    loadBranches(0);
}

function changeBranchPage(element) {
    loadBranches(element.dataset.page);
}

function confirmSave() {
    Swal.fire({
        title: '¿Desea guardar la sucursal?',
        icon: 'question',
        showCancelButton: true,
        confirmButtonText: 'Guardar',
        cancelButtonText: 'Cancelar'
    }).then((result) => {
        if (result.isConfirmed) {
            document.getElementById("branchForm").submit();
        }
    });
}

function confirmDeleteBranch(element) {
    let id = element.dataset.id;

    Swal.fire({
        title: '¿Está seguro de eliminar esta sucursal?',
        icon: 'warning',
        showCancelButton: true,
        confirmButtonText: 'Eliminar',
        cancelButtonText: 'Cancelar'
    }).then((result) => {
        if (result.isConfirmed) {
            window.location.href = "/admin/branches/delete/" + id;
        }
    });
}

function confirmToggleBranch(element) {
    let id = element.dataset.id;

    Swal.fire({
        title: '¿Desea cambiar el estado de la sucursal?',
        icon: 'question',
        showCancelButton: true,
        confirmButtonText: 'Sí',
        cancelButtonText: 'No'
    }).then((result) => {
        if (result.isConfirmed) {
            window.location.href = "/admin/branches/toggle/" + id;
        }
    });
}

function confirmCancelBranch() {
    Swal.fire({
        title: '¿Cancelar?',
        text: 'Los cambios no guardados se perderán.',
        icon: 'warning',
        showCancelButton: true,
        confirmButtonText: 'Sí, cancelar',
        cancelButtonText: 'Seguir editando'
    }).then((result) => {
        if (result.isConfirmed) {
            window.location.href = "/admin/branches";
        }
    });
}