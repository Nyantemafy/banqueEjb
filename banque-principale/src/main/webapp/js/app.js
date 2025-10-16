const UIManager = {
    /**
     * Affiche un message d'alerte
     */
    showAlert(elementId, message, isError = false) {
        const alert = document.getElementById(elementId);
        if (!alert) return;

        alert.className = `alert ${isError ? 'alert-error' : 'alert-success'} show`;
        alert.textContent = message;

        setTimeout(() => {
            alert.classList.remove('show');
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