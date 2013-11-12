package qa.framework;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;

import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SeDriver {
	
	protected static Logger selog = LoggerFactory.getLogger( "SeDriver" );
	public SauceDriver seDriver;
	protected DesiredCapabilities seAbilities;	
	
	public SeDriver() {
	    selog.info("Created SeDriver object.  Ready for browser initialization.");
	}

	public SauceDriver getDriver() {
		return seDriver;
	}
	
	public void initializeBrowser( BrowserType type ) {
		if ( seDriver == null ) {
		    seDriver = new SauceDriver( generateDesiredCapabilities( type ) );
		} else {
			selog.info("Browser already initialized.");
		}
	}
	
	//TODO Intention is to dynamically load these capability properties from the DataProvider
	private DesiredCapabilities generateDesiredCapabilities( BrowserType capabilityType ) {	
		switch ( capabilityType.getBrowser() ) {
		case "ie":
			seAbilities = DesiredCapabilities.internetExplorer();
			seAbilities.setCapability( CapabilityType.ForSeleniumServer.ENSURING_CLEAN_SESSION, true );
			seAbilities.setCapability( InternetExplorerDriver.ENABLE_PERSISTENT_HOVERING, true );
			break;
		case "chrome":
			seAbilities = DesiredCapabilities.chrome();
			seAbilities.setCapability( "chrome.switches", Arrays.asList("--no-default-browser-check") );
			HashMap<String, String> chromePreferences = new HashMap<String, String>();
			chromePreferences.put( "profile.password_manager_enabled", "false" );
			seAbilities.setCapability( "chrome.prefs", chromePreferences );
			break;
		case "firefox":
			seAbilities = DesiredCapabilities.firefox();
	        break;
		default:
			seAbilities = DesiredCapabilities.firefox();
			break;
		}
		return seAbilities;
	}	
	
	public void quitBrowser() {
		Set<String> handles = seDriver.getWindowHandles();
		if ( handles.size() > 1 ) {
			selog.info("Closing " + handles.size() + " window(s).");
			for ( String windowId : handles ) {
				selog.info("-- Closing window handle: " + windowId );
				seDriver.switchTo().window( windowId ).close();
			}
		} else if ( handles.size()==1 ) {
			selog.info("Closing last open window.");
		} else {
			selog.info("There were no window handles to close.");
		}
		seDriver.quit(); // this quit is critical, otherwise last window will hang open
	}

}
