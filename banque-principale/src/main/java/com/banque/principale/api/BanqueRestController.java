package com.banque.principale.api;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.banque.comptecourant.model.Transaction;
import com.banque.pret.entity.DemandePret;
import com.banque.pret.entity.Pret;
import com.banque.principale.model.Client;
import com.banque.principale.service.BanqueService;
import com.banque.principale.service.ClientService;

/**
 * Contrôleur REST pour l'API bancaire
 * Expose les services EJB et .NET via HTTP/JSON
 */
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BanqueRestController {

    private static BanqueService banqueService;
    private static ClientService clientService;

    // Initialisation des services au démarrage
    static {
        clientService = new ClientService();
        banqueService = new BanqueService(clientService);
        clientService.setBanqueService(banqueService);
    }

    // =================== ENDPOINTS CLIENTS ===================

    @GET
    @Path("/clients")
    public Response getAllClients() {
        try {
            List<Client> clients = clientService.getTousLesClients();
            return Response.ok(clients).build();
        } catch (Exception e) {
            return errorResponse(e.getMessage());
        }
    }

    @GET
    @Path("/clients/{numero}")
    public Response getClient(@PathParam("numero") String numero) {
        try {
            Client client = banqueService.getClient(numero);
            if (client == null) {
                return Response.status(404)
                        .entity(error("Client introuvable")).build();
            }
            return Response.ok(client).build();
        } catch (Exception e) {
            return errorResponse(e.getMessage());
        }
    }

    @POST
    @Path("/clients")
    public Response createClient(Client client) {
        try {
            boolean success = banqueService.creerClient(client);
            if (success) {
                return Response.ok(success("Client créé avec succès")).build();
            }
            return Response.status(400)
                    .entity(error("Échec création client")).build();
        } catch (Exception e) {
            return errorResponse(e.getMessage());
        }
    }

    // =================== ENDPOINTS COMPTE COURANT ===================

    @GET
    @Path("/comptecourant/{numero}/solde")
    public Response getSoldeCompteCourant(@PathParam("numero") String numero) {
        try {
            BigDecimal solde = banqueService.consulterSoldeCompteCourant(numero);
            return Response.ok(data("solde", solde)).build();
        } catch (Exception e) {
            return errorResponse(e.getMessage());
        }
    }

    @POST
    @Path("/comptecourant/{numero}/depot")
    public Response deposerCompteCourant(
            @PathParam("numero") String numero,
            Map<String, String> payload) {
        try {
            BigDecimal montant = new BigDecimal(payload.get("montant"));
            boolean success = banqueService.deposerCompteCourant(numero, montant);
            return success ? Response.ok(success("Dépôt effectué")).build()
                    : Response.status(400).entity(error("Échec dépôt")).build();
        } catch (Exception e) {
            return errorResponse(e.getMessage());
        }
    }

    @POST
    @Path("/comptecourant/{numero}/retrait")
    public Response retirerCompteCourant(
            @PathParam("numero") String numero,
            Map<String, String> payload) {
        try {
            BigDecimal montant = new BigDecimal(payload.get("montant"));
            boolean success = banqueService.retirerCompteCourant(numero, montant);
            return success ? Response.ok(success("Retrait effectué")).build()
                    : Response.status(400).entity(error("Échec retrait")).build();
        } catch (Exception e) {
            return errorResponse(e.getMessage());
        }
    }

    @GET
    @Path("/comptecourant/{numero}/historique")
    public Response getHistoriqueCompteCourant(@PathParam("numero") String numero) {
        try {
            List<Transaction> historique = banqueService.getHistoriqueCompteCourant(numero);
            return Response.ok(historique != null ? historique : java.util.Collections.emptyList()).build();
        } catch (Exception e) {
            return errorResponse(e.getMessage());
        }
    }

    // =================== ENDPOINTS COMPTE DÉPÔT ===================

    @GET
    @Path("/comptedepot/{numero}/solde")
    public Response getSoldeCompteDepot(@PathParam("numero") String numero) {
        try {
            BigDecimal solde = banqueService.consulterSoldeCompteDepot(numero);
            return Response.ok(data("solde", solde)).build();
        } catch (Exception e) {
            return errorResponse(e.getMessage());
        }
    }

    @POST
    @Path("/comptedepot/{numero}/depot")
    public Response deposerCompteDepot(
            @PathParam("numero") String numero,
            Map<String, String> payload) {
        try {
            BigDecimal montant = new BigDecimal(payload.get("montant"));
            boolean success = banqueService.deposerCompteDepot(numero, montant);
            return success ? Response.ok(success("Dépôt effectué")).build()
                    : Response.status(400).entity(error("Échec dépôt")).build();
        } catch (Exception e) {
            return errorResponse(e.getMessage());
        }
    }

    @POST
    @Path("/comptedepot/{numero}/retrait")
    public Response retirerCompteDepot(
            @PathParam("numero") String numero,
            Map<String, String> payload) {
        try {
            BigDecimal montant = new BigDecimal(payload.get("montant"));
            boolean success = banqueService.retirerCompteDepot(numero, montant);
            return success ? Response.ok(success("Retrait effectué")).build()
                    : Response.status(400).entity(error("Échec retrait")).build();
        } catch (Exception e) {
            return errorResponse(e.getMessage());
        }
    }

    // =================== ENDPOINTS PRÊTS ===================

    @POST
    @Path("/prets/demande")
    public Response demanderPret(Map<String, String> payload) {
        try {
            String numeroClient = payload.get("numeroClient");
            BigDecimal montant = new BigDecimal(payload.get("montant"));
            int duree = Integer.parseInt(payload.get("duree"));
            String objet = payload.get("objet");

            String numeroDemande = banqueService.demanderPret(
                    numeroClient, montant, duree, objet);

            return Response.ok(data("numeroDemande", numeroDemande)).build();
        } catch (Exception e) {
            return errorResponse(e.getMessage());
        }
    }

    @GET
    @Path("/prets/demandes")
    public Response getDemandesEnAttente() {
        try {
            List<DemandePret> demandes = banqueService.getDemandesEnAttente();
            return Response.ok(demandes != null ? demandes : java.util.Collections.emptyList()).build();
        } catch (Exception e) {
            return errorResponse(e.getMessage());
        }
    }

    @POST
    @Path("/prets/demandes/{numero}/approuver")
    public Response approuverDemande(@PathParam("numero") String numero) {
        try {
            boolean success = banqueService.approuverDemandePret(numero);
            return success ? Response.ok(success("Demande approuvée")).build()
                    : Response.status(400).entity(error("Échec approbation")).build();
        } catch (Exception e) {
            return errorResponse(e.getMessage());
        }
    }

    @POST
    @Path("/prets/demandes/{numero}/rejeter")
    public Response rejeterDemande(
            @PathParam("numero") String numero,
            Map<String, String> payload) {
        try {
            String motif = payload.get("motif");
            boolean success = banqueService.rejeterDemandePret(numero, motif);
            return success ? Response.ok(success("Demande rejetée")).build()
                    : Response.status(400).entity(error("Échec rejet")).build();
        } catch (Exception e) {
            return errorResponse(e.getMessage());
        }
    }

    @GET
    @Path("/prets/client/{numero}")
    public Response getPretsClient(@PathParam("numero") String numero) {
        try {
            List<Pret> prets = banqueService.getPretsClient(numero);
            return Response.ok(prets != null ? prets : java.util.Collections.emptyList()).build();
        } catch (Exception e) {
            return errorResponse(e.getMessage());
        }
    }

    @POST
    @Path("/prets/calculer-mensualite")
    public Response calculerMensualite(Map<String, String> payload) {
        try {
            BigDecimal montant = new BigDecimal(payload.get("montant"));
            int duree = Integer.parseInt(payload.get("duree"));
            BigDecimal mensualite = banqueService.calculerMensualitePret(montant, duree);
            return Response.ok(data("mensualite", mensualite)).build();
        } catch (Exception e) {
            return errorResponse(e.getMessage());
        }
    }

    // =================== MÉTHODES UTILITAIRES ===================

    private Map<String, Object> error(String message) {
        Map<String, Object> result = new HashMap<>();
        result.put("success", false);
        result.put("error", message);
        return result;
    }

    private Map<String, Object> success(String message) {
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", message);
        return result;
    }

    private Map<String, Object> data(String key, Object value) {
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put(key, value);
        return result;
    }

    private Response errorResponse(String message) {
        return Response.status(500).entity(error(message)).build();
    }
}