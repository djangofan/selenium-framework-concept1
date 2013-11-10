package qa.framework.page;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import qa.framework.SeUtil;
import qa.framework.WindowHelper;

public abstract class AbstractPage implements LoadablePage {

	protected Logger pagelog;
	protected WindowHelper window;
	protected SeUtil util;
	
	public AbstractPage() {
		pagelog = LoggerFactory.getLogger( this.getClass() );
		window = new WindowHelper();
		util = new SeUtil();
	}
	
    /* Default implementaion of get() */
	protected AbstractPage get() {
      try {
        isLoaded();
        return this;
      } catch (Error e) {
        load();
      }
      isLoaded();
      return this;
    }
    
	public  void isLoaded() {
    	pagelog.info( "Calling isLoaded()..." );
    }
    
	public  void load() {
		pagelog.info( "Calling load()..." );
    }	

}
