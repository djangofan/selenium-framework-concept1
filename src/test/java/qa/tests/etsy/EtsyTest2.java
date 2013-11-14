package qa.tests.etsy;

import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import qa.framework.SeHelper;
import qa.framework.SeTest;
import qa.framework.SeUtil;
import qa.framework.page.etsy.EtsySearchPage;

public class EtsyTest2 extends SeTest {	
	
	SeHelper window;
	SeUtil util;

	EtsyTest2() {
		super();
		testlog.info("Loaded EtsyTest2 class.");
	}

	@BeforeTest
	public void setUpTest() {	
		testlog.info("EtsyTest2.setUpTest()...");
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
		window = new SeHelper( driver );
		util = new SeUtil();
		driver.get( System.getProperty("testURL") );
		EtsySearchPage gs = new EtsySearchPage( driver );
		gs.setSearchString( sString );
		gs.selectInEtsyDropdown( dMatch );  
		gs.clickSearchButton();
		util.sleep(2);
		gs.clickEtsyLogo(); // click Etsy logo
		testlog.info("Page object test '{}' is done.", tName );
	}

	@AfterTest
	public void cleanUpTest() {		
		driver.get("about:about");
		util.sleep(2);
		window.quitBrowser();
		testlog.info("Finished cleanUp EtsyTest2");
	}

}
