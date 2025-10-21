// Simulation des données pour le frontend
const mockData = {
    user: {
        name: "Jean Dupont",
        accountNumber: "FR76 3000 4000 0100 1234 5678 900"
    },
    accounts: {
        courant: {
            solde: 5420.50,
            etat: "Actif"
        },
        depot: {
            solde: 12500.75,
            etat: "Actif",
            produits: [
                { nom: "Livret A", taux: 0.5, terme: "Illimité" },
                { nom: "LDDS", taux: 0.75, terme: "Illimité" },
                { nom: "PEL", taux: 1.0, terme: "4 ans" }
            ]
        }
    },
    credits: {
        montantRestant: 8500,
        prochaineEcheance: {
            date: "2023-12-15",
            montant: 450
        },
        echeances: [
            { date: "2023-12-15", montant: 450, statut: "À venir" },
            { date: "2024-01-15", montant: 450, statut: "À venir" },
            { date: "2024-02-15", montant: 450, statut: "À venir" }
        ]
    },
    transactions: [
        { date: "2023-11-10", description: "Retrait DAB", montant: -50, type: "retrait" },
        { date: "2023-11-08", description: "Virement reçu", montant: 1200, type: "depot" },
        { date: "2023-11-05", description: "Prélèvement EDF", montant: -85.30, type: "prelevement" }
    ],
    demandesPret: [
        { id: 1, client: "Marie Martin", montant: 15000, duree: 60, statut: "En attente" },
        { id: 2, client: "Pierre Durand", montant: 25000, duree: 84, statut: "En attente" }
    ]
};

// Gestion de la connexion
document.addEventListener('DOMContentLoaded', function() {
    const loginForm = document.getElementById('login-form');
    
    if (loginForm) {
        loginForm.addEventListener('submit', function(e) {
            e.preventDefault();
            
            const username = document.getElementById('username').value;
            const password = document.getElementById('password').value;
            
            // Simulation d'authentification
            if (username && password) {
                // Redirection vers le dashboard
                window.location.href = 'dashboard.html';
            } else {
                alert('Veuillez saisir un nom d\'utilisateur et un mot de passe');
            }
        });
    }
    
    // Initialisation du dashboard si nous sommes sur cette page
    if (window.location.pathname.includes('dashboard.html')) {
        initializeDashboard();
    }
    
    // Initialisation de la page admin si nous sommes sur cette page
    if (window.location.pathname.includes('admin.html')) {
        initializeAdminPage();
    }
    
    // Initialisation de la page compte courant si nous sommes sur cette page
    if (window.location.pathname.includes('compte-courant.html')) {
        initializeCompteCourant();
    }
    
    // Initialisation de la page compte depot si nous sommes sur cette page
    if (window.location.pathname.includes('compte-depot.html')) {
        initializeCompteDepot();
    }
    
    // Initialisation de la page credit si nous sommes sur cette page
    if (window.location.pathname.includes('credit.html')) {
        initializeCreditPage();
    }

    initializeNavigation();
});

// Fonctions d'initialisation des différentes pages
function initializeDashboard() {
    // Mise à jour des données du dashboard
    document.getElementById('solde-courant').textContent = `${mockData.accounts.courant.solde.toFixed(2)} €`;
    document.getElementById('solde-depot').textContent = `${mockData.accounts.depot.solde.toFixed(2)} €`;
    document.getElementById('montant-restant').textContent = `${mockData.credits.montantRestant.toFixed(2)} €`;
    document.getElementById('prochaine-echeance').textContent = `${mockData.credits.prochaineEcheance.montant.toFixed(2)} €`;
    
    // Calcul du total global
    const totalGlobal = mockData.accounts.courant.solde + mockData.accounts.depot.solde;
    document.getElementById('total-global').textContent = `${totalGlobal.toFixed(2)} €`;
    
    // Gestion des onglets
    const tabs = document.querySelectorAll('.nav-tab');
    tabs.forEach(tab => {
        tab.addEventListener('click', function() {
            tabs.forEach(t => t.classList.remove('active'));
            this.classList.add('active');
            
            // Ici, vous pourriez charger le contenu de l'onglet sélectionné
        });
    });
}

