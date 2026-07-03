package com.uniba.mining.processfeedback.ocpm;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class XesObjectLifecycleExtractor {

	public List<UmlObjectLifecycle> extract(File xesFile) throws Exception {

		Map<String, UmlObjectLifecycle> lifecycleMap = new LinkedHashMap<>();

		Document document = parseXml(xesFile);

		List<Element> eventElements = new ArrayList<>();
		collectEvents(document.getRootElement(), eventElements);

		int globalIndex = 0;

		for (Element eventElement : eventElements) {

			UmlObjectEvent event = buildEvent(eventElement, globalIndex);

			globalIndex++;

			if (isNoise(event)) {
				continue;
			}

			UmlObjectLifecycle lifecycle = lifecycleMap.get(event.getObjectId());

			if (lifecycle == null) {
				lifecycle = new UmlObjectLifecycle(event.getObjectId(), event.getObjectType(), event.getObjectName());

				lifecycleMap.put(event.getObjectId(), lifecycle);
			}

			lifecycle.addEvent(event);
		}

		return new ArrayList<>(lifecycleMap.values());
	}

	private Document parseXml(File xesFile) throws Exception {
		SAXReader reader = new SAXReader();
		return reader.read(xesFile);
	}

	private void collectEvents(Element element, List<Element> events) {

		if (element == null) {
			return;
		}

		if ("event".equalsIgnoreCase(element.getName())) {
			events.add(element);
			return;
		}

		for (Object child : element.elements()) {
			if (child instanceof Element) {
				collectEvents((Element) child, events);
			}
		}
	}

	private UmlObjectEvent buildEvent(Element eventElement, int index) {

		UmlObjectEvent event = new UmlObjectEvent();

		event.setIndex(index);

		event.setActivityName(getValue(eventElement, "concept:name"));

		event.setTimestamp(getValue(eventElement, "time:timestamp"));

		event.setObjectId(firstNonEmpty(getValue(eventElement, "umlelementid"), getValue(eventElement, "UMLElementId"),
				getValue(eventElement, "umlElementId"), getValue(eventElement, "elementId")));

		event.setObjectType(
				firstNonEmpty(getValue(eventElement, "umlelementtype"), getValue(eventElement, "UMLElementType"),
						getValue(eventElement, "umlElementType"), getValue(eventElement, "elementType")));

		event.setObjectName(
				firstNonEmpty(getValue(eventElement, "umlelementname"), getValue(eventElement, "UMLElementName"),
						getValue(eventElement, "umlElementName"), getValue(eventElement, "elementName")));

		event.setPropertyName(firstNonEmpty(getValue(eventElement, "propertyname"),
				getValue(eventElement, "PropertyName"), getValue(eventElement, "propertyName")));

		event.setPropertyValue(firstNonEmpty(getValue(eventElement, "propertyvalue"),
				getValue(eventElement, "PropertyValue"), getValue(eventElement, "propertyValue")));

		event.setRelationshipFrom(
				firstNonEmpty(getValue(eventElement, "RelationshipFrom"), getValue(eventElement, "relationshipFrom")));

		event.setRelationshipTo(
				firstNonEmpty(getValue(eventElement, "RelationshipTo"), getValue(eventElement, "relationshipTo")));
		return event;
	}

	private String getValue(Element eventElement, String key) {

		for (Object child : eventElement.elements()) {

			if (!(child instanceof Element)) {
				continue;
			}

			Element element = (Element) child;

			String elementKey = element.attributeValue("key");

			if (key.equals(elementKey)) {
				String value = element.attributeValue("value");
				return value == null ? "" : value.trim();
			}
		}

		return "";
	}

	private boolean isNoise(UmlObjectEvent event) {

		if (event == null) {
			return true;
		}

		if (isEmpty(event.getObjectId())) {
			return true;
		}

		if (isEmpty(event.getObjectType())) {
			return true;
		}

		if ("Project".equalsIgnoreCase(event.getObjectType())) {
			return true;
		}

		if ("unknown".equalsIgnoreCase(event.getObjectType())) {
			return true;
		}

		return false;
	}

	private String firstNonEmpty(String... values) {

		if (values == null) {
			return "";
		}

		for (String value : values) {
			if (!isEmpty(value)) {
				return value.trim();
			}
		}

		return "";
	}

	private boolean isEmpty(String value) {
		return value == null || value.trim().isEmpty();
	}
}