<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.multiplication.session.SessionInfo" %>
<%@ page import="com.multiplication.model.Transaction" %>
<%@ page import="java.util.List" %>
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
%>
<!DOCTYPE html>
<html>
<head>
    <title>Tableau de bord - Administrateur</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }
        
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: #f5f7fa;
        }
        
        .header {
            background: linear-gradient(135deg, #FA8BFF 0%, #2BD2FF 52%, #2BFF88 100%);
            color: white;
            padding: 20px;
            box-shadow: 0 2px 5px rgba(0,0,0,0.1);
        }
        
        .container {
            max-width: 1400px;
            margin: 0 auto;
            padding: 20px;
        }
        
        .welcome {
            display: flex;
            justify-content: space-between;
            align-items: center;
        }
        
        .card {
            background: white;
            border-radius: 10px;
            padding: 20px;
            margin-bottom: 20px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
        }
        
        h2 {
            color: #333;
            margin-bottom: 20px;
            padding-bottom: 10px;
            border-bottom: 2px solid #2BD2FF;
        }
        
        table {
            width: 100%;
            border-collapse: collapse;
        }
        
        th, td {
            padding: 12px;
            text-align: left;
            border-bottom: 1px solid #eee;
        }
        
        th {
            background: #f8f9fa;
            font-weight: 600;
            color: #555;
        }
        
        .status-valide {
            color: #28a745;
            font-weight: 600;
        }
        
        .status-en_attente {
            color: #ffc107;
            font-weight: 600;
        }
        
        .status-annule {
            color: #dc3545;
            font-weight: 600;
        }
        
        .action-buttons {
            display: flex;
            gap: 5px;
        }
        
        .btn {
            padding: 6px 12px;
            border: none;
            border-radius: 4px;
            font-size: 12px;
            cursor: pointer;
            transition: transform 0.2s;
        }
        
        .btn:hover {
            transform: translateY(-1px);
        }
        
        .btn-success {
            background: #28a745;
            color: white;
        }
        
        .btn-danger {
            background: #dc3545;
            color: white;
        }
        
        .btn-warning {
            background: #ffc107;
            color: #333;
        }
        
        .message {
            background: #d4edda;
            color: #155724;
            padding: 15px;
            border-radius: 5px;
            margin-bottom: 20px;
        }
        
        .error {
            background: #f8d7da;
            color: #721c24;
            padding: 15px;
            border-radius: 5px;
            margin-bottom: 20px;
        }
        
        .stats {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 20px;
            margin-bottom: 20px;
        }
        
        .stat-card {
            background: linear-gradient(135deg, #FA8BFF 0%, #2BD2FF 100%);
            color: white;
            padding: 20px;
            border-radius: 10px;
            text-align: center;
        }
        
        .stat-number {
            font-size: 36px;
            font-weight: bold;
            margin: 10px 0;
        }
        
        a {
            color: white;
            text-decoration: none;
        }
        
        a:hover {
            text-decoration: underline;
        }
    </style>
</head>
<body>
    <div class="header">
        <div class="container">
            <div class="welcome">
                <div>
                    <h1>🏦 Bienvenue, <%= sessionInfo.getUsername() %></h1>
                    <p>Administrateur</p>
                </div>
                <a href="<%= request.getContextPath() %>/logout">Déconnexion</a>
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
            <div class="stat-card" style="background: linear-gradient(135deg, #2BFF88 0%, #2BD2FF 100%);">
                <div>Total transactions</div>
                <div class="stat-number">
                    <%= toutesTransactions != null ? toutesTransactions.size() : 0 %>
                </div>
            </div>
        </div>
        
        <div class="card">
            <h2>⏳ Transactions en attente de validation</h2>
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
                                            <button type="submit" class="btn btn-success">✓ Valider</button>
                                        </form>
                                        <form method="post" action="<%= request.getContextPath() %>/admin/dashboard" style="display: inline;">
                                            <input type="hidden" name="action" value="annulerAvant">
                                            <input type="hidden" name="idTransaction" value="<%= t.getIdTransaction() %>">
                                            <button type="submit" class="btn btn-danger">✗ Annuler</button>
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
            <h2>📊 Toutes les transactions</h2>
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
                                            <button type="submit" class="btn btn-warning">↩ Annuler (inverse)</button>
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
    </div>
</body>
</html>