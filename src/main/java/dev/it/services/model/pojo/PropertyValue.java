package dev.it.services.model.pojo;

import java.io.Serializable;

public class PropertyValue implements Serializable {
    public String name;
    public Object value;

    public PropertyValue() {
    }

    @Override
    public String toString() {
        return "PropertyValue{" +
                "name='" + name + '\'' +
                ", value=" + value +
                '}';
    }
}
