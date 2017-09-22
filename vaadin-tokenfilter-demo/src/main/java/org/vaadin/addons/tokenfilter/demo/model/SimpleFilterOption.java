package org.vaadin.addons.tokenfilter.demo.model;

import org.vaadin.addons.tokenfilter.model.AbstractFilterOption;

public class SimpleFilterOption extends AbstractFilterOption<String> {

    public SimpleFilterOption(String value) {
        super(value, null, (long) (Math.random() * 1000));
    }

    @Override
    public String getName() {
        return getValue();
    }
}