package org.vaadin.addons.tokenfilter.model;

import java.util.Collection;

/**
 * 
 * @author Marten Prieß (http://non-rocket-science.com)
 *
 * @param <I>
 *            type of identifier
 */
public interface FilterType<I> {

	/**
	 * update current instance by properties of other
	 * 
	 * @param other
	 */
	void updateFilterTypeBy(FilterType<?> other);

	/**
	 * get displayed within dropdown suggestion and as title of the OptionGroup
	 * 
	 * @return
	 */
	String getTitle();

	/**
	 * 
	 * @return list of available options
	 */
	Collection<FilterOption<?>> getOptions();

	/**
	 * used to store a reference to database or index
	 * 
	 * @return
	 */
	I getIdentifier();

	/**
	 * 
	 * @return a none empty list
	 */
	Collection<FilterOption<?>> getSelected();

	/**
	 * 
	 * @param selection
	 *            collection of selection
	 */
	void setSelected(Collection<FilterOption<?>> selection);

	Boolean getMissingDocumentSelected();

	void setMissingDocumentSelected(Boolean missingDocumentSelected);

	@Override
	public abstract boolean equals(Object other);

	@Override
	public abstract int hashCode();
	
	/**
	 * differs from default hashCode - care's for selection and missingDocumentSelected
	 * 
	 * @return
	 */
	public abstract int getSelectionHashCode();

	/**
	 * in some cases the totalDocumentCount can different from the sum within
	 * options
	 * 
	 * @param count
	 */
	void setTotalDocumentCount(Long count);

	/**
	 * used to calculate the filter-percentage indicator. <br>
	 * totalDocumentCount can different from the sum within options
	 * 
	 * @return
	 */
	Long getTotalDocumentCount();

	/**
	 * sumup all null-values into one virtual option
	 * 
	 * @param count
	 */
	void setMissingDocumentCount(Long count);

	/**
	 * used to calculate if a virtual option of null should get added
	 * 
	 * @return
	 */
	Long getMissingDocumentCount();

}
