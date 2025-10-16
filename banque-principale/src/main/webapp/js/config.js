const CONFIG = {
    // URL de base de l'API REST
    API_BASE_URL: 'http://localhost:8080/banque-principale/api',
    
    // Durée d'affichage des alertes (ms)
    ALERT_DURATION: 5000,
    
    // Messages
    MESSAGES: {
        ERROR_NETWORK: 'Erreur de connexion au serveur',
        ERROR_INVALID_DATA: 'Données invalides',
        SUCCESS_OPERATION: 'Opération effectuée avec succès'
    },
    
    // Format de devises
    CURRENCY: {
        symbol: '€',
        locale: 'fr-FR',
        decimals: 2
    }
};

// Fonction utilitaire pour formater les montants
function formatCurrency(amount) {
    return new Intl.NumberFormat(CONFIG.CURRENCY.locale, {
        style: 'currency',
        currency: 'EUR',
        minimumFractionDigits: CONFIG.CURRENCY.decimals,
        maximumFractionDigits: CONFIG.CURRENCY.decimals
    }).format(amount);
}

// Export pour utilisation dans d'autres modules
if (typeof module !== 'undefined' && module.exports) {
    module.exports = { CONFIG, formatCurrency };
}