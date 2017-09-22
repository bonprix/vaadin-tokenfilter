package org.vaadin.addons.tokenfilter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.vaadin.data.Property;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.PaintException;
import com.vaadin.server.PaintTarget;
import com.vaadin.ui.OptionGroup;

/**
 * allows to add extra values to the {@link OptionGroup}
 * 
 * @author Marten Prieß (http://non-rocket-science.com)
 *
 * @param <BEANTYPE> of options
 */
@SuppressWarnings("serial")
public class ExtendedOptionGroup<BEANTYPE> extends OptionGroup {

    private BeanItemContainer<BEANTYPE> container;
    private Object itemStylePropertyId, itemCountPropertyId;
    private Integer itemCount;
    
    public ExtendedOptionGroup(BeanItemContainer<BEANTYPE> container) {
    	this.container = container;
    	setImmediate(true);
    	setMultiSelect(true);
    	setContainerDataSource(this.container);
    }
    
    public ExtendedOptionGroup(final Class<? super BEANTYPE> type) {
        this(new BeanItemContainer<>(type));
    }

    /**
     * set the maximum items that should get displayed - more will get displayed via scroll
     * 
     * @param itemCount
     */
    public void setMaxItemsVisible(int itemCount) {
        this.itemCount = itemCount;
    }

    @Override
    public void paintContent(PaintTarget target) throws PaintException {
        if (itemCount != null) {
            target.addAttribute("itemCount", itemCount);
        }
        super.paintContent(target);
    }

    public BeanItemContainer<BEANTYPE> getContainer() {
        return container;
    }

    @Override
    protected void paintItem(PaintTarget target, Object itemId) throws PaintException {
        super.paintItem(target, itemId);

        if (itemStylePropertyId != null) {
            final Property<?> p = getContainerProperty(itemId, getItemStylePropertyId());
            if (p != null && p.getValue() instanceof String) {
                target.addAttribute("style", (String) p.getValue());
            }
        }
        if (itemCountPropertyId != null) {
            final Property<?> p = getContainerProperty(itemId, getItemCountPropertyId());
            if (p != null && p.getValue() instanceof Long) {
                target.addAttribute("count", (Long) p.getValue());
            }
            else if (p != null && p.getValue() instanceof Integer) {
                target.addAttribute("count", (Integer) p.getValue());
            }
        }
    }

    public void replaceItems(List<BEANTYPE> items) {
        container.removeAllItems();
        if (items != null) {
            container.addAll(items);
        }
    }

    public Object getItemStylePropertyId() {
        return itemStylePropertyId;
    }

    public void setItemStylePropertyId(Object itemStylePropertyId) {
        this.itemStylePropertyId = itemStylePropertyId;
    }

    public Object getItemCountPropertyId() {
        return itemCountPropertyId;
    }

    public void setItemCountPropertyId(Object itemCountPropertyId) {
        this.itemCountPropertyId = itemCountPropertyId;
    }

    /**
     * 
     * @return null or first selection (works also for multiselect)
     */
    public BEANTYPE getSelectedItem() {
        if (!getSelectedItems().isEmpty()) {
            return getSelectedItems().get(0);
        }
        else {
            return null;
        }
    }

    /**
     * 
     * @return a not nullable List of beans
     */
    public List<BEANTYPE> getSelectedItems() {
        if (getValue() instanceof Collection) {
            return new ArrayList((Collection) getValue());
        }
        else if (getValue() != null) {
            return Arrays.asList((BEANTYPE) getValue());
        }
        else {
            return Collections.EMPTY_LIST;
        }
    }
}