function initializeAdminPage() {
    // Remplir le tableau des demandes de prêt
    const tbody = document.querySelector('#demandes-pret tbody');
    tbody.innerHTML = '';
    
    mockData.demandesPret.forEach(demande => {
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td>${demande.id}</td>
            <td>${demande.client}</td>
            <td>${demande.montant.toFixed(2)} €</td>
            <td>${demande.duree} mois</td>
            <td>${demande.statut}</td>
            <td>
                <button class="btn-primary" onclick="approuverDemande(${demande.id})">Approuver</button>
                <button class="btn-secondary" onclick="rejeterDemande(${demande.id})">Rejeter</button>
            </td>
        `;
        tbody.appendChild(tr);
    });
}

function initializeCompteCourant() {
    // Mise à jour du solde
    document.getElementById('solde-compte-courant').textContent = `${mockData.accounts.courant.solde.toFixed(2)} €`;
    
    // Remplir l'historique des transactions
    const tbody = document.querySelector('#historique-transactions tbody');
    tbody.innerHTML = '';
    
    mockData.transactions.forEach(transaction => {
        const tr = document.createElement('tr');
        const montantClass = transaction.montant >= 0 ? 'montant-positif' : 'montant-negatif';
        
        tr.innerHTML = `
            <td>${transaction.date}</td>
            <td>${transaction.description}</td>
            <td class="${montantClass}">${transaction.montant >= 0 ? '+' : ''}${transaction.montant.toFixed(2)} €</td>
        `;
        tbody.appendChild(tr);
    });
    
    // Gestion des formulaires de dépôt et retrait
    const formDepot = document.getElementById('form-depot');
    const formRetrait = document.getElementById('form-retrait');
    
    if (formDepot) {
        formDepot.addEventListener('submit', function(e) {
            e.preventDefault();
            const montant = parseFloat(document.getElementById('montant-depot').value);
            if (montant > 0) {
                alert(`Dépôt de ${montant.toFixed(2)} € effectué avec succès`);
                formDepot.reset();
            }
        });
    }
    
    if (formRetrait) {
        formRetrait.addEventListener('submit', function(e) {
            e.preventDefault();
            const montant = parseFloat(document.getElementById('montant-retrait').value);
            if (montant > 0 && montant <= mockData.accounts.courant.solde) {
                alert(`Retrait de ${montant.toFixed(2)} € effectué avec succès`);
                formRetrait.reset();
            } else {
                alert('Montant invalide ou solde insuffisant');
            }
        });
    }
}

function initializeCompteDepot() {
    // Mise à jour du solde
    document.getElementById('solde-compte-depot').textContent = `${mockData.accounts.depot.solde.toFixed(2)} €`;
    
    // Afficher les détails des produits
    const produitsContainer = document.getElementById('details-produits');
    produitsContainer.innerHTML = '';
    
    mockData.accounts.depot.produits.forEach(produit => {
        const div = document.createElement('div');
        div.className = 'produit-card';
        div.innerHTML = `
            <h4>${produit.nom}</h4>
            <p>Taux: ${produit.taux}%</p>
            <p>Terme: ${produit.terme}</p>
        `;
        produitsContainer.appendChild(div);
    });
}

function initializeCreditPage() {
    // Mise à jour du montant restant dû
    document.getElementById('montant-restant-du').textContent = `${mockData.credits.montantRestant.toFixed(2)} €`;
    
    // Remplir le tableau des échéances
    const tbody = document.querySelector('#tableau-echeances tbody');
    tbody.innerHTML = '';
    
    mockData.credits.echeances.forEach(echeance => {
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td>${echeance.date}</td>
            <td>${echeance.montant.toFixed(2)} €</td>
            <td>${echeance.statut}</td>
        `;
        tbody.appendChild(tr);
    });
}

// Fonctions pour la page admin
function approuverDemande(id) {
    alert(`Demande ${id} approuvée`);
    // Ici, vous appelleriez votre service backend
}

function rejeterDemande(id) {
    alert(`Demande ${id} rejetée`);
    // Ici, vous appelleriez votre service backend
}

// Simulation des appels backend
function simulerAppelBackend(service, donnees) {
    console.log(`Appel au service: ${service}`, donnees);
    // Dans une application réelle, vous feriez un appel AJAX/Fetch ici
    return new Promise(resolve => {
        setTimeout(() => {
            resolve({ success: true, data: {} });
        }, 500);
    });
}

function initializeNavigation() {
    const menuToggle = document.getElementById('menuToggle');
    const navLinks = document.getElementById('navLinks');
    
    if (menuToggle && navLinks) {
        menuToggle.addEventListener('click', function() {
            navLinks.classList.toggle('active');
        });
    }
    
    // Mettre à jour le badge d'administration
    const adminBadge = document.getElementById('admin-badge');
    if (adminBadge) {
        const pendingRequests = mockData.demandesPret.filter(d => d.statut === 'En attente').length;
        adminBadge.textContent = pendingRequests;
        if (pendingRequests === 0) {
            adminBadge.style.display = 'none';
        }
    }
    
    // Mettre à jour les liens actifs selon la page courante
    updateActiveNavLink();
}

function updateActiveNavLink() {
    const currentPage = window.location.pathname.split('/').pop();
    const navLinks = document.querySelectorAll('.nav-link');
    
    navLinks.forEach(link => {
        link.classList.remove('active');
        const linkPage = link.getAttribute('href');
        if (currentPage === linkPage) {
            link.classList.add('active');
        }
    });
}
