package org.apache.log4j.extended.jdbc;

import org.apache.log4j.spi.LoggingEvent;

public class PriorityIntLayout extends ObjectLayout {

    @Override
    public Object formatObject(LoggingEvent event) {
		return event.getLevel().toInt();
    }

    @Override
    public void activateOptions() {
        
    }

    @Override
    public boolean ignoresThrowable() {
        return false;
    }

}
