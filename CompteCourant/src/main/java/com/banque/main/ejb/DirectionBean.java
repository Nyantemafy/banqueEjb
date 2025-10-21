package com.banque.comptecourant.ejb;

import com.banque.comptecourant.entity.Direction;

import javax.ejb.Stateful;
import java.io.Serializable;

@Stateful
public class DirectionBean implements Serializable {
    private static final long serialVersionUID = 1L;

    private Direction[] directions;

    public void setDirections(Direction[] directions) {
        this.directions = directions;
    }

    public Direction[] getDirections() {
        return directions != null ? directions : new Direction[0];
    }
}
