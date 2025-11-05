<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.multiplication.session.SessionInfo" %>
<%@ page import="com.multiplication.model.ValidationVirement" %>
<%@ page import="java.util.List" %>
<!DOCTYPE html>
<html>
<head>
    <title>Validations de virements</title>
    <style>
        body { font-family: Arial, sans-serif; background: #f8f9fa; margin:0; }
        .container { max-width: 1200px; margin: 0 auto; padding: 1rem; }
        .card { background: white; border-radius: 5px; padding: 1.5rem; margin-top: 1rem; box-shadow: 0 1px 3px rgba(0,0,0,0.1); }
        h1 { margin: 0; padding: 1rem; background: #2c3e50; color: white; }
        table { width: 100%; border-collapse: collapse; }
        th, td { padding: 0.75rem; text-align: left; border-bottom: 1px solid #eee; }
        th { background: #f8f9fa; font-weight: 600; }
        a.btn { display:inline-block; padding: 0.5rem 1rem; background:#3498db; color:white; text-decoration:none; border-radius:3px; }
    </style>
</head>
<body>
    <h1>Validations de virements</h1>
    <div class="container">
        <a href="<%= request.getContextPath() %>/admin/dashboard" class="btn">← Retour au dashboard</a>
        <div class="card">
            <h2>Liste des validations</h2>
            <%
                List<ValidationVirement> validations = (List<ValidationVirement>) request.getAttribute("validations");
            %>
            <% if (validations != null && !validations.isEmpty()) { %>
                <table>
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Objet</th>
                            <th>Date</th>
                            <th>Status</th>
                            <th>Etat</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% for (ValidationVirement v : validations) { %>
                            <tr>
                                <td><%= v.getIdValidation() %></td>
                                <td><%= v.getIdObject() %></td>
                                <td><%= v.getDate() != null ? new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm").format(v.getDate()) : "" %></td>
                                <td><%= v.getStatus() %></td>
                                <td><%= v.getEtat() %></td>
                            </tr>
                        <% } %>
                    </tbody>
                </table>
            <% } else { %>
                <p>Aucune validation enregistrée.</p>
            <% } %>
        </div>
    </div>
</body>
</html>
