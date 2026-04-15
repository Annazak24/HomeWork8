package factory;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import factory.settings.ChromeSettings;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class WebDriverFactory {
    private final String runType = System.getProperty("run.type", "remote");

    public WebDriver create() {
        try {
            if ("remote".equals(runType)) {
                ChromeOptions options = new ChromeOptions();

                options.setCapability("browserName", "chrome");
                options.setCapability("browserVersion", "128.0");
                Map<String, Object> selenoidOptions = new HashMap<>();
                selenoidOptions.put("enableVNC", true);
                selenoidOptions.put("sessionTimeout", "15m");
                options.setCapability("selenoid:options", selenoidOptions);

                Map<String, String> mobileEmulation = new HashMap<>();
                mobileEmulation.put("deviceName", "Nexus 5");
                options.setExperimentalOption("mobileEmulation", mobileEmulation);


                return new RemoteWebDriver(
                      new URL("http://selenoid:4444/wd/hub"),
                        options
                );
            }

            return new ChromeDriver((ChromeOptions) new ChromeSettings().settings());

        } catch (Exception e) {
            throw new RuntimeException("Չհաջողվեց ստեղծել WebDriver-ը: " + e.getMessage(), e);
        }
    }
}
