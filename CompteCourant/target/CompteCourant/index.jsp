<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>CompteCourant - Accueil</title>
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
            <div class="card">
                <h2>Bienvenue sur CompteCourant</h2>
                
                <% if (session.getAttribute("authenticated") != null && 
                       (Boolean)session.getAttribute("authenticated")) { %>
                    <p>Vous êtes connecté en tant que: <strong><%= session.getAttribute("username") %></strong></p>
                    <p>Cette application fournit les services bancaires via EJB.</p>
                    <br>
                    <a href="logout" class="btn-primary">Se déconnecter</a>
                <% } else { %>
                    <p>Application de gestion des comptes courants</p>
                    <p>Veuillez vous connecter pour accéder aux services.</p>
                    <br>
                    <a href="login.jsp" class="btn-primary">Se connecter</a>
                <% } %>
                
                <% if (request.getParameter("login") != null && 
                       request.getParameter("login").equals("success")) { %>
                    <div style="margin-top: 20px; padding: 10px; background-color: #d4edda; color: #155724; border-radius: 4px;">
                        Authentification réussie !
                    </div>
                <% } %>
            </div>
        </main>
        
        <footer class="footer">
            <p>&copy; 2023 CompteCourant. Tous droits réservés.</p>
        </footer>
    </div>
</body>
</html>