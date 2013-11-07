package qa.framework;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class FirefoxProfileFactory {
	
	static Logger log = LoggerFactory.getLogger( "FirefoxProfileFactory" );

    static FirefoxProfile create() {
        FirefoxProfile profile = new FirefoxProfile();
		configureProfile(profile);
		return profile;
    }

    static void configureProfile( FirefoxProfile p ) {    	
        String firebugEnabled = System.getenv("FIREBUG_ENABLED");
        // Never update automatically
        p.setPreference("app.update.enabled", false);
        // Never show the updated page
        p.setPreference("browser.startup.homepage_override.mstone", "ignore");
        // Open "about:blank" when starting
        p.setPreference("browser.startup.page", 0);
        // Don't check default browser
        p.setPreference("browser.shell.checkDefaultBrowser", false);        
        // Setup FireBug maybe
        if ( firebugEnabled.equals("true") ) {
            log.info("Enabling Firebug...");
        	configureFirebug(p);
        }
    }

    static void configureFirebug( FirefoxProfile p ) {
        String firebugUrl = System.getenv("FIREBUG_URL");
        String firebugVersion = System.getenv("FIREBUG_VERSION");
        String firebugStartOpen = System.getenv("FIREBUG_START_OPEN");
        if ( StringUtils.isNotBlank(firebugUrl) && StringUtils.isNotBlank(firebugVersion) ) {
            File firebug = null;
			try {
				firebug = File.createTempFile("firebug-" + firebugVersion, ".xpi");
			} catch ( IOException e ) {
				log.info("Error creating a temp file.");
				e.printStackTrace();
			}
            try {
				FileUtils.copyURLToFile( new URL(firebugUrl), firebug );
			} catch ( IOException e ) {
				log.info("Error copying URL to file.");
				e.printStackTrace();
			}
            try {
				p.addExtension(firebug);
			} catch ( IOException e ) {
				log.info("Error adding Firebug extension.");
				e.printStackTrace();
			}
            p.setPreference("extensions.firebug.currentVersion", firebugVersion);
            if ( firebugStartOpen.equals("true") ) {
                p.setPreference("extensions.firebug.allPagesActivation", "on");
            }
            p.setPreference("extensions.firebug.defaultPanelName", "console");
            p.setPreference("extensions.firebug.console.enableSites", true);
            p.setPreference("extensions.firebug.showFirstRunPage", false);
            p.setPreference("extensions.firebug.toolbarCustomizationDone", true);
        } else {
        	log.info("Firebug properties are required.");
        }
    }

}
