package com.multiplication.dao;

import com.multiplication.model.ValidationVirement;

import javax.ejb.Remote;

@Remote
public interface ValidationVirementDAORemote {
    ValidationVirement findByIdObject(String idObject);
    ValidationVirement upsertValidation(String idObject, String roleLibelle);
    void markAnnule(String idObject);
    java.util.List<ValidationVirement> listAll();
}
