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

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import qa.framework.testng.SauceOnDemandAuthentication;

public class SauceDriver extends RemoteWebDriver implements TakesScreenshot {

	// These properties are all set from the Maven pom.xml as system properties
	private static final String SAUCE_JOB_NAME_ENV_NAME = "SAUCE_JOB_NAME";
	private static final String SELENIUM_VERSION_ENV_NAME = "SAUCE_SELENIUM_VERSION";
	private static final String SELENIUM_IEDRIVER_ENV_NAME = "SAUCE_IEDRIVER_VERSION";
	private static final String SELENIUM_CHROMEDRIVER_ENV_NAME = "SAUCE_CHROMEDRIVER_VERSION";
	private static final String DESIRED_BROWSER_VERSION_ENV_NAME = "SAUCE_BROWSER_VERSION";
	//private static final String SAUCE_APIKEY_ENV_NAME = "SAUCE_APIKEY";
	//private static final String SAUCE_USERNAME_ENV_NAME = "SAUCE_USERNAME";
	private static final String SAUCE_DISABLE_VIDEO_ENV_NAME = "SAUCE_DISABLE_VIDEO";
	private static final String SAUCE_BUILD_ENV_NAME = "SAUCE_BUILD_NUMBER";
	private static final String SAUCE_NATIVE_ENV_NAME = "NATIVE_EVENTS";
	private static final String SAUCE_REQUIRE_FOCUS_ENV_NAME = "REQUIRE_FOCUS";
	private static final String USE_SAUCE_ENV_NAME = "USE_SAUCE";
	private static final String DESIRED_OS_ENV_NAME = "SAUCE_OS";
	private static final String SAUCE_URL_ENV_NAME = "SAUCE_URL";
	private static final String DEFAULT_SAUCE_URL = "ondemand.saucelabs.com:80";
	
	private static SauceOnDemandAuthentication soda = new SauceOnDemandAuthentication();

	public SauceDriver(Capabilities desiredCapabilities) {
		super( getSauceEndpoint(),
			munge(
				desiredCapabilities,
				getSeleniumVersion(),
				getDesiredBrowserVersion(),
				getEffectivePlatform() 
		    ) 
		);
		System.out.println( "Started new SauceDriver; see job at https://saucelabs.com/jobs/" + this.getSessionId() );
	}

	private static String getDesiredBrowserVersion() {
		return System.getProperty(DESIRED_BROWSER_VERSION_ENV_NAME);
	}

	private static String getDesiredOS() {
		return getNonNullEnv(DESIRED_OS_ENV_NAME);
	}

	private static String getSeleniumVersion() {
		return getNonNullEnv(SELENIUM_VERSION_ENV_NAME);
	}

	private static String getNonNullEnv(String propertyName) {
		String value = System.getProperty(propertyName);
		Preconditions.checkNotNull(value);
		return value;
	}

	private static URL getSauceEndpoint() {
		//String sauceUsername = getNonNullEnv(SAUCE_USERNAME_ENV_NAME);
		String sauceUsername = soda.getUsername();
		//String sauceKey = getNonNullEnv( SAUCE_APIKEY_ENV_NAME );
		String sauceKey = soda.getAccessKey();
		String sauceUrl = System.getProperty(SAUCE_URL_ENV_NAME);
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
		mungedCapabilities.setCapability("build", System.getProperty(SAUCE_BUILD_ENV_NAME));

		String nativeEvents = System.getProperty(SAUCE_NATIVE_ENV_NAME);
		if (nativeEvents != null) {
			String[] tags = {nativeEvents};
			mungedCapabilities.setCapability("tags", tags);
		}
		mungedCapabilities.setCapability("prevent-requeue", false);

		if (!Strings.isNullOrEmpty(browserVersion)) {
			mungedCapabilities.setVersion(browserVersion);
		}
		mungedCapabilities.setPlatform(platform);

		String jobName = System.getProperty(SAUCE_JOB_NAME_ENV_NAME);
		if (jobName != null) {
			mungedCapabilities.setCapability("name", jobName);
		}

		if (DesiredCapabilities.internetExplorer().getBrowserName().equals(desiredCapabilities.getBrowserName())) {
			String ieDriverVersion = System.getProperty(SELENIUM_IEDRIVER_ENV_NAME);
			if (ieDriverVersion != null) {
				mungedCapabilities.setCapability("iedriver-version", ieDriverVersion);
			}
			mungedCapabilities.setCapability(InternetExplorerDriver.REQUIRE_WINDOW_FOCUS, true);
		}

		if (DesiredCapabilities.chrome().getBrowserName().equals(desiredCapabilities.getBrowserName())) {
			String chromeDriverVersion = System.getProperty(SELENIUM_CHROMEDRIVER_ENV_NAME);
			if (chromeDriverVersion == null) {
				chromeDriverVersion = "2.2";
			}
			System.out.println("Setting chromedriver-version capability to " + chromeDriverVersion);
			mungedCapabilities.setCapability("chromedriver-version", chromeDriverVersion);
		}

		String requireFocus = System.getProperty(SAUCE_REQUIRE_FOCUS_ENV_NAME);
		if (requireFocus != null) {
			mungedCapabilities.setCapability(InternetExplorerDriver.REQUIRE_WINDOW_FOCUS,
					Boolean.parseBoolean(requireFocus));
		}

		return mungedCapabilities;
	}

	public static boolean shouldUseSauce() {
		return System.getProperty(USE_SAUCE_ENV_NAME) != null;
	}

	public static boolean shouldRecordVideo() {
		return ! Boolean.parseBoolean(System.getProperty(SAUCE_DISABLE_VIDEO_ENV_NAME));
	}

	public static Platform getEffectivePlatform() {
		System.out.println("Effective architecture: " + Architecture.X64 );
		return Platform.extractFromSysProperty(getDesiredOS());
	}

	public <X> X getScreenshotAs(OutputType<X> target) {
		// Get the screenshot as base64.
		String base64 = (String) execute(DriverCommand.SCREENSHOT).getValue();
		// ... and convert it.
		return target.convertFromBase64Png(base64);
	}
}
