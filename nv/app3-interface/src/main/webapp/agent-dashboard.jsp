<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.banque.session.SessionInfo" %>
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
    <title>Tableau de bord - Agent</title>
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
            background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
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
            padding: 30px;
            margin-bottom: 20px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
        }
        
        h2 {
            color: #333;
            margin-bottom: 20px;
            padding-bottom: 10px;
            border-bottom: 2px solid #f5576c;
        }
        
        .form-group {
            margin-bottom: 20px;
        }
        
        label {
            display: block;
            margin-bottom: 5px;
            color: #555;
            font-weight: 500;
        }
        
        input, select {
            width: 100%;
            padding: 12px;
            border: 1px solid #ddd;
            border-radius: 5px;
            font-size: 14px;
        }
        
        input:focus, select:focus {
            outline: none;
            border-color: #f5576c;
        }
        
        button {
            padding: 12px 30px;
            background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
            color: white;
            border: none;
            border-radius: 5px;
            font-weight: 600;
            cursor: pointer;
            transition: transform 0.2s;
            font-size: 14px;
        }
        
        button:hover {
            transform: translateY(-2px);
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
        
        .two-column {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 20px;
        }
        
        @media (max-width: 768px) {
            .two-column {
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
                    <h1>🏦 Bienvenue, <%= sessionInfo.getUsername() %></h1>
                    <p>Agent Bancaire</p>
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
        
        <div class="two-column">
            <div class="card">
                <h2>💸 Effectuer un virement</h2>
                <form method="post" action="<%= request.getContextPath() %>/agent/dashboard">
                    <input type="hidden" name="action" value="effectuerVirement">
                    
                    <div class="form-group">
                        <label for="compteEmetteur">Compte émetteur (ID)</label>
                        <input type="number" id="compteEmetteur" name="compteEmetteur" required>
                    </div>
                    
                    <div class="form-group">
                        <label for="compteBeneficiaire">Compte bénéficiaire (ID)</label>
                        <input type="number" id="compteBeneficiaire" name="compteBeneficiaire" required>
                    </div>
                    
                    <div class="form-group">
                        <label for="montant">Montant</label>
                        <input type="number" id="montant" name="montant" step="0.01" required>
                    </div>
                    
                    <div class="form-group">
                        <label for="devise">Devise</label>
                        <select id="devise" name="devise" required>
                            <option value="AR">AR (Ariary)</option>
                            <option value="EUR">EUR (Euro)</option>
                            <option value="USD">USD (Dollar)</option>
                            <option value="GBP">GBP (Livre)</option>
                            <option value="JPY">JPY (Yen)</option>
                            <option value="CHF">CHF (Franc Suisse)</option>
                        </select>
                    </div>
                    
                    <div class="form-group">
                        <label for="date">Date du virement</label>
                        <input type="date" id="date" name="date" required>
                    </div>
                    
                    <button type="submit">Effectuer le virement</button>
                </form>
            </div>
            
            <div class="card">
                <h2>💱 Changer la devise d'une transaction</h2>
                <form method="post" action="<%= request.getContextPath() %>/agent/dashboard">
                    <input type="hidden" name="action" value="changerDevise">
                    
                    <div class="form-group">
                        <label for="idTransaction">ID Transaction</label>
                        <input type="number" id="idTransaction" name="idTransaction" required>
                    </div>
                    
                    <div class="form-group">
                        <label for="nouvelleDevise">Nouvelle devise</label>
                        <select id="nouvelleDevise" name="nouvelleDevise" required>
                            <option value="AR">AR (Ariary)</option>
                            <option value="EUR">EUR (Euro)</option>
                            <option value="USD">USD (Dollar)</option>
                            <option value="GBP">GBP (Livre)</option>
                            <option value="JPY">JPY (Yen)</option>
                            <option value="CHF">CHF (Franc Suisse)</option>
                        </select>
                    </div>
                    
                    <div class="form-group">
                        <label for="taux">Taux de change</label>
                        <input type="number" id="taux" name="taux" step="0.000001" required>
                        <small>Ex: 0.00020 pour convertir AR en EUR</small>
                    </div>
                    
                    <button type="submit">Changer la devise</button>
                </form>
            </div>
        </div>
        
        <div class="card">
            <h2>ℹ️ Informations</h2>
            <p><strong>Plafond journalier :</strong> 10.000.000 AR</p>
            <p><strong>Votre rôle :</strong> <%= sessionInfo.getRole().getLibelle() %></p>
            <p><strong>Direction :</strong> <%= sessionInfo.getDirections().get(0).getLibelle() %> (Niveau <%= sessionInfo.getNiveauDirection() %>)</p>
        </div>
    </div>
</body>
</html>