package org.vaadin.addons.tokenfilter.model;

import java.util.Random;

public class TestFilterOption extends AbstractFilterOption<String> {

	public TestFilterOption(String value) {
		super(value, null, new Random().nextLong());
	}
	
	public TestFilterOption(String value, Long documentCount) {
		super(value, null, documentCount);
	}
	
	@Override
	public String getName() {
		return getValue();
	}

}
