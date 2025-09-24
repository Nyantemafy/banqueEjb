package com.banque.pret.ejb;

import javax.ejb.Remote;
import java.math.BigDecimal;
import java.util.List;
import com.banque.pret.model.DemandePret;
import com.banque.pret.model.Pret;

@Remote
public interface PretRemote {
    // Gestion des demandes
    String soumettreDemandePret(String numeroClient, BigDecimal montant,
            int dureeEnMois, String objet);

    boolean approuverDemande(String numeroDemande, String motif);

    boolean rejeterDemande(String numeroDemande, String motif);

    // Calculs
    BigDecimal calculerMensualite(BigDecimal capital, double tauxAnnuel, int dureeEnMois);

    BigDecimal calculerCoutTotal(BigDecimal capital, double tauxAnnuel, int dureeEnMois);

    // Consultation
    List<DemandePret> getDemandesEnAttente();

    List<Pret> getPretsActifs(String numeroClient);

    DemandePret getDemande(String numeroDemande);
}