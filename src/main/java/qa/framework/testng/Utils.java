package qa.framework.testng;

import java.util.Map;

import com.google.common.base.Preconditions;

public final class Utils {

    public static void addBuildNumberToUpdate(Map<String, Object> updates) {
        String buildNumber = readPropertyOrEnv("BAMBOO_BUILDNUMBER", null);
        if ( buildNumber == null || buildNumber.equals("") ) {
            //try Jenkins
            buildNumber = readPropertyOrEnv("JENKINS_BUILD_NUMBER", null);
        }
        if ( buildNumber != null && !( buildNumber.equals("") ) ) {
            updates.put("build", buildNumber);
        }
    }

    public static String readPropertyOrEnv(String key, String defaultValue) {
        String v = System.getProperty(key);
        if (v == null)
            v = System.getenv(key);
        if (v == null)
            v = defaultValue;
        return v;
    }
    
	public static String getNonNullEnv(String propertyName) {
		String value = readPropertyOrEnv(propertyName, "");
		Preconditions.checkNotNull(value);
		return value;
	}
    
}
