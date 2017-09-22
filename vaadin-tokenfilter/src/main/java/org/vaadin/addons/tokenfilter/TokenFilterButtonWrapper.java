package org.vaadin.addons.tokenfilter;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.vaadin.addons.tokenfilter.model.FilterOption;
import org.vaadin.addons.tokenfilter.model.FilterType;
import org.vaadin.hene.popupbutton.PopupButton;
import org.vaadin.hene.popupbutton.PopupButton.PopupVisibilityEvent;
import org.vaadin.hene.popupbutton.PopupButton.PopupVisibilityListener;

import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.filter.SimpleStringFilter;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

/**
 * daws each selection within the {@link TokenFilter}
 * 
 * @author Marten Prieß (http://non-rocket-science.com)
 *
 * @param <I>
 *            instance of {@link FilterType}
 */
@SuppressWarnings("serial")
public class TokenFilterButtonWrapper<I extends FilterType<?>> extends CssLayout {

	private static final String COMPONENT_WIDTH = "250px";

	private TokenFilter<I> tokenFilter;
	private I tokenId;

	private long optionsTotalCount = 0;
	private PopupButton button;
	private Label progressBar;
	
	@SuppressWarnings("rawtypes")
	private ExtendedOptionGroup<FilterOption> optionGroup;
	
	private FieldGroup fieldGroup;
	private boolean freshlyAdded = false;

	private TextField filterField;
	private Button clearBtn;
	
	private CheckBox missingDocumentSelected;
	

	@SuppressWarnings("unchecked")
	public TokenFilterButtonWrapper(TokenFilter<I> tokenFilter, I tokenId) {
		this.tokenFilter = tokenFilter;
		this.tokenId = tokenId;

		this.fieldGroup = new FieldGroup();
		fieldGroup.setBuffered(false);
		fieldGroup.setItemDataSource(new BeanItem<I>(tokenId));

		setStyleName(TokenFilter.STYLE_TOKENFILTER_ITEM);

		progressBar = new Label("", ContentMode.HTML);
		progressBar.setStyleName(TokenFilter.STYLE_TOKENFILTER_PROGRESSBAR);
		addComponent(progressBar);

		initPopupButton();
		
		initFiltering();
		fieldGroup.bind(missingDocumentSelected, "missingDocumentSelected");
		
		initOptionGroup();
		fieldGroup.bind(optionGroup, "selected");

		HorizontalLayout filterLayout = new HorizontalLayout(filterField, clearBtn);
		filterLayout.setWidth(COMPONENT_WIDTH);
		filterLayout.setExpandRatio(filterField, 1);

		VerticalLayout layout = new VerticalLayout(filterLayout, missingDocumentSelected, optionGroup);
		layout.addStyleName("token-filter-button-content");
		button.setContent(layout);
		addComponent(button);

		Button deleteBtn = new Button("", event -> {
			getFieldGroupBean().setSelected(Collections.EMPTY_SET);
			TokenFilterButtonWrapper.this.tokenFilter.removeToken(tokenId);
		});
		deleteBtn.setIcon(FontAwesome.TIMES_CIRCLE);
		deleteBtn
				.addStyleName(TokenFilter.STYLE_TOKENFILTER_BUTTON + " " + TokenFilter.STYLE_TOKENFILTER_BUTTON_DELETE);
		deleteBtn.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
		deleteBtn.addStyleName(ValoTheme.BUTTON_BORDERLESS);
		deleteBtn.addStyleName(ValoTheme.BUTTON_SMALL);
		addComponent(deleteBtn);

		updateCaption();
	}

