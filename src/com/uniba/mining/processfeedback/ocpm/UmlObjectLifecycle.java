package com.uniba.mining.processfeedback.ocpm;

import java.util.ArrayList;
import java.util.List;

public class UmlObjectLifecycle {

    private String objectId;
    private String objectType;
    private String objectName;
    private final List<UmlObjectEvent> events = new ArrayList<>();

    public UmlObjectLifecycle(String objectId, String objectType, String objectName) {
        this.objectId = objectId;
        this.objectType = objectType;
        this.objectName = objectName;
    }

    public void addEvent(UmlObjectEvent event) {
        events.add(event);

        if (event.getObjectName() != null && !event.getObjectName().trim().isEmpty()) {
            this.objectName = event.getObjectName();
        }
    }

    public boolean hasActivityContaining(String token) {
        for (UmlObjectEvent e : events) {
            if (contains(e.getActivityName(), token)) {
                return true;
            }
        }
        return false;
    }

    public int countActivityContaining(String token) {
        int count = 0;
        for (UmlObjectEvent e : events) {
            if (contains(e.getActivityName(), token)) {
                count++;
            }
        }
        return count;
    }

    public int firstIndexOf(String token) {
        for (UmlObjectEvent e : events) {
            if (contains(e.getActivityName(), token)) {
                return e.getIndex();
            }
        }
        return -1;
    }

    public int lastIndexOf(String token) {
        int last = -1;
        for (UmlObjectEvent e : events) {
            if (contains(e.getActivityName(), token)) {
                last = e.getIndex();
            }
        }
        return last;
    }

    private boolean contains(String value, String token) {
        return value != null && token != null
                && value.toLowerCase().contains(token.toLowerCase());
    }

    public String getObjectId() { return objectId; }
    public String getObjectType() { return objectType; }
    public String getObjectName() { return objectName; }
    public List<UmlObjectEvent> getEvents() { return events; }
}