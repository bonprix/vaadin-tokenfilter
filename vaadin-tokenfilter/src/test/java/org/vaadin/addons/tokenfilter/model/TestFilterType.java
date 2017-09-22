package org.vaadin.addons.tokenfilter.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class TestFilterType extends AbstractFilterType<String> {

	public TestFilterType(String val) {
		super(val);
	}
	
	public TestFilterType(String val, String... options) {
		super(val);
		
		Collection<TestFilterOption> optionList = new ArrayList<>();
		for (String o : options) {
			TestFilterOption filterOption = new TestFilterOption(o);
			optionList.add(filterOption); 
			addOption(filterOption);
		}
		setSelected((Collection) optionList);
	}
	
	@Override
	public String getTitle() {
		return getIdentifier();
	}

}
