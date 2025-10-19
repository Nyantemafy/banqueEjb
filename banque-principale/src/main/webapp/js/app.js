const UIManager = {
    /**
     * Affiche un message d'alerte
     */
    showAlert(elementId, message, isError = false) {
        let container = document.getElementById('ui-toast-container');
        if (!container) {
            container = document.createElement('div');
            container.id = 'ui-toast-container';
            container.style.position = 'fixed';
            container.style.top = '16px';
            container.style.right = '16px';
            container.style.display = 'flex';
            container.style.flexDirection = 'column';
            container.style.gap = '10px';
            container.style.zIndex = '9999';
            document.body.appendChild(container);
        }

        const toast = document.createElement('div');
        toast.style.minWidth = '260px';
        toast.style.maxWidth = '360px';
        toast.style.padding = '12px 14px';
        toast.style.borderRadius = '8px';
        toast.style.boxShadow = '0 6px 20px rgba(0,0,0,0.15)';
        toast.style.color = isError ? '#7f1d1d' : '#064e3b';
        toast.style.background = isError ? '#fee2e2' : '#d1fae5';
        toast.style.border = isError ? '1px solid #fecaca' : '1px solid #a7f3d0';
        toast.style.display = 'flex';
        toast.style.alignItems = 'center';
        toast.style.justifyContent = 'space-between';
        toast.style.gap = '12px';

        const text = document.createElement('div');
        text.textContent = message;
        text.style.flex = '1';

        const btn = document.createElement('button');
        btn.textContent = '×';
        btn.style.border = 'none';
        btn.style.background = 'transparent';
        btn.style.cursor = 'pointer';
        btn.style.fontSize = '18px';
        btn.style.lineHeight = '1';
        btn.style.color = 'inherit';
        btn.onclick = () => {
            if (toast && toast.parentNode) toast.parentNode.removeChild(toast);
        };

        toast.appendChild(text);
        toast.appendChild(btn);
        container.appendChild(toast);

        setTimeout(() => {
            if (toast && toast.parentNode) toast.parentNode.removeChild(toast);
        }, CONFIG.ALERT_DURATION);
    },

    /**
     * Affiche un indicateur de chargement
     */
    showLoading(elementId, message = 'Chargement...') {
        const element = document.getElementById(elementId);
        if (!element) return;

        element.innerHTML = `
            <div class="loading">
                <div class="spinner"></div>
                <p>${message}</p>
            </div>
        `;
    },

    /**
     * Change d'onglet
     */
    showTab(tabName) {
        // Désactiver tous les onglets
        document.querySelectorAll('.tab').forEach(tab => {
            tab.classList.remove('active');
        });

        // Masquer tous les contenus
        document.querySelectorAll('.content').forEach(content => {
            content.classList.remove('active');
        });

        // Activer l'onglet sélectionné
        const selectedTab = document.querySelector(`[data-tab="${tabName}"]`);
        if (selectedTab) {
            selectedTab.classList.add('active');
        }

        // Afficher le contenu correspondant
        const selectedContent = document.getElementById(tabName);
        if (selectedContent) {
            selectedContent.classList.add('active');
        }
    }
};

/**
 * Application principale
 */
const App = {
    /**
     * Initialise l'application
     */
    init() {
        console.log('🚀 Initialisation du système bancaire...');
        
        // Initialiser les modules
        this.initModules();
        
        // Initialiser la navigation
        this.initNavigation();
        
        console.log('✅ Application initialisée');
    },

    /**
     * Initialise tous les modules
     */
    initModules() {
        ClientsModule.init();
        CompteCourantModule.init();
        CompteDepotModule.init();
        PretsModule.init();
    },

    /**
     * Initialise la navigation par onglets
     */
    initNavigation() {
        const tabs = document.querySelectorAll('.tab');
        
        tabs.forEach(tab => {
            tab.addEventListener('click', (e) => {
                const tabName = e.target.getAttribute('data-tab');
                UIManager.showTab(tabName);
            });
        });
    }
};

/**
 * Démarrage de l'application au chargement de la page
 */
document.addEventListener('DOMContentLoaded', () => {
    App.init();
});