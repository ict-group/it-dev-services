package dev.it.services.model.pojo;

import java.io.Serializable;

public class ActionValue implements Serializable {

    public String action;
    public Long numberOf;

    public ActionValue() {
        this.numberOf = 1L;
    }

    public ActionValue(String action) {
        this.action = action;
        this.numberOf = 1L;
    }


    @Override
    public String toString() {
        return "ActionValue{" +
                "action='" + action + '\'' +
                ", numberOf=" + numberOf +
                '}';
    }
}
