package qa.framework;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;

public class SeUtil {

	public Logger log;

	public SeUtil() {
		log = LoggerFactory.getLogger("Util");
		log.info("Invoked SeUtil helper class.");
	}
	
	public static void addBuildNumberToUpdate(Map<String, Object> updates) {
		String buildNumber = readPropertyOrEnv("BAMBOO_BUILDNUMBER", null);
		if (buildNumber == null || buildNumber.equals("")) {
			// try Jenkins
			buildNumber = readPropertyOrEnv("JENKINS_BUILD_NUMBER", null);
		}
		if (buildNumber != null && !(buildNumber.equals(""))) {
			updates.put("build", buildNumber);
		}
	}
	public static String getNonNullEnv(String propertyName) {
		String value = readPropertyOrEnv(propertyName, "");
		Preconditions.checkNotNull(value);
		return value;
	}

	/**
	 * Locates a file in the current project
	 * 
	 * @param path
	 *     path to file to locate from root of project
	 * @return file being sought, if it exists
	 * @throws org.openqa.selenium.WebDriverException
	 *             wrapped FileNotFoundException if file could not be found
	 */
	public static File locate(String path) {
		File dir = new File(".").getAbsoluteFile();
		while (dir != null) {
			File needle = new File(dir, path);
			if (needle.exists()) {
				return needle;
			}
			dir = dir.getParentFile();
		}
		throw new WebDriverException( new FileNotFoundException(
				"Could not find " + path + " in the project") );
	}

	public static String readPropertyOrEnv(String key, String defaultValue) {
		String v = System.getProperty(key);
		if (v == null)
			v = System.getenv(key);
		if (v == null)
			v = defaultValue;
		return v;
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

	public void sleep( int seconds ) {
		log.info("Sleep for " + seconds + " seconds...");
		try {
			Thread.sleep(seconds * 1000);
		} catch ( InterruptedException ex ) {
			ex.printStackTrace();
		}
	}

}
