package org.apache.log4j.extended.jdbc;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.log4j.Layout;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.ThrowableInformation;

/**
 * @author Simon Galperin
 *
 */
public class ThrowableLayout extends Layout implements ColumnDefinitionAware {
    private int maxChars = 8000;
    private boolean nullable = true;

    
    @Override
    public void activateOptions() {
    }

    @Override
    public String format(LoggingEvent event) {
        return getThrowableRepresentationFromLoggingEvent(event);
    }

    /**
     * Extracts Stack trace of Throwable contained in LogginEvent, if there is
     * any
     * 
     * @param event
     *            logging event
     * @return stack trace of throwable
     */
    public String getThrowableRepresentationFromLoggingEvent(LoggingEvent event) {
        String result = null;
        
        // extract throwable information from loggingEvent if available
        ThrowableInformation throwableinfo = event.getThrowableInformation();

        if (throwableinfo != null) {
            StringWriter writer = new StringWriter();
            throwableinfo.getThrowable().printStackTrace(new PrintWriter(writer));
            
            result = writer.toString();

            if (maxChars != -1 && result.length() > maxChars) {
                result = result.substring(0, maxChars - 1);
            }
        }

        if (!nullable && result == null) {
        	result = "";
        }
        
        return result;
    }

	/* (non-Javadoc)
	 * @see org.apache.log4j.extended.jdbc.ColumnDefinitionAware#setDbColumn(org.apache.log4j.extended.jdbc.DbColumn)
	 */
	public void setDbColumn(DbColumn column) {
		this.maxChars = column.getMaxLength();
		this.nullable = column.isNullable();
	}
    
    @Override
    public boolean ignoresThrowable() {
        return false;
    }

}
