package org.apache.log4j.extended.jdbc;

/**
 * @author Simon Galperin
 */
public interface ColumnDefinitionAware {
    /**
     * @param nullable
     */
    public void setDbColumn(DbColumn column);
}