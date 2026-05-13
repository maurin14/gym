let currentPage = 0;
let pageSize = 5;

function loadpage(page) {
    currentPage = parseInt(page);
  
  window.history.pushState({}, '', `/equip?page=${currentPage}&size=${pageSize}`);
    
    fetch(`/equip?page=${currentPage}&size=${pageSize}`)
        .then(res => res.text())
        .then(html => {
            document.getElementById("tableContainer").innerHTML = html;
            document.getElementById("tableContainer").style.display = "block";
            document.getElementById("formContainer").style.display = "none";
        });
}

function changeSize(size) {
    pageSize = parseInt(size);
    cargarPagina(0);
}

function loadForm() {
    fetch('/add')
        .then(res => res.text())
        .then(html => {
            document.getElementById("formContainer").innerHTML = html;
            document.getElementById("tableContainer").style.display = "none";
            document.getElementById("formContainer").style.display = "block";
        });
}

function returnTable() {
    document.getElementById("formContainer").style.display = "none";
    cargarPagina(currentPage);
}

function saveForm(form) {
    let formData = new FormData(form);
    
 // Nombre
if (!formData.get("name")?.trim()) {
    Swal.fire('Error', 'Nombre requerido', 'error');
    return false;
}

// Tipo
if (!formData.get("type")?.trim()) {
    Swal.fire('Error', 'Tipo requerido', 'error');
    return false;
}

// Estado
if (!formData.get("state")?.trim()) {
    Swal.fire('Error', 'Estado requerido', 'error');
    return false;
}

// Costo
let cost = formData.get("cost");
if (!cost || isNaN(cost) || cost <= 0) {
    Swal.fire('Error', 'Costo inválido', 'error');
    return false;
}

// Fecha
let fecha = formData.get("purchaseDate");
if (!fecha) {
    Swal.fire('Error', 'Fecha requerida', 'error');
    return false;
}

if (!formData.get("available")) {
    Swal.fire('Error', 'Seleccione disponibilidad', 'error');
    return false;
}


    fetch('/save', {
        method: 'POST',
        body: formData
    })
    .then(() => {
        Swal.fire('Éxito', 'Guardado correctamente', 'success');
        volverTabla();
    });

    return false;
}

function loadEdit(btn) {
    let id = btn.getAttribute("data-id");

    fetch(`/edit/${id}`)
        .then(res => res.text())
        .then(html => {
            document.getElementById("formContainer").innerHTML = html;
            document.getElementById("tableContainer").style.display = "none";
            document.getElementById("formContainer").style.display = "block";
        });
}

function confirmDelete(btn) {
    let id = btn.getAttribute("data-id");

    Swal.fire({
        title: '¿Eliminar?',
        icon: 'warning',
        showCancelButton: true
    }).then(r => {
        if (r.isConfirmed) {
            fetch(`/delete/${id}`)
                .then(() => {
                    Swal.fire('Eliminado', '', 'success');
                    cargarPagina(currentPage);
                });
        }
    });
}

document.addEventListener("DOMContentLoaded", () => {
    cargarPagina(0);
});

//Busqueda
function changeSearch() {
    let tipo = document.getElementById("searchtype").value;

    let idDiv = document.getElementById("searchId");
    let costoDiv = document.getElementById("searchCost");

 
    idDiv.style.display = "none";
    costoDiv.style.display = "none";


    if (tipo === "id") {
        idDiv.style.display = "block";
    } else if (tipo === "costo") {
        costoDiv.style.display = "block";
    }

    document.querySelector(".add-btn").style.display = tipo ? "none" : "block";
}

function searchById() {
    let id = document.getElementById("inputId").value;

    fetch(`/equip?page=0&size=${pageSize}&id=${id}`, {
        headers: {
            "X-Requested-With": "XMLHttpRequest"
        }
    })
    .then(res => res.text())
    .then(html => {
       document.getElementById("tableContent").innerHTML = html;
    });
}
function searchByCost() {
    let min = document.getElementById("minCosto").value;
    let max = document.getElementById("maxCosto").value;

    fetch(`/equip?page=0&size=${pageSize}&min=${min}&max=${max}`, {
        headers: {
            "X-Requested-With": "XMLHttpRequest"
        }
    })
    .then(res => res.text())
    .then(html => {
      document.getElementById("tableContent").innerHTML = html;
    });
}