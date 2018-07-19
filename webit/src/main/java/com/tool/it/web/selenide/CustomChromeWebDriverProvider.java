package com.tool.it.web.selenide;

import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverProvider;

public class CustomChromeWebDriverProvider implements WebDriverProvider {

	public static String downloadDirectory = Configuration.reportsFolder;

	public WebDriver createDriver(DesiredCapabilities desiredCapabilities) {
		final Map<String, Object> prefs = new HashMap<String, Object>();
		prefs.put("profile.default_content_settings.popups", 0);
		prefs.put("download.prompt_for_download", false);
		prefs.put("download.default_directory", downloadDirectory);
		final ChromeOptions options = new ChromeOptions();
		options.setExperimentalOption("prefs", prefs);
		options.setPageLoadStrategy(PageLoadStrategy.NORMAL);
		return new ChromeDriver(options);
	}

}
