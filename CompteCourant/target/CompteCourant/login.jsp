<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>CompteCourant - Connexion</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>
    <div class="container">
        <header class="header">
            <div class="header-content">
                <div class="logo">
                    <h1>Compte Courant</h1>
                </div>
            </div>
        </header>
        
        <main class="main-content">
            <div class="login-card">
                <h2>Connexion</h2>
                
                <% if (request.getAttribute("error") != null) { %>
                    <div style="margin-bottom: 15px; padding: 10px; background-color: #f8d7da; color: #721c24; border-radius: 4px;">
                        <%= request.getAttribute("error") %>
                    </div>
                <% } %>
                
                <form id="login-form" method="post" action="login">
                    <div class="form-group">
                        <label for="username">Nom d'utilisateur</label>
                        <input type="text" id="username" name="username" required autofocus>
                    </div>
                    <div class="form-group">
                        <label for="password">Mot de passe</label>
                        <input type="password" id="password" name="password" required>
                    </div>
                    <button type="submit" class="btn-primary">Se connecter</button>
                </form>
                
                <div style="margin-top: 20px; text-align: center;">
                    <a href="index.jsp" style="color: var(--primary-color); text-decoration: none;">Retour à l'accueil</a>
                </div>
            </div>
        </main>
        
        <footer class="footer">
            <p>&copy; 2023 CompteCourant. Tous droits réservés.</p>
        </footer>
    </div>
</body>
</html>