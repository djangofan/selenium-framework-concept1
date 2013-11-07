package qa.framework;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SeUtil {
	
	public Logger log;	
	
	public SeUtil() {
		log = LoggerFactory.getLogger( "Util" );
		log.info("Invoked SeUtil helper class.");
	}
	
	public void sleep( int seconds ) {
		log.info("Sleep for " + seconds + " seconds.");
		try {
			Thread.sleep( seconds * 1000 );
		} catch ( InterruptedException ex ) {
			ex.printStackTrace();
		}
	}

}
