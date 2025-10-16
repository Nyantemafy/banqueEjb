const CompteDepotModule = {
    
    init() {
        this.bindEvents();
    },

    bindEvents() {
        const soldeBtn = document.getElementById('depotSoldeBtn');
        const depotBtn = document.getElementById('depotDepotBtn');
        const retraitBtn = document.getElementById('depotRetraitBtn');

        if (soldeBtn) soldeBtn.addEventListener('click', () => this.consulterSolde());
        if (depotBtn) depotBtn.addEventListener('click', () => this.deposer());
        if (retraitBtn) retraitBtn.addEventListener('click', () => this.retirer());
    },

    /**
     * Consulter le solde
     */
    async consulterSolde() {
        const numero = document.getElementById('depotNumero').value.trim();

        if (!numero) {
            UIManager.showAlert('depotAlert', 'Veuillez saisir un numéro de compte', true);
            return;
        }

        try {
            const result = await apiClient.getSoldeCompteDepot(numero);
            document.getElementById('depotSolde').innerHTML = 
                `<strong>Solde:</strong> ${formatCurrency(result.solde)}`;
            document.getElementById('depotSolde').style.color = '#0ea5e9';
        } catch (error) {
            UIManager.showAlert('depotAlert', error.message, true);
            document.getElementById('depotSolde').innerHTML = '';
        }
    },

    /**
     * Déposer de l'argent
     */
    async deposer() {
        const numero = document.getElementById('depotNumero').value.trim();
        const montant = parseFloat(document.getElementById('depotDepotMontant').value);

        if (!numero || !montant || montant <= 0) {
            UIManager.showAlert('depotAlert', 'Données invalides', true);
            return;
        }

        try {
            const result = await apiClient.deposerCompteDepot(numero, montant);
            UIManager.showAlert('depotAlert', result.message || 'Dépôt effectué avec succès');
            document.getElementById('depotDepotMontant').value = '';
            this.consulterSolde();
        } catch (error) {
            UIManager.showAlert('depotAlert', error.message, true);
        }
    },

    /**
     * Retirer de l'argent
     */
    async retirer() {
        const numero = document.getElementById('depotNumero').value.trim();
        const montant = parseFloat(document.getElementById('depotRetraitMontant').value);

        if (!numero || !montant || montant <= 0) {
            UIManager.showAlert('depotAlert', 'Données invalides', true);
            return;
        }

        try {
            const result = await apiClient.retirerCompteDepot(numero, montant);
            UIManager.showAlert('depotAlert', result.message || 'Retrait effectué avec succès');
            document.getElementById('depotRetraitMontant').value = '';
            this.consulterSolde();
        } catch (error) {
            UIManager.showAlert('depotAlert', error.message, true);
        }
    }
};