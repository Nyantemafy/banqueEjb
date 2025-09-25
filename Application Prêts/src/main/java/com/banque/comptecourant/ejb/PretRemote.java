package com.banque.pret.ejb;

import javax.ejb.Remote;
import java.math.BigDecimal;
import java.util.List;
import com.banque.pret.entity.DemandePret;
import com.banque.pret.entity.Pret;
import com.banque.pret.model.CritereEligibilite;

@Remote
public interface PretRemote {

    // === GESTION DES DEMANDES ===

    /**
     * Soumet une nouvelle demande de prêt
     * 
     * @param numeroClient Numéro du client
     * @param montant      Montant demandé
     * @param dureeEnMois  Durée en mois
     * @param objet        Objet du prêt
     * @return Numéro de la demande créée ou null si erreur
     */
    String soumettreDemandePret(String numeroClient, BigDecimal montant,
            int dureeEnMois, String objet);

    /**
     * Approuve une demande de prêt
     * 
     * @param numeroDemande Numéro de la demande
     * @param motif         Motif de l'approbation
     * @return true si succès
     */
    boolean approuverDemande(String numeroDemande, String motif);

    /**
     * Rejette une demande de prêt
     * 
     * @param numeroDemande Numéro de la demande
     * @param motif         Motif du rejet
     * @return true si succès
     */
    boolean rejeterDemande(String numeroDemande, String motif);

    /**
     * Évalue automatiquement une demande selon les critères
     * 
     * @param numeroDemande Numéro de la demande
     * @return true si la demande peut être approuvée
     */
    boolean evaluerDemande(String numeroDemande);

    // === CALCULS FINANCIERS ===

    /**
     * Calcule la mensualité d'un prêt
     * 
     * @param capital     Capital emprunté
     * @param tauxAnnuel  Taux d'intérêt annuel
     * @param dureeEnMois Durée en mois
     * @return Montant de la mensualité
     */
    BigDecimal calculerMensualite(BigDecimal capital, double tauxAnnuel, int dureeEnMois);

    /**
     * Calcule le coût total du prêt (intérêts)
     * 
     * @param capital     Capital emprunté
     * @param tauxAnnuel  Taux d'intérêt annuel
     * @param dureeEnMois Durée en mois
     * @return Coût total des intérêts
     */
    BigDecimal calculerCoutTotal(BigDecimal capital, double tauxAnnuel, int dureeEnMois);

    /**
     * Calcule le taux d'intérêt selon le profil du client
     * 
     * @param numeroClient Numéro du client
     * @param montant      Montant du prêt
     * @param dureeEnMois  Durée du prêt
     * @return Taux d'intérêt proposé
     */
    double calculerTauxInteret(String numeroClient, BigDecimal montant, int dureeEnMois);

    /**
     * Calcule la capacité d'emprunt maximale
     * 
     * @param numeroClient     Numéro du client
     * @param revenusMenuels   Revenus mensuels
     * @param chargesMenuelles Charges mensuelles
     * @return Capacité d'emprunt maximale
     */
    BigDecimal calculerCapaciteEmprunt(String numeroClient, BigDecimal revenusMenuels,
            BigDecimal chargesMenuelles);

    // === CONSULTATION ===

    /**
     * Récupère les demandes en attente
     * 
     * @return Liste des demandes en attente
     */
    List<DemandePret> getDemandesEnAttente();

    /**
     * Récupère les demandes par statut
     * 
     * @param statut Statut recherché
     * @return Liste des demandes
     */
    List<DemandePret> getDemandesParStatut(String statut);

    /**
     * Récupère les prêts actifs d'un client
     * 
     * @param numeroClient Numéro du client
     * @return Liste des prêts actifs
     */
    List<Pret> getPretsActifs(String numeroClient);

    /**
     * Récupère une demande par son numéro
     * 
     * @param numeroDemande Numéro de la demande
     * @return La demande ou null
     */
    DemandePret getDemande(String numeroDemande);

    /**
     * Récupère un prêt par son numéro
     * 
     * @param numeroPret Numéro du prêt
     * @return Le prêt ou null
     */
    Pret getPret(String numeroPret);

    /**
     * Récupère l'historique complet d'un client
     * 
     * @param numeroClient Numéro du client
     * @return Historique des prêts et demandes
     */
    String getHistoriqueClient(String numeroClient);

    // === GESTION DES REMBOURSEMENTS ===

    /**
     * Enregistre un remboursement
     * 
     * @param numeroPret Numéro du prêt
     * @param montant    Montant remboursé
     * @return true si succès
     */
    boolean enregistrerRemboursement(String numeroPret, BigDecimal montant);

    /**
     * Calcule les échéances à venir
     * 
     * @param numeroPret      Numéro du prêt
     * @param nombreEcheances Nombre d'échéances à calculer
     * @return Tableau des prochaines échéances
     */
    BigDecimal[] calculerEcheancesAVenir(String numeroPret, int nombreEcheances);

    /**
     * Vérifie si un prêt est en retard
     * 
     * @param numeroPret Numéro du prêt
     * @return true si en retard
     */
    boolean estEnRetard(String numeroPret);
}