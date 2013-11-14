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

    public SauceOnDemandAuthentication( String keyFileLoc ) {
        	File keyFile = new File( keyFileLoc );
        	if ( keyFile.exists() ) {
                loadCredentialsFromFile( keyFile );
            } else {
            	System.out.println("Could not load SauceLabs key file from:\n " + 
                    keyFile.getAbsolutePath() );
            }
    }

    public SauceOnDemandAuthentication( String username, String accessKey ) {
        this.username = username;
        this.accessKey = accessKey;
    }
    
    public boolean isLoaded() {
    	if ( this.username.isEmpty() || this.accessKey.isEmpty() ) {
    		return false;
    	} else {
    		return true;
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
