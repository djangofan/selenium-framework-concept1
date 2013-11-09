package qa.framework;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Listeners;

import qa.framework.testng.SauceOnDemandAuthentication;
import qa.framework.testng.SauceOnDemandSessionIdProvider;
import qa.framework.testng.SauceOnDemandAuthenticationProvider;
import qa.framework.testng.SauceOnDemandTestListener;

/*
 * Root class that all test classes must extend
 * Implements https://github.com/saucelabs/sauce-java
 * 
 * Expected method names:
 *   BeforeTest - setUpTest 
 *   AfterTest - cleanUpTest
 *   BeforeMethod - setUpMethod
 *   AfterMethod - cleanUpMethod
 *   BeforeClass - static setUpClass
 *   AfterClass - static tearDownClass
 *   BeforeSuite - static setUpSuite
 *   AfterSuite - static tearDownSuite
 */
@Listeners({SauceOnDemandTestListener.class})
public abstract class SeTest implements SauceOnDemandSessionIdProvider, SauceOnDemandAuthenticationProvider {

	// helpers
	protected static Logger testlog;
	protected SeUtil util;
	protected WindowHelper helper;
	protected BrowserType browserType;
	protected DesiredCapabilities abilities;
	
	public SeTest() {
		testlog = LoggerFactory.getLogger( this.getClass() );
		util = new SeUtil();
		helper = new WindowHelper();
	}
	
	@BeforeSuite
	public static void setUpSuite() {
		testlog.info("Calling SeTest.setUpSuite...");
		//TODO Load api key from  target/test-classes/api.key file
	}
	
	@BeforeTest
	public void setUpTest() {
		testlog.info("Calling SeTest.setUpTest...");
	}

	@AfterSuite
	public static void tearDownSuite() {
		testlog.info("Calling SeTest.tearDownSuite...");
		ThreadedWebDriver.access().quit();
		ThreadedWebDriver.remove();
	}

	@AfterMethod
	public void cleanUpMethod() {
		testlog.info("Calling SeTest.cleanUpMethod...");
		ThreadedWebDriver.access().manage().deleteAllCookies();
	}

	protected WebDriver getDriver() {
		return ThreadedWebDriver.access();
	}

	private DesiredCapabilities generateDesiredCapabilities( BrowserType capabilityType ) {	
		//TODO Intention is to dynamically load these capability properties from the DataProvider
		switch ( capabilityType.getBrowser() ) {
		case "ie":
			abilities = DesiredCapabilities.internetExplorer();
			abilities.setCapability(CapabilityType.ForSeleniumServer.ENSURING_CLEAN_SESSION, true);
			abilities.setCapability(InternetExplorerDriver.ENABLE_PERSISTENT_HOVERING, true);
			break;
		case "chrome":
			abilities = DesiredCapabilities.chrome();
			abilities.setCapability("chrome.switches", Arrays.asList("--no-default-browser-check"));
			HashMap<String, String> chromePreferences = new HashMap<String, String>();
			chromePreferences.put("profile.password_manager_enabled", "false");
			abilities.setCapability("chrome.prefs", chromePreferences);
			break;
		case "firefox":
			abilities = DesiredCapabilities.firefox();
	        break;
		default:
			abilities = DesiredCapabilities.firefox();
			break;
		}
		return abilities;
	}	

	public void initializeSauceBrowser() {			
       SauceDriver driver = new SauceDriver( generateDesiredCapabilities( BrowserType.FIREFOX ) );
       ThreadedWebDriver.sync( driver );        
	}
	
	@Override
	public SauceOnDemandAuthentication getAuthentication() {
		File props = new File(new File(System.getProperty("user.home")), ".sauce-ondemand");
		if ( props.exists() ) {
		    SauceOnDemandAuthentication soda = new SauceOnDemandAuthentication();
		    return soda;
		} else {
			testlog.info("The .sauce-ondemand properties file, containing key and username, is missing.");
			return null;
		}
	}	
	
	@Override
	public String getSessionId() {
		return ThreadedWebDriver.access().getSessionId().toString();
	}

}
