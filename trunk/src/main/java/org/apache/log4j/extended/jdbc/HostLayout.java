package org.apache.log4j.extended.jdbc;

import org.apache.log4j.Layout;
import org.apache.log4j.spi.LoggingEvent;

public class HostLayout extends Layout {
    private String host;
    
    @Override
    public void activateOptions() {
        try {
            java.net.InetAddress localMachine = java.net.InetAddress.getLocalHost();
            host = localMachine.getHostName();
        }
        catch (java.net.UnknownHostException uhe) {
            host = "[unknown]";
        }    
    }

    @Override
    public String format(LoggingEvent event) {
        if (host == null) {
            activateOptions();
        }
        return host;
    }

    @Override
    public boolean ignoresThrowable() {
        return true;
    }

}
