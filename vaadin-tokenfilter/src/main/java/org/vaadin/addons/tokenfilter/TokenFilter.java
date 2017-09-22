package org.vaadin.addons.tokenfilter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.vaadin.addons.tokenfilter.model.FilterOption;
import org.vaadin.addons.tokenfilter.model.FilterType;
import org.vaadin.addons.tokenfilter.model.MissingDocumentFilterOption;

import com.vaadin.data.Container.Filter;
import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.Layout;
import com.vaadin.ui.themes.ValoTheme;

/**
 * mutlidimension tagfield
 * 
 * @author Marten Prieß (http://non-rocket-science.com)
 *
 * @param <I> instanct of {@link FilterType}
 */
public class TokenFilter<I extends FilterType<?>> extends CustomField<Set<I>> {

    private static final long serialVersionUID = 5729769445164284999L;

    public enum InsertPosition {
        /**
         * Tokens will be added after the input
         */
        AFTER,
        /**
         * Add tokens before the input
         */
        BEFORE
    }

    public static final String STYLE_TOKENFILTER = "tokenfilter";
    public static final String STYLE_TOKENFILTER_MARKED_OUT = "marked--out";
    public static final String STYLE_TOKENFILTER_TEXTFIELD = "tokenfilter__textfield";
    public static final String STYLE_TOKENFILTER_ITEM = "tokenfilter__item";
    public static final String STYLE_TOKENFILTER_PROGRESSBAR = "tokenfilter__progressbar";
    public static final String STYLE_TOKENFILTER_BUTTON = "tokenfilter__button";
    public static final String STYLE_TOKENFILTER_BUTTON_VALUE = "tokenfilter__button-value";
    public static final String STYLE_TOKENFILTER_BUTTON_DELETE = "tokenfilter__button--delete";

    private BeanItemContainer<I> filterTypeContainer = new BeanItemContainer<I>(FilterType.class);
    private I currentlyAdded = null;

    private Integer maxItemsVisible;

    protected Set<I> blendedOutTokens = new HashSet<>();
    protected LinkedHashMap<I, TokenFilterButtonWrapper<I>> buttons = new LinkedHashMap<I, TokenFilterButtonWrapper<I>>();
    protected MissingDocumentFilterOption missingDocumentRepresentation = null;
    
    /**
     * The layout currently in use
     */
    protected Layout layout = new CssLayout();

    /**
     * Current insert position
     */
    protected InsertPosition insertPosition = InsertPosition.BEFORE;

    /**
     * The ComboBox used for input - should probably not be touched.
     */
    protected AbstractSelect tokenTypeSelect;

    public TokenFilter() {
        this(null);
    }

    /**
     * cofigure how many items get displayed within popup-button - more item's will get visibile after scroll!
     * 
     * @param maxItemsVisible
     */
    public void setMaxItemsVisible(Integer maxItemsVisible) {
        this.maxItemsVisible = maxItemsVisible;
    }

    /**
     * Create a new TokenField with a caption and a {@link InsertPosition}.
     * 
     * @param caption the desired caption
     */
    @SuppressWarnings("serial")
    public TokenFilter(String caption) {
        if (caption != null) {
            setCaption(caption);
        }
        
        filterTypeContainer.setItemSorter(new CaseInsensitiveItemSorter());

        setStyleName(STYLE_TOKENFILTER);

        getTokenTypeSelect().setContainerDataSource(filterTypeContainer);
        getTokenTypeSelect().setItemCaptionPropertyId("title");
        getTokenTypeSelect().addValueChangeListener(event -> {
            final Object tokenId = event.getProperty()
                                        .getValue();
            if (tokenId instanceof FilterType) {
                onTokenInput((I) tokenId);
                getTokenTypeSelect().setValue(null);
            }
        });

        rebuild();
    }

