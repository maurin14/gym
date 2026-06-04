(function () {
    if (window.clientHomeUiInitialized) {
        document.body.classList.remove('menu-open');
        return;
    }

    window.clientHomeUiInitialized = true;

    const clientHeader = document.querySelector('.client-header');
    const menuToggles = document.querySelectorAll('[data-menu-toggle]');
    const menuCloseButtons = document.querySelectorAll('[data-menu-close]');

    function setMenuState(isOpen) {
        document.body.classList.toggle('menu-open', Boolean(isOpen));

        menuToggles.forEach(function (toggle) {
            toggle.setAttribute('aria-expanded', String(Boolean(isOpen)));
        });
    }

    function clearTransientUiState() {
        setMenuState(false);
        document.body.classList.remove('modal-open', 'drawer-open', 'sidebar-open');

        if (document.body.style.overflow === 'hidden') {
            document.body.style.overflow = '';
        }
    }

    clearTransientUiState();

    if (clientHeader) {
        const toggleHeaderState = function () {
            clientHeader.classList.toggle('is-scrolled', window.scrollY > 24);
        };

        toggleHeaderState();
        window.addEventListener('scroll', toggleHeaderState, { passive: true });
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

    document.querySelectorAll('.client-menu-drawer a').forEach(function (link) {
        link.addEventListener('click', clearTransientUiState);
    });

    document.addEventListener('keydown', function (event) {
        if (event.key === 'Escape') {
            setMenuState(false);
        }
    });

    window.addEventListener('pagehide', clearTransientUiState);
    window.addEventListener('beforeunload', clearTransientUiState);
    window.addEventListener('pageshow', function () {
        clearTransientUiState();
    });

    document.querySelectorAll('[data-placeholder-module]').forEach(function (button) {
        button.addEventListener('click', function () {
            const moduleName = button.dataset.placeholderModule;
            const messages = window.systemMessages || {};

            if (!window.Swal) {
                return;
            }

            Swal.fire({
                icon: 'info',
                title: moduleName + ' ' + (messages.clientPlaceholderTitleSuffix || 'estará disponible pronto'),
                text: messages.clientPlaceholderText || 'Esta sección se habilitará cuando el módulo correspondiente esté integrado al portal cliente.',
                confirmButtonText: messages.acceptButton || 'Aceptar',
                confirmButtonColor: '#d97818'
            });
        });
    });
}());
