package com.tool.it.web.selenide;

import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.codeborne.selenide.WebDriverProvider;

public class CustomFirefoxWebDriverProvider implements WebDriverProvider {

	@Override
	public WebDriver createDriver(DesiredCapabilities desiredCapabilities) {
		final FirefoxOptions options = new FirefoxOptions();
		options.setPageLoadStrategy(PageLoadStrategy.NORMAL);
		return new FirefoxDriver(options);
	}

}