    /**
     * initialize a AbstractSelect<br>
     * by default it's the TokenFilterComboBox but for a bride use it's overwriteable
     * 
     * @return select of tokenTypes
     */
    public AbstractSelect getTokenTypeSelect() {
        if (tokenTypeSelect == null) {
            tokenTypeSelect = new TokenFilterComboBox(insertPosition) {

                private static final long serialVersionUID = -5550767105896319355L;

                @SuppressWarnings("unchecked")
                @Override
                protected void onDelete() {
                    if (!buttons.isEmpty()) {
                        Object[] keys = buttons.keySet()
                                               .toArray();
                        onTokenDelete((I) keys[keys.length - 1]);
                        tokenTypeSelect.focus();
                    }
                }
            };

            tokenTypeSelect.setImmediate(true);
            tokenTypeSelect.addStyleName(ValoTheme.COMBOBOX_BORDERLESS);
            tokenTypeSelect.setNewItemsAllowed(false);
            tokenTypeSelect.setNullSelectionAllowed(false);
        }
        return tokenTypeSelect;
    }

    private void sortFilterTokenCombo() {
        filterTypeContainer.sort(new Object[] { "title" }, new boolean[] { true });
    }

    /**
     * remove all filterTypes adds new one
     * 
     * @param items
     */
    public void replaceItems(List<I> items) {
        filterTypeContainer.removeAllItems();
        filterTypeContainer.removeAllContainerFilters();
        if (items != null) {
            filterTypeContainer.addAll(items);
            sortFilterTokenCombo();
            
            if (blendedOutTokens != null) {
            	Set<I> newSet = new HashSet<>();
            	blendedOutTokens.stream().filter(d -> items.contains(d)).forEach(d -> newSet.add(d));
            	blendedOutTokens = newSet;
            }
            if (buttons != null) {
            	LinkedHashMap<I, TokenFilterButtonWrapper<I>> newMap = new LinkedHashMap<>();
            	buttons.keySet().stream().filter(d -> items.contains(d)).forEach(d -> newMap.put(d, buttons.get(d)));
            	buttons = newMap;
            }
        }
    }

    /**
     * also clear's the field and it's already painted/selected tokens
     * 
     */
    public void clearItems() {
        replaceItems(null);
    }

    /**
     * will not add new items but updated all properties <br>
     * check's not null properties and update the stored tokenId<br>
     * in case of options get removed and it was selected - this selection will get cleared
     * 
     * @param items
     */
    public void updateItems(List<I> items) {
        if (items != null) {
            I foundToken = null;
            for (I tokenId : items) {
                if (buttons.containsKey(tokenId)) {
                    foundToken = buttons.get(tokenId)
                                        .getTokenId();
                    foundToken.updateFilterTypeBy(tokenId);
                    buttons.get(tokenId)
                           .updateOptions(foundToken);
                    buttons.get(tokenId)
                           .updateSelection(foundToken);
                }
                else {
                    int index = filterTypeContainer.indexOfId(tokenId);
                    if (index >= 0) {
                        foundToken = filterTypeContainer.getIdByIndex(index);
                        foundToken.updateFilterTypeBy(tokenId);
                    }
                }
            }
            filterTypeContainer.removeAllContainerFilters();
            blendedOutTokens.clear();
            for(I token : filterTypeContainer.getItemIds()) {
            	if (!items.contains(token)) {
            		blendedOutTokens.add(token);
            	}
            }
            updateFilterTypeContainer();
        }
    }

    public List<I> getAllItems() {
        return filterTypeContainer.getItemIds();
    }

    /*
     * Rebuilds from scratch
     */
    private void rebuild() {
        layout.removeAllComponents();
        if (!isReadOnly() && insertPosition == InsertPosition.AFTER) {
            layout.addComponent(getTokenTypeSelect());
        }
        for (TokenFilterButtonWrapper<I> wrapper : buttons.values()) {
            layout.addComponent(wrapper);
        }
        if (!isReadOnly() && insertPosition == InsertPosition.BEFORE) {
            layout.addComponent(getTokenTypeSelect());
        }
    }

