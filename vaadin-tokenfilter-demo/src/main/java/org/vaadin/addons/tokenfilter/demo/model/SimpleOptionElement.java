package org.vaadin.addons.tokenfilter.demo.model;

public class SimpleOptionElement {

    private String value;
    private String styleName;
    private Long documentCount;

    public SimpleOptionElement(String value, String styleName, Long documentCount) {
        super();
        this.value = value;
        this.styleName = styleName;
        this.documentCount = documentCount;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getStyleName() {
        return styleName;
    }

    public void setStyleName(String styleName) {
        this.styleName = styleName;
    }

    public Long getDocumentCount() {
        return documentCount;
    }

    public void setDocumentCount(Long documentCount) {
        this.documentCount = documentCount;
    }

}
