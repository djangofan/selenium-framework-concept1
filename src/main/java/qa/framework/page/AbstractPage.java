package qa.framework.page;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import qa.framework.SeUtil;
import qa.framework.ThreadedWebDriver;
import qa.framework.WindowHelper;

public abstract class AbstractPage implements LoadablePage {

	protected Logger pagelog;
	protected WindowHelper window;
	protected SeUtil util;
	
	public AbstractPage() {
		pagelog = LoggerFactory.getLogger( this.getClass() );
		window = new WindowHelper();
		util = new SeUtil();
	}
	
    /* Default implementaion of get() */
	protected AbstractPage get() {
      try {
        isLoaded();
        return this;
      } catch (Error e) {
        load();
      }
      isLoaded();
      return this;
    }
    
	public  void isLoaded() {
    	pagelog.info( "Calling isLoaded()..." );
    }
    
	public  void load() {
		pagelog.info( "Calling load()..." );
    }	
	
	public void clearAndSetValue(WebElement field, String text) {
		field.clear();
		field.sendKeys(Keys.chord(Keys.CONTROL, "a"), text);
	}

	public void clearAndType(WebElement field, String text) {
		field.submit();
		field.clear();
		field.sendKeys(text);
	}

	public WebElement getElementByLocator(final By locator) {
		Wait<WebDriver> wait = new FluentWait<WebDriver>( ThreadedWebDriver.access() )
				.withTimeout(30, TimeUnit.SECONDS)
				.pollingEvery(5, TimeUnit.SECONDS)
				.ignoring(NoSuchElementException.class,
						StaleElementReferenceException.class);
		WebElement we = wait.until(ExpectedConditions
				.presenceOfElementLocated(locator));
		return we;
	}

}
