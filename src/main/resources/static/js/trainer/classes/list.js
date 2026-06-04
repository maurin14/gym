let currentPage = 1;
let totalPages = 1;

const rowsPerPage = 4;

const classBasePath = window.classBasePath || (window.location.pathname.startsWith("/admin/classes")
        ? "/admin/classes"
        : "/trainer/classes");

const canManageClasses = Boolean(window.canManageClasses);

const i18n = window.i18n || {
    edit: "Edit",
    delete: "Delete",
    branchAll: "All Branches",
    statusActive: "Active",
    statusInactive: "Inactive",
    previous: "Previous",
    next: "Next",
    deleteConfirmation: "This action cannot be undone.",
    deleting: "Deleting...",
    deleteError: "Could not delete.",
    emptyClasses: "No classes available.",
    noBranch: "No Branch",
    difficultyLow: "Low",
    difficultyMedium: "Medium",
    difficultyHigh: "High"
};

document.addEventListener("DOMContentLoaded", function () {
    loadBranchesFilter();
    loadClasses();
});

function loadBranchesFilter() {
    const branchFilter = document.getElementById("branchFilter");
    if (!branchFilter) return;

    fetch("/classes/branches")
        .then(response => response.json())
        .then(data => {
            branchFilter.innerHTML = `<option value="">${i18n.branchAll}</option>`;
            data.forEach(branch => {
                branchFilter.innerHTML += `<option value="${branch.id}">${branch.name}</option>`;
            });
        });
}

function filterClassesByBranch() {
    currentPage = 1;
    loadClasses();
}

function loadClasses() {
    const branchFilter = document.getElementById("branchFilter");
    const branchId = branchFilter ? branchFilter.value : "";

    let url = "/classes/page?page=" + (currentPage - 1) + "&size=" + rowsPerPage;
    if (branchId !== "") url += "&branchId=" + encodeURIComponent(branchId);

    fetch(url)
        .then(response => response.json())
        .then(data => {
            totalPages = data.totalPages;
            currentPage = data.currentPage;
            showClasses(data.classes);
            showPagination();
        });
}

function showClasses(classes) {
    const tbody = document.getElementById("classesTableBody");
    tbody.innerHTML = "";

    if (!classes || classes.length === 0) {
        tbody.innerHTML = `
            <tr>
                <td colspan="${canManageClasses ? 13 : 12}" class="empty-message">
                    ${i18n.emptyClasses}
                </td>
            </tr>
        `;
        return;
    }

    classes.forEach(gymClass => {
        const statusClass = gymClass.status ? "status-active" : "status-inactive";
        const statusText = gymClass.status ? i18n.statusActive : i18n.statusInactive;
        const difficultyClass = getDifficultyClass(gymClass.difficultyLevel);

        // --- Traducción de dificultad ---
        let difficultyLabel = gymClass.difficultyLevel;
        if (gymClass.difficultyLevel) {
            const diff = gymClass.difficultyLevel.toLowerCase();
            if (diff === "alto") difficultyLabel = i18n.difficultyHigh;
            else if (diff === "medio") difficultyLabel = i18n.difficultyMedium;
            else if (diff === "bajo") difficultyLabel = i18n.difficultyLow;
        }

        tbody.innerHTML += `
            <tr>
                <td>${gymClass.classType}</td>
                <td>${gymClass.trainerName}</td>
                <td>${gymClass.branchName || i18n.noBranch}</td>
                <td>${formatDate(gymClass.classDate)}</td>
                <td>${gymClass.startTime}</td>
                <td>${gymClass.endTime}</td>
                <td>${gymClass.duration} min</td>
                <td>${gymClass.maxCapacity}</td>
                <td>${gymClass.enrolledCount}</td>

                <td><span class="badge ${statusClass}">${statusText}</span></td>
                <td><span class="badge ${difficultyClass}">${difficultyLabel}</span></td>
                <td>${gymClass.description}</td>

                ${canManageClasses ? `
                <td>
                    <div class="actions">
                        <a class="btn-primary" href="${classBasePath}/edit/${gymClass.idClass}">
                            ${i18n.edit}
                        </a>
                        <button type="button" class="btn-danger" onclick="deleteClass(${gymClass.idClass})">
                            ${i18n.delete}
                        </button>
                    </div>
                </td>` : ""}
            </tr>
        `;
    });
}

function getDifficultyClass(difficultyLevel) {
    const difficulty = difficultyLevel ? difficultyLevel.toLowerCase() : "";
    if (difficulty === "alto") return "status-inactive";
    if (difficulty === "medio") return "status-pending";
    return "status-active";
}

function formatDate(dateValue) {
    if (!dateValue) return "";
    const parts = String(dateValue).split("-");
    if (parts.length !== 3) return dateValue;
    return parts[2] + "-" + parts[1] + "-" + parts[0]; // DD-MM-AAAA
}

function showPagination() {
    const pagination = document.getElementById("classesPagination");
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
    const button = document.createElement("button");
    button.type = "button";
    button.className = "pagination-link";
    button.textContent = text;
    button.disabled = Boolean(disabled);
    if (disabled) button.classList.add("disabled");
    button.addEventListener("click", () => {
        if (!disabled && page >= 1 && page <= totalPages && page !== currentPage) {
            currentPage = page;
            loadClasses();
        }
    });
    return button;
}

function deleteClass(idClass) {
    if (!canManageClasses) return;

    confirmAdminAction({
        title: i18n.delete + "?",
        text: i18n.deleteConfirmation,
        confirmText: i18n.delete,
        icon: "warning"
    }).then(result => {
        if (!result.isConfirmed) return;

        showAdminLoading(i18n.deleting);

        fetch("/classes/" + idClass, { method: "DELETE" })
            .then(response => {
                if (!response.ok) throw new Error(i18n.deleteError);
                showAdminSuccess(i18n.delete + "d.").then(loadClasses);
            })
            .catch(error => {
                showAdminError(error.message || i18n.deleteError);
            });
    });
}