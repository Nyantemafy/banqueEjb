package com.multiplication.ejb;

import com.multiplication.model.Transaction;
import javax.ejb.Remote;

@Remote
public interface VirementService {
    Transaction effectuerVirement(Integer idCompteEmetteur, String compteBeneficiaire,
            String montant, String devise, String date);

    void annulerVirementAvant(Integer idTransaction);

    Transaction annulerVirementApres(Integer idTransaction);

    void validerVirement(Integer idTransaction);
}