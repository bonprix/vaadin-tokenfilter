package org.vaadin.addons.tokenfilter.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

/**
 * 
 * @author Marten Prieß (http://non-rocket-science.com)
 *
 * @param <I>
 *            type of the identifier
 */
public abstract class AbstractFilterType<I> implements FilterType<I> {

	private I identifier;
	private Collection<FilterOption<?>> options = new HashSet<>();
	private Collection<FilterOption<?>> selected = new HashSet<>();
	private Long totalDocumentCount;
	private Long missingDocumentCount;
	private Boolean missingDocumentSelected = false;

	public AbstractFilterType(I identifier) {
		setIdentifier(identifier);
	}

	public AbstractFilterType<I> addOption(FilterOption<?> option) {
		options.add(option);
		return this;
	}

	@Override
	public I getIdentifier() {
		return identifier;
	}

	private void setIdentifier(I identifier) {
		this.identifier = identifier;
	}

	@Override
	public Collection<FilterOption<?>> getOptions() {
		return options;
	}

	@Override
	public void setSelected(Collection<FilterOption<?>> selected) {
		this.selected = selected;
	}

	@Override
	public Collection<FilterOption<?>> getSelected() {
		return selected;
	}
	
	@Override
	public Boolean getMissingDocumentSelected() {
		return missingDocumentSelected;
	}

	@Override
	public void setMissingDocumentSelected(Boolean missingDocumentSelected) {
		this.missingDocumentSelected = missingDocumentSelected;
	}

	@Override
	public Long getTotalDocumentCount() {
		return totalDocumentCount;
	}

	@Override
	public void setTotalDocumentCount(Long totalDocumentCount) {
		this.totalDocumentCount = totalDocumentCount;
	}

	@Override
	public void setMissingDocumentCount(Long count) {
		this.missingDocumentCount = count;
	}

	@Override
	public Long getMissingDocumentCount() {
		return missingDocumentCount;
	}

	protected void syncOptions(FilterType<?> other) {
		if (other != null && other.getOptions() != null) {

			List<FilterOption<?>> othersOptions = new ArrayList<>(other.getOptions());

			for (FilterOption<?> option : getOptions()) {
				int index = othersOptions.indexOf(option);
				if (index >= 0) {
					FilterOption<?> foundOption = othersOptions.get(index);
					if (foundOption.getDocumentCount() != null) {
						option.setDocumentCount(foundOption.getDocumentCount());
					}
					option.setMarkedOut(foundOption.isMarkedOut());
				} else {
					option.setMarkedOut(true);
					// option.setDocumentCount(0L);
				}
			}
			for (FilterOption<?> o : other.getOptions()) {
				if (!getOptions().contains(o)) {
					options.add(o);
				}
			}
		}
	}

	protected void syncSelection(FilterType<?> other) {
		if (getSelected() != null) {
			// sync possible changes within options and selection
			List<FilterOption<?>> thisOptionList = new ArrayList<>(getOptions());
			for (FilterOption<?> o : getSelected()) {
				int index = thisOptionList.indexOf(o);
				if (index >= 0) {
					FilterOption<?> optionO = thisOptionList.get(index);
					if (optionO.getDocumentCount() != null) {
						o.setDocumentCount(optionO.getDocumentCount());
					}
					o.setMarkedOut(optionO.isMarkedOut());
				}
			}
		}
		if (other != null && other.getSelected() != null && !other.getSelected().isEmpty()) {
			List<FilterOption<?>> thisOptionList = new ArrayList<>(getOptions());
			for (FilterOption<?> o : other.getSelected()) {
				if (!thisOptionList.contains(o)) {
					getSelected().add(o);
				}
			}
			// deselect old one that are not within new selection
			thisOptionList.removeAll(other.getSelected());
			for (FilterOption<?> o : thisOptionList) {
				getSelected().remove(o);
			}

		}
	}

	@Override
	public void updateFilterTypeBy(FilterType<?> other) {
		if (other != null && other.getTotalDocumentCount() != null) {
			setTotalDocumentCount(other.getTotalDocumentCount());
		}
		if (other != null && other.getMissingDocumentCount() != null) {
			setMissingDocumentCount(other.getMissingDocumentCount());
		}
		syncOptions(other);
		syncSelection(other);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((identifier == null) ? 0 : identifier.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AbstractFilterType<?> other = (AbstractFilterType<?>) obj;
		if (identifier == null) {
			if (other.identifier != null)
				return false;
		} else if (!identifier.equals(other.identifier))
			return false;
		return true;
	}
	
	@Override
	public int getSelectionHashCode() {
		return String.format("%d-%d", getSelected() != null ? getSelected().hashCode() : 0,
				getMissingDocumentSelected() != null && getMissingDocumentSelected() ? 1 : 0).hashCode();
	}

	@Override
	public String toString() {
		return String.format("%s, withSelection: %s", getTitle(), getSelected());
	}

}
