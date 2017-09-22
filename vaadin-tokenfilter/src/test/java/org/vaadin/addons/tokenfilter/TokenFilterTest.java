package org.vaadin.addons.tokenfilter;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.vaadin.addons.tokenfilter.model.TestFilterType;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;


public class TokenFilterTest {
	
	@SuppressWarnings("unchecked")
	@Test
	public void getClearedEmptySelection() {
		// given
		TokenFilter<TestFilterType> tokenFilter = new TokenFilter<>();
		
		
		Set<TestFilterType> vals = new HashSet<>();
		vals.add(new TestFilterType("get removed"));
		
		TestFilterType type = new TestFilterType("get removed - without selection", "val-1", "val-2");
		type.setSelected(Collections.emptyList());
		vals.add(type);
		
		TestFilterType shouldStay = new TestFilterType("should stay", "val-1", "val-2", "val-3");
		vals.add(shouldStay);
	
		// when	
		Set<TestFilterType> result = tokenFilter.getClearedEmptySelection(vals);
		
		// then
		assertThat(result, notNullValue());
		assertThat(result, hasSize(1));
        assertThat(result, hasItems(hasProperty("identifier", is(shouldStay.getIdentifier()))));
	}

}
