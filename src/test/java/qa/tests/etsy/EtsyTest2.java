package qa.tests.etsy;

import java.io.File;

import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.saucelabs.common.SauceOnDemandAuthentication;

import qa.framework.SeTest;
import qa.framework.ThreadedWebDriver;
import qa.framework.page.etsy.EtsySearchPage;

public class EtsyTest2 extends SeTest {

	int gridPort;
	String hubIP, browser;

	EtsyTest2() {
		super();
		testlog.info("Loaded EtsyTest2 class.");
	}

	@BeforeTest
	public void setUpTest() {	
		if ( ThreadedWebDriver.access() == null ) {
			this.initializeSauceBrowser();
		}
		testlog.info("Finished setUp EtsyTest2");
	}

	/*  Will implement this as soon as I get the framework working
	 * 
	@DataProvider(name = "Etsy2")
	public static List<String[]> loadTestsFromFile2() {
		File tFile = new File( System.getProperty("user.dir") + File.separator +  "target" +
				File.separator + "test-classes" + File.separator + "testdata2.csv" );
        if ( tFile.exists() ) {
            System.out.println( "The file '" + tFile.getAbsolutePath() + "' exists." );
        } else {
        	System.out.println( "Problem loading File resource: " + tFile.getAbsolutePath() );
        } 
		List<String[]> rows = null;
		if ( tFile.exists() ) {
			CSVReader reader = null;
			try {
				reader = new CSVReader( new FileReader( tFile ), ',' );
				rows = reader.readAll();				
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}	
		}
		System.out.println( rows.toString() );
		return rows;
	} */ 

	@DataProvider(name = "dummy2")
	public String[][] createData1() {
		return new String[][] {
				{ "test1", "ring", "knuckle" },
				{ "test2", "shirt", "cat" }
		};
	}


	@Test(dataProvider = "dummy2", threadPoolSize = 1, invocationCount = 1,  timeOut = 60000)
	public void test2( String tName, String sString, String dMatch ) {
		testlog.info("{} being run...", tName );
		ThreadedWebDriver.access().get( System.getProperty("testURL") );
		EtsySearchPage gs = new EtsySearchPage();
		gs.setSearchString( sString );
		gs.selectInEtsyDropdown( dMatch );  
		gs.clickSearchButton();
		util.sleep(2);
		gs.clickEtsyLogo(); // click Etsy logo
		testlog.info("Page object test '{}' is done.", tName );
	}

	@AfterTest
	public void cleanUpTest() {		
		ThreadedWebDriver.access().get("about:about");
		util.sleep(2);
		helper.closeAllBrowserWindows();
		testlog.info("Finished cleanUp EtsyTest2");
	}

	@Override
	public String getSessionId() {
        return ThreadedWebDriver.access().getSessionId().toString();
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

}
