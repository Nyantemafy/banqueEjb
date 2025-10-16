const CompteCourantModule = {
    
    init() {
        this.bindEvents();
    },

    bindEvents() {
        const soldeBtn = document.getElementById('ccSoldeBtn');
        const depotBtn = document.getElementById('ccDepotBtn');
        const retraitBtn = document.getElementById('ccRetraitBtn');
        const histoBtn = document.getElementById('ccHistoriqueBtn');

        if (soldeBtn) soldeBtn.addEventListener('click', () => this.consulterSolde());
        if (depotBtn) depotBtn.addEventListener('click', () => this.deposer());
        if (retraitBtn) retraitBtn.addEventListener('click', () => this.retirer());
        if (histoBtn) histoBtn.addEventListener('click', () => this.voirHistorique());
    },

    /**
     * Consulter le solde
     */
    async consulterSolde() {
        const numero = document.getElementById('ccNumero').value.trim();

        if (!numero) {
            UIManager.showAlert('ccAlert', 'Veuillez saisir un numéro de compte', true);
            return;
        }

        try {
            const result = await apiClient.getSoldeCompteCourant(numero);
            document.getElementById('ccSolde').innerHTML = 
                `<strong>Solde:</strong> ${formatCurrency(result.solde)}`;
            document.getElementById('ccSolde').style.color = '#28a745';
        } catch (error) {
            UIManager.showAlert('ccAlert', error.message, true);
            document.getElementById('ccSolde').innerHTML = '';
        }
    },

    /**
     * Déposer de l'argent
     */
    async deposer() {
        const numero = document.getElementById('ccNumero').value.trim();
        const montant = parseFloat(document.getElementById('ccDepotMontant').value);

        if (!numero || !montant || montant <= 0) {
            UIManager.showAlert('ccAlert', 'Données invalides', true);
            return;
        }

        try {
            const result = await apiClient.deposerCompteCourant(numero, montant);
            UIManager.showAlert('ccAlert', result.message || 'Dépôt effectué avec succès');
            document.getElementById('ccDepotMontant').value = '';
            this.consulterSolde();
        } catch (error) {
            UIManager.showAlert('ccAlert', error.message, true);
        }
    },

    /**
     * Retirer de l'argent
     */
    async retirer() {
        const numero = document.getElementById('ccNumero').value.trim();
        const montant = parseFloat(document.getElementById('ccRetraitMontant').value);

        if (!numero || !montant || montant <= 0) {
            UIManager.showAlert('ccAlert', 'Données invalides', true);
            return;
        }

        try {
            const result = await apiClient.retirerCompteCourant(numero, montant);
            UIManager.showAlert('ccAlert', result.message || 'Retrait effectué avec succès');
            document.getElementById('ccRetraitMontant').value = '';
            this.consulterSolde();
        } catch (error) {
            UIManager.showAlert('ccAlert', error.message, true);
        }
    },

    /**
     * Voir l'historique des transactions
     */
    async voirHistorique() {
        const numero = document.getElementById('ccNumero').value.trim();

        if (!numero) {
            UIManager.showAlert('ccAlert', 'Veuillez saisir un numéro de compte', true);
            return;
        }

        try {
            UIManager.showLoading('ccHistorique', 'Chargement de l\'historique...');
            const transactions = await apiClient.getHistoriqueCompteCourant(numero);
            this.displayHistorique(transactions);
        } catch (error) {
            UIManager.showAlert('ccAlert', error.message, true);
            document.getElementById('ccHistorique').innerHTML = '';
        }
    },

    /**
     * Affiche l'historique
     */
    displayHistorique(transactions) {
        const container = document.getElementById('ccHistorique');

        if (!transactions || transactions.length === 0) {
            container.innerHTML = '<p style="text-align: center; color: #999; padding: 20px;">Aucune transaction</p>';
            return;
        }

        let html = `
            <table>
                <thead>
                    <tr>
                        <th>Date</th>
                        <th>Type</th>
                        <th>Montant</th>
                        <th>Solde après</th>
                    </tr>
                </thead>
                <tbody>
        `;

        transactions.forEach(tx => {
            const date = new Date(tx.dateTransaction).toLocaleString('fr-FR');
            const typeColor = tx.type === 'DEPOT' ? '#28a745' : '#dc3545';
            
            html += `
                <tr>
                    <td>${date}</td>
                    <td style="color: ${typeColor}; font-weight: bold;">${tx.type}</td>
                    <td>${formatCurrency(tx.montant)}</td>
                    <td>${formatCurrency(tx.soldeApres)}</td>
                </tr>
            `;
        });

        html += '</tbody></table>';
        container.innerHTML = html;
    }
};