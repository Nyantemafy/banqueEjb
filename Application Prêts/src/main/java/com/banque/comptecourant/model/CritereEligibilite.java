package com.banque.pret.model;

import com.banque.pret.entity.DemandePret;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe pour évaluer l'éligibilité d'une demande de prêt
 * selon différents critères bancaires
 */
public class CritereEligibilite {

    // Constantes de critères
    private static final BigDecimal TAUX_ENDETTEMENT_MAX = new BigDecimal("33.0"); // 33%
    private static final BigDecimal REVENUS_MIN = new BigDecimal("1500.0"); // 1500€/mois
    private static final int ANCIENNETE_MIN_MOIS = 6; // 6 mois minimum
    private static final int SCORE_RISQUE_MIN = 400; // Score minimum sur 1000

    private List<String> criteresNonRespectees;
    private List<String> avertissements;

    public CritereEligibilite() {
        this.criteresNonRespectees = new ArrayList<>();
        this.avertissements = new ArrayList<>();
    }

    /**
     * Évalue l'éligibilité d'une demande de prêt
     * 
     * @param demande La demande à évaluer
     * @return true si éligible, false sinon
     */
    public boolean evaluer(DemandePret demande) {
        if (demande == null) {
            criteresNonRespectees.add("Demande nulle");
            return false;
        }

        criteresNonRespectees.clear();
        avertissements.clear();

        // Vérification des critères obligatoires
        boolean eligible = true;

        // 1. Critère de montant
        if (!verifierMontant(demande)) {
            eligible = false;
        }

        // 2. Critère de durée
        if (!verifierDuree(demande)) {
            eligible = false;
        }

        // 3. Critère de revenus
        if (!verifierRevenus(demande)) {
            eligible = false;
        }

        // 4. Critère de taux d'endettement
        if (!verifierTauxEndettement(demande)) {
            eligible = false;
        }

        // 5. Critère d'ancienneté professionnelle
        if (!verifierAnciennete(demande)) {
            eligible = false;
        }

        // 6. Critère de score de risque
        if (!verifierScoreRisque(demande)) {
            eligible = false;
        }

        // 7. Critère d'objet du prêt
        if (!verifierObjetPret(demande)) {
            eligible = false;
        }

        return eligible;
    }

    private boolean verifierMontant(DemandePret demande) {
        BigDecimal montant = demande.getMontantDemande();

        if (montant == null) {
            criteresNonRespectees.add("Montant non spécifié");
            return false;
        }

        if (montant.compareTo(new BigDecimal("1000")) < 0) {
            criteresNonRespectees.add("Montant trop faible (minimum 1000€)");
            return false;
        }

        if (montant.compareTo(new BigDecimal("500000")) > 0) {
            criteresNonRespectees.add("Montant trop élevé (maximum 500000€)");
            return false;
        }

        // Avertissement pour gros montants
        if (montant.compareTo(new BigDecimal("200000")) > 0) {
            avertissements.add("Montant élevé - Vérification approfondie recommandée");
        }

        return true;
    }

    private boolean verifierDuree(DemandePret demande) {
        Integer duree = demande.getDureeEnMois();

        if (duree == null) {
            criteresNonRespectees.add("Durée non spécifiée");
            return false;
        }

        if (duree < 12) {
            criteresNonRespectees.add("Durée trop courte (minimum 12 mois)");
            return false;
        }

        if (duree > 360) {
            criteresNonRespectees.add("Durée trop longue (maximum 360 mois)");
            return false;
        }

        // Avertissement pour durées très longues
        if (duree > 300) {
            avertissements.add("Durée très longue - Risque accru");
        }

        return true;
    }

