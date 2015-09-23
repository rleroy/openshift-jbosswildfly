package com.leroy.ronan.wow.beans;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class WowHeadItem {

	private static final Logger log = Logger.getLogger(new Object() { }.getClass().getEnclosingClass());

	private String xml;
	
	private long id;
	private String name;
	private Set<WowHeadSpell> createdBy = new HashSet<>();

	public WowHeadItem(String xml) {
		this.xml = xml;

		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(new ByteArrayInputStream(xml.getBytes()));
			NodeList nodeList = document.getDocumentElement().getChildNodes();
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node node = nodeList.item(i);
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					this.id = Long.valueOf(node.getAttributes().getNamedItem("id").getNodeValue());
					
					NodeList children = node.getChildNodes();
					for (int j = 0; j < children.getLength(); j++) {
						Node child = children.item(j);
						if ("createdBy".equals(child.getNodeName())) {
							NodeList spells = child.getChildNodes();
							for (int k = 0; k < spells.getLength(); k++) {
								this.createdBy.add(new WowHeadSpell(spells.item(k)));
							}
						}
					}
				}
			}
		} catch (ParserConfigurationException | SAXException | IOException e) {
			log.error(e.getMessage(), e);
		}

	}

	public String xml() {
		return this.xml;
	}
	
	public long getId() {
		return id;
	}

	public String getName() {
		return this.name;
	}

	public Set<WowHeadSpell> getCreatedBy() {
		return createdBy;
	}

	public boolean isCraftable() {
		return createdBy.size() > 0;
	}
	
}
