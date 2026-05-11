const clientHeader = document.querySelector('.client-header');
const menuToggle = document.querySelector('[data-menu-toggle]');
const menuCloseButtons = document.querySelectorAll('[data-menu-close]');

if (clientHeader) {
    const toggleHeaderState = function () {
        clientHeader.classList.toggle('is-scrolled', window.scrollY > 24);
    };

    toggleHeaderState();
    window.addEventListener('scroll', toggleHeaderState, { passive: true });
}

function setMenuState(isOpen) {
    document.body.classList.toggle('menu-open', isOpen);

    if (menuToggle) {
        menuToggle.setAttribute('aria-expanded', String(isOpen));
    }
}

if (menuToggle) {
    menuToggle.addEventListener('click', function () {
        setMenuState(!document.body.classList.contains('menu-open'));
    });
}

menuCloseButtons.forEach(function (button) {
    button.addEventListener('click', function () {
        setMenuState(false);
    });
});

document.addEventListener('keydown', function (event) {
    if (event.key === 'Escape') {
        setMenuState(false);
    }
});

document.querySelectorAll('[data-placeholder-module]').forEach(function (button) {
    button.addEventListener('click', function () {
        const moduleName = button.dataset.placeholderModule;

        Swal.fire({
            icon: 'info',
            title: moduleName + ' estará disponible pronto',
            text: 'Esta sección se habilitará cuando el módulo correspondiente esté integrado al portal cliente.',
            confirmButtonText: 'Aceptar',
            confirmButtonColor: '#d97818'
        });
    });
});
