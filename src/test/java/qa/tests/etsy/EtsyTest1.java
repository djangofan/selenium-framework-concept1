package qa.tests.etsy;

import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import qa.framework.SeTest;
import qa.framework.page.etsy.EtsySearchPage;

public class EtsyTest1 extends SeTest {

	EtsyTest1() {
		super();
	}

	@BeforeTest
	public void setUpTest() {
		selog.info("EtsyTest1.setUpTest()...");
	}

	/*  Will implement this as soon as I get the framework working
	 * 
	@DataProvider(name = "Etsy1")
	public static List<String[]> loadTestsFromFile1() {
		File tFile = new File( System.getProperty("user.dir") + File.separator +  "target" +
				File.separator + "test-classes" + File.separator + "testdata1.csv" );
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

	@DataProvider(name = "dummy1")
	public String[][] createData1() {
		return new String[][] {
				{ "test4", "ring", "silver" },
				{ "test5", "shirt", "dog" }
		};
	}

	@Test(dataProvider = "dummy1", threadPoolSize = 1, invocationCount = 1,  timeOut = 60000)
	public void test1( String tName, String sString, String dMatch ) {
		selog.info("{} being run...", tName );
		seDriver.get( System.getProperty("testURL") );
		EtsySearchPage gs = new EtsySearchPage( seDriver );
		gs.setSearchString( sString );
		gs.selectInEtsyDropdown( dMatch );  
		gs.clickSearchButton();
		util.sleep(2);
		gs.clickEtsyLogo(); // click Etsy logo
		selog.info("Page object test '{}' is done.", tName );
	}

	@AfterTest
	public void cleanUpTest() {		
		seDriver.get("about:about");
		util.sleep(2);
		this.quitBrowser();
		selog.info("Finished cleanUp EtsyTest1");
	}

}
