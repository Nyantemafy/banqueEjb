package com.interface_app.ejb;

import com.interface_app.model.Devise;
import javax.ejb.Local;
import java.util.List;

@Local
public interface InterfaceService {
    List<String> getNomsDevises();

    Devise getDeviseDetail(String nom);
}