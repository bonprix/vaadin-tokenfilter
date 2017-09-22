package org.vaadin.addons.tokenfilter.client;

import org.vaadin.addons.tokenfilter.TokenFilterComboBox;
import org.vaadin.addons.tokenfilter.client.VTokenFilter.DeleteListener;

import com.google.gwt.core.client.GWT;
import com.vaadin.client.communication.RpcProxy;
import com.vaadin.client.ui.combobox.ComboBoxConnector;
import com.vaadin.shared.ui.Connect;

/**
 * 
 * @author Marten Prieß (http://non-rocket-science.com)
 *
 */
@Connect(TokenFilterComboBox.class)
public class TokenFilterConnector extends ComboBoxConnector {

    private TokenFilterServerRpc rpc = RpcProxy.create(TokenFilterServerRpc.class, this);

    protected boolean after = false;

    @Override
    protected void init() {
        getWidget().addListener(new DeleteListener() {
            @Override
            public void onDelete() {
                rpc.deleteToken();
            }
        });

    }

    @Override
    public VTokenFilter getWidget() {
        return (VTokenFilter) super.getWidget();
    }

    @Override
    protected VTokenFilter createWidget() {
        // TODO Auto-generated method stub
        return GWT.create(VTokenFilter.class);
    }

}
