package com.leroy.ronan.wow.beans;

import org.w3c.dom.Node;

public class WowHeadReagent {

	private long id;
	private String name;
	private int count;

	public WowHeadReagent(Node item) {
		this.id = Long.valueOf(item.getAttributes().getNamedItem("id").getNodeValue());
		this.name = item.getAttributes().getNamedItem("name").getNodeValue();
		this.count = Integer.valueOf(item.getAttributes().getNamedItem("count").getNodeValue());
	}

	public long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public int getCount() {
		return count;
	}

}
