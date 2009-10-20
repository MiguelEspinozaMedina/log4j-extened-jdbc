package org.apache.log4j.extended.jdbc;

import java.sql.Date;

import org.apache.log4j.spi.LoggingEvent;

public class DateLayout extends ObjectLayout {

    @Override
    public Object formatObject(LoggingEvent event) {
        return new Date(event.timeStamp);
    }

    @Override
    public void activateOptions() {
        
    }

    @Override
    public boolean ignoresThrowable() {
        return false;
    }

}
