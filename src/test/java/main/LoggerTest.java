/**
 * 
 */
package main;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * @author sgalperin
 *
 */
public class LoggerTest extends TestCase {
    private static final Log log = LogFactory.getLog(LoggerTest.class);
    private static final Log test = LogFactory.getLog("testLogger");
    
    /**
     * @param args
     */
    public void testLog() {        
        log.debug("Test");
        log.info("Test 'test' hello");
        log.warn("Test", new Exception("Test exception"));
        log.error("Test", new Exception("Test exception"));
    
        test.debug("Test");
        test.info("Test");
        test.warn("Test", new Exception("Test exception"));
        test.error("Test", new Exception("Test exception"));
        
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
