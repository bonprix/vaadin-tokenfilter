package org.vaadin.addons.tokenfilter.demo.model;

import java.util.List;

import org.vaadin.addons.tokenfilter.model.AbstractFilterType;

public class SimpleFilterType extends AbstractFilterType<String> {

    public SimpleFilterType(String identifier, List<String> options) {
        super(identifier);
        if (options != null) {
            for (String option : options) {
                addOption(new SimpleFilterOption(option));
            }
        }
    }

    @Override
    public String getTitle() {
        return getIdentifier();
    }

}