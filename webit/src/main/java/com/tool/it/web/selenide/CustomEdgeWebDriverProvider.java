package com.tool.it.web.selenide;

import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.codeborne.selenide.WebDriverProvider;

public class CustomEdgeWebDriverProvider implements WebDriverProvider {

	@Override
	public WebDriver createDriver(DesiredCapabilities desiredCapabilities) {
		final EdgeOptions options = new EdgeOptions();
		options.setPageLoadStrategy(PageLoadStrategy.NORMAL.toString());
		return new EdgeDriver(options);
	}

}
