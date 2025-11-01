<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.interface_app.model.Devise" %>
<%
    // Si la liste n'est pas chargée, rediriger vers le servlet
    List<String> noms = (List<String>) request.getAttribute("noms");
    if (noms == null) {
        response.sendRedirect("deviseDetail");
        return;
    }
%>
<!DOCTYPE html>
<html>
<head>
    <title>Consultation Devises</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            max-width: 600px;
            margin: 50px auto;
            padding: 20px;
        }
        h1 {
            color: #333;
        }
        select {
            width: 100%;
            padding: 10px;
            font-size: 16px;
            margin: 10px 0;
        }
        button {
            background-color: #4CAF50;
            color: white;
            padding: 10px 20px;
            border: none;
            cursor: pointer;
            font-size: 16px;
        }
        button:hover {
            background-color: #45a049;
        }
        .result {
            margin-top: 20px;
            padding: 15px;
            background-color: #f0f0f0;
            border-radius: 5px;
        }
        .result p {
            margin: 5px 0;
        }
    </style>
</head>
<body>
    <h1>Consultation des Devises</h1>
    
    <form action="deviseDetail" method="get">
        <label for="devise">Sélectionnez une devise :</label>
        <select name="devise" id="devise">
            <option value="">-- Choisir une devise --</option>
            <%
                for (String nom : noms) {
            %>
                <option value="<%= nom %>"><%= nom %></option>
            <%
                }
            %>
        </select>
        <button type="submit">Afficher le cours multiplié</button>
    </form>
    
    <%
        Devise devise = (Devise) request.getAttribute("devise");
        if (devise != null) {
    %>
    <div class="result">
        <h2>Détails de la devise</h2>
        <p><strong>Nom :</strong> <%= devise.getNomDevise() %></p>
        <p><strong>Date début :</strong> <%= devise.getDateDebut() %></p>
        <p><strong>Date fin :</strong> <%= devise.getDateFin() %></p>
        <p><strong>Cours multiplié par 2 :</strong> <%= devise.getCours() %></p>
    </div>
    <%
        }
    %>
</body>
</html>