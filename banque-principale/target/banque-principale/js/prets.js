const PretsModule = {
    
    init() {
        this.bindEvents();
    },

    bindEvents() {
        const demandeBtn = document.getElementById('pretDemandeBtn');
        const calcBtn = document.getElementById('pretCalculBtn');
        const listeBtn = document.getElementById('pretListeBtn');
        const adminBtn = document.getElementById('adminLoginBtn');

        if (demandeBtn) demandeBtn.addEventListener('click', () => this.demanderPret());
        if (calcBtn) calcBtn.addEventListener('click', () => this.calculerMensualite());
        if (listeBtn) listeBtn.addEventListener('click', () => this.listerDemandesEnAttente());
        if (adminBtn) adminBtn.addEventListener('click', () => this.loginAdmin());

        // Afficher l'état admin si déjà défini
        const savedAdmin = window.localStorage.getItem('adminClient');
        if (savedAdmin) {
            const status = document.getElementById('adminStatus');
            if (status) status.textContent = `Connecté en admin: ${savedAdmin}`;
        }
    },

    /**
     * Demander un prêt
     */
    async demanderPret() {
        const numeroClient = document.getElementById('pretClientNumero').value.trim();
        const montant = parseFloat(document.getElementById('pretMontant').value);
        const duree = parseInt(document.getElementById('pretDuree').value);
        const objet = document.getElementById('pretObjet').value.trim();

        if (!numeroClient || !montant || !duree || !objet) {
            UIManager.showAlert('pretAlert', 'Veuillez remplir tous les champs', true);
            return;
        }

        if (montant <= 0 || duree <= 0) {
            UIManager.showAlert('pretAlert', 'Montant et durée doivent être positifs', true);
            return;
        }

        try {
            const result = await apiClient.demanderPret(numeroClient, montant, duree, objet);
            UIManager.showAlert('pretAlert', 
                `Demande créée avec succès. Numéro: ${result.numeroDemande}`);
            this.clearDemandeForm();
        } catch (error) {
            UIManager.showAlert('pretAlert', error.message, true);
        }
    },

    /**
     * Login admin (sauvegarde du numero client admin)
     */
    loginAdmin() {
        const input = document.getElementById('adminClientNumero');
        const adminNumero = input ? input.value.trim() : '';
        if (!adminNumero) {
            UIManager.showAlert('pretAlert', 'Entrez un numéro client admin', true);
            return;
        }
        window.localStorage.setItem('adminClient', adminNumero);
        const status = document.getElementById('adminStatus');
        if (status) status.textContent = `Connecté en admin: ${adminNumero}`;
        UIManager.showAlert('pretAlert', 'Admin défini. Vous pouvez approuver/rejeter.', false);
    },

    /**
     * Calculer la mensualité
     */
    async calculerMensualite() {
        const montant = parseFloat(document.getElementById('calcMontant').value);
        const duree = parseInt(document.getElementById('calcDuree').value);

        if (!montant || !duree || montant <= 0 || duree <= 0) {
            UIManager.showAlert('pretAlert', 'Données invalides', true);
            return;
        }

        try {
            const result = await apiClient.calculerMensualite(montant, duree);
            document.getElementById('mensualiteResult').innerHTML = 
                `<strong>Mensualité:</strong> ${formatCurrency(result.mensualite)}<br>
                 <small>sur ${duree} mois (Taux: 4.5%)</small>`;
        } catch (error) {
            UIManager.showAlert('pretAlert', error.message, true);
            document.getElementById('mensualiteResult').innerHTML = '';
        }
    },

    /**
     * Lister les demandes en attente
     */
    async listerDemandesEnAttente() {
        try {
            UIManager.showLoading('demandesList', 'Chargement des demandes...');
            const demandes = await apiClient.getDemandesEnAttente();
            this.displayDemandes(demandes);
        } catch (error) {
            UIManager.showAlert('pretAlert', error.message, true);
            document.getElementById('demandesList').innerHTML = '';
        }
    },

    /**
     * Affiche les demandes
     */
    displayDemandes(demandes) {
        const container = document.getElementById('demandesList');

        if (!demandes || demandes.length === 0) {
            container.innerHTML = '<p style="text-align: center; color: #999; padding: 20px;">Aucune demande en attente</p>';
            return;
        }

        let html = '<div style="margin-top: 20px;">';
        
        demandes.forEach(demande => {
            const date = new Date(demande.dateDemande).toLocaleDateString('fr-FR');
            
            html += `
                <div class="card" style="border-left-color: #ed8936;">
                    <div style="display: flex; justify-content: space-between; align-items: start;">
                        <div style="flex: 1;">
                            <h4 style="color: #ed8936; margin-bottom: 10px;">
                                📋 Demande ${demande.numeroDemande}
                            </h4>
                            <div class="card-info">
                                <span>Client:</span>
                                <span>${demande.numeroClient}</span>
                            </div>
                            <div class="card-info">
                                <span>Montant:</span>
                                <span style="font-weight: bold; color: #667eea;">
                                    ${formatCurrency(demande.montant)}
                                </span>
                            </div>
                            <div class="card-info">
                                <span>Durée:</span>
                                <span>${demande.dureeEnMois} mois</span>
                            </div>
                            <div class="card-info">
                                <span>Objet:</span>
                                <span>${demande.objet}</span>
                            </div>
                            <div class="card-info">
                                <span>Date demande:</span>
                                <span>${date}</span>
                            </div>
                            <div class="card-info">
                                <span>Statut:</span>
                                <span style="color: #ff9800; font-weight: bold;">
                                    ${demande.statut}
                                </span>
                            </div>
                        </div>
                        <div class="action-btns" style="flex-direction: column;">
                            <button class="btn btn-success" 
                                onclick="PretsModule.approuverDemande('${demande.numeroDemande}')">
                                ✓ Approuver
                            </button>
                            <button class="btn btn-danger" 
                                onclick="PretsModule.rejeterDemande('${demande.numeroDemande}')">
                                ✗ Rejeter
                            </button>
                        </div>
                    </div>
                </div>
            `;
        });

        html += '</div>';
        container.innerHTML = html;
    },

    /**
     * Approuver une demande
     */
    async approuverDemande(numeroDemande) {
        if (!confirm(`Confirmer l'approbation de la demande ${numeroDemande} ?`)) {
            return;
        }

        try {
            const result = await apiClient.approuverDemande(numeroDemande);
            UIManager.showAlert('pretAlert', result.message || 'Demande approuvée');
            this.listerDemandesEnAttente();
        } catch (error) {
            UIManager.showAlert('pretAlert', error.message, true);
        }
    },

    /**
     * Rejeter une demande
     */
    async rejeterDemande(numeroDemande) {
        const motif = prompt('Motif du rejet:');
        
        if (!motif) {
            return;
        }

        try {
            const result = await apiClient.rejeterDemande(numeroDemande, motif);
            UIManager.showAlert('pretAlert', result.message || 'Demande rejetée');
            this.listerDemandesEnAttente();
        } catch (error) {
            UIManager.showAlert('pretAlert', error.message, true);
        }
    },

    /**
     * Réinitialise le formulaire de demande
     */
    clearDemandeForm() {
        document.getElementById('pretClientNumero').value = '';
        document.getElementById('pretMontant').value = '';
        document.getElementById('pretDuree').value = '';
        document.getElementById('pretObjet').value = '';
    }
};