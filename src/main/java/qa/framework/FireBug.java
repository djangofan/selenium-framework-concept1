package qa.framework;

import org.openqa.selenium.firefox.FirefoxProfile;
import java.io.File;
import java.io.IOException;
import static qa.framework.SeUtil.locate;

public class FireBug {

  public static void addTo(FirefoxProfile profile) throws IOException {
    File firebug = locate("third_party/firebug/firebug-1.5.0-fx.xpi");
    profile.addExtension(firebug);
    profile.setPreference("extensions.firebug.addonBarOpened", true);
    profile.setPreference("extensions.firebug.allPagesActivation", "on");
    profile.setPreference("extensions.firebug.console.enableSites", true);
    profile.setPreference("extensions.firebug.defaultPanelName", "console");
    profile.setPreference("extensions.firebug.net.enableSites", true);
    profile.setPreference("extensions.firebug.openInWindow", false);
    profile.setPreference("extensions.firebug.script.enableSites", true);
    profile.setPreference("extensions.firebug.showErrorCount", true);
    profile.setPreference("extensions.firebug.showJSErrors", true);
    profile.setPreference("extensions.firefox.toolbarCustomizationDone", true);

    // Prevent the first run page being displayed
    profile.setPreference("extensions.firebug.currentVersion", "1.7.3");
  }

}