package org.vaadin.addons.tokenfilter.demo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.annotation.WebServlet;

import org.vaadin.addons.tokenfilter.ExtendedOptionGroup;
import org.vaadin.addons.tokenfilter.TokenFilter;
import org.vaadin.addons.tokenfilter.TokenPopupButton;
import org.vaadin.addons.tokenfilter.demo.model.SimpleFilterOption;
import org.vaadin.addons.tokenfilter.demo.model.SimpleFilterType;
import org.vaadin.addons.tokenfilter.demo.model.SimpleOptionElement;

import com.google.gwt.thirdparty.guava.common.base.Joiner;
import com.google.gwt.thirdparty.guava.common.collect.Sets;
import com.vaadin.annotations.StyleSheet;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@Theme("valo")
@Title("TokenFilter Add-on Demo")
@StyleSheet("custom.css")
@SuppressWarnings("serial")
public class DemoUI extends UI {

    @WebServlet(
            value = "/*",
            asyncSupported = true)
    @VaadinServletConfiguration(
            productionMode = false,
            ui = DemoUI.class,
            widgetset = "org.vaadin.addons.tokenfilter.demo.DemoWidgetSet")
    public static class Servlet extends VaadinServlet {
    }

    @Override
    protected void init(VaadinRequest request) {

        // Show it in the middle of the screen
        final VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);
        layout.setSpacing(true);

        final TokenFilter<SimpleFilterType> filter = genTokenFilterField();

        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.addComponent(new Button("setValue", click -> {
            Set<SimpleFilterType> newValue = new HashSet<SimpleFilterType>();
            SimpleFilterType firstValue = new SimpleFilterType("first", null);
            firstValue.setSelected(Sets.newHashSet(new SimpleFilterOption("value 2")));
            newValue.add(firstValue);
            filter.setValue(newValue);
        }));
        buttonLayout.addComponent(new Button("update", click -> {
            SimpleFilterType firstValue = new SimpleFilterType("first", Arrays.asList("value 1", "value 2", "value 3", "value 10"));
            firstValue.setSelected(Sets.newHashSet(new SimpleFilterOption("value 2"), new SimpleFilterOption("value 10")));
            filter.updateItems(Arrays.asList(firstValue));
        }));

        layout.addComponent(new Panel("TokenFilter", new VerticalLayout(filter, buttonLayout)));

        filter.addValueChangeListener(new ValueChangeListener() {

            @Override
            public void valueChange(ValueChangeEvent event) {
                if (filter.getValue() != null) {
                    Notification.show(Joiner.on(",")
                                            .join(filter.getValue()),
                                      Type.TRAY_NOTIFICATION);
                }
            }
        });

        layout.addComponent(new Panel("TokenPopupButton", genTokenPopupButton()));
        layout.addComponent(new Panel("ExtendedOptionGroup", genExtendedOptionGroup()));

        setContent(layout);

    }

    private TokenPopupButton genTokenPopupButton() {
        return new TokenPopupButton("It's Caption", genExtendedOptionGroup());
    }

    private ExtendedOptionGroup<SimpleOptionElement> genExtendedOptionGroup() {
        ExtendedOptionGroup<SimpleOptionElement> optionGroup = new ExtendedOptionGroup<>(SimpleOptionElement.class);
        optionGroup.setCaption("It's Caption");
        optionGroup.setItemCaptionPropertyId("value");
        optionGroup.setItemStylePropertyId("styleName");
        optionGroup.setItemCountPropertyId("documentCount");
        optionGroup.setMaxItemsVisible(3);

        optionGroup.addItem(new SimpleOptionElement("value red", "red", 912L));
        optionGroup.addItem(new SimpleOptionElement("value blue", "blue", 323L));
        optionGroup.addItem(new SimpleOptionElement("value green", "green", 654L));
        optionGroup.addItem(new SimpleOptionElement("value yellow", "yellow", 232L));
        optionGroup.addItem(new SimpleOptionElement("value black", "black", 349L));

        return optionGroup;
    }

    private TokenFilter<SimpleFilterType> genTokenFilterField() {

        List<SimpleFilterType> filter = new ArrayList<SimpleFilterType>();
        filter.add(new SimpleFilterType("first", Arrays.asList("value 1", "value 2", "value 3")));
        filter.add(new SimpleFilterType("second", Arrays.asList("abcd", "efgh", "ijkl", "mnop", "qrst")));
        filter.add(new SimpleFilterType("third", Arrays.asList("123", "456", "789", "012")));

        final TokenFilter<SimpleFilterType> f = new TokenFilter<>("It's Caption");
        f.replaceItems(filter);
        return f;
    }

}
