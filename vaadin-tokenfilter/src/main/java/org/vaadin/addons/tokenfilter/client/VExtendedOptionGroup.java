package org.vaadin.addons.tokenfilter.client;

import java.util.Iterator;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.UIDL;
import com.vaadin.client.ui.VOptionGroup;

/**
 * 
 * @author Marten Prieß (http://non-rocket-science.com)
 *
 */
public class VExtendedOptionGroup extends VOptionGroup {

    private static final String STYLE_CHECKED = "checked";
    private Integer itemCount;
    private Integer offsetHeight;

    public VExtendedOptionGroup() {
        setStyleName(CLASSNAME);
        addStyleName("extended");
    }

    @Override
    public void buildOptions(UIDL uidl) {
        super.buildOptions(uidl);

        // Update styles.
        final Iterator<?> it = uidl.getChildIterator();

        int totalOptionCount = 0;
        for (Widget op : panel) {
            totalOptionCount++;

            if (itemCount != null) {
            	// calculate only when needed
	            if (offsetHeight == null) {
	                offsetHeight = getOffsetHeightWithMargin(op.getElement());
	            }
	
	            // in many cases the second element has a different styling than the first
	            if (totalOptionCount == 2 && offsetHeight != null) {
	                Integer secondHeight = getOffsetHeightWithMargin(op.getElement());
	                if (secondHeight > offsetHeight) {
	                    offsetHeight = secondHeight;
	                }
	            }
            }

            final UIDL opUidl = (UIDL) it.next();

            String opStyle = opUidl.getStringAttribute("style");
            if (opStyle != null && opStyle.trim()
                                          .length() > 0) {
                op.addStyleName(opStyle);

            }

            if (op instanceof CheckBox) {
                CheckBox checkbox = (CheckBox) op;

                String count = opUidl.getStringAttribute("count");
                if (count != null) {
                    checkbox.setHTML(checkbox.getHTML() + " <span>(" + count + ")</span>");
                }
                if (checkbox.getValue()) {
                    checkbox.addStyleName(STYLE_CHECKED);
                }
                else {
                    checkbox.removeStyleName(STYLE_CHECKED);
                }
            }
        }

        if (itemCount != null) {
            if (offsetHeight != null && totalOptionCount > itemCount) {
                getElement().getStyle()
                            .setHeight(offsetHeight * itemCount, Unit.PX);
            }
            else {
                getElement().getStyle()
                            .clearHeight();
            }
        }
    }

    private Integer getOffsetHeightWithMargin(Element el) {
        Integer offsetHeight = el.getClientHeight();

        String marginTop = getStyleProperty(el, "marginTop");
        String marginBottom = getStyleProperty(el, "marginBottom");
        if (marginTop != null && marginTop.endsWith("px")) {
            offsetHeight = offsetHeight + new Integer(marginTop.replace("px", "")
                                                               .trim());
        }
        if (marginBottom != null && marginBottom.endsWith("px")) {
            offsetHeight = offsetHeight + new Integer(marginBottom.replace("px", "")
                                                                  .trim());
        }
        return offsetHeight;
    }

    public void setItemCount(int itemCount) {
        this.itemCount = itemCount;
        addStyleName("itemCount");
    }

    native String getStyleProperty(Element el, String prop) /*-{
                                                            var computedStyle;
                                                            if (document.defaultView && document.defaultView.getComputedStyle) { // standard (includes ie9)
                                                            computedStyle = document.defaultView.getComputedStyle(el, null)[prop];
                                                            
                                                            } else if (el.currentStyle) { // IE older
                                                            computedStyle = el.currentStyle[prop];
                                                            
                                                            } else { // inline style
                                                            computedStyle = el.style[prop];
                                                            }
                                                            return computedStyle;
                                                            
                                                            }-*/;

    /**
     * used to log in javascript console
     * 
     * @param message info to get logged
     */
    native void consoleLog(final String message) /*-{
                                                 console.log( message );
                                                 }-*/;
}