    protected Set<I> getClearedEmptySelection(Set<I> vals) {
        Set<I> result = new HashSet<>();
        if (vals != null) {
            result.addAll(vals);
        }
        result.removeAll(result.stream()
        		.filter(v -> {
        			boolean hasNoSelection = v.getSelected() == null || v.getSelected().isEmpty();
        			boolean isMissingDocSelected= v.getMissingDocumentSelected() != null && v.getMissingDocumentSelected();
        			return hasNoSelection && !isMissingDocSelected;
        		})
        		.collect(Collectors.toSet()));
        return result;
    }
    
    /**
     * called internally and triggers paint of buttons
     */
    @Override
    protected void setInternalValue(Set<I> newValue) {

        Set<I> vals = getClearedEmptySelection(newValue);
        Set<I> old = new HashSet<>();

        if (buttons.keySet() != null) {
            old.addAll(buttons.keySet());
        }

        Set<I> remove = new HashSet<I>(old);
        remove.removeAll(vals);

        Set<I> add = new HashSet<I>(vals);
        add.removeAll(old);

        for (I tokenId : remove) {
            removeTokenButton(tokenId);
        }
        for (I tokenId : add) {
            int index = filterTypeContainer.indexOfId(tokenId);
            if (index >= 0) {
                // take care that all it's options get keeped and only the selection get changed
                I containerToken = filterTypeContainer.getItemIds()
                                                      .get(index);
                containerToken.setSelected(tokenId.getSelected());
                addTokenButton(containerToken, false);
            }
        }
        
        updateFilterTypeContainer();
        // for those that are already painted check it's options
        List<I> newValueList = new ArrayList<>(vals);
        for (I o : old) {
            int index = newValueList.indexOf(o);
            if (index >= 0) {
                buttons.get(o)
                       .updateSelection(newValueList.get(index));
            }
        }
        sortFilterTokenCombo();

        super.setInternalValue(vals);
    }
    
    @SuppressWarnings("serial")
	private void updateFilterTypeContainer() {
    	filterTypeContainer.removeAllContainerFilters();
    	filterTypeContainer.addContainerFilter(new Filter() {
			
			@Override
			public boolean passesFilter(Object itemId, Item item) throws UnsupportedOperationException {
				return !buttons.keySet().contains(itemId) && !blendedOutTokens.contains(itemId);
			}
			
			@Override
			public boolean appliesToProperty(Object propertyId) {
				return true;
			}
		});
    }

    /**
     * Called when the user is adding a new token via the UI; called after the newItemHandler. Can be used to make customize the adding process; e.g to notify
     * that the token was not added because it's duplicate, to ask for additional informationr to disallow addition due to some heuristics (not both A and Q).
     * <br/>
     * The default is to call {@link #addToken(Object)} which will add the token if it's not a duplicate.
     * 
     * @param tokenId the token id selected (or input)
     */
    protected void onTokenInput(I tokenId) {
        addToken(tokenId);
    }

    protected void onTokenClick(I tokenId) {
        TokenFilterButtonWrapper<I> wrapper = buttons.get(tokenId);
        wrapper.getButton()
               .setPopupVisible(true);
    }

    protected void onTokenDelete(I tokenId) {
        removeToken(tokenId);
    }

    private void addTokenButton(final I tokenId, boolean keepCurrentlyAdded) {

        TokenFilterButtonWrapper<I> popupButtonWrapper = new TokenFilterButtonWrapper<I>(this, tokenId);
        buttons.put(tokenId, popupButtonWrapper);

        if (insertPosition == InsertPosition.BEFORE) {
            layout.replaceComponent(getTokenTypeSelect(), popupButtonWrapper);
            layout.addComponent(getTokenTypeSelect());
        }
        else {
            layout.addComponent(popupButtonWrapper);
        }

        if (keepCurrentlyAdded && currentlyAdded != null && currentlyAdded.equals(tokenId)) {
            popupButtonWrapper.getButton()
                              .setPopupVisible(true);
            popupButtonWrapper.focus();
        }

    }
    
    public Integer getMaxItemsVisible() {
		return maxItemsVisible;
	}
    
    public MissingDocumentFilterOption getMissingDocumentRepresentation() {
		return missingDocumentRepresentation;
	}
    public void setMissingDocumentRepresentation(MissingDocumentFilterOption missingDocumentRepresentation) {
		this.missingDocumentRepresentation = missingDocumentRepresentation;
	}
    
