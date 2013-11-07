package qa.framework.page.etsy;

import java.util.List;

import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotVisibleException;
import org.openqa.selenium.Keys;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import qa.framework.ThreadedWebDriver;
import qa.framework.page.AbstractPage;

public class EtsySearchPage extends AbstractPage {

	public final String searchFieldName = "search_query";
	public final String searchButtonName = "search_submit";
	public final String suggestIons = "div.nav-search-text div#search-suggestions ul li";

	@FindBy(name = searchFieldName ) public WebElement searchField;
	@FindBy(name = searchButtonName ) public WebElement searchButton;

	public EtsySearchPage() {
        super();
		this.get();
		pagelog.info("EtsySearchPage constructor...");
	}

	@Override
	public void isLoaded() {    	
		pagelog.info("EtsySearchPage.isLoaded()...");
		boolean loaded = false;
		if ( !(searchField == null ) ) {
			try {
				if ( searchField.isDisplayed() ) {
					loaded = true;
				}
			} catch ( ElementNotVisibleException e ) {
				pagelog.info( "Element may not be displayed yet." );
			}
		}
		Assert.assertTrue( "Etsy search field is not yet displayed.", loaded );
	}

	@Override
	public void load() {
		pagelog.info("EtsySearchPage.load()...");
		PageFactory.initElements( ThreadedWebDriver.access(), this ); // initialize WebElements on page
		util.sleep(2);
	}

	public void clickSearchButton() {
		if ( searchButton == null ) {
			searchButton = getElementByLocator( By.id( searchButtonName ) );
		} else {
			try {
				searchButton.click();
			} catch ( ElementNotVisibleException e ) {
				pagelog.info( "Element not visible exception clicking search button.\n" + e.getMessage() );
				e.printStackTrace();
			} catch ( Exception e ) {
				pagelog.info( "Exception clicking search button.\n" + e.getMessage() );
				e.printStackTrace();
			}
		}
	}

	public void setSearchString( String sstr ) {
		clearAndType( searchField, sstr );
	}

	public void clickEtsyLogo() {
		pagelog.info("Click Etsy logo...");
		WebElement logo = null;
		By locator = By.cssSelector( "h1#etsy a" );
		logo = getElementByLocator( locator );
		logo.click();
		util.sleep(2);;
	}

	/**
	 * Method: selectInEtsyDropdown
	 * Selects element in dropdown using keydowns method (just for fun)
	 * as long as you typed a string first.  The thread sleeps and the 
	 * key arrow down are safe to comment out within the below block.
	 * @return	void
	 * @throws	StaleElementReferenceException
	 */
	public void selectInEtsyDropdown( String match ) {
		pagelog.info("Selecting \"" + match + "\" from Etsy dynamic dropdown.");
		List<WebElement> allSuggestions = ThreadedWebDriver.access().findElements( By.cssSelector( suggestIons ) );  
		try {
			for ( WebElement suggestion : allSuggestions ) {
				Thread.sleep(600);
				searchField.sendKeys( Keys.ARROW_DOWN); // show effect of selecting item with keyboard arrow down
				if ( suggestion.getText().contains( match ) ) {
					suggestion.click();
					pagelog.info("Found item and clicked it.");
					Thread.sleep(2000); // just to slow it down so human eyes can see it
					break;
				}
			}
		} catch ( StaleElementReferenceException see ) {
			pagelog.info("Error while iterating dropdown list:\n" + see.getMessage() );
		} catch ( InterruptedException ie ) {
			ie.printStackTrace();
		}
		pagelog.info("Finished select in Etsy dropdown.");
	}

}
