package qa.framework;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Wait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.openqa.selenium.support.ui.FluentWait;

/*
 * Defines common functionality for handling Selenium windows
 */
public class WindowHelper {
	
	final int DEFAULT_IMPLICIT_WAIT = 30;
	final int DEFAULT_EXPLICIT_WAIT = 10;
	String mainHandle = "";
	String mainWindowTitle = "";
	Set<String> handleCache = new HashSet<String>();
	Logger helperlog;
	
	public WindowHelper() {
		helperlog = LoggerFactory.getLogger( "WindowHelper" );
	}
	
	public WebElement getElementByLocator( By locator ) {
		Wait<WebDriver> wait = new FluentWait<WebDriver>( ThreadedWebDriver.access() )
			    .withTimeout(30, TimeUnit.SECONDS)
			    .pollingEvery(5, TimeUnit.SECONDS)
			    .ignoring( NoSuchElementException.class, StaleElementReferenceException.class );
		WebElement we = wait.until( ExpectedConditions.presenceOfElementLocated( locator ) );
		return we;
	}

	public void closeAllBrowserWindows() {
		Set<String> handles = ThreadedWebDriver.access().getWindowHandles();
		if ( handles.size() > 1 ) {
			helperlog.info("Closing " + handles.size() + " window(s).");
			for ( String windowId : handles ) {
				helperlog.info("-- Closing window handle: " + windowId );
				ThreadedWebDriver.access().switchTo().window( windowId ).close();
			}
		} else if ( handles.size()==1 ) {
			helperlog.info("Closing last open window.");
		} else {
			helperlog.info("There were no window handles to close.");
		}
		ThreadedWebDriver.access().quit(); // this quit is critical, otherwise last window will hang open
		ThreadedWebDriver.remove();
	}

	/**
	 * Loops to determine if WebDriver.getWindowHandles() returns any
	 *  additional windows that the allHandles cache does not currently
	 *  contain. If new windows are found, switch to latest window and
	 *  update allHandles cache.
	 */
	public String handleNewWindow() {
		String newHandle = "";
		printHandles();
		Set<String> updatedHandles = ThreadedWebDriver.access().getWindowHandles();
		if ( updatedHandles.size() < handleCache.size() ) {
			mainHandle = "";
			helperlog.info("Illegal state: actually, I saw a window close.");
			throw new IllegalStateException("This method handleNewWindow is not appropriate\n" +
					"in this case.  You are probably looking for the\n"+
					"use of the updateHandleCache method.");
		} else if ( updatedHandles.size() == handleCache.size() ) {
			helperlog.info("handleNewWindow() will do nothing because there are no new window handles.");
		} else {
			if ( !updatedHandles.isEmpty() ) {
				for ( String windowId : updatedHandles ) {
					if ( !windowId.equals( mainHandle ) ) { // for all windows except main window
						if ( !handleCache.contains( windowId) ) { // for child windows not in allHandles cache
							newHandle = windowId; // set value of newly found window handle                                                
							helperlog.info("-- Open window handle: " + newHandle + " (new window)" );
						}
					}
				}
				if ( !newHandle.equals("") ) { // outside loop so it catches latest window handle if there are multiple
					helperlog.info("Switch to new window.");
					ThreadedWebDriver.access().switchTo().window( newHandle ); // switch to new window handle
				}
			} else {
				mainHandle = "";
				throw new IllegalStateException("No browser window handles are open.");
			}
		}
		handleCache = updatedHandles; // updates remembered set of open windows
		return newHandle;
	}

	/*void positionMainHandle() {
		handleCache = ThreadWebDriver.get().getWindowHandles();
		if ( handleCache.size() == 0 ) {
			mainHandle = "";
			throw new IllegalStateException("No browser window handles are open.\n" +
					"Browser is uninitialized.");
		} else if ( handleCache.size() > 1 ) {
			mainHandle = "";
			throw new IllegalStateException("More than one browser window handle is open.\n" +
					"Please close all browsers and restart test.");
		} else {
			mainHandle = ThreadWebDriver.get().switchTo().defaultContent().getWindowHandle();
			mainWindowTitle = ThreadWebDriver.get().switchTo().defaultContent().getTitle();
			int fromLeft = Integer.parseInt( System.getProperty("windowXPosition") );
			int fromTop = Integer.parseInt( System.getProperty("windowYPosition") );
			int width = Integer.parseInt( System.getProperty("windowWidth") );
			int height = Integer.parseInt( System.getProperty("windowHeight") );
			setWindowPosition( mainHandle, width, height, fromLeft + getTestXOffset(), fromTop );
		}
	}*/

	public void printHandles() {
		helperlog.info( "Open windows:" );
		for ( String windowId : handleCache ) {
			helperlog.info( "-- Open window handle: " + windowId );
			if ( windowId.equals( mainHandle ) ) {
				helperlog.info(" (main handle)");
			}
		}
	}
	
	/*public void setWindowPosition(String handle, int width, int height, int fleft, int ftop) {
		ThreadWebDriver.get().switchTo().window( handle ).manage().window().setPosition( new Point(fleft, ftop) );
		ThreadWebDriver.get().switchTo().window( handle ).manage().window().setSize( new Dimension( width, height) );
	}*/

	void updateHandleCache() {
		helperlog.info("Updating cache of window handles...");
		printHandles();
		Set<String> updatedHandles = ThreadedWebDriver.access().getWindowHandles();
		if ( !updatedHandles.isEmpty() ) {
			if ( updatedHandles.size() > handleCache.size() ) {
				helperlog.info( "Window handle number increased to: " + updatedHandles.size() );
			} else if ( updatedHandles.size() == handleCache.size() ) {
				helperlog.info( "Window handle number is unchanged from: " + updatedHandles.size() );
			} else {
				helperlog.info( "Window handle number decreased to: " + updatedHandles.size() );
			}                        
		} else {
			mainHandle = null;
			throw new IllegalStateException("No browser window handles are open.");
		}
		handleCache = updatedHandles; // updates remembered set of open windows
	}

}
