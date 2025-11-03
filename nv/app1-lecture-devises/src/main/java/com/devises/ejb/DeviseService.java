package com.devises.ejb;

import com.devises.model.Devise;
import javax.ejb.Remote;
import java.util.List;

@Remote
public interface DeviseService {
    List<Devise> getAllDevises();

    Devise getDeviseByNom(String nom);

    void addDevise(Devise devise);
}