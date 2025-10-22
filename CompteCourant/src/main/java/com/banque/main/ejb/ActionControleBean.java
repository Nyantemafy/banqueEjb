package com.banque.comptecourant.ejb;

import com.banque.comptecourant.entity.Action;

import javax.ejb.Stateful;
import javax.ejb.Remove;
import java.io.Serializable;

@Stateful
public class ActionControleBean implements Serializable {
    private static final long serialVersionUID = 1L;

    private Action[] actions;

    public void setActions(Action[] actions) {
        this.actions = actions;
    }

    public Action[] getActions() {
        return actions;
    }

    public boolean hasAction(String actionLibelle) {
        if (actions == null) return false;
        for (Action a : actions) {
            if (a.getLibelle().equalsIgnoreCase(actionLibelle)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasActionId(Integer actionId) {
        if (actions == null) return false;
        for (Action a : actions) {
            if (a.getIdAction().equals(actionId)) {
                return true;
            }
        }
        return false;
    }

    public boolean canCreate() {
        return hasAction("CREATE") || hasAction("CREER");
    }

    public boolean canRead() {
        return hasAction("READ") || hasAction("LIRE");
    }

    public boolean canUpdate() {
        return hasAction("UPDATE") || hasAction("MODIFIER");
    }

    public boolean canDelete() {
        return hasAction("DELETE") || hasAction("SUPPRIMER");
    }

    @Remove
    public void clear() {
        actions = null;
    }
}