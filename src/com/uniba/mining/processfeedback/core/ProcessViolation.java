package com.uniba.mining.processfeedback.core;

public class ProcessViolation {

    private String constraint;
    private String activities;
    private String resultType;
    private String activityName;
    private String activityIndex;

    private String elementType;
    private String elementName;

    private String propertyName;
    private String propertyValue;

    private String relationshipFrom;
    private String relationshipTo;

    private String elementId;

    private String description;

    public boolean isViolation() {
        return "violation".equalsIgnoreCase(resultType);
    }

    public String getConstraint() {
        return constraint;
    }

    public void setConstraint(String constraint) {
        this.constraint = constraint;
    }

    public String getActivities() {
        return activities;
    }

    public void setActivities(String activities) {
        this.activities = activities;
    }

    public String getResultType() {
        return resultType;
    }

    public void setResultType(String resultType) {
        this.resultType = resultType;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public String getActivityIndex() {
        return activityIndex;
    }

    public void setActivityIndex(String activityIndex) {
        this.activityIndex = activityIndex;
    }

    public String getElementType() {
        return elementType;
    }

    public void setElementType(String elementType) {
        this.elementType = elementType;
    }

    public String getElementName() {
        return elementName;
    }

    public void setElementName(String elementName) {
        this.elementName = elementName;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public String getPropertyValue() {
        return propertyValue;
    }

    public void setPropertyValue(String propertyValue) {
        this.propertyValue = propertyValue;
    }

    public String getRelationshipFrom() {
        return relationshipFrom;
    }

    public void setRelationshipFrom(String relationshipFrom) {
        this.relationshipFrom = relationshipFrom;
    }

    public String getRelationshipTo() {
        return relationshipTo;
    }

    public void setRelationshipTo(String relationshipTo) {
        this.relationshipTo = relationshipTo;
    }

    public String getElementId() {
        return elementId;
    }

    public void setElementId(String elementId) {
        this.elementId = elementId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}