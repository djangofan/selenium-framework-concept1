package qa.framework;

import java.net.MalformedURLException;
import java.net.URL;

import org.openqa.selenium.Architecture;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Platform;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.DriverCommand;
import org.openqa.selenium.remote.RemoteWebDriver;

import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import qa.framework.testng.SauceOnDemandAuthentication;

public class SauceDriver extends RemoteWebDriver implements TakesScreenshot {
	
	private static SauceOnDemandAuthentication soda = new SauceOnDemandAuthentication();
	private static SeProps props = new SeProps(); 
	private static final String DEFAULT_SAUCE_URL = "ondemand.saucelabs.com:80";

	public SauceDriver(Capabilities desiredCapabilities) {
		super( getSauceEndpoint(),
			munge(
				desiredCapabilities,
				getSeleniumVersion(),
				getDesiredBrowserVersion(),
				getDesiredOS() 
		    ) 
		);
		System.out.println( "Started new SauceDriver; see job at https://saucelabs.com/jobs/" + this.getSessionId() );
	}

	private static String getDesiredBrowserVersion() {
		return props.getProperty( "SAUCE_BROWSER_VERSION" );
	}

	private static Platform getDesiredOS() {
		return Platform.extractFromSysProperty( props.getProperty( "SAUCE_OS" ) );
	}

	private static String getSeleniumVersion() {
		return props.getProperty( "SAUCE_SELENIUM_VERSION" );
	}

	private static URL getSauceEndpoint() {
		String sauceUsername = soda.getUsername();
		String sauceKey = soda.getAccessKey();
		String sauceUrl = props.getProperty( "SAUCE_URL" );
		if ( sauceUrl == null ) {
			sauceUrl = DEFAULT_SAUCE_URL;
		}
		try {
			return new URL( String.format("http://%s:%s@%s/wd/hub", sauceUsername, sauceKey, sauceUrl) );
		} catch ( MalformedURLException e ) {
			Throwables.propagate(e);
		}
		throw new IllegalStateException("Should have returned or thrown");
	}

	private static Capabilities munge(Capabilities desiredCapabilities, String seleniumVersion, String browserVersion, Platform platform) {
		DesiredCapabilities mungedCapabilities = new DesiredCapabilities(desiredCapabilities);
		mungedCapabilities.setCapability("selenium-version", seleniumVersion);
		mungedCapabilities.setCapability("idle-timeout", 180);
		mungedCapabilities.setCapability("disable-popup-handler", true);
		mungedCapabilities.setCapability("public", "public");
		mungedCapabilities.setCapability("record-video", shouldRecordVideo());
		mungedCapabilities.setCapability("build", props.getProperty( "SAUCE_BUILD_NUMBER" ) );

		String nativeEvents = props.getProperty( "NATIVE_EVENTS" );
		if (nativeEvents != null) {
			String[] tags = {nativeEvents};
			mungedCapabilities.setCapability("tags", tags);
		}
		mungedCapabilities.setCapability("prevent-requeue", false);

		if (!Strings.isNullOrEmpty(browserVersion)) {
			mungedCapabilities.setVersion(browserVersion);
		}
		mungedCapabilities.setPlatform(platform);

		String jobName = props.getProperty( "SAUCE_JOB_NAME" );
		if ( jobName != null ) {
			mungedCapabilities.setCapability("name", jobName);
		}

		if (DesiredCapabilities.internetExplorer().getBrowserName().equals(desiredCapabilities.getBrowserName())) {
			String ieDriverVersion = props.getProperty( "SAUCE_IE_DRIVER_VERSION" );
			if (ieDriverVersion != null) {
				mungedCapabilities.setCapability("iedriver-version", ieDriverVersion);
			}
			mungedCapabilities.setCapability(InternetExplorerDriver.REQUIRE_WINDOW_FOCUS, true);
		}

		if ( DesiredCapabilities.chrome().getBrowserName().equals( desiredCapabilities.getBrowserName()  ) ) {
			String chromeDriverVersion = props.getProperty( "SAUCE_CHROME_DRIVER_VERSION" );
			if (chromeDriverVersion == null) {
				chromeDriverVersion = "2.2";
			}
			System.out.println("Setting chromedriver-version capability to " + chromeDriverVersion);
			mungedCapabilities.setCapability("chromedriver-version", chromeDriverVersion);
		}

		String requireFocus = props.getProperty( "REQUIRE_FOCUS" );
		if (requireFocus != null) {
			mungedCapabilities.setCapability( InternetExplorerDriver.REQUIRE_WINDOW_FOCUS,
					Boolean.parseBoolean(requireFocus) );
		}

		return mungedCapabilities;
	}

	public static boolean shouldRecordVideo() {
		return ! Boolean.parseBoolean( props.getProperty( "SAUCE_DISABLE_VIDEO" ) );
	}

	public <X> X getScreenshotAs(OutputType<X> target) {
		String base64 = (String) execute( DriverCommand.SCREENSHOT ).getValue();
		return target.convertFromBase64Png(base64);
	}
}
