package qa.framework.testng;

import qa.framework.SeDriver;
import qa.framework.SeProps;
import qa.framework.SeUtil;
import qa.framework.testng.SauceOnDemandAuthentication;
import qa.framework.testng.SauceOnDemandSessionIdProvider;

import com.saucelabs.saucerest.SauceREST;

import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;

import java.util.HashMap;
import java.util.Map;

/**
 * Test Listener that providers helper logic for TestNG tests.  Upon startup, the class
 * will store any SELENIUM_* environment variables (typically set by a Sauce OnDemand CI
 * plugin) as system parameters, so that they can be retrieved by tests as parameters.
 *   Code from: https://github.com/saucelabs/sauce-java
 * <p/>
 * TODO how to specify whether to download log/video?
 */
public class SauceOnDemandTestListener extends TestListenerAdapter {

    //private static final String SELENIUM_BROWSER = "SELENIUM_BROWSER";
    //private static final String SELENIUM_PLATFORM = "SELENIUM_PLATFORM";
    //private static final String SELENIUM_VERSION = "SELENIUM_VERSION";
	
	public static SeProps props = SeDriver.props;

    /**
     * The underlying {@link com.saucelabs.common.SauceOnDemandSessionIdProvider} instance which contains the Selenium session id.  This is typically
     * the unit test being executed.
     */
    private SauceOnDemandSessionIdProvider sessionIdProvider;

    /**
     * The instance of the Sauce OnDemand Java REST API client.
     */
    private SauceREST sauceREST;

    /**
     * Check to see if environment variables that define the Selenium browser to be used have been set (typically by
     * a Sauce OnDemand CI plugin).  If so, then populate the appropriate system parameter, so that tests can use
     * these values.
     *
     * @param testContext
     */
    @Override
    public void onStart(ITestContext testContext) {
        super.onStart(testContext);
        /*String browser = SeUtil.readPropertyOrEnv( SELENIUM_BROWSER, "" );
        if ( browser != null && !browser.equals("") ) {
            System.setProperty( "browser", browser );
        }
        String platform = SeUtil.readPropertyOrEnv( SELENIUM_PLATFORM, "" );
        if ( platform != null && !platform.equals("") ) {
            System.setProperty( "os", platform );
        } 
        String version = SeUtil.readPropertyOrEnv( SELENIUM_VERSION, "" );
        if ( version != null && !version.equals("") ) {
            System.setProperty( "version", version );
        }*/
    }

    /**
     * @param result
     */
    @Override
    public void onTestStart(ITestResult result) {
        super.onTestStart( result );
        if ( result.getInstance() instanceof SauceOnDemandSessionIdProvider ) {
            this.sessionIdProvider = (SauceOnDemandSessionIdProvider) result.getInstance();
            //log the session id to the system out
            if ( sessionIdProvider.getSessionId() != null ) {
                System.out.println(	String.format( "SauceOnDemandSessionID=%1$s job-name=%2$s", 
                		sessionIdProvider.getSessionId(), result.getMethod().getMethodName() ) );
            }
        }
        SauceOnDemandAuthentication sauceOnDemandAuthentication;
        if (result.getInstance() instanceof SauceOnDemandAuthenticationProvider) {
            //use the authentication information provided by the test class
            SauceOnDemandAuthenticationProvider provider = (SauceOnDemandAuthenticationProvider) result.getInstance();
            sauceOnDemandAuthentication = provider.getAuthentication();
        } else {
            //otherwise use the default authentication
            sauceOnDemandAuthentication = new SauceOnDemandAuthentication( props.getProperty("KEY_FILE") );
        }
        this.sauceREST = new SauceREST( sauceOnDemandAuthentication.getUsername(), sauceOnDemandAuthentication.getAccessKey() );
    }

    @Override
    public void onTestFailure(ITestResult tr) {
        super.onTestFailure(tr);
        markJobAsFailed();
    }

    private void markJobAsFailed() {
        if ( this.sauceREST != null && sessionIdProvider != null ) {
            String sessionId = sessionIdProvider.getSessionId();
            if (sessionId != null) {
                Map<String, Object> updates = new HashMap<String, Object>();
                updates.put("passed", false);
                SeUtil.addBuildNumberToUpdate(updates);
                sauceREST.updateJobInfo(sessionId, updates);
            }
        }
    }

    @Override
    public void onTestSuccess(ITestResult tr) {
        super.onTestSuccess(tr);
        markJobAsPassed();
    }

    private void markJobAsPassed() {
        if ( this.sauceREST != null && sessionIdProvider != null ) {
            String sessionId = sessionIdProvider.getSessionId();
            if ( sessionId != null ) {
                Map<String, Object> updates = new HashMap<String, Object>();
                updates.put("passed", true);
                SeUtil.addBuildNumberToUpdate(updates);
                sauceREST.updateJobInfo(sessionId, updates);
            }
        }
    }

}
