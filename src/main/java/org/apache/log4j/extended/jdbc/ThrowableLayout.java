package org.apache.log4j.extended.jdbc;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.log4j.Layout;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.ThrowableInformation;

public class ThrowableLayout extends Layout {
    private int throwableMaxChars = 8000;
    
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

            if (throwableMaxChars != -1 && result.length() > throwableMaxChars) {
                result = result.substring(0, throwableMaxChars - 1);
            }
            
//            StringBuilder throwableStringBuffer = new StringBuilder();
//
//            String[] lines = throwableinfo.getThrowableStrRep();
//            for (int index = 0; index < lines.length; index++) {
//                throwableStringBuffer.append(lines[index]).append("\r\n");
//            }
            
//            result = throwableStringBuffer.toString();
        }

        return result;
    }
    
    /**
     * @param throwableMaxChars the throwableMaxChars to set
     */
    public void setThrowableMaxChars(int throwableMaxChars) {
        this.throwableMaxChars = throwableMaxChars;
    }

    @Override
    public boolean ignoresThrowable() {
        return false;
    }

}
