package com.multiplication.ejb;

import com.multiplication.model.Devise;
import javax.ejb.Local;
import java.util.List;

@Local
public interface MultiplicationService {
    List<Devise> getDevisesMultipliees();

    Devise getDeviseMultipliee(String nom);
}