    private boolean verifierRevenus(DemandePret demande) {
        BigDecimal revenus = demande.getRevenusMenuels();

        if (revenus == null) {
            criteresNonRespectees.add("Revenus non renseignés");
            return false;
        }

        if (revenus.compareTo(REVENUS_MIN) < 0) {
            criteresNonRespectees.add("Revenus insuffisants (minimum " + REVENUS_MIN + "€/mois)");
            return false;
        }

        // Vérification de la cohérence revenus/montant demandé
        if (demande.getMontantDemande() != null) {
            BigDecimal ratio = demande.getMontantDemande().divide(revenus, 2, BigDecimal.ROUND_HALF_UP);
            if (ratio.compareTo(new BigDecimal("40")) > 0) { // Plus de 40 fois les revenus mensuels
                avertissements.add("Montant demandé élevé par rapport aux revenus");
            }
        }

        return true;
    }

    private boolean verifierTauxEndettement(DemandePret demande) {
        BigDecimal tauxEndettement = demande.getTauxEndettement();

        if (tauxEndettement == null || demande.getRevenusMenuels() == null) {
            avertissements.add("Impossible de calculer le taux d'endettement");
            return true; // Ne pas bloquer si pas de données
        }

        if (tauxEndettement.compareTo(TAUX_ENDETTEMENT_MAX) > 0) {
            criteresNonRespectees.add("Taux d'endettement trop élevé (" +
                    tauxEndettement.setScale(1, BigDecimal.ROUND_HALF_UP) +
                    "% > " + TAUX_ENDETTEMENT_MAX + "%)");
            return false;
        }

        if (tauxEndettement.compareTo(new BigDecimal("25")) > 0) {
            avertissements.add("Taux d'endettement élevé (" +
                    tauxEndettement.setScale(1, BigDecimal.ROUND_HALF_UP) + "%)");
        }

        return true;
    }

    private boolean verifierAnciennete(DemandePret demande) {
        Integer anciennete = demande.getAncienneteEmploiMois();

        if (anciennete == null) {
            avertissements.add("Ancienneté professionnelle non renseignée");
            return true; // Ne pas bloquer si pas de données
        }

        if (anciennete < ANCIENNETE_MIN_MOIS) {
            criteresNonRespectees.add("Ancienneté professionnelle insuffisante (" +
                    anciennete + " mois < " + ANCIENNETE_MIN_MOIS + " mois)");
            return false;
        }

        if (anciennete < 12) {
            avertissements.add("Ancienneté professionnelle courte (" + anciennete + " mois)");
        }

        return true;
    }

    private boolean verifierScoreRisque(DemandePret demande) {
        Integer score = demande.getScoreRisque();

        if (score == null) {
            avertissements.add("Score de risque non calculé");
            return true; // Ne pas bloquer si pas de score
        }

        if (score < SCORE_RISQUE_MIN) {
            criteresNonRespectees.add("Score de risque trop faible (" + score + " < " + SCORE_RISQUE_MIN + ")");
            return false;
        }

        if (score < 500) {
            avertissements.add("Score de risque moyen (" + score + "/1000)");
        }

        return true;
    }

    private boolean verifierObjetPret(DemandePret demande) {
        String objet = demande.getObjetPret();

        if (objet == null || objet.trim().isEmpty()) {
            criteresNonRespectees.add("Objet du prêt non spécifié");
            return false;
        }

        if (objet.trim().length() < 5) {
            criteresNonRespectees.add("Objet du prêt trop vague");
            return false;
        }

        // Vérification d'objets interdits ou risqués
        String objetLower = objet.toLowerCase();
        if (objetLower.contains("casino") || objetLower.contains("jeu") ||
                objetLower.contains("pari") || objetLower.contains("loterie")) {
            criteresNonRespectees.add("Objet du prêt non autorisé (jeux d'argent)");
            return false;
        }

        // Objets préférentiels
        if (objetLower.contains("immobilier") || objetLower.contains("rénovation") ||
                objetLower.contains("travaux") || objetLower.contains("véhicule")) {
            avertissements.add("Objet du prêt favorable");
        }

        return true;
    }

