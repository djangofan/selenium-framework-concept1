package qa.framework;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class SeProps {
	
	public Properties props;
	private final String propFileName = "sauce.properties";
	
	public SeProps() {
		props = getSauceProperties();
	}
	
	public String getProperty( String key ) {
		return (String) props.get(key);
	}
	
	public void setProperty( String key, String val ) {
		props.setProperty(key, val);
	}

	private Properties getSauceProperties() {
		File propsFile = new File( propFileName );
		System.out.println("Loading SauceLabs properties file: " + propsFile.getAbsolutePath() );
		Properties props = new Properties();
		if ( propsFile.exists() ) {			
			FileInputStream in = null;
			try {
				in = new FileInputStream( propsFile );
				props.load( in );
			} catch ( IOException e ) {
				e.printStackTrace();
			} finally {
				try {
					if ( in != null ) {
						in.close();
					}
				} catch ( IOException e ) {
					//ignore error and continue
				}
			}		    
		} else {
			System.out.println("The sauce.properties file is missing.");
		}
		return props;
	}
	
}