	private void initPopupButton() {
		button = new PopupButton();
		button.setCaptionAsHtml(true);
		button.addStyleName(TokenFilter.STYLE_TOKENFILTER_BUTTON);
		button.setButtonClickTogglesPopupVisibility(false);
		button.setDirection(Alignment.BOTTOM_CENTER);
		button.setClosePopupOnOutsideClick(true);
		button.addClickListener(event -> TokenFilterButtonWrapper.this.tokenFilter.onTokenClick(tokenId));
		button.addPopupVisibilityListener(new PopupVisibilityListener() {

			private int selectionHash = 0;

			@Override
			public void popupVisibilityChange(PopupVisibilityEvent event) {
				if (!event.isPopupVisible()) {
					optionGroup.getContainer().removeAllContainerFilters();
					filterField.clear();

					if ((getFieldGroupBean().getSelected() == null || getFieldGroupBean().getSelected().isEmpty()) 
							&& (getFieldGroupBean().getMissingDocumentSelected() == null || !getFieldGroupBean().getMissingDocumentSelected())) {
						TokenFilterButtonWrapper.this.tokenFilter.removeToken(tokenId);
					}
					int newSelectionHash = getFieldGroupBean().getSelectionHashCode();

					updateCaption();

					if (freshlyAdded) {
						TokenFilterButtonWrapper.this.tokenFilter.addNewToken(tokenId);
					} else {
						if (newSelectionHash != selectionHash) {
							TokenFilterButtonWrapper.this.tokenFilter.updateToken(tokenId);
						}
					}
					freshlyAdded = false;
				} else {
					// keep selectionHash on open
					selectionHash = getFieldGroupBean().getSelectionHashCode();
				}
			}

		});
	}

	@SuppressWarnings("rawtypes")
	private void initOptionGroup() {
		BeanItemContainer<FilterOption> container = new BeanItemContainer<FilterOption>(FilterOption.class) {
			@Override
			public Collection<?> getSortableContainerPropertyIds() {
				return Arrays.asList("missingDocumentRepresentation", "markedOut", "name");
			}
		};
		container.setItemSorter(new CaseInsensitiveItemSorter());

		optionGroup = new ExtendedOptionGroup<FilterOption>(container);
		if (tokenFilter.getMaxItemsVisible() != null) {
			optionGroup.setMaxItemsVisible(tokenFilter.getMaxItemsVisible());
		}
		optionGroup.addStyleName("option-group");
		optionGroup.setItemCaptionPropertyId("name");
		optionGroup.setItemStylePropertyId("styleName");
		optionGroup.setItemCountPropertyId("documentCount");
		optionGroup.setMultiSelect(true);
		optionGroup.removeAllItems();
		optionGroup.setWidth(COMPONENT_WIDTH);
		updateOptions(tokenId);
	}

