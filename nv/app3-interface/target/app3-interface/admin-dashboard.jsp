<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.multiplication.session.SessionInfo" %>
<%@ page import="com.multiplication.model.Transaction" %>
<%@ page import="java.util.List" %>
<%@ page import="com.multiplication.model.Historique" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.text.NumberFormat" %>
<%@ page import="java.util.Locale" %>
<%
    SessionInfo sessionInfo = (SessionInfo) session.getAttribute("sessionInfo");
    if (sessionInfo == null) {
        response.sendRedirect(request.getContextPath() + "/login");
        return;
    }
    List<Transaction> transactionsEnAttente = (List<Transaction>) request.getAttribute("transactionsEnAttente");
    List<Transaction> toutesTransactions = (List<Transaction>) request.getAttribute("toutesTransactions");
    NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("fr", "MG"));
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    List<Historique> historiques = (List<Historique>) request.getAttribute("historiques");
%>
<!DOCTYPE html>
<html>
<head>
    <title>Administrateur</title>
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
            max-width: 1400px;
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

        table {
            width: 100%;
            border-collapse: collapse;
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

        .btn {
            padding: 0.4rem 0.8rem;
            border: none;
            border-radius: 3px;
            font-size: 0.875rem;
            cursor: pointer;
            margin-right: 0.25rem;
        }

        .btn-success { background: #27ae60; color: white; }
        .btn-danger { background: #e74c3c; color: white; }
        .btn-warning { background: #f39c12; color: white; }

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

        .stats {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 1rem;
            margin-bottom: 1rem;
        }

        .stat-card {
            background: #3498db;
            color: white;
            padding: 1.5rem;
            border-radius: 5px;
            text-align: center;
        }

        .stat-number {
            font-size: 2rem;
            font-weight: bold;
            margin: 0.5rem 0;
        }

        .logout {
            color: white;
            text-decoration: none;
        }

        .logout:hover {
            text-decoration: underline;
        }

        .action-buttons {
            display: flex;
            gap: 0.25rem;
        }
    </style>
</head>
<body>
    <div class="header">
        <div class="container">
            <div class="welcome">
                <div>
                    <h1>Bienvenue, <%= sessionInfo.getUsername() %></h1>
                    <p>Administrateur</p>
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
        
        <div class="stats">
            <div class="stat-card">
                <div>Transactions en attente</div>
                <div class="stat-number">
                    <%= transactionsEnAttente != null ? transactionsEnAttente.size() : 0 %>
                </div>
            </div>
            <div class="stat-card">
                <div>Total transactions</div>
                <div class="stat-number">
                    <%= toutesTransactions != null ? toutesTransactions.size() : 0 %>
                </div>
            </div>
        </div>
        
        <div class="card">
            <h2>Transactions en attente de validation</h2>
            <% if (transactionsEnAttente != null && !transactionsEnAttente.isEmpty()) { %>
                <table>
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Date</th>
                            <th>Type</th>
                            <th>Compte émetteur</th>
                            <th>Compte bénéficiaire</th>
                            <th>Montant</th>
                            <th>Devise</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% for (Transaction t : transactionsEnAttente) { %>
                            <tr>
                                <td>#<%= t.getIdTransaction() %></td>
                                <td><%= dateFormat.format(t.getDateTransaction()) %></td>
                                <td><%= t.getType().getLibelle() %></td>
                                <td>#<%= t.getCompteCourant().getIdCompteCourant() %></td>
                                <td>#<%= t.getCompteBeneficiaire() %></td>
                                <td><%= currencyFormat.format(t.getMontant()) %></td>
                                <td><%= t.getDevise() != null ? t.getDevise() : "AR" %></td>
                                <td>
                                    <div class="action-buttons">
                                        <form method="post" action="<%= request.getContextPath() %>/admin/dashboard" style="display: inline;">
                                            <input type="hidden" name="action" value="valider">
                                            <input type="hidden" name="idTransaction" value="<%= t.getIdTransaction() %>">
                                            <button type="submit" class="btn btn-success">Valider</button>
                                        </form>
                                        <form method="post" action="<%= request.getContextPath() %>/admin/dashboard" style="display: inline;">
                                            <input type="hidden" name="action" value="annulerAvant">
                                            <input type="hidden" name="idTransaction" value="<%= t.getIdTransaction() %>">
                                            <button type="submit" class="btn btn-danger">Annuler</button>
                                        </form>
                                    </div>
                                </td>
                            </tr>
                        <% } %>
                    </tbody>
                </table>
            <% } else { %>
                <p>Aucune transaction en attente.</p>
            <% } %>
        </div>
        
        <div class="card">
            <h2>Toutes les transactions</h2>
            <% if (toutesTransactions != null && !toutesTransactions.isEmpty()) { %>
                <table>
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Date</th>
                            <th>Type</th>
                            <th>Compte</th>
                            <th>Montant</th>
                            <th>Devise</th>
                            <th>Statut</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% for (Transaction t : toutesTransactions) { %>
                            <tr>
                                <td>#<%= t.getIdTransaction() %></td>
                                <td><%= dateFormat.format(t.getDateTransaction()) %></td>
                                <td><%= t.getType().getLibelle() %></td>
                                <td>#<%= t.getCompteCourant().getIdCompteCourant() %></td>
                                <td><%= currencyFormat.format(t.getMontant()) %></td>
                                <td><%= t.getDevise() != null ? t.getDevise() : "AR" %></td>
                                <td class="status-<%= t.getStatut().toLowerCase().replace("_", "-") %>">
                                    <%= t.getStatut() %>
                                </td>
                                <td>
                                    <% if ("VALIDE".equals(t.getStatut())) { %>
                                        <form method="post" action="<%= request.getContextPath() %>/admin/dashboard" style="display: inline;">
                                            <input type="hidden" name="action" value="annulerApres">
                                            <input type="hidden" name="idTransaction" value="<%= t.getIdTransaction() %>">
                                            <button type="submit" class="btn btn-warning">Annuler</button>
                                        </form>
                                    <% } %>
                                </td>
                            </tr>
                        <% } %>
                    </tbody>
                </table>
            <% } else { %>
                <p>Aucune transaction.</p>
            <% } %>
        </div>

        <div class="card">
            <h2>Historique des actions</h2>
            <% if (historiques != null && !historiques.isEmpty()) { %>
                <table>
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Date/Heure</th>
                            <th>Objet</th>
                            <th>Action</th>
                            <th>Utilisateur</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% for (Historique h : historiques) { %>
                            <tr>
                                <td><%= h.getIdHistorique() %></td>
                                <td><%= h.getDateHeure() != null ? new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm").format(h.getDateHeure()) : "" %></td>
                                <td><%= h.getObjet() %></td>
                                <td><%= h.getActionHistorique() != null ? h.getActionHistorique().getIntitule() : "" %></td>
                                <td><%= h.getUtilisateur() != null ? h.getUtilisateur().getUsername() : "" %></td>
                            </tr>
                        <% } %>
                    </tbody>
                </table>
            <% } else { %>
                <p>Aucun historique.</p>
            <% } %>
        </div>
    </div>
</body>
</html>