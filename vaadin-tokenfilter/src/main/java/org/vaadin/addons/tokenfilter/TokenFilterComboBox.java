package org.vaadin.addons.tokenfilter;

import org.vaadin.addons.tokenfilter.client.TokenFilterServerRpc;

import com.vaadin.server.PaintException;
import com.vaadin.server.PaintTarget;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.themes.ValoTheme;

/**
 * little customization of the {@link ComboBox}
 * 
 * @author Marten Prieß (http://non-rocket-science.com)
 *
 */
public abstract class TokenFilterComboBox extends ComboBox {

    private static final long serialVersionUID = 8382983756053298383L;

    protected TokenFilter.InsertPosition insertPosition;

    private TokenFilterServerRpc rpc = new TokenFilterServerRpc() {
        @Override
        public void deleteToken() {
            onDelete();
        }
    };

    public TokenFilterComboBox(TokenFilter.InsertPosition insertPosition) {
        this.insertPosition = insertPosition;
        registerRpc(rpc);
        setStyleName(TokenFilter.STYLE_TOKENFILTER_TEXTFIELD);
        addStyleName(ValoTheme.COMBOBOX_BORDERLESS);
        addStyleName(ValoTheme.COMBOBOX_LARGE);
        setFilteringMode(FilteringMode.CONTAINS);
    }

    @Override
    public void paintContent(PaintTarget target) throws PaintException {
        super.paintContent(target);
        target.addVariable(this, "del", false);
        if (insertPosition == TokenFilter.InsertPosition.AFTER) {
            target.addAttribute("after", true);
        }
    }

    abstract protected void onDelete();

}
