package com.uniba.mining.processfeedback.ocpm;

public class UmlObjectEvent {

	private String objectId;
	private String objectType;
	private String objectName;
	private String activityName;
	private String timestamp;
	private String propertyName;
	private String propertyValue;
	private int index;
	private String relationshipFrom;
	private String relationshipTo;

	public String getObjectId() {
		return objectId;
	}

	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}

	public String getObjectType() {
		return objectType;
	}

	public void setObjectType(String objectType) {
		this.objectType = objectType;
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

	public String getObjectName() {
		return objectName;
	}

	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}

	public String getActivityName() {
		return activityName;
	}

	public void setActivityName(String activityName) {
		this.activityName = activityName;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
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

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}
}