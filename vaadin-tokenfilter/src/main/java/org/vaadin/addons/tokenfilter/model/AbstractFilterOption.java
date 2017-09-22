package org.vaadin.addons.tokenfilter.model;

/**
 * 
 * @author Marten Prieß (http://non-rocket-science.com)
 *
 * @param <V> type of the value
 */
public abstract class AbstractFilterOption<V> implements FilterOption<V> {

    private V value;
    private String styleName;
    private Long documentCount;
    private boolean markedOut = false;

    public AbstractFilterOption(V value, String styleName, Long documentCount) {
        setValue(value);
        setStyleName(styleName);
        setDocumentCount(documentCount);
    }

    @Override
    public V getValue() {
        return value;
    }

    private void setValue(V value) {
        this.value = value;
    }

    @Override
    public String getStyleName() {
        if (isMarkedOut()) {
            return "marked--out" + (styleName != null ? " " + styleName : "");
        }
        return styleName;
    }

    public void setStyleName(String styleName) {
        this.styleName = styleName;
    }

    @Override
    public Long getDocumentCount() {
        return documentCount;
    }

    @Override
    public void setDocumentCount(Long documentCount) {
        this.documentCount = documentCount;
    }

    @Override
    public boolean isMarkedOut() {
        return markedOut;
    }

    @Override
    public void setMarkedOut(boolean markedOut) {
        this.markedOut = markedOut;
    }

    @Override
    public String toString() {
        return getValue().toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((value == null) ? 0 : value.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AbstractFilterOption<?> other = (AbstractFilterOption<?>) obj;
        if (value == null) {
            if (other.value != null)
                return false;
        }
        else if (!value.equals(other.value))
            return false;
        return true;
    }

}
