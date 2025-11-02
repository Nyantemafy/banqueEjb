package com.multiplication.ejb;

import com.multiplication.metier.Change;

import javax.ejb.Remote;
import java.math.BigDecimal;

@Remote
public interface ChangeService {
    Change effectuerChange(BigDecimal montant, String deviseSource, String deviseCible, BigDecimal tauxChange);
    Change correctionAvant(Integer idTransaction, String nouvelleDevise, BigDecimal tauxChange);
    Change correctionApres(Integer idTransaction, String nouvelleDevise, BigDecimal tauxChange);
}
