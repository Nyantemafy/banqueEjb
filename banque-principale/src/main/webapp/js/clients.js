const ClientsModule = {
    
    /**
     * Initialise le module
     */
    init() {
        this.bindEvents();
    },

    /**
     * Lie les événements aux éléments
     */
    bindEvents() {
        const createBtn = document.getElementById('createClientBtn');
        const searchBtn = document.getElementById('searchClientBtn');

        if (createBtn) {
            createBtn.addEventListener('click', () => this.createClient());
        }
        if (searchBtn) {
            searchBtn.addEventListener('click', () => this.searchClient());
        }
    },

    /**
     * Crée un nouveau client
     */
    async createClient() {
        const clientData = {
            numeroClient: document.getElementById('clientNumero').value.trim(),
            nom: document.getElementById('clientNom').value.trim(),
            prenom: document.getElementById('clientPrenom').value.trim(),
            email: document.getElementById('clientEmail').value.trim()
        };

        // Validation
        if (!clientData.numeroClient || !clientData.nom || !clientData.prenom) {
            UIManager.showAlert('clientAlert', 'Veuillez remplir tous les champs obligatoires', true);
            return;
        }

        try {
            UIManager.showLoading('clientAlert', 'Création du client...');
            const result = await apiClient.createClient(clientData);
            
            if (result.success) {
                UIManager.showAlert('clientAlert', result.message || 'Client créé avec succès');
                this.clearForm();
            } else {
                UIManager.showAlert('clientAlert', result.error || 'Erreur création', true);
            }
        } catch (error) {
            UIManager.showAlert('clientAlert', error.message, true);
        }
    },

    /**
     * Recherche un client
     */
    async searchClient() {
        const numero = document.getElementById('searchClientNumero').value.trim();

        if (!numero) {
            UIManager.showAlert('clientAlert', 'Veuillez saisir un numéro de client', true);
            return;
        }

        try {
            UIManager.showLoading('clientDetails', 'Recherche en cours...');
            const client = await apiClient.getClient(numero);
            this.displayClientDetails(client);
        } catch (error) {
            UIManager.showAlert('clientAlert', error.message, true);
            document.getElementById('clientDetails').innerHTML = '';
        }
    },

    /**
     * Affiche les détails d'un client
     */
    displayClientDetails(client) {
        const container = document.getElementById('clientDetails');
        
        const patrimoineTotal = (
            parseFloat(client.soldeCompteCourant || 0) +
            parseFloat(client.soldeCompteDepot || 0) -
            parseFloat(client.totalPrets || 0)
        );

        container.innerHTML = `
            <div class="card">
                <h3>👤 ${client.prenom} ${client.nom}</h3>
                <div class="card-info">
                    <span>Numéro Client:</span>
                    <span><strong>${client.numeroClient}</strong></span>
                </div>
                <div class="card-info">
                    <span>Email:</span>
                    <span>${client.email || 'Non renseigné'}</span>
                </div>
                <div class="card-info">
                    <span>Téléphone:</span>
                    <span>${client.telephone || 'Non renseigné'}</span>
                </div>
                <hr style="margin: 15px 0; border: none; border-top: 1px solid #ddd;">
                <h4 style="color: #667eea; margin-bottom: 10px;">💰 Comptes</h4>
                <div class="card-info">
                    <span>Compte Courant (Java/EJB):</span>
                    <span style="color: #28a745; font-weight: bold;">
                        ${formatCurrency(client.soldeCompteCourant || 0)}
                    </span>
                </div>
                <div class="card-info">
                    <span>Compte Dépôt (.NET/WCF):</span>
                    <span style="color: #28a745; font-weight: bold;">
                        ${formatCurrency(client.soldeCompteDepot || 0)}
                    </span>
                </div>
                <div class="card-info">
                    <span>Total Prêts:</span>
                    <span style="color: #dc3545; font-weight: bold;">
                        ${formatCurrency(client.totalPrets || 0)}
                    </span>
                </div>
                <hr style="margin: 15px 0; border: none; border-top: 1px solid #ddd;">
                <div class="card-info">
                    <span><strong>Patrimoine Net:</strong></span>
                    <span style="font-size: 1.3em; color: ${patrimoineTotal >= 0 ? '#28a745' : '#dc3545'}; font-weight: bold;">
                        ${formatCurrency(patrimoineTotal)}
                    </span>
                </div>
            </div>
        `;
    },

    /**
     * Réinitialise le formulaire
     */
    clearForm() {
        document.getElementById('clientNumero').value = '';
        document.getElementById('clientNom').value = '';
        document.getElementById('clientPrenom').value = '';
        document.getElementById('clientEmail').value = '';
    }
};