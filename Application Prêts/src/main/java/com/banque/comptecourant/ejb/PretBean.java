package com.banque.pret.ejb;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Stateless
public class PretBean implements PretRemote {

    @PersistenceContext(unitName = "pretsPU")
    private EntityManager em;

    private final double TAUX_DEFAUT = 4.5; // 4.5% annuel

    @Override
    public String soumettreDemandeP

    ret(String numeroClient, BigDecimal montant, 
                                       int dureeEnMois, String objet) {
        try {
            String numeroDemande = "DEM-" + UUID.randomUUID().toString().substring(0, 8);
            
            DemandePret demande = new DemandePret();
            demande.setNumeroDemande(numeroDemande);
            demande.setNumeroClient(numeroClient);
            demande.setMontantDemande(montant);
            demande.setDureeEnMois(dureeEnMois);
            demande.setObjetPret(objet);
            demande.setStatut("EN_ATTENTE");
            demande.setDateDemande(new Date());
            demande.setTauxPropose(TAUX_DEFAUT);
            
            // Calcul automatique de la mensualité
            BigDecimal mensualite = calculerMensualite(montant, TAUX_DEFAUT, dureeEnMois);
            demande.setMensualiteCalculee(mensualite);
            
            em.persist(demande);
            
            System.out.println("Demande de prêt créée: " + numeroDemande);
            return numeroDemande;
            
        } catch (Exception e) {
            System.err.println("Erreur création demande: " + e.getMessage());
            return null;
        }
    }

    @Override
    public boolean approuverDemande(String numeroDemande, String motif) {
        try {
            DemandePret demande = em.createQuery(
                    "SELECT d FROM DemandePret d WHERE d.numeroDemande = :numero",
                    DemandePret.class)
                    .setParameter("numero", numeroDemande)
                    .getSingleResult();

            if (!"EN_ATTENTE".equals(demande.getStatut())) {
                return false;
            }

            // Mise à jour de la demande
            demande.setStatut("APPROUVEE");
            demande.setMotifDecision(motif);
            demande.setDateDecision(new Date());

            // Création du prêt actif
            Pret pret = new Pret();
            pret.setNumeroPret("PRET-" + UUID.randomUUID().toString().substring(0, 8));
            pret.setNumeroDemande(numeroDemande);
            pret.setNumeroClient(demande.getNumeroClient());
            pret.setMontantInitial(demande.getMontantDemande());
            pret.setMontantRestant(demande.getMontantDemande());
            pret.setTauxInteret(demande.getTauxPropose());
            pret.setDureeEnMois(demande.getDureeEnMois());
            pret.setMensualite(demande.getMensualiteCalculee());
            pret.setDateDebut(new Date());
            pret.setStatutPret("ACTIF");

            em.merge(demande);
            em.persist(pret);

            return true;

        } catch (Exception e) {
            System.err.println("Erreur approbation: " + e.getMessage());
            return false;
        }
    }

    @Override
    public BigDecimal calculerMensualite(BigDecimal capital, double tauxAnnuel, int dureeEnMois) {
        try {
            if (capital.compareTo(BigDecimal.ZERO) <= 0 || dureeEnMois <= 0) {
                return BigDecimal.ZERO;
            }

            double tauxMensuel = tauxAnnuel / 100 / 12;

            if (tauxMensuel == 0) {
                // Si pas d'intérêts, division simple
                return capital.divide(BigDecimal.valueOf(dureeEnMois), 2, RoundingMode.HALF_UP);
            }

            // Formule de calcul de mensualité avec intérêts
            double facteur = Math.pow(1 + tauxMensuel, dureeEnMois);
            double mensualite = capital.doubleValue() * tauxMensuel * facteur / (facteur - 1);

            return BigDecimal.valueOf(mensualite).setScale(2, RoundingMode.HALF_UP);

        } catch (Exception e) {
            System.err.println("Erreur calcul mensualité: " + e.getMessage());
            return BigDecimal.ZERO;
        }
    }

    @Override
    public BigDecimal calculerCoutTotal(BigDecimal capital, double tauxAnnuel, int dureeEnMois) {
        try {
            BigDecimal mensualite = calculerMensualite(capital, tauxAnnuel, dureeEnMois);
            BigDecimal coutTotal = mensualite.multiply(BigDecimal.valueOf(dureeEnMois));
            return coutTotal.subtract(capital); // Coût = total payé - capital
        } catch (Exception e) {
            System.err.println("Erreur calcul coût: " + e.getMessage());
            return BigDecimal.ZERO;
        }
    }

    @Override
    public List<DemandePret> getDemandesEnAttente() {
        try {
            return em.createQuery(
                    "SELECT d FROM DemandePret d WHERE d.statut = 'EN_ATTENTE' ORDER BY d.dateDemande",
                    DemandePret.class)
                    .getResultList();
        } catch (Exception e) {
            System.err.println("Erreur récupération demandes: " + e.getMessage());
            return null;
        }
    }

    // ... autres méthodes
}