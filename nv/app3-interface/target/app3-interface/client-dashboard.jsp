<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.multiplication.session.SessionInfo" %>
<%@ page import="com.multiplication.model.CompteCourant" %>
<%@ page import="com.multiplication.model.Transaction" %>
<%@ page import="java.util.List" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.text.NumberFormat" %>
<%@ page import="java.util.Locale" %>
<%@ page import="java.math.BigDecimal" %>
<%@ page import="java.text.DecimalFormat" %>
<%
    SessionInfo sessionInfo = (SessionInfo) session.getAttribute("sessionInfo");
    if (sessionInfo == null) {
        response.sendRedirect(request.getContextPath() + "/login");
        return;
    }
    CompteCourant compte = (CompteCourant) request.getAttribute("compte");
    List<Transaction> transactions = (List<Transaction>) request.getAttribute("transactions");
    NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("fr", "MG"));
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    BigDecimal tauxAffichage = (BigDecimal) request.getAttribute("tauxAffichage");
    String deviseAffichage = (String) request.getAttribute("deviseAffichage");
    String dateCours = (String) request.getAttribute("dateCours");
    DecimalFormat df = new DecimalFormat("#,##0.00");
%>
<!DOCTYPE html>
<html>
<head>
    <title>Mon Compte</title>
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

        .solde-card {
            background: #3498db;
            color: white;
            text-align: center;
        }

        .solde-amount {
            font-size: 2rem;
            font-weight: bold;
            margin: 1rem 0;
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

        table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 1rem;
        }

        th, td {
            padding: 0.75rem;
            text-align: left;
            border-bottom: 1px solid #eee;
        }

        th {
            background: #f8f9fa;
            font-weight: 600;
        }

        .status-valide { color: #27ae60; }
        .status-en_attente { color: #f39c12; }
        .status-annule { color: #e74c3c; }

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

        .logout {
            color: white;
            text-decoration: none;
        }

        .logout:hover {
            text-decoration: underline;
        }

        .grid-form {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 1rem;
        }

        .flex-form {
            display: flex;
            gap: 1rem;
            align-items: end;
            flex-wrap: wrap;
            margin: 1rem 0;
        }
    </style>
</head>
<body>
    <div class="header">
        <div class="container">
            <div class="welcome">
                <div>
                    <h1>Bienvenue, <%= sessionInfo.getUsername() %></h1>
                    <p>Client</p>
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
        
        <% if (compte != null) { %>
            <div class="card solde-card">
                <h2>Votre solde</h2>
                <div class="solde-amount">
                    <%= currencyFormat.format(compte.getSolde()) %> AR
                </div>
                <p>Compte N° <%= compte.getIdCompteCourant() %></p>
                
                <form method="post" action="<%= request.getContextPath() %>/client/dashboard">
                    <input type="hidden" name="action" value="convertir">
                    <div class="grid-form">
                        <div class="form-group">
                            <label>Montant à convertir</label>
                            <input type="number" name="montant" step="0.01" required>
                        </div>
                        <div class="form-group">
                            <label>Devise</label>
                            <select name="devise" required>
                                <option value="">-- Choisir --</option>
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
                    </div>
                    <button type="submit">Convertir</button>
                </form>
            </div>
            
            <div class="card">
                <h2>Historique des transactions</h2>
                <form method="get" action="<%= request.getContextPath() %>/client/dashboard" class="flex-form">
                    <div class="form-group">
                        <label>Afficher en:</label>
                        <select name="deviseAffichage">
                            <option value="">AR (par défaut)</option>
                            <%
                                java.util.List<com.devises.model.Devise> listeDevisesDedup2 = (java.util.List<com.devises.model.Devise>) request.getAttribute("listeDevisesDedup");
                                if (listeDevisesDedup2 != null && !listeDevisesDedup2.isEmpty()) {
                                    for (com.devises.model.Devise dv : listeDevisesDedup2) {
                                        String code = dv.getNomDevise();
                            %>
                                        <option value="<%= code %>" <%= code.equals(deviseAffichage) ? "selected" : "" %>><%= code %> (<%= dv.getCours() %>)</option>
                            <%
                                    }
                                } else {
                            %>
                                        <option value="EUR" <%= "EUR".equals(deviseAffichage) ? "selected" : "" %>>EUR</option>
                                        <option value="USD" <%= "USD".equals(deviseAffichage) ? "selected" : "" %>>USD</option>
                                        <option value="GBP" <%= "GBP".equals(deviseAffichage) ? "selected" : "" %>>GBP</option>
                                        <option value="JPY" <%= "JPY".equals(deviseAffichage) ? "selected" : "" %>>JPY</option>
                                        <option value="CHF" <%= "CHF".equals(deviseAffichage) ? "selected" : "" %>>CHF</option>
                            <%
                                }
                            %>
                        </select>
                    </div>
                    <div class="form-group">
                        <label>Date du cours:</label>
                        <input type="date" name="dateCours" value="<%= dateCours != null ? dateCours : "" %>" />
                    </div>
                    <button type="submit">Appliquer</button>
                </form>
                
                <% if (transactions != null && !transactions.isEmpty()) { %>
                    <table>
                        <thead>
                            <tr>
                                <th>Référence</th>
                                <th>Date</th>
                                <th>Type</th>
                                <th>Montant</th>
                                <th>Devise</th>
                                <th>Statut</th>
                            </tr>
                        </thead>
                        <tbody>
                            <% for (Transaction t : transactions) { %>
                                <tr>
                                    <td>#<%= t.getIdTransaction() %></td>
                                    <td><%= dateFormat.format(t.getDateTransaction()) %></td>
                                    <td><%= t.getType().getLibelle() %></td>
                                    <td>
                                        <%
                                            if (tauxAffichage != null && deviseAffichage != null && !deviseAffichage.isEmpty()) {
                                                BigDecimal montantConv = t.getMontant().multiply(tauxAffichage);
                                                out.print(df.format(montantConv));
                                            } else {
                                                out.print(currencyFormat.format(t.getMontant()));
                                            }
                                        %>
                                    </td>
                                    <td>
                                        <%= (tauxAffichage != null && deviseAffichage != null && !deviseAffichage.isEmpty()) ? deviseAffichage : (t.getDevise() != null ? t.getDevise() : "AR") %>
                                    </td>
                                    <td class="status-<%= t.getStatut().toLowerCase().replace("_", "-") %>">
                                        <%= t.getStatut() %>
                                    </td>
                                </tr>
                            <% } %>
                        </tbody>
                    </table>
                <% } else { %>
                    <p>Aucune transaction pour le moment.</p>
                <% } %>
            </div>

            <div class="card">
                <h2>Ajouter une devise</h2>
                <form method="post" action="<%= request.getContextPath() %>/client/dashboard" class="grid-form">
                    <input type="hidden" name="action" value="ajouterDevise" />
                    <div class="form-group">
                        <label>Nom devise</label>
                        <input type="text" name="nomDevise" required />
                    </div>
                    <div class="form-group">
                        <label>Date début</label>
                        <input type="date" name="dateDebut" required />
                    </div>
                    <div class="form-group">
                        <label>Date fin</label>
                        <input type="date" name="dateFin" required />
                    </div>
                    <div class="form-group">
                        <label>Cours</label>
                        <input type="number" name="cours" step="0.0001" required />
                    </div>
                    <div>
                        <button type="submit">Enregistrer</button>
                    </div>
                </form>
            </div>
        <% } else { %>
            <div class="card">
                <p>Aucun compte associé à votre profil.</p>
            </div>
        <% } %>
    </div>
</body>
</html>