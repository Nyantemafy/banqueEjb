package com.multiplication.dao;

import com.multiplication.model.VirementRef;

import javax.ejb.Remote;

@Remote
public interface VirementDAORemote {
    VirementRef create(VirementRef v);
    VirementRef findByTransactionId(Integer idTransaction);
}
