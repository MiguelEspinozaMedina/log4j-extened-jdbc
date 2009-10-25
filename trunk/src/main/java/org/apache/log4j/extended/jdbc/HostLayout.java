package org.apache.log4j.extended.jdbc;

import org.apache.log4j.Layout;
import org.apache.log4j.spi.LoggingEvent;

public class HostLayout extends Layout implements ColumnDefinitionAware {
    private String host;

    private int maxChars = -1;
    private boolean nullable = true;

    @Override
    public void activateOptions() {
        try {
            java.net.InetAddress localMachine = java.net.InetAddress.getLocalHost();
            host = localMachine.getHostName();
            
            if (maxChars > -1 && host.length() > maxChars) {
            	host = host.substring(0, maxChars);
            }
        }
        catch (java.net.UnknownHostException uhe) {
            host = nullable ? null : "[unknown]";
        }    
    }

    @Override
    public String format(LoggingEvent event) {
        if (host == null) {
            activateOptions();
        }
        return host;
    }

	/* (non-Javadoc)
	 * @see org.apache.log4j.extended.jdbc.ColumnDefinitionAware#setDbColumn(org.apache.log4j.extended.jdbc.DbColumn)
	 */
	@Override
	public void setDbColumn(DbColumn column) {
		this.maxChars = column.getMaxLength();
		this.nullable = column.isNullable();
	}
    
    @Override
    public boolean ignoresThrowable() {
        return true;
    }

}
