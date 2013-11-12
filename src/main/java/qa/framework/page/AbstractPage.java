package qa.framework.page;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import qa.framework.SeUtil;
import qa.framework.SeHelper;

public abstract class AbstractPage implements LoadablePage {

	protected Logger pagelog;
	protected SeHelper window;
	protected SeUtil util;
	
	public AbstractPage() {
		pagelog = LoggerFactory.getLogger( this.getClass() );
		window = new SeHelper();
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
