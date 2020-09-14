package dev.it.services.model.pojo;

public class TagEvent {

    private String tag;

    private boolean added;

    public TagEvent(String tag, boolean added) {
        this.tag = tag;
        this.added = added;
    }

    public String getTag() {
        return tag;
    }

    public boolean isAdded() {
        return added;
    }
}
