package com.banque.pret.ejb;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import com.banque.pret.entity.DemandePret;
import com.banque.pret.entity.Pret;

@Stateless
public class PretBean implements PretRemote {

    @PersistenceContext(unitName = "pretsPU")
    private EntityManager em;

    private static final double TAUX_DEFAUT = 4.5; // 4.5%

    @Override
    public String demanderPret(String numeroClient, BigDecimal montant, int dureeEnMois, String objet) {
        System.out.println("💰 Demande de prêt - Client: " + numeroClient + ", Montant: " + montant + "€");

        // Vérifications simples
        if (numeroClient == null || montant == null || montant.compareTo(BigDecimal.ZERO) <= 0 ||
                dureeEnMois < 12 || dureeEnMois > 360 || objet == null || objet.length() < 5) {
            System.out.println("❌ Paramètres invalides");
            return null;
        }

        // Générer numéro de demande
        String numeroDemande = "DEM-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        // Calculer la mensualité proposée
        BigDecimal mensualite = calculerMensualite(montant, TAUX_DEFAUT, dureeEnMois);

        // Créer la demande
        DemandePret demande = new DemandePret(numeroDemande, numeroClient, montant, dureeEnMois, objet);
        demande.setTauxPropose(TAUX_DEFAUT);
        demande.setMensualiteCalculee(mensualite);

        // Sauvegarder
        em.persist(demande);

        System.out.println("✅ Demande créée: " + numeroDemande + " - Mensualité: " + mensualite + "€");
        return numeroDemande;
    }

    @Override
    public BigDecimal calculerMensualite(BigDecimal montant, double tauxInteret, int dureeEnMois) {
        System.out.println("🧮 Calcul mensualité - Montant: " + montant + "€, Taux: " + tauxInteret + "%, Durée: "
                + dureeEnMois + " mois");

        if (montant == null || montant.compareTo(BigDecimal.ZERO) <= 0 || dureeEnMois <= 0 || tauxInteret < 0) {
            return BigDecimal.ZERO;
        }

        if (tauxInteret == 0) {
            // Prêt gratuit, juste diviser
            return montant.divide(BigDecimal.valueOf(dureeEnMois), 2, RoundingMode.HALF_UP);
        }

        // Formule classique : M = C * (t * (1+t)^n) / ((1+t)^n - 1)
        double tauxMensuel = tauxInteret / 100 / 12; // Taux mensuel
        double facteur = Math.pow(1 + tauxMensuel, dureeEnMois);
        double mensualiteDouble = montant.doubleValue() * tauxMensuel * facteur / (facteur - 1);

        BigDecimal mensualite = BigDecimal.valueOf(mensualiteDouble).setScale(2, RoundingMode.HALF_UP);
        System.out.println("✅ Mensualité calculée: " + mensualite + "€");
        return mensualite;
    }

    @Override
    public boolean approuverDemande(String numeroDemande) {
        System.out.println("✅ Approbation demande: " + numeroDemande);

        // Trouver la demande
        DemandePret demande = em.find(DemandePret.class, numeroDemande);
        if (demande == null) {
            System.out.println("❌ Demande non trouvée");
            return false;
        }

        if (!demande.estEnAttente()) {
            System.out.println("❌ Demande déjà traitée: " + demande.getStatut());
            return false;
        }

        // Mettre à jour la demande
        demande.setStatut("APPROUVEE");
        demande.setDateDecision(new Date());

        // Créer le prêt actif
        String numeroPret = "PRET-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Pret pret = new Pret(numeroPret, numeroDemande, demande.getNumeroClient(),
                demande.getMontantDemande(), demande.getTauxPropose(),
                demande.getDureeEnMois(), demande.getMensualiteCalculee());

        // Sauvegarder
        em.merge(demande);
        em.persist(pret);

        System.out.println("✅ Demande approuvée et prêt créé: " + numeroPret);
        return true;
    }

    @Override
    public boolean rejeterDemande(String numeroDemande, String motif) {
        System.out.println("❌ Rejet demande: " + numeroDemande + " - Motif: " + motif);

        DemandePret demande = em.find(DemandePret.class, numeroDemande);
        if (demande == null || !demande.estEnAttente()) {
            System.out.println("❌ Demande non trouvée ou déjà traitée");
            return false;
        }

        // Rejeter la demande
        demande.setStatut("REJETEE");
        demande.setDateDecision(new Date());
        demande.setMotifRejet(motif != null ? motif : "Non spécifié");

        em.merge(demande);

        System.out.println("✅ Demande rejetée");
        return true;
    }

    @Override
    public List<DemandePret> getDemandesEnAttente() {
        System.out.println("📋 Récupération demandes en attente");

        List<DemandePret> demandes = em.createQuery(
                "SELECT d FROM DemandePret d WHERE d.statut = 'EN_ATTENTE' ORDER BY d.dateDemande",
                DemandePret.class)
                .getResultList();

        System.out.println("✅ " + demandes.size() + " demandes en attente trouvées");
        return demandes;
    }

    @Override
    public List<DemandePret> getDemandes(String statut) {
        System.out.println("📋 Récupération demandes - statut=" + statut);
        if (statut == null || statut.trim().isEmpty()) {
            return em.createQuery(
                    "SELECT d FROM DemandePret d ORDER BY d.dateDemande DESC",
                    DemandePret.class)
                    .getResultList();
        } else {
            return em.createQuery(
                    "SELECT d FROM DemandePret d WHERE d.statut = :statut ORDER BY d.dateDemande DESC",
                    DemandePret.class)
                    .setParameter("statut", statut)
                    .getResultList();
        }
    }

    @Override
    public List<Pret> getPretsClient(String numeroClient) {
        System.out.println("📋 Récupération prêts du client: " + numeroClient);

        List<Pret> prets = em.createQuery(
                "SELECT p FROM Pret p WHERE p.numeroClient = :client ORDER BY p.dateDebut DESC",
                Pret.class)
                .setParameter("client", numeroClient)
                .getResultList();

        System.out.println("✅ " + prets.size() + " prêts trouvés");
        return prets;
    }

    @Override
    public DemandePret getDemande(String numeroDemande) {
        System.out.println("🔍 Recherche demande: " + numeroDemande);

        DemandePret demande = em.find(DemandePret.class, numeroDemande);
        if (demande != null) {
            System.out.println("✅ Demande trouvée: " + demande);
        } else {
            System.out.println("❌ Demande non trouvée");
        }

        return demande;
    }
}