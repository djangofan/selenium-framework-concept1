package qa.framework;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.Platform;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.DriverCommand;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import qa.framework.testng.SauceOnDemandAuthentication;

import com.google.common.base.Throwables;

public class SeDriver extends RemoteWebDriver implements TakesScreenshot{
	
	public static Logger driverlog = LoggerFactory.getLogger( "SeDriver" );
	public static SeProps props = new SeProps();
	public static SauceOnDemandAuthentication soda = new SauceOnDemandAuthentication( props.getProperty("KEY_FILE") );
	public static final String DEFAULT_SAUCE_URL = "ondemand.saucelabs.com:80";	
	public static DesiredCapabilities myCapabilities; //TODO not really necessary, or is it
	
	public SeDriver() {
		super( getSauceEndpoint(), generateCapabilities() );
	    driverlog.info( "Starting new Sauce RemoteWebDriver; see job at https://saucelabs.com/jobs/" + this.getSessionId() );
	}

	//TODO Intention is to dynamically load these capability properties from the DataProvider
	private static DesiredCapabilities generateCapabilities() {	
		DesiredCapabilities seAbilities;
		switch ( props.getProperty("BROWSER") ) {
		case "internet explorer":
			seAbilities = DesiredCapabilities.internetExplorer();
			seAbilities.setVersion( props.getProperty( "SAUCE_IE_VERSION" ) );
			seAbilities.setCapability( CapabilityType.ForSeleniumServer.ENSURING_CLEAN_SESSION, true );
			seAbilities.setCapability( InternetExplorerDriver.ENABLE_PERSISTENT_HOVERING, true );
			String requireFocus = props.getProperty( "REQUIRE_FOCUS" );
			if ( requireFocus != null ) {
				seAbilities.setCapability( InternetExplorerDriver.REQUIRE_WINDOW_FOCUS, Boolean.parseBoolean( requireFocus ) );
			}
			break;
		case "chrome":
			seAbilities = DesiredCapabilities.chrome();
			seAbilities.setCapability( "chrome.switches", Arrays.asList("--no-default-browser-check") );
			HashMap<String, String> chromePreferences = new HashMap<String, String>();
			chromePreferences.put( "profile.password_manager_enabled", "false" );
			seAbilities.setCapability( "chrome.prefs", chromePreferences );
			seAbilities.setVersion( props.getProperty( "SAUCE_CHROME_VERSION" ) );
			//ChromeOptions options = new ChromeOptions();
			//options.addArguments("--start-maximized");
			break;
		case "firefox":
			seAbilities = DesiredCapabilities.firefox();
			seAbilities.setVersion( props.getProperty( "SAUCE_FIREFOX_VERSION" ) );
	        break;
		case "safari":
			seAbilities = DesiredCapabilities.safari();
			seAbilities.setVersion( props.getProperty( "SAUCE_SAFARI_VERSION" ) );
	        break;
		case "opera":
			seAbilities = DesiredCapabilities.opera();
			seAbilities.setVersion( props.getProperty( "SAUCE_OPERA_VERSION" ) );
	        break;
		default:
			//TODO Perhaps start a local driver?
			seAbilities = DesiredCapabilities.firefox();
			break;
		}
		
		/* Possbile platforms: WINDOWS, XP, VISTA, WIN8, MAC, UNIX, LINUX, ANDROID, ANY */
		seAbilities.setPlatform( Platform.valueOf( props.getProperty( "PLATFORM" ) ) );
		
		if ( getDesiredSeleniumVersion() != null ) seAbilities.setCapability( "selenium-version", getDesiredSeleniumVersion() );
		if ( getIdleTimeout() != null ) seAbilities.setCapability( "idle-timeout", getIdleTimeout() );

		if ( getDisableVideo() ) seAbilities.setCapability( "record-video", false );
		if ( getBuildNumber() != null ) seAbilities.setCapability( "build", getBuildNumber() );

		String nativeEvents = props.getProperty( "NATIVE_EVENTS" );
		if ( nativeEvents != null ) {
			String[] tags = {nativeEvents};
			seAbilities.setCapability("tags", tags);
		}
		seAbilities.setCapability("prevent-requeue", false);	
		seAbilities.setCapability( "disable-popup-handler", true );
		seAbilities.setCapability( "public", "public" );		

		String jobName = props.getProperty( "SAUCE_JOB_NAME" );
		if ( jobName != null ) seAbilities.setCapability( "name", jobName );
		driverlog.info("Finished preparing Capabilities for new browser: " + seAbilities.getBrowserName() );
		myCapabilities = seAbilities;
		return seAbilities;
	}	

	public static String getBuildNumber() {
        String build = props.getProperty( "SAUCE_BUILD_NUMBER" );
		if ( build != null ) {
			return build;
		} else {
			return "0";
		}
	}	
	
	public static boolean getDisableVideo() {
		return Boolean.parseBoolean( props.getProperty( "SAUCE_DISABLE_VIDEO" ) );
	}
	
	public static Integer getIdleTimeout() {
		Integer myInt;
		try {
			myInt = Integer.parseInt( props.getProperty( "IDLE_TIMEOUT" ) );
			return myInt;
		} catch ( NumberFormatException e ) {
			driverlog.info("Number format exception with IDLE_TIMEOUT property.  Returning default value: 180");
			driverlog.info( e.getMessage() );
			return 180;
		}
	}

	public static URL getSauceEndpoint() {
		String sauceUsername = soda.getUsername();
		String sauceKey = soda.getAccessKey();
		String sauceUrl = props.getProperty( "SAUCE_URL" );
		if ( sauceUrl == null ) sauceUrl = DEFAULT_SAUCE_URL;
		try {
			return new URL( String.format("http://%s:%s@%s/wd/hub", sauceUsername, sauceKey, sauceUrl) );
		} catch ( MalformedURLException e ) {
			Throwables.propagate(e);
		}
		throw new IllegalStateException("Should have returned or thrown.");
	}
	
	public static String getDesiredSeleniumVersion() {
		return props.getProperty( "SAUCE_SELENIUM_VERSION" );
	}	

	public <X> X getScreenshotAs(OutputType<X> target) {
		String base64 = (String) execute( DriverCommand.SCREENSHOT ).getValue();
		return target.convertFromBase64Png(base64);
	}
	
	public DesiredCapabilities getCapabilities() {
		return myCapabilities; 
	}

}
