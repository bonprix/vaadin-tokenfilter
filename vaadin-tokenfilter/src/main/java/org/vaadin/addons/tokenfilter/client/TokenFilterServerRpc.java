package org.vaadin.addons.tokenfilter.client;

import com.vaadin.shared.communication.ServerRpc;

/**
 * 
 * @author Marten Prieß (http://non-rocket-science.com)
 *
 */
public interface TokenFilterServerRpc extends ServerRpc {

    public void deleteToken();
}
