package qa.framework.testng;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Stores the Sauce OnDemand authentication (retrieved from
 * either System properties/environment variables or by parsing the default key file).
 */
public class SauceOnDemandAuthentication {

    private String username = "";
    private String accessKey = "";
    private static final String SAUCE_USER_NAME = "SAUCE_USER_NAME";
    private static final String SAUCE_API_KEY = "SAUCE_API_KEY";
    private static String defaultKeyFile = "sauce-labs.key";

    //TODO These constructors could be improved
    public SauceOnDemandAuthentication() {
        	File keyFile = new File( defaultKeyFile );
        	if ( keyFile.exists() ) {
                loadCredentialsFromFile( keyFile );
            } else {
            	System.out.println("Could not load SauceLabs key file from your project root:\n " + 
                    keyFile.getAbsolutePath() );
            	System.out.println("Will try to load values from environment instead.");
                this.username = getPropertyOrEnvironmentVariable( SAUCE_USER_NAME );
                this.accessKey = getPropertyOrEnvironmentVariable( SAUCE_API_KEY );
            }
    }

    public SauceOnDemandAuthentication(String username, String accessKey) {
        this.username = username;
        this.accessKey = accessKey;
        String fileLoc = System.getProperty("user.dir") + File.pathSeparatorChar + defaultKeyFile;
        File myFile = new File( fileLoc );
        if ( !myFile.exists() ) {
            System.out.println("Creating new " + defaultKeyFile + " file in your home directory.");
            saveTo( myFile );
        }
    }

    private void loadCredentialsFromFile( File propertyFile ) {
        Properties props = new Properties();
        FileInputStream in = null;
        try {
            if ( propertyFile.exists() ) {
                in = new FileInputStream( propertyFile );
                props.load(in);
                this.username = props.getProperty("username");
                this.accessKey = props.getProperty("key");
            }
        } catch ( IOException e ) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch ( IOException e ) {
                //ignore error and continue
            }
        }
    }

    private static String getPropertyOrEnvironmentVariable(String property) {
        String value = System.getProperty(property);
        if (value == null || value.equals("")) {
            value = System.getenv(property);
        }
        return value;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public void saveTo( File propertyFile ) {
        Properties props = new Properties();
        props.put("username", username);
        props.put("key", accessKey);
        FileOutputStream out;
		try {
			out = new FileOutputStream( propertyFile );
	        try {
	            props.store(out, "Sauce OnDemand access credential");
	        } finally {
	            out.close();
	        }
		} catch ( IOException e ) {
			e.printStackTrace();
		}

    }
}
