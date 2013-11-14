package test;

import org.openqa.selenium.Platform;
import org.openqa.selenium.remote.DesiredCapabilities;

/*
Test: DesiredCapabilities.firefox().getBrowserName() = "firefox"
Test: DesiredCapabilities.chrome().getBrowserName() = "chrome"
Test: DesiredCapabilities.internetExplorer().getBrowserName() = "internet explorer"
Test: DesiredCapabilities.safari().getBrowserName() = "safari"
Test: DesiredCapabilities.opera().getBrowserName() = "opera"

*/

public class Test {

	public static void main( String[] args) {
		DesiredCapabilities cap = DesiredCapabilities.firefox();

		System.out.println("Test: " + cap.getBrowserName() );
		
		cap = DesiredCapabilities.chrome();
		
		System.out.println("Test: " + cap.getBrowserName() );
		
		cap = DesiredCapabilities.internetExplorer();
		
		System.out.println("Test: " + cap.getBrowserName() );
		
        cap = DesiredCapabilities.safari();
		
		System.out.println("Test: " + cap.getBrowserName() );
		
		 cap = DesiredCapabilities.opera();
			
			System.out.println("Test: " + cap.getBrowserName() );
		
			for (Platform c : Platform.values())
			    System.out.println(c);
			
	}


}
