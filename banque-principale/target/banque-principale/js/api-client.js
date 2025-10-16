class ApiClient {
    constructor(baseUrl) {
        this.baseUrl = baseUrl;
    }

    /**
     * Requête GET générique
     */
    async get(endpoint) {
        try {
            const response = await fetch(`${this.baseUrl}${endpoint}`, {
                method: 'GET',
                headers: {
                    'Accept': 'application/json'
                }
            });
            return await this.handleResponse(response);
        } catch (error) {
            throw new Error(`Erreur GET ${endpoint}: ${error.message}`);
        }
    }

    /**
     * Requête POST générique
     */
    async post(endpoint, data) {
        try {
            const response = await fetch(`${this.baseUrl}${endpoint}`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Accept': 'application/json'
                },
                body: JSON.stringify(data)
            });
            return await this.handleResponse(response);
        } catch (error) {
            throw new Error(`Erreur POST ${endpoint}: ${error.message}`);
        }
    }

    /**
     * Gestion de la réponse
     */
    async handleResponse(response) {
        const contentType = response.headers.get('content-type');
        
        if (!contentType || !contentType.includes('application/json')) {
            throw new Error('Réponse non-JSON reçue du serveur');
        }

        const data = await response.json();

        if (!response.ok) {
            throw new Error(data.error || `Erreur HTTP: ${response.status}`);
        }

        return data;
    }

    // =================== API CLIENTS ===================

    async getAllClients() {
        return this.get('/clients');
    }

    async getClient(numero) {
        return this.get(`/clients/${numero}`);
    }

    async createClient(clientData) {
        return this.post('/clients', clientData);
    }

    // =================== API COMPTE COURANT ===================

    async getSoldeCompteCourant(numero) {
        return this.get(`/comptes/courant/${numero}/solde`);
    }

    async deposerCompteCourant(numero, montant) {
        return this.post(`/comptes/courant/${numero}/depot?montant=${encodeURIComponent(montant.toString())}`, {});
    }

    async retirerCompteCourant(numero, montant) {
        return this.post(`/comptes/courant/${numero}/retrait?montant=${encodeURIComponent(montant.toString())}`, {});
    }

    async getHistoriqueCompteCourant(numero) {
        return this.get(`/comptes/courant/${numero}/historique`);
    }

    // =================== API COMPTE DÉPÔT ===================

    async getSoldeCompteDepot(numero) {
        return this.get(`/comptes/depot/${numero}/solde`);
    }

    async deposerCompteDepot(numero, montant) {
        return this.post(`/comptes/depot/${numero}/depot?montant=${encodeURIComponent(montant.toString())}`, {});
    }

    async retirerCompteDepot(numero, montant) {
        return this.post(`/comptes/depot/${numero}/retrait?montant=${encodeURIComponent(montant.toString())}`, {});
    }

    // =================== API PRÊTS ===================

    async demanderPret(numeroClient, montant, duree, objet) {
        const qp = `numeroClient=${encodeURIComponent(numeroClient)}&montant=${encodeURIComponent(montant.toString())}&duree=${encodeURIComponent(duree.toString())}&objet=${encodeURIComponent(objet)}`;
        return this.post(`/prets/demander?${qp}`, {});
    }

    async getDemandesEnAttente() {
        return this.get('/prets/demandes?statut=EN_ATTENTE');
    }

    async approuverDemande(numeroDemande) {
        const adminClient = window.localStorage.getItem('adminClient');
        return fetch(`${this.baseUrl}/prets/demandes/${encodeURIComponent(numeroDemande)}/approuver`, {
            method: 'POST',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json',
                ...(adminClient ? { 'X-Admin-Client': adminClient } : {})
            },
            body: JSON.stringify({})
        }).then(res => this.handleResponse(res));
    }

    async rejeterDemande(numeroDemande, motif) {
        const adminClient = window.localStorage.getItem('adminClient');
        return fetch(`${this.baseUrl}/prets/demandes/${encodeURIComponent(numeroDemande)}/rejeter?motif=${encodeURIComponent(motif)}`, {
            method: 'POST',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json',
                ...(adminClient ? { 'X-Admin-Client': adminClient } : {})
            },
            body: JSON.stringify({})
        }).then(res => this.handleResponse(res));
    }

    async getPretsClient(numeroClient) {
        return this.get(`/prets/client/${numeroClient}`);
    }

    async calculerMensualite(montant, duree) {
        const qp = `montant=${encodeURIComponent(montant.toString())}&duree=${encodeURIComponent(duree.toString())}`;
        return this.get(`/prets/calculer-mensualite?${qp}`);
    }
}

// Instance globale du client API
const apiClient = new ApiClient(CONFIG.API_BASE_URL);

