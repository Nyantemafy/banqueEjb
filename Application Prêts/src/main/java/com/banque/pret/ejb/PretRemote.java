package com.banque.pret.ejb;

import javax.ejb.Remote;
import java.math.BigDecimal;
import java.util.List;
import com.banque.pret.entity.DemandePret;
import com.banque.pret.entity.Pret;

@Remote
public interface PretRemote {
        // Demander un prêt
        String demanderPret(String numeroClient, BigDecimal montant, int dureeEnMois, String objet);

        // Calculer la mensualité
        BigDecimal calculerMensualite(BigDecimal montant, double tauxInteret, int dureeEnMois);

        // Approuver/Rejeter
        boolean approuverDemande(String numeroDemande);

        boolean rejeterDemande(String numeroDemande, String motif);

        // Consulter
        List<DemandePret> getDemandesEnAttente();

        List<Pret> getPretsClient(String numeroClient);

        DemandePret getDemande(String numeroDemande);

        // Nouveau: lister demandes par statut (null => toutes)
        List<DemandePret> getDemandes(String statut);
}