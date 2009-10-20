package org.apache.log4j.extended.jdbc;

import java.sql.Timestamp;

import org.apache.log4j.spi.LoggingEvent;

public class TimestampLayout extends ObjectLayout {

    @Override
    public Object formatObject(LoggingEvent event) {
        return new Timestamp(event.timeStamp);
    }

    @Override
    public void activateOptions() {
        
    }

    @Override
    public boolean ignoresThrowable() {
        return false;
    }

}
