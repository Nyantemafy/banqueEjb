package com.banque.principale.rest;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.util.List;

import com.banque.pret.entity.DemandePret;
import com.banque.pret.entity.Pret;
import com.banque.principale.service.BanqueService;
import com.banque.principale.service.ClientService;

@Path("/prets")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PretResource {

    private BanqueService banqueService;
    private ClientService clientService;

    public PretResource() {
        this.clientService = new ClientService();
        this.banqueService = new BanqueService(clientService);
    }

    @POST
    @Path("/demander")
    public Response demanderPret(@QueryParam("numeroClient") String numeroClient,
                               @QueryParam("montant") BigDecimal montant,
                               @QueryParam("duree") int dureeEnMois,
                               @QueryParam("objet") String objet) {
        try {
            String numeroDemande = banqueService.demanderPret(numeroClient, montant, dureeEnMois, objet);
            if (numeroDemande != null) {
                return Response.ok("{\"numeroDemande\":\"" + numeroDemande + "\"}").build();
            } else {
                return Response.status(400).entity("{\"error\":\"Échec demande prêt\"}").build();
            }
        } catch (Exception e) {
            return Response.status(500).entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }

    @GET
    @Path("/calculer-mensualite")
    public Response calculerMensualite(@QueryParam("montant") BigDecimal montant,
                                     @QueryParam("duree") int dureeEnMois) {
        try {
            BigDecimal mensualite = banqueService.calculerMensualitePret(montant, dureeEnMois);
            return Response.ok("{\"mensualite\":\"" + mensualite + "\"}").build();
        } catch (Exception e) {
            return Response.status(500).entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }

    @GET
    @Path("/demandes")
    public Response getDemandes(@QueryParam("statut") String statut) {
        try {
            List<DemandePret> demandes;
            if (statut == null || statut.trim().isEmpty()) {
                demandes = banqueService.getDemandes(null);
            } else if ("EN_ATTENTE".equalsIgnoreCase(statut) || "APPROUVEE".equalsIgnoreCase(statut) || "REJETEE".equalsIgnoreCase(statut)) {
                demandes = banqueService.getDemandes(statut.toUpperCase());
            } else {
                return Response.status(400).entity("{\"error\":\"Statut invalide\"}").build();
            }
            return Response.ok(demandes).build();
        } catch (Exception e) {
            return Response.status(500).entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }

    @POST
    @Path("/demandes/{numeroDemande}/approuver")
    public Response approuverDemande(@PathParam("numeroDemande") String numeroDemande,
                                     @HeaderParam("X-Admin-Client") String adminClient) {
        try {
            if (adminClient == null || !clientService.isAdmin(adminClient)) {
                return Response.status(403).entity("{\"error\":\"Accès refusé (admin requis)\"}").build();
            }
            boolean success = banqueService.approuverDemandePret(numeroDemande);
            if (success) {
                return Response.ok("{\"message\":\"Demande approuvée\"}").build();
            } else {
                return Response.status(400).entity("{\"error\":\"Échec approbation\"}").build();
            }
        } catch (Exception e) {
            return Response.status(500).entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }

    @POST
    @Path("/demandes/{numeroDemande}/rejeter")
    public Response rejeterDemande(@PathParam("numeroDemande") String numeroDemande,
                                 @QueryParam("motif") String motif,
                                 @HeaderParam("X-Admin-Client") String adminClient) {
        try {
            if (adminClient == null || !clientService.isAdmin(adminClient)) {
                return Response.status(403).entity("{\"error\":\"Accès refusé (admin requis)\"}").build();
            }
            boolean success = banqueService.rejeterDemandePret(numeroDemande, motif);
            if (success) {
                return Response.ok("{\"message\":\"Demande rejetée\"}").build();
            } else {
                return Response.status(400).entity("{\"error\":\"Échec rejet\"}").build();
            }
        } catch (Exception e) {
            return Response.status(500).entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }

    @GET
    @Path("/client/{numeroClient}")
    public Response getPretsClient(@PathParam("numeroClient") String numeroClient) {
        try {
            List<Pret> prets = banqueService.getPretsClient(numeroClient);
            return Response.ok(prets).build();
        } catch (Exception e) {
            return Response.status(500).entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }
}
