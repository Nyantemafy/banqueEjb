package com.multiplication.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "actionhistorique")
public class ActionHistorique implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_actionhistorique")
    private Integer idActionHistorique;

    @Column(name = "intitule", nullable = false, length = 50)
    private String intitule;

    @OneToMany(mappedBy = "actionHistorique", fetch = FetchType.LAZY)
    private List<Historique> historiques;

    public Integer getIdActionHistorique() {
        return idActionHistorique;
    }

    public void setIdActionHistorique(Integer idActionHistorique) {
        this.idActionHistorique = idActionHistorique;
    }

    public String getIntitule() {
        return intitule;
    }

    public void setIntitule(String intitule) {
        this.intitule = intitule;
    }

    public List<Historique> getHistoriques() {
        return historiques;
    }

    public void setHistoriques(List<Historique> historiques) {
        this.historiques = historiques;
    }
}
