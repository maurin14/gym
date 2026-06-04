const clientHeader = document.querySelector('.client-header');
const menuToggles = document.querySelectorAll('[data-menu-toggle]');
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

    menuToggles.forEach(function (toggle) {
        toggle.setAttribute('aria-expanded', String(isOpen));
    });
}

menuToggles.forEach(function (toggle) {
    toggle.addEventListener('click', function () {
        setMenuState(!document.body.classList.contains('menu-open'));
    });
});

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
        const messages = window.systemMessages || {};

        Swal.fire({
            icon: 'info',
            title: moduleName + ' ' + (messages.clientPlaceholderTitleSuffix || 'estara disponible pronto'),
            text: messages.clientPlaceholderText || 'Esta seccion se habilitara cuando el modulo correspondiente este integrado al portal cliente.',
            confirmButtonText: messages.acceptButton || 'Aceptar',
            confirmButtonColor: '#d97818'
        });
    });
});
