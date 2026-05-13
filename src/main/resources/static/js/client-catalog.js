(function () {
    function buildFragmentUrl(form, pageUrl) {
        const fragmentUrl = form.dataset.fragmentUrl;
        const targetUrl = pageUrl ? new URL(pageUrl, window.location.origin) : new URL(form.action, window.location.origin);
        const params = pageUrl ? targetUrl.searchParams : new URLSearchParams(new FormData(form));

        return {
            fragment: fragmentUrl + (params.toString() ? '?' + params.toString() : ''),
            page: form.action + (params.toString() ? '?' + params.toString() : '')
        };
    }

    function loadCatalog(form, pageUrl, updateHistory) {
        const targetSelector = form.dataset.target;
        const target = document.querySelector(targetSelector);

        if (!target || !form.dataset.fragmentUrl) {
            return;
        }

        const urls = buildFragmentUrl(form, pageUrl);
        target.classList.add('is-loading');

        fetch(urls.fragment, {
            headers: {
                'X-Requested-With': 'XMLHttpRequest'
            }
        })
            .then(function (response) {
                if (!response.ok) {
                    throw new Error('No se pudo cargar el catálogo.');
                }
                return response.text();
            })
            .then(function (html) {
                target.innerHTML = html;
                if (updateHistory) {
                    window.history.pushState({ catalogUrl: urls.page }, '', urls.page);
                }
            })
            .catch(function () {
                window.location.href = pageUrl || urls.page;
            })
            .finally(function () {
                target.classList.remove('is-loading');
            });
    }

    document.querySelectorAll('[data-catalog-form]').forEach(function (form) {
        form.addEventListener('submit', function (event) {
            event.preventDefault();
            const pageInput = form.querySelector('input[name="page"]');
            if (pageInput) {
                pageInput.value = '0';
            }
            loadCatalog(form, null, true);
        });

        document.addEventListener('click', function (event) {
            const link = event.target.closest('.client-pagination a');
            const target = document.querySelector(form.dataset.target);

            if (!link || !target || !target.contains(link)) {
                return;
            }

            if (link.classList.contains('disabled') || link.classList.contains('active')) {
                event.preventDefault();
                return;
            }

            event.preventDefault();
            loadCatalog(form, link.href, true);
        });

        window.addEventListener('popstate', function () {
            loadCatalog(form, window.location.href, false);
        });
    });
})();
