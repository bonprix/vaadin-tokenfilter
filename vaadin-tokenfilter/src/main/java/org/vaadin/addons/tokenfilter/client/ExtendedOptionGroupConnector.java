package org.vaadin.addons.tokenfilter.client;

import org.vaadin.addons.tokenfilter.ExtendedOptionGroup;

import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.UIDL;
import com.vaadin.client.ui.optiongroup.OptionGroupConnector;
import com.vaadin.shared.ui.Connect;

/**
 * 
 * @author Marten Prieß (http://non-rocket-science.com)
 *
 */
@Connect(ExtendedOptionGroup.class)
public class ExtendedOptionGroupConnector extends OptionGroupConnector {

    @Override
    public VExtendedOptionGroup getWidget() {
        return (VExtendedOptionGroup) super.getWidget();
    }

    @Override
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        if (uidl.hasAttribute("itemCount")) {
            getWidget().setItemCount(uidl.getIntAttribute("itemCount"));
        }
        super.updateFromUIDL(uidl, client);
    }

}
