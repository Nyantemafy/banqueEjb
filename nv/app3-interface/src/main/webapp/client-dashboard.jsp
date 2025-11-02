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
    <title>Mon Compte - Client</title>
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
        
        .status-en_attente {
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
                    <p>Client</p>
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
                                <option value="EUR">EUR (Euro)</option>
                                <option value="USD">USD (Dollar)</option>
                                <option value="GBP">GBP (Livre Sterling)</option>
                                <option value="JPY">JPY (Yen)</option>
                                <option value="CHF">CHF (Franc Suisse)</option>
                            </select>
                        </div>
                        <button type="submit">Convertir</button>
                    </div>
                </form>
            </div>
            
            <div class="card">
                <h2>📜 Historique des transactions</h2>
                <form method="get" action="<%= request.getContextPath() %>/client/dashboard" style="margin: 10px 0; display: flex; gap: 10px; align-items: center; flex-wrap: wrap;">
                    <label for="deviseAffichage" style="color:#333;">Afficher les montants en:</label>
                    <select id="deviseAffichage" name="deviseAffichage" style="padding:8px;">
                        <option value="">AR (par défaut)</option>
                        <option value="EUR" <%= "EUR".equals(deviseAffichage) ? "selected" : "" %>>EUR</option>
                        <option value="USD" <%= "USD".equals(deviseAffichage) ? "selected" : "" %>>USD</option>
                        <option value="GBP" <%= "GBP".equals(deviseAffichage) ? "selected" : "" %>>GBP</option>
                        <option value="JPY" <%= "JPY".equals(deviseAffichage) ? "selected" : "" %>>JPY</option>
                        <option value="CHF" <%= "CHF".equals(deviseAffichage) ? "selected" : "" %>>CHF</option>
                    </select>
                    <label for="dateCours" style="color:#333;">Date du cours (optionnel):</label>
                    <input type="date" id="dateCours" name="dateCours" value="<%= dateCours != null ? dateCours : "" %>" style="padding:8px;" />
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
        <% } else { %>
            <div class="card">
                <p>Aucun compte associé à votre profil.</p>
            </div>
        <% } %>
    </div>
</body>
</html>