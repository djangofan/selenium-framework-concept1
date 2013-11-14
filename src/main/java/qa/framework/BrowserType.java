package qa.framework;

public enum BrowserType {
	
	/*
	DesiredCapabilities.firefox().getBrowserName() = "firefox"
	DesiredCapabilities.chrome().getBrowserName() = "chrome"
	DesiredCapabilities.internetExplorer().getBrowserName() = "internet explorer"
	DesiredCapabilities.safari().getBrowserName() = "safari"
	DesiredCapabilities.opera().getBrowserName() = "opera"
	*/
	
	FIREFOX("firefox"),
	CHROME("chrome"),
	IE("internet explorer"),
	SAFARI("safari"),
	OPERA("opera");

	private final String browser;

	BrowserType( String browser ) {
		this.browser = browser;
	}

	public String getBrowser() {
		return browser;
	}

}