    /**
     * Évalue l'éligibilité avec des critères assouplis
     * 
     * @param demande La demande à évaluer
     * @return true si éligible avec critères assouplis
     */
    public boolean evaluerAvecCriteresAssouplis(DemandePret demande) {
        if (demande == null) {
            return false;
        }

        // Critères assouplis pour les bons profils
        if (demande.getRevenusMenuels() != null &&
                demande.getRevenusMenuels().compareTo(new BigDecimal("5000")) > 0) {

            // Client à hauts revenus : critères plus souples
            return evaluerHautRevenus(demande);
        }

        if (demande.getAncienneteEmploiMois() != null &&
                demande.getAncienneteEmploiMois() >= 60) {

            // Client avec longue ancienneté : critères plus souples
            return evaluerLongueAnciennete(demande);
        }

        return evaluer(demande); // Critères standards
    }

    private boolean evaluerHautRevenus(DemandePret demande) {
        // Assouplissement pour hauts revenus
        // Taux d'endettement jusqu'à 40%
        // Montant jusqu'à 750000€
        criteresNonRespectees.clear();
        avertissements.clear();

        boolean eligible = true;

        // Vérifications de base avec seuils assouplis
        if (demande.getMontantDemande().compareTo(new BigDecimal("750000")) > 0) {
            criteresNonRespectees.add("Montant trop élevé même pour hauts revenus");
            eligible = false;
        }

        BigDecimal tauxEndettement = demande.getTauxEndettement();
        if (tauxEndettement != null && tauxEndettement.compareTo(new BigDecimal("40")) > 0) {
            criteresNonRespectees.add("Taux d'endettement trop élevé même pour hauts revenus");
            eligible = false;
        }

        return eligible;
}

private boolean evaluerLongueAnciennete(DemandePret demande## 5. PretBean.java (Implémentation EJB)
```java
package com.banque.pret.ejb;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.OptimisticLockException;
import javax.persistence.NoResultException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.Calendar;

import com.banque.pret.entity.DemandePret;
import com.banque.pret.entity.Pret;
import com.banque.pret.entity.RemboursementPret;
import com.banque.pret.model.CritereEligibilite;
import com.banque.pret.exception.PretException;

@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class PretBean implements PretRemote {

    private static final Logger LOGGER = Logger.getLogger(PretBean.class.getName());

    @PersistenceContext(unitName = "pretsPU")
    private EntityManager em;

    // Configuration par défaut
    private static final double TAUX_DEFAUT = 4.5;
    private static final double TAUX_ENDETTEMENT_MAX = 33.0; // 33%
    private static final BigDecimal MONTANT_MIN = new BigDecimal("1000");
    private static final BigDecimal MONTANT_MAX = new BigDecimal("500000");
    private static final int DUREE_MIN_MOIS = 12;
    private static final int DUREE_MAX_MOIS = 360;

    @Override
    public String soumettreDemandePret(String numeroClient, BigDecimal montant,
            int dureeEnMois, String objet) {
        try {
            LOGGER.log(Level.INFO, "Soumission demande prêt - Client: {0}, Montant: {1}",
                    new Object[] { numeroClient, montant });

            // Validation des paramètres
            if (!validerParametresDemande(numeroClient, montant, dureeEnMois, objet)) {
                return null;
            }

            String numeroDemande = genererNumeroDemande();

            DemandePret demande = new DemandePret();
            demande.setNumeroDemande(numeroDemande);
            demande.setNumeroClient(numeroClient);
            demande.setMontantDemande(montant);
            demande.setDureeEnMois(dureeEnMois);
            demande.setObjetPret(objet);
            demande.setStatut(DemandePret.StatutDemande.EN_ATTENTE);
            demande.setDateDemande(new Date());

            // Calcul du taux proposé selon le profil
            double tauxPropose = calculerTauxInteret(numeroClient, montant, dureeEnMois);
            demande.setTauxPropose(tauxPropose);

            // Calcul de la mensualité
            BigDecimal mensualite = calculerMensualite(montant, tauxPropose, dureeEnMois);
            demande.setMensualiteCalculee(mensualite);

            // Calcul du score de risque initial
            int scoreRisque = calculerScoreRisque(demande);
            demande.setScoreRisque(scoreRisque);

            em.persist(demande);
            em.flush();

            LOGGER.log(Level.INFO, "Demande créée avec succès: {0}", numeroDemande);
            return numeroDemande;

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur création demande prêt", e);
            return null;
        }
    }

    @Override
    public boolean approuverDemande(String numeroDemande, String motif) {
        try {
            LOGGER.log(Level.INFO, "Approbation demande: {0}", numeroDemande);

            DemandePret demande = em.find(DemandePret.class, numeroDemande);
            if (demande == null) {
                LOGGER.log(Level.WARNING, "Demande non trouvée: {0}", numeroDemande);
                return false;
            }

            if (!demande.peutEtreEvaluee()) {
                LOGGER.log(Level.WARNING, "Demande ne peut pas être évaluée: {0}, Statut: {1}",
                        new Object[] { numeroDemande, demande.getStatut() });
                return false;
            }

            // Mise à jour de la demande
            demande.setStatut(DemandePret.StatutDemande.APPROUVEE);
            demande.setMotifDecision(motif);
            demande.setDateDecision(new Date());
            demande.setEvaluateur("SYSTEM"); // À remplacer par l'utilisateur connecté

            // Création du prêt actif
            Pret pret = creerPretDepuisDemande(demande);

            em.merge(demande);
            em.persist(pret);
            em.flush();

            LOGGER.log(Level.INFO, "Demande approuvée et prêt créé: {0} -> {1}",
                    new Object[] { numeroDemande, pret.getNumeroPret() });
            return true;

        } catch (OptimisticLockException e) {
            LOGGER.log(Level.WARNING, "Conflit de concurrence lors de l'approbation", e);
            return false;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de l'approbation de " + numeroDemande, e);
            return false;
        }
    }

    @Override
    public boolean rejeterDemande(String numeroDemande, String motif) {
        try {
            LOGGER.log(Level.INFO, "Rejet demande: {0}", numeroDemande);

            DemandePret demande = em.find(DemandePret.class, numeroDemande);
            if (demande == null || !demande.peutEtreEvaluee()) {
                return false;
            }

            demande.setStatut(DemandePret.StatutDemande.REJETEE);
            demande.setMotifDecision(motif);
            demande.setDateDecision(new Date());
            demande.setEvaluateur("SYSTEM");

            em.merge(demande);
            em.flush();

            LOGGER.log(Level.INFO, "Demande rejetée: {0}", numeroDemande);
            return true;

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors du rejet de " + numeroDemande, e);
            return false;
        }
    }

    @Override
    public boolean evaluerDemande(String numeroDemande) {
        try {
            DemandePret demande = em.find(DemandePret.class, numeroDemande);
            if (demande == null || !demande.peutEtreEvaluee()) {
                return false;
            }

            // Mise en cours d'évaluation
            demande.setStatut(DemandePret.StatutDemande.EN_COURS_EVALUATION);

            // Évaluation automatique selon les critères
            CritereEligibilite criteres = new CritereEligibilite();
            boolean eligible = criteres.evaluer(demande);

            if (eligible) {
                return approuverDemande(numeroDemande, "Approbation automatique - Critères respectés");
            } else {
                return rejeterDemande(numeroDemande, "Rejet automatique - Critères non respectés");
            }

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur évaluation demande " + numeroDemande, e);
            return false;
        }
    }

    @Override
    public BigDecimal calculerMensualite(BigDecimal capital, double tauxAnnuel, int dureeEnMois) {
        try {
            if (capital == null || capital.compareTo(BigDecimal.ZERO) <= 0 ||
                    dureeEnMois <= 0 || tauxAnnuel < 0) {
                return BigDecimal.ZERO;
            }

            if (tauxAnnuel == 0) {
                // Prêt sans intérêt
                return capital.divide(BigDecimal.valueOf(dureeEnMois), 2, RoundingMode.HALF_UP);
            }

            // Formule classique de calcul de mensualité
            double tauxMensuel = tauxAnnuel / 100 / 12;
            double facteur = Math.pow(1 + tauxMensuel, dureeEnMois);
            double mensualite = capital.doubleValue() * tauxMensuel * facteur / (facteur - 1);

            return BigDecimal.valueOf(mensualite).setScale(2, RoundingMode.HALF_UP);

        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Erreur calcul mensualité", e);
            return BigDecimal.ZERO;
        }
    }

    @Override
    public BigDecimal calculerCoutTotal(BigDecimal capital, double tauxAnnuel, int dureeEnMois) {
        try {
            BigDecimal mensualite = calculerMensualite(capital, tauxAnnuel, dureeEnMois);
            if (mensualite.compareTo(BigDecimal.ZERO) <= 0) {
                return BigDecimal.ZERO;
            }

            BigDecimal coutTotal = mensualite.multiply(BigDecimal.valueOf(dureeEnMois));
            return coutTotal.subtract(capital); // Intérêts = total payé - capital

        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Erreur calcul coût total", e);
            return BigDecimal.ZERO;
        }
    }

    @Override
    public double calculerTauxInteret(String numeroClient, BigDecimal montant, int dureeEnMois) {
        try {
            double tauxBase = TAUX_DEFAUT;

            // Ajustement selon le montant
            if (montant.compareTo(new BigDecimal("50000")) > 0) {
                tauxBase -= 0.2; // Réduction pour gros montants
            } else if (montant.compareTo(new BigDecimal("10000")) < 0) {
                tauxBase += 0.3; // Majoration pour petits montants
            }

            // Ajustement selon la durée
            if (dureeEnMois > 240) { // Plus de 20 ans
                tauxBase += 0.5;
            } else if (dureeEnMois < 60) { // Moins de 5 ans
                tauxBase -= 0.2;
            }

            // TODO: Ajustement selon le profil client (historique, revenus, etc.)

            // Bornes min/max
            tauxBase = Math.max(1.0, Math.min(15.0, tauxBase));

            return Math.round(tauxBase * 100.0) / 100.0; // Arrondi à 2 décimales

        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Erreur calcul taux intérêt", e);
            return TAUX_DEFAUT;
        }
    }

    @Override
    public BigDecimal calculerCapaciteEmprunt(String numeroClient, BigDecimal revenusMenuels,
            BigDecimal chargesMenuelles) {
        try {
            if (revenusMenuels == null || revenusMenuels.compareTo(BigDecimal.ZERO) <= 0) {
                return BigDecimal.ZERO;
            }

            BigDecimal charges = chargesMenuelles != null ? chargesMenuelles : BigDecimal.ZERO;

            // Capacité = (revenus - charges) * 0.33 (taux d'endettement max)
            BigDecimal capaciteMensuelle = revenusMenuels.subtract(charges)
                    .multiply(new BigDecimal("0.33"));

            if (capaciteMensuelle.compareTo(BigDecimal.ZERO) <= 0) {
                return BigDecimal.ZERO;
            }

            // Calcul du montant empruntable sur 240 mois (20 ans) au taux moyen
            double tauxMoyen = 4.0;
            int dureeStandard = 240;

            // Formule inverse de la mensualité
            double tauxMensuel = tauxMoyen / 100 / 12;
            double facteur = Math.pow(1 + tauxMensuel, dureeStandard);

            return BigDecimal.valueOf(capital).setScale(2, RoundingMode.HALF_UP);

        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Erreur calcul capacité emprunt", e);
            return BigDecimal.ZERO;
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public List<DemandePret> getDemandesEnAttente() {
        try {
            return em.createNamedQuery("DemandePret.findEnAttente", DemandePret.class)
                    .getResultList();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur récupération demandes en attente", e);
            return null;
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public List<DemandePret> getDemandesParStatut(String statut) {
        try {
            DemandePret.StatutDemande statutEnum;
            try {
                statutEnum = DemandePret.StatutDemande.valueOf(statut);
            } catch (IllegalArgumentException e) {
                LOGGER.log(Level.WARNING, "Statut invalide: {0}", statut);
                return null;
            }

            return em.createNamedQuery("DemandePret.findByStatut", DemandePret.class)
                    .setParameter("statut", statutEnum)
                    .getResultList();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur récupération demandes par statut", e);
            return null;
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public List<Pret> getPretsActifs(String numeroClient) {
        try {
            return em.createNamedQuery("Pret.findByClient", Pret.class)
                    .setParameter("numeroClient", numeroClient)
                    .getResultList()
                    .stream()
                    .filter(Pret::estActif)
                    .collect(java.util.stream.Collectors.toList());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur récupération prêts actifs", e);
            return null;
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public DemandePret getDemande(String numeroDemande) {
        try {
            return em.find(DemandePret.class, numeroDemande);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Erreur récupération demande " + numeroDemande, e);
            return null;
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public Pret getPret(String numeroPret) {
        try {
            return em.find(Pret.class, numeroPret);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Erreur récupération prêt " + numeroPret, e);
            return null;
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public String getHistoriqueClient(String numeroClient) {
        try {
            StringBuilder historique = new StringBuilder();
            historique.append("=== HISTORIQUE CLIENT ").append(numeroClient).append(" ===\n\n");

            // Récupération des demandes
            List<DemandePret> demandes = em.createNamedQuery("DemandePret.findByClient", DemandePret.class)
                    .setParameter("numeroClient", numeroClient)
                    .getResultList();

            historique.append("DEMANDES DE PRÊT (").append(demandes.size()).append("):\n");
            for (DemandePret demande : demandes) {
                historique.append("- ").append(demande.getNumeroDemande())
                        .append(" | ").append(demande.getMontantDemande()).append("€")
                        .append(" | ").append(demande.getStatutLibelle())
                        .append(" | ").append(demande.getDateDemande())
                        .append("\n");
            }

            // Récupération des prêts
            List<Pret> prets = em.createNamedQuery("Pret.findByClient", Pret.class)
                    .setParameter("numeroClient", numeroClient)
                    .getResultList();

            historique.append("\nPRÊTS (").append(prets.size()).append("):\n");
            for (Pret pret : prets) {
                historique.append("- ").append(pret.getNumeroPret())
                        .append(" | ").append(pret.getMontantInitial()).append("€")
                        .append(" | Reste: ").append(pret.getMontantRestant()).append("€")
                        .append(" | ").append(pret.getStatutLibelle())
                        .append(" | Mensualité: ").append(pret.getMensualite()).append("€")
                        .append("\n");
            }

            return historique.toString();

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur génération historique client " + numeroClient, e);
            return "Erreur lors de la génération de l'historique";
        }
    }

    @Override
    public boolean enregistrerRemboursement(String numeroPret, BigDecimal montant) {
        try {
            LOGGER.log(Level.INFO, "Enregistrement remboursement - Prêt: {0}, Montant: {1}",
                    new Object[] { numeroPret, montant });

            if (montant == null || montant.compareTo(BigDecimal.ZERO) <= 0) {
                LOGGER.log(Level.WARNING, "Montant de remboursement invalide: {0}", montant);
                return false;
            }

            Pret pret = em.find(Pret.class, numeroPret);
            if (pret == null) {
                LOGGER.log(Level.WARNING, "Prêt non trouvé: {0}", numeroPret);
                return false;
            }

            if (!pret.estActif()) {
                LOGGER.log(Level.WARNING, "Prêt non actif: {0}, Statut: {1}",
                        new Object[] { numeroPret, pret.getStatutPret() });
                return false;
            }

            // Mise à jour du prêt
            BigDecimal nouveauMontantRestant = pret.getMontantRestant().subtract(montant);
            if (nouveauMontantRestant.compareTo(BigDecimal.ZERO) < 0) {
                // Remboursement supérieur au capital restant
                nouveauMontantRestant = BigDecimal.ZERO;
            }

            pret.setMontantRestant(nouveauMontantRestant);
            pret.setMontantTotalRembourse(pret.getMontantTotalRembourse().add(montant));

            // Mise à jour des échéances
            if (montant.compareTo(pret.getMensualite()) >= 0) {
                int echeancesPayees = montant.divide(pret.getMensualite(), 0, RoundingMode.DOWN).intValue();
                pret.setNombreEcheancesPayees(pret.getNombreEcheancesPayees() + echeancesPayees);
                pret.setNombreEcheancesRestantes(pret.getDureeEnMois() - pret.getNombreEcheancesPayees());
            }

            // Vérification si prêt remboursé
            if (nouveauMontantRestant.compareTo(BigDecimal.ZERO) <= 0) {
                pret.setStatutPret(Pret.StatutPret.REMBOURSE);
                pret.setDateFinReelle(new Date());
                LOGGER.log(Level.INFO, "Prêt totalement remboursé: {0}", numeroPret);
            } else {
                // Mise à jour de la prochaine échéance
                pret.calculerProchaineEcheance();
            }

            // Création de l'enregistrement de remboursement
            RemboursementPret remboursement = new RemboursementPret(
                    numeroPret,
                    montant,
                    pret.getNombreEcheancesPayees());

            em.merge(pret);
            em.persist(remboursement);
            em.flush();

            LOGGER.log(Level.INFO, "Remboursement enregistré avec succès");
            return true;

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur enregistrement remboursement", e);
            return false;
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public BigDecimal[] calculerEcheancesAVenir(String numeroPret, int nombreEcheances) {
        try {
            Pret pret = em.find(Pret.class, numeroPret);
            if (pret == null || !pret.estActif()) {
                return new BigDecimal[0];
            }

            int echeancesRestantes = pret.getMoisRestants();
            int nombreACalculer = Math.min(nombreEcheances, echeancesRestantes);

            if (nombreACalculer <= 0) {
                return new BigDecimal[0];
            }

            BigDecimal[] echeances = new BigDecimal[nombreACalculer];
            BigDecimal mensualite = pret.getMensualite();

            // Pour simplifier, on retourne la même mensualité
            // Dans un cas réel, il faudrait calculer capital/intérêts pour chaque échéance
            for (int i = 0; i < nombreACalculer; i++) {
                echeances[i] = mensualite;
            }

            return echeances;

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur calcul échéances à venir", e);
            return new BigDecimal[0];
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public boolean estEnRetard(String numeroPret) {
        try {
            Pret pret = em.find(Pret.class, numeroPret);
            if (pret == null) {
                return false;
            }

            // Mise à jour du statut de retard
            pret.calculerProchaineEcheance();
            em.merge(pret);

            return pret.estEnRetard();

        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Erreur vérification retard prêt " + numeroPret, e);
            return false;
        }
    }

    // === MÉTHODES PRIVÉES UTILITAIRES ===

    private boolean validerParametresDemande(String numeroClient, BigDecimal montant,
            int dureeEnMois, String objet) {
        if (numeroClient == null || numeroClient.trim().isEmpty()) {
            LOGGER.log(Level.WARNING, "Numéro client invalide");
            return false;
        }

        if (montant == null || montant.compareTo(MONTANT_MIN) < 0 || montant.compareTo(MONTANT_MAX) > 0) {
            LOGGER.log(Level.WARNING, "Montant invalide: {0} (min: {1}, max: {2})",
                    new Object[] { montant, MONTANT_MIN, MONTANT_MAX });
            return false;
        }

        if (dureeEnMois < DUREE_MIN_MOIS || dureeEnMois > DUREE_MAX_MOIS) {
            LOGGER.log(Level.WARNING, "Durée invalide: {0} (min: {1}, max: {2})",
                    new Object[] { dureeEnMois, DUREE_MIN_MOIS, DUREE_MAX_MOIS });
            return false;
        }

        if (objet == null || objet.trim().length() < 5) {
            LOGGER.log(Level.WARNING, "Objet du prêt invalide: {0}", objet);
            return false;
        }

        return true;
    }

    private String genererNumeroDemande() {
        String uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
        return "DEM-" + uuid;
    }

    private Pret creerPretDepuisDemande(DemandePret demande) {
        String numeroPret = "PRET-" + UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();

        Pret pret = new Pret();
        pret.setNumeroPret(numeroPret);
        pret.setNumeroDemande(demande.getNumeroDemande());
        pret.setNumeroClient(demande.getNumeroClient());
        pret.setMontantInitial(demande.getMontantDemande());
        pret.setMontantRestant(demande.getMontantDemande());
        pret.setTauxInteret(demande.getTauxPropose());
        pret.setDureeEnMois(demande.getDureeEnMois());
        pret.setMensualite(demande.getMensualiteCalculee());
        pret.setDateDebut(new Date());
        pret.setStatutPret(Pret.StatutPret.ACTIF);
        pret.setNombreEcheancesPayees(0);
        pret.setNombreEcheancesRestantes(demande.getDureeEnMois());

        // Calcul de la date de fin prévue
        Calendar cal = Calendar.getInstance();
        cal.setTime(pret.getDateDebut());
        cal.add(Calendar.MONTH, demande.getDureeEnMois());
        pret.setDateFinPrevue(cal.getTime());

        // Calcul de la prochaine échéance (1 mois après le début)
        cal.setTime(pret.getDateDebut());
        cal.add(Calendar.MONTH, 1);
        pret.setProchaineEcheance(cal.getTime());

        // Frais de dossier (exemple: 1% du montant, max 500€)
        BigDecimal fraisDossier = demande.getMontantDemande()
                .multiply(new BigDecimal("0.01"))
                .min(new BigDecimal("500.00"));
        pret.setFraisDossier(fraisDossier);

        return pret;
    }

    private int calculerScoreRisque(DemandePret demande) {
        int score = 500; // Score de base (sur 1000, 500 = neutre)

        try {
            // Facteur montant
            if (demande.getMontantDemande().compareTo(new BigDecimal("100000")) > 0) {
                score -= 50; // Gros montants = plus de risque
            } else if (demande.getMontantDemande().compareTo(new BigDecimal("10000")) < 0) {
                score += 30; // Petits montants = moins de risque
            }

            // Facteur durée
            if (demande.getDureeEnMois() > 240) {
                score -= 40; // Longue durée = plus de risque
            } else if (demande.getDureeEnMois() < 60) {
                score += 20; // Courte durée = moins de risque
            }

            // Facteur revenus/charges
            if (demande.getRevenusMenuels() != null && demande.getChargesMenuelles() != null) {
                BigDecimal tauxEndettement = demande.getTauxEndettement();
                if (tauxEndettement.compareTo(new BigDecimal("20")) < 0) {
                    score += 100; // Faible endettement
                } else if (tauxEndettement.compareTo(new BigDecimal("30")) > 0) {
                    score -= 100; // Fort endettement
                }
            }

            // Facteur ancienneté emploi
            if (demande.getAncienneteEmploiMois() != null) {
                if (demande.getAncienneteEmploiMois() >= 24) {
                    score += 50; // Stabilité professionnelle
                } else if (demande.getAncienneteEmploiMois() < 6) {
                    score -= 50; // Instabilité
                }
            }

            // Facteur autres prêts en cours
            if (demande.getAutresPretsEnCours() != null) {
                if (demande.getAutresPretsEnCours().compareTo(BigDecimal.ZERO) > 0) {
                    score -= 30; // Autres engagements
                }
            }

        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Erreur calcul score de risque", e);
        }

        // Bornes du score
        return Math.max(100, Math.min(900, score));
    }
}