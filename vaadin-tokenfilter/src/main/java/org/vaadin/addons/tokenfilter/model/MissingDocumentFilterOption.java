package org.vaadin.addons.tokenfilter.model;

public class MissingDocumentFilterOption implements Cloneable {
	
	private String name;
    private Long documentCount;
	
	public MissingDocumentFilterOption(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setDocumentCount(Long count) {
		this.documentCount = count;
	}

	public Long getDocumentCount() {
		return documentCount;
	}
	
	@Override
	protected Object clone() throws CloneNotSupportedException {
		return new MissingDocumentFilterOption(this.name);
	}

}
