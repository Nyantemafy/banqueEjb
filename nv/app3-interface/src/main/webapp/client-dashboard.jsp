<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.banque.session.SessionInfo" %>
<%@ page import="com.banque.entities.CompteCourant" %>
<%@ page import="com.banque.entities.Transaction" %>
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
    CompteCourant compte = (CompteCourant) request.getAttribute("compte");
    List<Transaction> transactions = (List<Transaction>) request.getAttribute("transactions");
    NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("fr", "MG"));
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
%>
<!DOCTYPE html>
<html>
<head>
    <title>Tableau de bord - Client</title>
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
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 20px;
            box-shadow: 0 2px 5px rgba(0,0,0,0.1);
        }
        
        .container {
            max-width: 1200px;
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
        
        .solde-card {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            text-align: center;
            padding: 40px;
        }
        
        .solde-amount {
            font-size: 48px;
            font-weight: bold;
            margin: 20px 0;
        }
        
        .conversion-form {
            display: flex;
            gap: 10px;
            margin-top: 20px;
            align-items: flex-end;
        }
        
        .form-group {
            flex: 1;
        }
        
        label {
            display: block;
            margin-bottom: 5px;
            color: white;
            font-weight: 500;
        }
        
        input, select {
            width: 100%;
            padding: 10px;
            border: none;
            border-radius: 5px;
            font-size: 14px;
        }
        
        button {
            padding: 10px 20px;
            background: white;
            color: #667eea;
            border: none;
            border-radius: 5px;
            font-weight: 600;
            cursor: pointer;
            transition: transform 0.2s;
        }
        
        button:hover {
            transform: translateY(-2px);
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
        
        .status-attente {
            color: #ffc107;
            font-weight: 600;
        }
        
        .status-annule {
            color: #dc3545;
            font-weight: 600;
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
    </style>
</head>
<body>
    <div class="header">
        <div class="container">
            <div class="welcome">
                <div>
                    <h1>🏦 Bienvenue, <%= sessionInfo.getUsername() %></h1>
                    <p>Client</p>
                </div>
                <a href="<%= request.getContextPath() %>/logout" style="color: white;">Déconnexion</a>
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
                    <div class="conversion-form">
                        <div class="form-group">
                            <label>Montant à convertir</label>
                            <input type="number" name="montant" step="0.01" required>
                        </div>
                        <div class="form-group">
                            <label>Devise</label>
                            <select name="devise" required>
                                <option value="">-- Choisir --</option>
                                <option value="EUR">EUR</option>
                                <option value="USD">USD</option>
                                <option value="GBP">GBP</option>
                                <option value="JPY">JPY</option>
                                <option value="CHF">CHF</option>
                            </select>
                        </div>
                        <button type="submit">Convertir</button>
                    </div>
                </form>
            </div>
            
            <div class="card">
                <h2>Historique des transactions</h2>
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
                                    <td><%= currencyFormat.format(t.getMontant()) %></td>
                                    <td><%= t.getDevise() != null ? t.getDevise() : "AR" %></td>
                                    <td class="status-<%= t.getStatut().toLowerCase() %>">
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
        <% } else { %>
            <div class="card">
                <p>Aucun compte associé à votre profil.</p>
            </div>
        <% } %>
    </div>
</body>
</html>