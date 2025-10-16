package com.banque.principale.rest;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.util.List;

import com.banque.principale.model.Client;
import com.banque.principale.service.BanqueService;
import com.banque.principale.service.ClientService;

@Path("/clients")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ClientResource {

    private BanqueService banqueService;
    private ClientService clientService;

    public ClientResource() { 
        this.clientService = new ClientService();
        this.banqueService = new BanqueService(clientService);
    }

    @GET
    @Path("/{numeroClient}")
    public Response getClient(@PathParam("numeroClient") String numeroClient) {
        try {
            Client client = banqueService.getClient(numeroClient);
            if (client != null) {
                return Response.ok(client)
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS")
                    .header("Access-Control-Allow-Headers", "Content-Type, Authorization")
                    .build();
            } else {
                return Response.status(404).entity("{\"error\":\"Client non trouvé\"}")
                    .header("Access-Control-Allow-Origin", "*")
                    .build();
            }
        } catch (Exception e) {
            return Response.status(500).entity("{\"error\":\"" + e.getMessage() + "\"}")
                .header("Access-Control-Allow-Origin", "*")
                .build();
        }
    }

    @POST
    public Response createClient(Client client) {
        try {
            boolean success = banqueService.creerClient(client);
            if (success) {
                return Response.status(201).entity("{\"message\":\"Client créé avec succès\"}")
                    .header("Access-Control-Allow-Origin", "*")
                    .build();
            } else {
                return Response.status(400).entity("{\"error\":\"Échec création client\"}")
                    .header("Access-Control-Allow-Origin", "*")
                    .build();
            }
        } catch (Exception e) {
            return Response.status(500).entity("{\"error\":\"" + e.getMessage() + "\"}")
                .header("Access-Control-Allow-Origin", "*")
                .build();
        }
    }

    @OPTIONS
    @Path("/{numeroClient}")
    public Response optionsClient(@PathParam("numeroClient") String numeroClient) {
        return Response.ok()
            .header("Access-Control-Allow-Origin", "*")
            .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS")
            .header("Access-Control-Allow-Headers", "Content-Type, Authorization")
            .build();
    }

    @OPTIONS
    public Response optionsClients() {
        return Response.ok()
            .header("Access-Control-Allow-Origin", "*")
            .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS")
            .header("Access-Control-Allow-Headers", "Content-Type, Authorization")
            .build();
    }

    @GET
    @Path("/test")
    public Response test() {
        return Response.ok("{\"message\":\"API fonctionne\",\"timestamp\":\"" + System.currentTimeMillis() + "\"}")
            .header("Access-Control-Allow-Origin", "*")
            .build();
    }
}
