package org.vaadin.addons.tokenfilter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.vaadin.hene.popupbutton.PopupButton;

import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.CssLayout;

/**
 * same layout as {@link TokenFilter} but as a single popupbutton that holds a Field
 * 
 * @author Marten Prieß (http://non-rocket-science.com)
 *
 */
@SuppressWarnings("serial")
public class TokenPopupButton extends CssLayout {

    private PopupButton button;
    private String title;
    private AbstractSelect field;

    public TokenPopupButton(String title, AbstractSelect field) {
        this.title = title;
        this.field = field;

        setStyleName(TokenFilter.STYLE_TOKENFILTER_ITEM);
        addStyleName("tokenfilter__single-one");

        button = new PopupButton();
        button.setCaptionAsHtml(true);
        button.addStyleName(TokenFilter.STYLE_TOKENFILTER_BUTTON);
        button.setButtonClickTogglesPopupVisibility(true);
        button.setClosePopupOnOutsideClick(true);
        button.setContent(field);

        field.addValueChangeListener(event -> updateCaption());

        updateCaption();
        addComponent(button);
    }

    private void updateCaption() {
        StringBuffer strBuffer = new StringBuffer();

        List<String> values = new ArrayList<>();
        if (field.getValue() != null) {
            if (field.getValue() instanceof Collection) {
                for (Object itemId : (Collection<?>) field.getValue()) {
                    values.add(field.getItemCaption(itemId));
                }
            }
            else {
                values.add(field.getItemCaption(field.getValue()));
            }
        }

        for (String value : values) {
            strBuffer.append("<span class=\"")
                     .append(TokenFilter.STYLE_TOKENFILTER_BUTTON_VALUE)
                     .append("\">")
                     .append(value)
                     .append("</span>");
        }
        button.setCaption(String.format("<span class=tokenfilter__button-caption>%s</span><span class=tokenfilter__button-values>%s</p>", title, strBuffer.toString()));
    }

}
