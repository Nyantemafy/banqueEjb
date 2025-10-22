package com.banque.comptecourant.ejb;

import com.banque.comptecourant.entity.Direction;

import javax.ejb.Stateful;
import javax.ejb.Remove;
import java.io.Serializable;

@Stateful
public class DirectionBean implements Serializable {
    private static final long serialVersionUID = 1L;

    private Direction[] directions;

    public void setDirections(Direction[] directions) {
        this.directions = directions;
    }

    public Direction[] getDirections() {
        return directions;
    }

    public boolean hasDirection(Integer directionId) {
        if (directions == null) return false;
        for (Direction d : directions) {
            if (d.getIdDirection().equals(directionId)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasNiveau(Integer niveau) {
        if (directions == null) return false;
        for (Direction d : directions) {
            if (d.getNiveau().equals(niveau)) {
                return true;
            }
        }
        return false;
    }

    @Remove
    public void clear() {
        directions = null;
    }
}
