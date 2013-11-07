package qa.framework;

/**
 * Used to reference a common WebDriver instance or other data from a single thread, or its child threads.
 */
public class ThreadedWebDriver {

	// instead of using Threadguard.protect()
    static final ThreadLocal<SauceDriver> threadWebDriver = new InheritableThreadLocal<SauceDriver>();

    ThreadedWebDriver() {
    	// does nothing
    }

    public static SauceDriver access() {
        return threadWebDriver.get();
    }

    public static void sync(SauceDriver driver) {
        threadWebDriver.set(driver);
    }

    public static void remove() {
        threadWebDriver.remove();
    }

}

