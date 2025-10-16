package com.banque.principale.rest;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.util.List;

import com.banque.comptecourant.model.Transaction;
import com.banque.principale.service.BanqueService;
import com.banque.principale.service.ClientService;

@Path("/comptes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CompteResource {

    private BanqueService banqueService;

    public CompteResource() {
        ClientService clientService = new ClientService();
        this.banqueService = new BanqueService(clientService);
    }

    // Compte Courant
    @GET
    @Path("/courant/{numeroCompte}/solde")
    public Response getSoldeCompteCourant(@PathParam("numeroCompte") String numeroCompte) {
        try {
            BigDecimal solde = banqueService.consulterSoldeCompteCourant(numeroCompte);
            return Response.ok("{\"solde\":\"" + solde + "\"}").build();
        } catch (Exception e) {
            return Response.status(500).entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }

    @POST
    @Path("/courant/{numeroCompte}/depot")
    public Response deposerCompteCourant(@PathParam("numeroCompte") String numeroCompte, 
                                       @QueryParam("montant") BigDecimal montant) {
        try {
            boolean success = banqueService.deposerCompteCourant(numeroCompte, montant);
            if (success) {
                return Response.ok("{\"message\":\"Dépôt effectué\"}").build();
            } else {
                return Response.status(400).entity("{\"error\":\"Échec dépôt\"}").build();
            }
        } catch (Exception e) {
            return Response.status(500).entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }

    @POST
    @Path("/courant/{numeroCompte}/retrait")
    public Response retirerCompteCourant(@PathParam("numeroCompte") String numeroCompte, 
                                       @QueryParam("montant") BigDecimal montant) {
        try {
            boolean success = banqueService.retirerCompteCourant(numeroCompte, montant);
            if (success) {
                return Response.ok("{\"message\":\"Retrait effectué\"}").build();
            } else {
                return Response.status(400).entity("{\"error\":\"Échec retrait\"}").build();
            }
        } catch (Exception e) {
            return Response.status(500).entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }

    @GET
    @Path("/courant/{numeroCompte}/historique")
    public Response getHistoriqueCompteCourant(@PathParam("numeroCompte") String numeroCompte) {
        try {
            List<Transaction> transactions = banqueService.getHistoriqueCompteCourant(numeroCompte);
            return Response.ok(transactions).build();
        } catch (Exception e) {
            return Response.status(500).entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }

    // Compte Dépôt
    @GET
    @Path("/depot/{numeroCompte}/solde")
    public Response getSoldeCompteDepot(@PathParam("numeroCompte") String numeroCompte) {
        try {
            BigDecimal solde = banqueService.consulterSoldeCompteDepot(numeroCompte);
            return Response.ok("{\"solde\":\"" + solde + "\"}").build();
        } catch (Exception e) {
            return Response.status(500).entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }

    @POST
    @Path("/depot/{numeroCompte}/depot")
    public Response deposerCompteDepot(@PathParam("numeroCompte") String numeroCompte, 
                                     @QueryParam("montant") BigDecimal montant) {
        try {
            boolean success = banqueService.deposerCompteDepot(numeroCompte, montant);
            if (success) {
                return Response.ok("{\"message\":\"Dépôt effectué\"}").build();
            } else {
                return Response.status(400).entity("{\"error\":\"Échec dépôt\"}").build();
            }
        } catch (Exception e) {
            return Response.status(500).entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }

    @POST
    @Path("/depot/{numeroCompte}/retrait")
    public Response retirerCompteDepot(@PathParam("numeroCompte") String numeroCompte, 
                                     @QueryParam("montant") BigDecimal montant) {
        try {
            boolean success = banqueService.retirerCompteDepot(numeroCompte, montant);
            if (success) {
                return Response.ok("{\"message\":\"Retrait effectué\"}").build();
            } else {
                return Response.status(400).entity("{\"error\":\"Échec retrait\"}").build();
            }
        } catch (Exception e) {
            return Response.status(500).entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }
}
