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
            this.updateAdminStatus(savedAdmin);
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
            this.showAlert('Veuillez remplir tous les champs', 'error');
            return;
        }

        if (montant <= 0 || duree <= 0) {
            this.showAlert('Montant et durée doivent être positifs', 'error');
            return;
        }

        try {
            this.showLoading('Soumission en cours...');
            const result = await apiClient.demanderPret(numeroClient, montant, duree, objet);
            this.showAlert(`✅ Demande créée avec succès. Numéro: ${result.numeroDemande}`, 'success');
            this.clearDemandeForm();
        } catch (error) {
            this.showAlert(error.message, 'error');
        } finally {
            this.hideLoading();
        }
    },

    /**
     * Login admin
     */
    loginAdmin() {
        const input = document.getElementById('adminClientNumero');
        const adminNumero = input ? input.value.trim() : '';
        if (!adminNumero) {
            this.showAlert('Entrez un numéro client admin', 'error');
            return;
        }
        window.localStorage.setItem('adminClient', adminNumero);
        this.updateAdminStatus(adminNumero);
        this.showAlert('🔐 Mode admin activé. Vous pouvez maintenant approuver/rejeter les demandes.', 'success');
    },

    /**
     * Calculer la mensualité
     */
    async calculerMensualite() {
        const montant = parseFloat(document.getElementById('calcMontant').value);
        const duree = parseInt(document.getElementById('calcDuree').value);

        if (!montant || !duree || montant <= 0 || duree <= 0) {
            this.showAlert('Données invalides pour le calcul', 'error');
            return;
        }

        try {
            this.showLoading('Calcul en cours...');
            const result = await apiClient.calculerMensualite(montant, duree);
            this.displayCalculResult(result.mensualite, duree, montant);
        } catch (error) {
            this.showAlert(error.message, 'error');
            document.getElementById('mensualiteResult').innerHTML = '';
        } finally {
            this.hideLoading();
        }
    },

    /**
     * Afficher le résultat du calcul
     */
    displayCalculResult(mensualite, duree, montant) {
        const totalInterets = (mensualite * duree) - montant;
        const tauxAnnuel = 4.5; // Taux fixe pour l'exemple
        
        document.getElementById('mensualiteResult').innerHTML = `
            <div class="calculation-card">
                <div class="calculation-header">
                    <i class="fas fa-calculator"></i>
                    <h4>Simulation de Prêt</h4>
                </div>
                <div class="calculation-grid">
                    <div class="calc-item">
                        <span class="calc-label">Mensualité</span>
                        <span class="calc-value highlight">${this.formatCurrency(mensualite)}</span>
                    </div>
                    <div class="calc-item">
                        <span class="calc-label">Durée</span>
                        <span class="calc-value">${duree} mois</span>
                    </div>
                    <div class="calc-item">
                        <span class="calc-label">Montant</span>
                        <span class="calc-value">${this.formatCurrency(montant)}</span>
                    </div>
                    <div class="calc-item">
                        <span class="calc-label">Taux annuel</span>
                        <span class="calc-value">${tauxAnnuel}%</span>
                    </div>
                    <div class="calc-item">
                        <span class="calc-label">Intérêts totaux</span>
                        <span class="calc-value">${this.formatCurrency(totalInterets)}</span>
                    </div>
                    <div class="calc-item">
                        <span class="calc-label">Coût total</span>
                        <span class="calc-value">${this.formatCurrency(mensualite * duree)}</span>
                    </div>
                </div>
            </div>
        `;
    },

    /**
     * Lister les demandes en attente
     */
    async listerDemandesEnAttente() {
        try {
            this.showLoading('Chargement des demandes...', 'demandesList');
            const demandes = await apiClient.getDemandesEnAttente();
            this.displayDemandes(demandes);
        } catch (error) {
            this.showAlert(error.message, 'error');
            document.getElementById('demandesList').innerHTML = '';
        } finally {
            this.hideLoading();
        }
    },

    /**
     * Affiche les demandes avec design moderne
     */
    displayDemandes(demandes) {
        const container = document.getElementById('demandesList');

        if (!demandes || demandes.length === 0) {
            container.innerHTML = `
                <div class="empty-state">
                    <i class="fas fa-inbox"></i>
                    <h3>Aucune demande en attente</h3>
                    <p>Les nouvelles demandes de prêt apparaîtront ici</p>
                </div>
            `;
            return;
        }

        let html = '<div class="demandes-grid">';
        
        const isAdmin = window.localStorage.getItem('adminClient');

        demandes.forEach((demande, index) => {
            const date = new Date(demande.dateDemande).toLocaleDateString('fr-FR', {
                year: 'numeric',
                month: 'long',
                day: 'numeric'
            });
            
            const statut = (demande.statut || 'EN_ATTENTE').toUpperCase();
            const statutConfig = this.getStatutConfig(statut);

            html += `
                <div class="demande-card ${statutConfig.class}" data-demand-id="${demande.numeroDemande}">
                    <div class="demande-header">
                        <div class="demande-badge ${statutConfig.badgeClass}">
                            <i class="${statutConfig.icon}"></i>
                            ${statutConfig.label}
                        </div>
                        <div class="demande-numero">#${demande.numeroDemande}</div>
                    </div>
                    
                    <div class="demande-client">
                        <i class="fas fa-user"></i>
                        <span>${demande.numeroClient}</span>
                    </div>
                    
                    <div class="demande-montant">
                        <div class="montant">${this.formatCurrency(demande.montantDemande)}</div>
                        <div class="duree">${demande.dureeEnMois} mois</div>
                    </div>
                    
                    <div class="demande-objet">
                        <i class="fas fa-tag"></i>
                        ${demande.objetPret || demande.objet || 'Non spécifié'}
                    </div>
                    
                    <div class="demande-date">
                        <i class="fas fa-calendar"></i>
                        Déposée le ${date}
                    </div>
                    
                    ${isAdmin && statut === 'EN_ATTENTE' ? `
                    <div class="demande-actions">
                        <button class="btn-action btn-approve" onclick="PretsModule.approuverDemande('${demande.numeroDemande}')">
                            <i class="fas fa-check"></i>
                            Approuver
                        </button>
                        <button class="btn-action btn-reject" onclick="PretsModule.rejeterDemande('${demande.numeroDemande}')">
                            <i class="fas fa-times"></i>
                            Rejeter
                        </button>
                    </div>
                    ` : ''}
                    
                    ${statut !== 'EN_ATTENTE' ? `
                    <div class="demande-motif">
                        <strong>Motif:</strong> ${demande.motifRejet || 'Demande traitée'}
                    </div>
                    ` : ''}
                </div>
            `;
        });

        html += '</div>';
        container.innerHTML = html;
    },

    /**
     * Configuration des statuts
     */
    getStatutConfig(statut) {
        const configs = {
            'EN_ATTENTE': {
                label: 'En attente',
                icon: 'fas fa-clock',
                badgeClass: 'badge-waiting',
                class: 'demande-waiting'
            },
            'APPROUVEE': {
                label: 'Approuvée',
                icon: 'fas fa-check-circle',
                badgeClass: 'badge-approved',
                class: 'demande-approved'
            },
            'REJETEE': {
                label: 'Rejetée',
                icon: 'fas fa-times-circle',
                badgeClass: 'badge-rejected',
                class: 'demande-rejected'
            }
        };
        
        return configs[statut] || configs['EN_ATTENTE'];
    },

    /**
     * Approuver une demande
     */
    async approuverDemande(numeroDemande) {
        if (!confirm(`Êtes-vous sûr de vouloir approuver la demande ${numeroDemande} ?`)) {
            return;
        }

        try {
            this.showLoading('Traitement en cours...');
            const result = await apiClient.approuverDemande(numeroDemande);
            this.showAlert('✅ Demande approuvée avec succès', 'success');
            this.listerDemandesEnAttente();
        } catch (error) {
            this.showAlert(error.message, 'error');
        } finally {
            this.hideLoading();
        }
    },

    /**
     * Rejeter une demande
     */
    async rejeterDemande(numeroDemande) {
        const motif = prompt('Veuillez saisir le motif du rejet:');
        
        if (!motif) {
            this.showAlert('Le motif est obligatoire pour rejeter une demande', 'warning');
            return;
        }

        try {
            this.showLoading('Traitement en cours...');
            const result = await apiClient.rejeterDemande(numeroDemande, motif);
            this.showAlert('❌ Demande rejetée', 'success');
            this.listerDemandesEnAttente();
        } catch (error) {
            this.showAlert(error.message, 'error');
        } finally {
            this.hideLoading();
        }
    },

    /**
     * Mettre à jour le statut admin
     */
    updateAdminStatus(adminNumero) {
        const status = document.getElementById('adminStatus');
        if (status) {
            status.innerHTML = `
                <div class="admin-status-active">
                    <i class="fas fa-shield-alt"></i>
                    <span>Connecté en tant qu'admin: <strong>${adminNumero}</strong></span>
                </div>
            `;
        }
    },

    /**
     * Helpers
     */
    showAlert(message, type = 'info') {
        // Implémentez votre système d'alertes ici
        console.log(`[${type.toUpperCase()}] ${message}`);
        // Exemple basique :
        const alertDiv = document.createElement('div');
        alertDiv.className = `alert alert-${type} show`;
        alertDiv.textContent = message;
        document.getElementById('pretAlert').appendChild(alertDiv);
        
        setTimeout(() => {
            alertDiv.remove();
        }, 5000);
    },

    showLoading(message = 'Chargement...', containerId = null) {
        // Implémentez votre système de loading
        console.log(`[LOADING] ${message}`);
    },

    hideLoading() {
        // Implémentez votre système de loading
    },

    formatCurrency(amount) {
        return new Intl.NumberFormat('fr-FR', {
            style: 'currency',
            currency: 'EUR'
        }).format(amount);
    },

    clearDemandeForm() {
        document.getElementById('pretClientNumero').value = '';
        document.getElementById('pretMontant').value = '';
        document.getElementById('pretDuree').value = '';
        document.getElementById('pretObjet').value = '';
    }
};