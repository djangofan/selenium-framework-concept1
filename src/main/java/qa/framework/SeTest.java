package qa.framework;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;

import qa.framework.testng.SauceOnDemandAuthentication;
import qa.framework.testng.SauceOnDemandSessionIdProvider;
import qa.framework.testng.SauceOnDemandAuthenticationProvider;
import qa.framework.testng.SauceOnDemandTestListener;

@Listeners({SauceOnDemandTestListener.class})
public abstract class SeTest implements SauceOnDemandSessionIdProvider, 
        SauceOnDemandAuthenticationProvider {

	protected static Logger testlog = LoggerFactory.getLogger( "SeTest" );
	public static SeProps props = SeDriver.props;
	public SeDriver driver;

	public SeTest() {
		super();		
		driver = new SeDriver();
	}

	@BeforeClass
	public static void setUpClass() {
		testlog.info("Calling SeTest.setUpClass...");
	}

	@AfterClass
	public void tearDownClass() {
		testlog.info("Calling SeTest.tearDownClass...");
		driver.quit();
	}

	@Override
	public SauceOnDemandAuthentication getAuthentication() {
		SauceOnDemandAuthentication soda = new SauceOnDemandAuthentication( props.getProperty("KEY_FILE") );
		if ( soda.isLoaded() ) {			
			return soda;
		} else {
			testlog.info("The SauceLabs authentication properties file, containing key and username, is not loaded.");
			return null;
		}
	}	

	@Override
	public String getSessionId() {
		return driver.getSessionId().toString();
	}

}
