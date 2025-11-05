<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.multiplication.session.SessionInfo" %>
<%
    SessionInfo sessionInfo = (SessionInfo) session.getAttribute("sessionInfo");
    if (sessionInfo == null) {
        response.sendRedirect(request.getContextPath() + "/login");
        return;
    }
%>
<!DOCTYPE html>
<html>
<head>
    <title>Agent</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background: #f8f9fa;
            margin: 0;
            padding: 0;
        }

        .header {
            background: #2c3e50;
            color: white;
            padding: 1rem;
        }

        .container {
            max-width: 1200px;
            margin: 0 auto;
            padding: 1rem;
        }

        .welcome {
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        .card {
            background: white;
            border-radius: 5px;
            padding: 1.5rem;
            margin-bottom: 1rem;
            box-shadow: 0 1px 3px rgba(0,0,0,0.1);
        }

        h2 {
            color: #333;
            margin-bottom: 1rem;
            padding-bottom: 0.5rem;
            border-bottom: 1px solid #ddd;
        }

        .form-group {
            margin-bottom: 1rem;
        }

        label {
            display: block;
            margin-bottom: 0.5rem;
            font-weight: 500;
        }

        input, select {
            width: 100%;
            padding: 0.5rem;
            border: 1px solid #ddd;
            border-radius: 3px;
        }

        button {
            background: #3498db;
            color: white;
            border: none;
            padding: 0.5rem 1rem;
            border-radius: 3px;
            cursor: pointer;
        }

        button:hover {
            background: #2980b9;
        }

        .message {
            background: #d4edda;
            color: #155724;
            padding: 1rem;
            border-radius: 3px;
            margin-bottom: 1rem;
        }

        .error {
            background: #f8d7da;
            color: #721c24;
            padding: 1rem;
            border-radius: 3px;
            margin-bottom: 1rem;
        }

        .two-column {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 1rem;
        }

        .grid-form {
            display: grid;
            grid-template-columns: repeat(4, 1fr);
            gap: 1rem;
        }

        .logout {
            color: white;
            text-decoration: none;
        }

        .logout:hover {
            text-decoration: underline;
        }

        small {
            color: #777;
            font-size: 0.875rem;
        }

        @media (max-width: 768px) {
            .two-column, .grid-form {
                grid-template-columns: 1fr;
            }
        }
    </style>
</head>
<body>
    <div class="header">
        <div class="container">
            <div class="welcome">
                <div>
                    <h1>Bienvenue, <%= sessionInfo.getUsername() %></h1>
                    <p>Agent Bancaire</p>
                </div>
                <a href="<%= request.getContextPath() %>/logout" class="logout">Déconnexion</a>
            </div>
        </div>
    </div>
    
    <div class="container">
        <% if (request.getAttribute("message") != null) { %>
            <div class="message">
                <%= request.getAttribute("message") %>
            </div>
        <% } %>
        
        <% if (request.getAttribute("error") != null) { %>
            <div class="error">
                <%= request.getAttribute("error") %>
            </div>
        <% } %>
        
        <div class="two-column">
            <div class="card">
                <h2>Effectuer un virement</h2>
                <form method="post" action="<%= request.getContextPath() %>/agent/dashboard">
                    <input type="hidden" name="action" value="effectuerVirement">
                    
                    <div class="form-group">
                        <label>Compte émetteur</label>
                        <select name="compteEmetteur" required>
                            <%
                                java.util.List<com.multiplication.model.CompteCourant> comptesEmetteur = (java.util.List<com.multiplication.model.CompteCourant>) request.getAttribute("comptesEmetteur");
                                if (comptesEmetteur != null && !comptesEmetteur.isEmpty()) {
                                    for (com.multiplication.model.CompteCourant c : comptesEmetteur) {
                            %>
                                        <option value="<%= c.getIdCompteCourant() %>"><%= (c.getUtilisateur() != null ? c.getUtilisateur().getUsername() : ("#" + c.getIdCompteCourant())) %> — <%= c.getSolde() %> AR</option>
                            <%
                                    }
                                }
                            %>
                        </select>
                        <small>Vos comptes seulement</small>
                    </div>
                    
                    <div class="form-group">
                        <label>Compte bénéficiaire</label>
                        <select name="compteBeneficiaire" required>
                            <%
                                java.util.List<com.multiplication.model.CompteCourant> comptesBeneficiaire = (java.util.List<com.multiplication.model.CompteCourant>) request.getAttribute("comptesBeneficiaire");
                                if (comptesBeneficiaire != null && !comptesBeneficiaire.isEmpty()) {
                                    for (com.multiplication.model.CompteCourant c : comptesBeneficiaire) {
                            %>
                                        <option value="<%= c.getIdCompteCourant() %>"><%= (c.getUtilisateur() != null ? c.getUtilisateur().getUsername() : ("#" + c.getIdCompteCourant())) %> — <%= c.getSolde() %> AR</option>
                            <%
                                    }
                                }
                            %>
                        </select>
                        <small>Tous les comptes disponibles</small>
                    </div>
                    
                    <div class="form-group">
                        <label>Montant</label>
                        <input type="number" name="montant" step="0.01" required>
                    </div>
                    
                    <div class="form-group">
                        <label>Devise</label>
                        <select name="devise" required>
                            <option value="AR">AR (Ariary)</option>
                            <%
                                java.util.List<com.devises.model.Devise> listeDevisesDedup = (java.util.List<com.devises.model.Devise>) request.getAttribute("listeDevisesDedup");
                                if (listeDevisesDedup != null && !listeDevisesDedup.isEmpty()) {
                                    for (com.devises.model.Devise dv : listeDevisesDedup) {
                            %>
                                        <option value="<%= dv.getNomDevise() %>"><%= dv.getNomDevise() %> (<%= dv.getCours() %>)</option>
                            <%
                                    }
                                } else {
                            %>
                                        <option value="EUR">EUR</option>
                                        <option value="USD">USD</option>
                                        <option value="GBP">GBP</option>
                                        <option value="JPY">JPY</option>
                                        <option value="CHF">CHF</option>
                            <%
                                }
                            %>
                        </select>
                    </div>
                    
                    <div class="form-group">
                        <label>Date du virement</label>
                        <input type="date" name="date" required>
                    </div>
                    
                    <button type="submit">Effectuer le virement</button>
                </form>
            </div>
            
            <div class="card">
                <h2>Changer la devise d'une transaction</h2>
                <form method="post" action="<%= request.getContextPath() %>/agent/dashboard">
                    <input type="hidden" name="action" value="changerDevise">
                    
                    <div class="form-group">
                        <label>ID Transaction</label>
                        <input type="number" name="idTransaction" required>
                    </div>
                    
                    <div class="form-group">
                        <label>Nouvelle devise</label>
                        <select name="nouvelleDevise" required>
                            <option value="AR">AR (Ariary)</option>
                            <%
                                if (listeDevisesDedup != null && !listeDevisesDedup.isEmpty()) {
                                    for (com.devises.model.Devise dv : listeDevisesDedup) {
                            %>
                                        <option value="<%= dv.getNomDevise() %>"><%= dv.getNomDevise() %> (<%= dv.getCours() %>)</option>
                            <%
                                    }
                                } else {
                            %>
                                        <option value="EUR">EUR</option>
                                        <option value="USD">USD</option>
                                        <option value="GBP">GBP</option>
                                        <option value="JPY">JPY</option>
                                        <option value="CHF">CHF</option>
                            <%
                                }
                            %>
                        </select>
                    </div>
                    
                    <button type="submit">Changer la devise</button>
                </form>
            </div>
        </div>

        <div class="card">
            <h2>Ajouter un cours</h2>
            <form method="post" action="<%= request.getContextPath() %>/agent/dashboard" class="grid-form">
                <input type="hidden" name="action" value="ajouterCours">
                <div class="form-group">
                    <label>Devise source</label>
                    <select name="deviseSource" required>
                        <option value="AR">AR</option>
                        <%
                            if (listeDevisesDedup != null && !listeDevisesDedup.isEmpty()) {
                                for (com.devises.model.Devise dv : listeDevisesDedup) {
                        %>
                                    <option value="<%= dv.getNomDevise() %>"><%= dv.getNomDevise() %> (<%= dv.getCours() %>)</option>
                        <%
                                }
                            } else {
                        %>
                                <option value="EUR">EUR</option>
                                <option value="USD">USD</option>
                                <option value="GBP">GBP</option>
                                <option value="JPY">JPY</option>
                                <option value="CHF">CHF</option>
                        <%
                            }
                        %>
                    </select>
                </div>
                <div class="form-group">
                    <label>Devise cible</label>
                    <select name="deviseCible" required>
                        <option value="AR">AR</option>
                        <%
                            if (listeDevisesDedup != null && !listeDevisesDedup.isEmpty()) {
                                for (com.devises.model.Devise dv : listeDevisesDedup) {
                        %>
                                    <option value="<%= dv.getNomDevise() %>"><%= dv.getNomDevise() %> (<%= dv.getCours() %>)</option>
                        <%
                                }
                            } else {
                        %>
                                <option value="EUR">EUR</option>
                                <option value="USD">USD</option>
                                <option value="GBP">GBP</option>
                                <option value="JPY">JPY</option>
                                <option value="CHF">CHF</option>
                        <%
                            }
                        %>
                    </select>
                </div>
                <div class="form-group">
                    <label>Montant de référence</label>
                    <input type="number" name="montantCours" step="0.01" placeholder="Ex: 100.00" required />
                </div>
                <div>
                    <button type="submit">Ajouter au journal</button>
                </div>
            </form>
            <small>Le taux est calculé automatiquement à partir des derniers cours.</small>
        </div>
        
        <div class="card">
            <h2>Informations</h2>
            <p><strong>Plafond journalier :</strong> 10.000.000 AR par compte</p>
            <p><strong>Votre rôle :</strong> <%= sessionInfo.getRole().getLibelle() %></p>
            <% if (sessionInfo.getDirections() != null && !sessionInfo.getDirections().isEmpty()) { %>
                <p><strong>Direction :</strong> <%= sessionInfo.getDirections().get(0).getLibelle() %> 
                   (Niveau <%= sessionInfo.getNiveauDirection() %>)</p>
            <% } %>
        </div>
    </div>
</body>
</html>