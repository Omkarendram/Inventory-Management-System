(function () {
    const storageKey = 'inventory-theme';

    function getInitialTheme() {
        const savedTheme = localStorage.getItem(storageKey);
        if (savedTheme === 'light' || savedTheme === 'dark') {
            return savedTheme;
        }

        return window.matchMedia('(prefers-color-scheme: dark)').matches ? 'dark' : 'light';
    }

    function setTheme(theme) {
        document.documentElement.setAttribute('data-theme', theme);
        localStorage.setItem(storageKey, theme);

        const toggle = document.getElementById('themeToggleButton');
        if (toggle) {
            toggle.textContent = theme === 'dark' ? '☀ Light' : '🌙 Dark';
            toggle.setAttribute('aria-label', theme === 'dark' ? 'Switch to light mode' : 'Switch to dark mode');
            toggle.setAttribute('title', '☀ Light / 🌙 Dark');
        }
    }

    function ensureToggle() {
        if (document.getElementById('themeToggleButton')) {
            return;
        }

        const sidebar = document.querySelector('.sidebar');
        const button = document.createElement('button');
        button.type = 'button';
        button.id = 'themeToggleButton';
        button.className = 'theme-toggle-btn';
        button.addEventListener('click', function () {
            const currentTheme = document.documentElement.getAttribute('data-theme') === 'dark' ? 'dark' : 'light';
            setTheme(currentTheme === 'dark' ? 'light' : 'dark');
        });

        if (sidebar) {
            const wrapper = document.createElement('div');
            wrapper.className = 'sidebar-theme-toggle';
            wrapper.appendChild(button);
            sidebar.appendChild(wrapper);
        } else {
            button.classList.add('theme-toggle-btn-floating');
            document.body.appendChild(button);
        }

        setTheme(document.documentElement.getAttribute('data-theme') || getInitialTheme());
    }

    document.documentElement.setAttribute('data-theme', getInitialTheme());

    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', ensureToggle);
    } else {
        ensureToggle();
    }
})();
