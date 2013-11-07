package qa.framework;

import java.util.List;

import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;

/*
 * @DataProvider(name = "DP1")
public Object[][] createData() {
  Object[][] retObjArr = {
  {"Link1","link_to_page"},
  {"Link2","link_to_page"},
  return retObjArr;
} 

@Test (dataProvider = "DP1")
public void isActive(String name, String link){
  this.context.setAttribute("name", name);
  browser.click(link);
  Assert.assertTrue(browser.isLinkActive(link));
}
  */
public class MyListener extends TestListenerAdapter {

	@Override
	public void onTestSuccess(ITestResult tr) {
		log("+", tr);
	}

	// and similar
	private void log(String string, ITestResult tr) {
		List<ITestContext> k = this.getTestContexts();
		String testName = tr.getTestClass().getName();
		for (ITestContext i : k)
		{
			if (i.getAttribute("name") != null)
				System.out.println(testName + "." + i.getAttribute("name"));
		}
	}

}