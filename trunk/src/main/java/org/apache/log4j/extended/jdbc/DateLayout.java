package org.apache.log4j.extended.jdbc;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;

import org.apache.log4j.spi.LoggingEvent;

public class DateLayout extends ObjectLayout implements ColumnDefinitionAware {
	private int sqlType = Types.DATE;
	private LimitedPatternLayout layout = new LimitedPatternLayout("%d");
	
	public DateLayout() {
	}
	
	public DateLayout(DbColumn column) {
		setDbColumn(column);
	}
		
    @Override
    public Object formatObject(LoggingEvent event) {
    	if (sqlType == Types.TIMESTAMP) {
    		return new Timestamp(event.timeStamp);
    	} else if (sqlType == Types.DATE) {
            return new Date(event.timeStamp);
    	} else if (sqlType == Types.TIME) {
            return new Time(event.timeStamp);
        } else if (sqlType == Types.INTEGER || sqlType == Types.NUMERIC) {
        	return event.timeStamp;
        } else {
        	return layout.format(event);
    	}
    }

    @Override
    public void activateOptions() {
    }

	/* (non-Javadoc)
	 * @see org.apache.log4j.extended.jdbc.ColumnDefinitionAware#setDbColumn(org.apache.log4j.extended.jdbc.DbColumn)
	 */
	public void setDbColumn(DbColumn column) {
		this.sqlType = column.getDataType();
		this.layout.setDbColumn(column);
	}
	
    @Override
    public boolean ignoresThrowable() {
        return true;
    }

}
