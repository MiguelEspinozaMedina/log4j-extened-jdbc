package org.apache.log4j.extended.jdbc;

import java.sql.Time;

import org.apache.log4j.spi.LoggingEvent;

public class TimeLayout extends ObjectLayout {

    @Override
    public Object formatObject(LoggingEvent event) {
        return new Time(event.timeStamp);
    }

    @Override
    public void activateOptions() {
        
    }

    @Override
    public boolean ignoresThrowable() {
        return false;
    }

}
