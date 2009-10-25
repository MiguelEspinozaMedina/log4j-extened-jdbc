package org.apache.log4j.extended.jdbc;

import org.apache.log4j.PatternLayout;
import org.apache.log4j.spi.LoggingEvent;

/**
 * @author Simon Galperin
 */
public class LimitedPatternLayout extends PatternLayout implements ColumnDefinitionAware {
    private int maxChars = -1;
    private boolean nullable = true;
    
    public LimitedPatternLayout() {
		super();
	}

	public LimitedPatternLayout(String pattern) {
		super(pattern);
	}

	@Override
	public String format(LoggingEvent event) {
		String text = super.format(event);
		
		if (nullable && text.isEmpty()) {
			return null;
		} else if (maxChars > -1 && text.length() > maxChars) {
			return text.substring(0, maxChars);
		} else {
			return text;
		}
	}

	/* (non-Javadoc)
	 * @see org.apache.log4j.extended.jdbc.ColumnDefinitionAware#setDbColumn(org.apache.log4j.extended.jdbc.DbColumn)
	 */
	@Override
	public void setDbColumn(DbColumn column) {
		this.maxChars = column.getMaxLength();
		this.nullable = column.isNullable();
	}
}
