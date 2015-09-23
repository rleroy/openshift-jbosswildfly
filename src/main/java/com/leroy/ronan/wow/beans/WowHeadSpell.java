package com.leroy.ronan.wow.beans;

import java.util.HashSet;
import java.util.Set;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class WowHeadSpell {

	private long id;
	private String name;
	
	private Set<WowHeadReagent> reagents;
	
	public WowHeadSpell(Node item) {
		this.id = Long.valueOf(item.getAttributes().getNamedItem("id").getNodeValue());
		this.name = item.getAttributes().getNamedItem("name").getNodeValue();
		this.reagents = new HashSet<>();
		NodeList children = item.getChildNodes();
		for (int i  = 0; i < children.getLength(); i++) {
			reagents.add(new WowHeadReagent(children.item(i)));
		}
	}

	public long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public Set<WowHeadReagent> getReagents() {
		return reagents;
	}

}
