package dev.it.services.model.pojo;

import java.io.Serializable;

public class ActionValue implements Serializable {

    public String action;
    public Long numberOf;

    public ActionValue() {
    }

    @Override
    public String toString() {
        return "ActionValue{" +
                "action='" + action + '\'' +
                ", numberOf=" + numberOf +
                '}';
    }
}
