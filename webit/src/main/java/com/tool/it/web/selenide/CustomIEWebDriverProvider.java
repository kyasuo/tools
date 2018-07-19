package com.tool.it.web.selenide;

import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerOptions;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.codeborne.selenide.WebDriverProvider;

public class CustomIEWebDriverProvider implements WebDriverProvider {

	@Override
	public WebDriver createDriver(DesiredCapabilities desiredCapabilities) {
		final InternetExplorerOptions options = new InternetExplorerOptions();
		options.setPageLoadStrategy(PageLoadStrategy.NORMAL);
		return new InternetExplorerDriver(options);
	}

}
