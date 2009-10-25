/**
 * 
 */
package main;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.LogManager;


/**
 * @author sgalperin
 *
 */
public class LoggerTest extends TestCase {
    private static final Log log = LogFactory.getLog(LoggerTest.class);
    private static final Log test = LogFactory.getLog("testLogger");
    
    private Runnable logExecution = new Runnable() {
		public void run() {
	        log.debug("Test");
	        log.info("Test 'test' hello");
	        log.warn("Test", new Exception("Test exception"));
	        log.error("Test", new Exception("Test exception"));

	        test.debug("Test");
	        test.info("Test");
	        test.warn("Test", new Exception("Test exception"));
	        test.error("Test", new Exception("Test exception"));
		}
	};

	/**
     * @param args
     */
    public void testThreads() {   
    	
    	logExecution.run();
    	
    	int threadCount = 10;
    	
    	Thread[] thread = new Thread[threadCount];
    	for (int i = 0; i < threadCount; i++) {
    		thread[i] = new Thread(logExecution);
    		thread[i].start();
    	}
    	
    	for (int i = 0; i < threadCount; i++) {
    		try {
				thread[i].join(100000);
			} catch (InterruptedException e) {}
    	}
    	
    }
    
    @Override
    protected void tearDown() throws Exception {
        LogManager.getLoggerRepository().shutdown();
    }
}
