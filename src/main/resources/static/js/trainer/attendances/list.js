let currentPage = 1;
let totalPages = 1;
const rowsPerPage = 4;

const attendanceBasePath = window.attendanceBasePath ||
    (window.location.pathname.startsWith("/admin/attendance") ? "/admin/attendance" : "/trainer/attendances");

const canDeleteAttendances = Boolean(window.canDeleteAttendances);

const i18n = window.i18n || {
    edit: "Edit",
    delete: "Delete",
    statusPresent: "Present",
    statusAbsent: "Absent",
    statusLate: "Late",
    emptyAttendances: "No attendances available.",
    previous: "Previous",
    next: "Next",
    deleteConfirmation: "This action cannot be undone.",
    deleting: "Deleting...",
    deleteError: "Could not delete.",
    noBranch: "No Branch",
    locale: "en"
};

document.addEventListener("DOMContentLoaded", function () {
    loadBranchesFilter();
    loadAttendances();
});

function loadBranchesFilter() {
    const branchFilter = document.getElementById("branchFilter");
    if (!branchFilter) return;

    fetch("/attendances/branches")
        .then(response => response.json())
        .then(data => {
            branchFilter.innerHTML = `<option value="">${i18n.branchAll || "All branches"}</option>`;
            data.forEach(branch => {
                branchFilter.innerHTML += `<option value="${branch.id}">${branch.name}</option>`;
            });
        });
}

function filterAttendancesByBranch() {
    currentPage = 1;
    loadAttendances();
}

function loadAttendances() {
    const branchFilter = document.getElementById("branchFilter");
    const branchId = branchFilter ? branchFilter.value : "";
    let url = `/attendances/page?page=${currentPage - 1}&size=${rowsPerPage}`;
    if (branchId) url += `&branchId=${encodeURIComponent(branchId)}`;

    fetch(url)
        .then(res => res.json())
        .then(data => {
            totalPages = data.totalPages;
            currentPage = data.currentPage;
            renderAttendances(data.attendances);
            renderPagination();
        });
}

function renderAttendances(attendances) {
    const tbody = document.getElementById("attendancesTableBody");
    tbody.innerHTML = "";

    if (!attendances || attendances.length === 0) {
        tbody.innerHTML = `<tr>
            <td colspan="8" class="empty-message">${i18n.emptyAttendances}</td>
        </tr>`;
        return;
    }

    attendances.forEach(att => {
        const statusClass = getStatusClass(att.attendanceStatus);

        let statusText = att.attendanceStatus.toLowerCase();
        if (statusText === "presente" || statusText === "present") statusText = i18n.statusPresent;
        else if (statusText === "ausente" || statusText === "absent") statusText = i18n.statusAbsent;
        else if (statusText === "tarde" || statusText === "late") statusText = i18n.statusLate;

        tbody.innerHTML += `
            <tr>
                <td>${att.clientName}</td>
                <td>${att.classType}</td>
                <td>${att.branchName || i18n.noBranch}</td>
                <td>${formatDate(att.attendanceDate)}</td>
                <td><span class="badge ${statusClass}">${statusText}</span></td>
                <td>${att.observation || ""}</td>
                <td>${formatDate(att.registerDate)}</td>
                <td>
                    <div class="actions">
                        <a class="btn-primary" href="${attendanceBasePath}/form/${att.idAttendance}">${i18n.edit}</a>
                        ${canDeleteAttendances ? `<button class="btn-danger" onclick="deleteAttendance(${att.idAttendance})">${i18n.delete}</button>` : ""}
                    </div>
                </td>
            </tr>
        `;
    });
}

function getStatusClass(status) {
    const s = status ? status.toLowerCase() : "";
    if (s === "ausente" || s === "absent") return "status-inactive";
    if (s === "tarde" || s === "late") return "status-pending";
    return "status-active";
}

function formatDate(dateValue) {
    if (!dateValue) return "";
    const parts = String(dateValue).split("-");
    if (parts.length !== 3) return dateValue;
    return `${parts[2].padStart(2,'0')}-${parts[1].padStart(2,'0')}-${parts[0]}`;
}

function renderPagination() {
    const pagination = document.getElementById("attendancesPagination");
    pagination.innerHTML = "";
    const visualTotalPages = Math.max(totalPages, 1);

    pagination.appendChild(createPageButton(i18n.previous, Math.max(currentPage - 1, 1), currentPage === 1));

    for (let page = 1; page <= visualTotalPages; page++) {
        const link = createPageButton(page, page, false);
        if (page === currentPage) link.classList.add("active");
        pagination.appendChild(link);
    }

    pagination.appendChild(createPageButton(i18n.next, Math.min(currentPage + 1, visualTotalPages), currentPage >= visualTotalPages));
}

function createPageButton(text, page, disabled) {
    const btn = document.createElement("button");
    btn.type = "button";
    btn.className = "pagination-link";
    btn.textContent = text;
    btn.disabled = Boolean(disabled);
    if (disabled) btn.classList.add("disabled");
    btn.addEventListener("click", () => {
        if (!disabled && page >= 1 && page <= totalPages && page !== currentPage) {
            currentPage = page;
            loadAttendances();
        }
    });
    return btn;
}

function deleteAttendance(idAttendance) {
    if (!canDeleteAttendances) return;

    confirmAdminAction({
        title: i18n.delete + "?",
        text: i18n.deleteConfirmation,
        confirmText: i18n.delete,
        icon: "warning"
    }).then(result => {
        if (!result.isConfirmed) return;

        showAdminLoading(i18n.deleting);

        fetch(`${attendanceBasePath}/${idAttendance}`, { method: "DELETE" })
            .then(res => {
                if (!res.ok) throw new Error(i18n.deleteError);
                showAdminSuccess(i18n.delete + ".").then(loadAttendances);
            })
            .catch(err => showAdminError(err.message || i18n.deleteError));
    });
}