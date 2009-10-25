package org.apache.log4j.extended.jdbc;

import java.util.UUID;

import org.apache.log4j.Layout;
import org.apache.log4j.spi.LoggingEvent;

public class UUIDLayout extends Layout {
	
	public UUIDLayout() {
	}

	@Override
	public String format(LoggingEvent event) {
		return UUID.randomUUID().toString();
	}

    @Override
    public void activateOptions() {
    }

	
    @Override
    public boolean ignoresThrowable() {
        return true;
    }

}
