let currentPage = 0;
let pageSize = 10;

function loadPageSchedule(page) {
    currentPage = parseInt(page);
     window.history.pushState({}, '', `/admin/schedules?page=${currentPage}&size=${pageSize}`);
    fetch(`/admin/schedules?page=${currentPage}&size=${pageSize}`, {
        headers: {
            "X-Requested-With": "XMLHttpRequest"
        }
    })
    .then(res => res.text())
    .then(html => {
            document.getElementById("tableContainer").innerHTML = html;
            document.getElementById("tableContainer").style.display = "block";
            document.getElementById("formContainer").style.display = "none";
    });
}

function changeSizeSchedule(size) {
    pageSize = parseInt(size);
    loadPageSchedule(0);
}

function loadFormSchedule() {
    fetch('/admin/schedules/new')
        .then(res => res.text())
        .then(html => {
            document.getElementById("formContainer").innerHTML = html;
            document.getElementById("tableContainer").style.display = "none";
            document.getElementById("formContainer").style.display = "block";
        });
}

function backScheduleTable() {
    document.getElementById("formContainer").style.display = "none";
    
    loadPageSchedule(currentPage);
    
}

function safeFormSchedule(form) {
    let formData = new FormData(form);

    if (!formData.get("branch.id")) {
        Swal.fire('Error', 'Debe seleccionar una sucursal', 'error');
        return false;
    }
    //Alertas :D
    if (!formData.get("dayOfWeek")) {
        Swal.fire('Error', 'Debe seleccionar un día', 'error');
        return false;
    }
    if (!formData.get("startTime")) {
        Swal.fire('Error', 'Hora de inicio requerida', 'error');
        return false;
    }
    if (!formData.get("endTime")) {
        Swal.fire('Error', 'Hora de fin requerida', 'error');
        return false;
    }
    if (!formData.get("scheduleType")?.trim()) {
        Swal.fire('Error', 'Tipo de horario requerido', 'error');
        return false;
    }

 
    let start = formData.get("startTime");
    let end = formData.get("endTime");

    if (start >= end) {
        Swal.fire('Error', 'La hora fin debe ser mayor a la de inicio', 'error');
        return false;
    }

    fetch('/admin/schedules/save', {
        method: 'POST',
        body: formData
    })
    .then(() => {
        Swal.fire('Éxito', 'Horario guardado correctamente', 'success');
        backScheduleTable();
    });

    return false;
}

function loadEditSchedule(btn) {
    let id = btn.getAttribute("data-id");
    fetch(`/admin/schedules/edit/${id}`)
        .then(res => res.text())
        .then(html => {
            document.getElementById("formContainer").innerHTML = html;
            document.getElementById("tableContainer").style.display = "none";
            document.getElementById("formContainer").style.display = "block";
        });
}

function confirmDeleteSchedule(btn) {
    let id = btn.getAttribute("data-id");

    Swal.fire({
        title: '¿Eliminar?', 
        icon: 'warning', //Alerta 
        showCancelButton: true 
    }).then(r => {
        if (r.isConfirmed) {
            fetch(`/admin/schedules/delete/${id}`)
                .then(() => {
                    Swal.fire('Eliminado', '', 'success');
            
                    loadPageSchedule(currentPage);
                });
        }
    });
}
function changeSearch() {
    let tipo = document.getElementById("searchType").value;

    document.getElementById("searchBranch").style.display = "none";
    document.getElementById("searchHour").style.display = "none";

    if (tipo === "branch") {
        document.getElementById("searchBranch").style.display = "block";
    } 
    else if (tipo === "Hour") {
        document.getElementById("searchHour").style.display = "block";
    }
}

function searchForBranch() {
    let id = document.getElementById("branchFilter").value;

    fetch(`/admin/schedules?page=0&size=${pageSize}&branchId=${id}`, {
        headers: { "X-Requested-With": "XMLHttpRequest" }
    })
    .then(res => res.text())
    .then(html => {
       document.getElementById("tableContainer").innerHTML = html;
    });
}

function searchForHour() {
    let start = document.getElementById("startFilter").value;
    let end = document.getElementById("endFilter").value;

    fetch(`/admin/schedules?page=0&size=${pageSize}&start=${start}&end=${end}`, {
        headers: { "X-Requested-With": "XMLHttpRequest" }
    })
    .then(res => res.text())
    .then(html => {
        //obten el elemento con id
       document.getElementById("tableContainer").innerHTML = html;
    });
}
//Sin este no carga nada XD
document.addEventListener("DOMContentLoaded", () => {
    loadPageSchedule(0);
});