	private void initFiltering() {
		filterField = new TextField();
		filterField.setWidth("100%");
		filterField.setInputPrompt("...");
		filterField.addStyleName("filter-field");
		filterField.addStyleName(ValoTheme.TEXTFIELD_INLINE_ICON);
		filterField.addStyleName(ValoTheme.TEXTFIELD_SMALL);
		filterField.setIcon(FontAwesome.SEARCH);
		filterField.addTextChangeListener(new TextChangeListener() {

			@Override
			public void textChange(TextChangeEvent event) {
				filterFieldChanged(event.getText());
			}
		});

		clearBtn = new Button(FontAwesome.TIMES);
		clearBtn.addStyleName(ValoTheme.BUTTON_BORDERLESS);
		clearBtn.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
		clearBtn.addStyleName(ValoTheme.BUTTON_SMALL);
		clearBtn.setVisible(false);
		clearBtn.addClickListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				filterField.clear();
				filterFieldChanged(null);
			}
		});
		
		missingDocumentSelected = new CheckBox(getMissingDocumentRepresentation());
		missingDocumentSelected.setWidth("100%");
		missingDocumentSelected.setCaptionAsHtml(true);
		missingDocumentSelected.addStyleName("missing-representation");
		missingDocumentSelected.setVisible(tokenFilter.getMissingDocumentRepresentation() != null);
		missingDocumentSelected.setImmediate(true);
		missingDocumentSelected.addValueChangeListener(e -> {
			Boolean value = (Boolean) e.getProperty().getValue();
			if (value != null && value) {
				missingDocumentSelected.addStyleName("checked");
			} else {
				missingDocumentSelected.removeStyleName("checked");
			}
		});
	}
	
	private String getMissingDocumentRepresentation() {
		return tokenFilter.getMissingDocumentRepresentation() != null ? tokenFilter.getMissingDocumentRepresentation().getName() : "MISSING_DOCUMENT";
	}

	private void filterFieldChanged(String text) {
		optionGroup.getContainer().removeAllContainerFilters();
		clearBtn.setVisible(false);
		if (text != null && text.trim().length() > 0) {
			optionGroup.getContainer().addContainerFilter(new SimpleStringFilter("name", text.trim(), true, false));
			clearBtn.setVisible(true);
		}
	}

	private I getFieldGroupBean() {
		return ((BeanItem<I>) fieldGroup.getItemDataSource()).getBean();
	}

	private void updateCaption() {
		long selectedCount = 0;
		StringBuffer strBuffer = new StringBuffer();
		
		if (getFieldGroupBean().getMissingDocumentSelected() != null
				&& getFieldGroupBean().getMissingDocumentSelected()) {
			strBuffer.append("<span class=\"").append(TokenFilter.STYLE_TOKENFILTER_BUTTON_VALUE).append("\">")
					.append(getMissingDocumentRepresentation()).append("</span>");
			selectedCount += getFieldGroupBean().getMissingDocumentCount() != null
					? getFieldGroupBean().getMissingDocumentCount() : 0;
		}

		for (FilterOption<?> option : getFieldGroupBean().getSelected()) {
			strBuffer.append("<span class=\"").append(TokenFilter.STYLE_TOKENFILTER_BUTTON_VALUE)
					.append(option.isMarkedOut() ? " " + TokenFilter.STYLE_TOKENFILTER_MARKED_OUT : "").append("\">")
					.append(option.getName()).append("</span>");
			selectedCount += option.getDocumentCount() != null ? option.getDocumentCount() : 0;
		}
		button.setCaption(String.format(
				"<span class=tokenfilter__button-caption>%s</span><span class=tokenfilter__button-values>%s</p>",
				tokenId.getTitle(), strBuffer.toString()));

		float totalPlusMissing = tokenId.getTotalDocumentCount() != null
				? tokenId.getTotalDocumentCount() : optionsTotalCount;
		totalPlusMissing += tokenId.getMissingDocumentCount() != null ? tokenId.getMissingDocumentCount() : 0;
		
		progressBar.setValue(String.format("<div class=tokenfilter__progress style=\"width: %s%%\"><div>",
				Math.round((selectedCount / totalPlusMissing) * 100)));

	}

	protected void updateOptions(I newTokenId) {
		Object currentValue = optionGroup.getValue();
		optionGroup.removeAllItems();
		optionGroup.addItems(newTokenId.getOptions());
		boolean hasMissingRepresentation = newTokenId.getMissingDocumentCount() != null && tokenFilter.getMissingDocumentRepresentation() != null;
		if (tokenId.getTotalDocumentCount() == null && newTokenId.getOptions() != null) {
			optionsTotalCount = 0;
			newTokenId.getOptions().forEach(o -> {
				optionsTotalCount += o.getDocumentCount() != null ? o.getDocumentCount() : 0;
			});
			if (hasMissingRepresentation) {
				optionsTotalCount += newTokenId.getMissingDocumentCount().longValue();
			}
		}
		
		boolean showMissingDocument = newTokenId.getMissingDocumentCount() != null && newTokenId.getMissingDocumentCount().longValue() > 0;
		if(hasMissingRepresentation && showMissingDocument) {
			missingDocumentSelected.setCaption(String.format("%s <span>(%d)</span>", getMissingDocumentRepresentation(), newTokenId.getMissingDocumentCount()));
		}
		missingDocumentSelected.setVisible(hasMissingRepresentation && showMissingDocument);
		missingDocumentSelected.setEnabled(hasMissingRepresentation && showMissingDocument);
		
		optionGroup.setValue(currentValue);
		sortOptionGroup();
	}

	protected void sortOptionGroup() {
		optionGroup.getContainer().sort(new Object[] { "markedOut", "name" },
				new boolean[] { true, true });
	}

	protected void updateSelection(I newTokenId) {
		getFieldGroupBean().setSelected(newTokenId.getSelected());
		updateCaption();
	}

	/**
	 * change display to display when token is markedout
	 * 
	 * @param markedOut
	 *            get extra styleName
	 */
	public void setMarkedOut(boolean markedOut) {
		if (markedOut) {
			this.addStyleName(TokenFilter.STYLE_TOKENFILTER_MARKED_OUT);
		} else {
			this.removeStyleName(TokenFilter.STYLE_TOKENFILTER_MARKED_OUT);
		}
	}

	public PopupButton getButton() {
		return button;
	}

	@Override
	public void focus() {
		optionGroup.focus();
		freshlyAdded = true;
	}

	public I getTokenId() {
		return tokenId;
	}

}
