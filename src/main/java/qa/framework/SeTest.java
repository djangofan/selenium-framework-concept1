package qa.framework;

import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Listeners;

import qa.framework.testng.SauceOnDemandAuthentication;
import qa.framework.testng.SauceOnDemandSessionIdProvider;
import qa.framework.testng.SauceOnDemandAuthenticationProvider;
import qa.framework.testng.SauceOnDemandTestListener;

@Listeners({SauceOnDemandTestListener.class})
public abstract class SeTest extends SeDriver implements SauceOnDemandSessionIdProvider, 
        SauceOnDemandAuthenticationProvider {

	public SeUtil util;
	public SeProps props;
	public SeHelper window;

	public SeTest() {
		super();
		this.initializeBrowser( BrowserType.FIREFOX );		
		util = new SeUtil();
		props = new SeProps();
		window = new SeHelper();
	}

	@BeforeClass
	public static void setUpClass() {
		selog.info("Calling SeTest.setUpClass...");
	}

	@BeforeTest
	public void setUpTest() {
		selog.info("Calling SeTest.setUpTest...");
	}

	@AfterClass
	public void tearDownClass() {
		selog.info("Calling SeTest.tearDownClass...");
		getDriver().quit();
	}

	@AfterMethod
	public void cleanUpMethod() {
		selog.info("Calling SeTest.cleanUpMethod...");
		getDriver().manage().deleteAllCookies();
	}

	@Override
	public SauceOnDemandAuthentication getAuthentication() {
		SauceOnDemandAuthentication soda = new SauceOnDemandAuthentication();
		if ( soda.isLoaded() ) {			
			return soda;
		} else {
			selog.info("The SauceLabs authentication properties file, containing key and username, is not loaded.");
			return null;
		}
	}	

	@Override
	public String getSessionId() {
		return getDriver().getSessionId().toString();
	}

}