    protected void addNewToken(I tokenId) {
    	 Set<I> vals = new HashSet<>();
         if (getValue() != null) {
             vals.addAll(getValue());
         }
        vals.add(tokenId);
        setValue(vals);
        fireValueChange(false);
        updateFilterTypeContainer();
    }

    public void updateToken(I tokenId) {
        Set<I> vals = new HashSet<>();
        if (getValue() != null) {
            vals.addAll(getValue());
        }
        List<I> valList = new ArrayList<>(vals);
        int index = valList.indexOf(tokenId);
        if (index >= 0) {
            I toUpdateVal = valList.get(index);
            toUpdateVal.updateFilterTypeBy(tokenId);
        }
        fireValueChange(false);
    }

    private void addToken(I tokenId) {
        Set<I> set = getClearedEmptySelection(getValue());
        if (set == null) {
            set = new LinkedHashSet<I>();
        }
        if (set.contains(tokenId)) {
            return;
        }
        // to trigger open popupButton on draw event
        currentlyAdded = tokenId;
        addTokenButton(tokenId, true);
    }

    protected void removeToken(I tokenId) {
        Set<I> set = new HashSet<>();
        if (getValue() != null) {
            set.addAll(getValue());
        }
        LinkedHashSet<I> newSet = new LinkedHashSet<I>(set);
        newSet.remove(tokenId);

        removeTokenButton(tokenId);
        setValue(newSet);
        fireValueChange(false);
        updateFilterTypeContainer();
    }

    private void removeTokenButton(I tokenId) {
        TokenFilterButtonWrapper<I> button = buttons.get(tokenId);
        layout.removeComponent(button);
        buttons.remove(tokenId);
        sortFilterTokenCombo();
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        if (readOnly == isReadOnly()) {
            return;
        }
        for (TokenFilterButtonWrapper<I> wrapper : buttons.values()) {
            wrapper.setReadOnly(readOnly);
        }
        super.setReadOnly(readOnly);
        if (readOnly) {
            layout.removeComponent(getTokenTypeSelect());
        }
        else {
            rebuild();
        }
    }

    /*
     * (non-Javadoc)
     * @see org.vaadin.tokenfield.CustomField#focus()
     */
    @Override
    public void focus() {
        getTokenTypeSelect().focus();
    }

    /**
     * Gets all tokenIds currently in the token container.
     * 
     * @return a collection of all tokenIds in the container
     */
    public List<I> getTokenIds() {
        return filterTypeContainer.getItemIds();
    }

    /*
     * (non-Javadoc)
     * @see org.vaadin.tokenfield.CustomField#getTabIndex()
     */
    @Override
    public int getTabIndex() {
        return getTokenTypeSelect().getTabIndex();
    }

    @Override
    public void setHeight(float height, Unit unit) {
        if (this.layout != null) {
            this.layout.setHeight(height, unit);
        }
        super.setHeight(height, unit);
    }

    @Override
    public void setWidth(float width, Unit unit) {
        if (this.layout != null) {
            this.layout.setWidth(width, unit);
        }
        super.setWidth(width, unit);
    }

    @Override
    public void setSizeFull() {
        if (this.layout != null) {
            this.layout.setSizeFull();
        }
        super.setSizeFull();
    }

    @Override
    public void setSizeUndefined() {
        if (this.layout != null) {
            this.layout.setSizeUndefined();
        }
        super.setSizeUndefined();
    }

    /*
     * (non-Javadoc)
     * @see org.vaadin.tokenfield.CustomField#setTabIndex(int)
     */
    @Override
    public void setTabIndex(int tabIndex) {
        getTokenTypeSelect().setTabIndex(tabIndex);
    }

    /*
     * (non-Javadoc)
     * @see org.vaadin.tokenfield.CustomField#getType()
     */
    @Override
    public Class getType() {
        return Set.class;
    }

    @Override
    protected Component initContent() {
        return layout;
    }

}
