package org.apache.log4j.extended.jdbc;

import org.apache.log4j.Layout;
import org.apache.log4j.spi.LoggingEvent;

public abstract class ObjectLayout extends Layout {
    @Override
    public String format(LoggingEvent event) {
        return String.valueOf(formatObject(event));
    }

    public abstract Object formatObject(LoggingEvent event);
}
