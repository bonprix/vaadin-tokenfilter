package org.vaadin.addons.tokenfilter.model;

/**
 * 
 * @author Marten Prieß (http://non-rocket-science.com)
 *
 * @param <V> type of value
 */
public interface FilterOption<V extends Object> {

    /**
     * basic value that is a representation of db / index
     * 
     * @return
     */
    V getValue();

    /**
     * get used to display within dropdown
     * 
     * @return
     */
    String getName();

    /**
     * optional - used for custom stylings
     * 
     * @return
     */
    String getStyleName();

    void setMarkedOut(boolean markedOut);

    /**
     * used to mark options that shouldn't exists normally after a type update
     * 
     * @return
     */
    boolean isMarkedOut();

    void setDocumentCount(Long count);

    /**
     * optional - will get displayed after name
     * 
     * @return
     */
    Long getDocumentCount();

    @Override
    public abstract boolean equals(Object other);

    @Override
    public abstract int hashCode();
}
