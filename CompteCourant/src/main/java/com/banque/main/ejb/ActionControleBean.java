package com.banque.comptecourant.ejb;

import com.banque.comptecourant.entity.Action;

import javax.ejb.Stateful;
import java.io.Serializable;

@Stateful
public class ActionControleBean implements Serializable {
    private static final long serialVersionUID = 1L;

    private Action[] actions;

    public void setActions(Action[] actions) {
        this.actions = actions;
    }

    public Action[] getActions() {
        return actions != null ? actions : new Action[0];
    }
}
