package com.tool.it.web;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.open;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.LoggerFactory;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Configuration.FileDownloadMode;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;
import com.tool.it.web.ashot.ViewportPastingDecoratorEx;
import com.tool.it.web.excel.TestData;
import com.tool.it.web.excel.TestDataFactory;
import com.tool.it.web.excel.bean.Element;
import com.tool.it.web.excel.bean.TestPage;
import com.tool.it.web.selenide.CustomChromeWebDriverProvider;
import com.tool.it.web.selenide.CustomEdgeWebDriverProvider;
import com.tool.it.web.selenide.CustomFirefoxWebDriverProvider;
import com.tool.it.web.selenide.CustomIEWebDriverProvider;
import com.tool.util.PropertyUtil;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.FileAppender;
import ru.yandex.qatools.ashot.AShot;
import ru.yandex.qatools.ashot.Screenshot;
import ru.yandex.qatools.ashot.shooting.SimpleShootingStrategy;

public class SelenideTestRunner {

	private static final File XLXSFILE = new File(SelenideTestRunner.class.getResource("/test1-1.xlsx").getFile());

	private static final Logger logger = (Logger) LoggerFactory.getLogger(SelenideTestRunner.class);
	private static final String[] WEBDRIVER_KEYS = { "webdriver.ie.driver", "webdriver.edge.driver",
			"webdriver.gecko.driver", "webdriver.chrome.driver" };
	private static final Map<String, String> BROWSER_MAP = new HashMap<String, String>();
	static {
		for (String key : WEBDRIVER_KEYS) {
			System.setProperty(key, SelenideTestRunner.class.getResource(PropertyUtil.getProperty(key)).getFile());
		}
		BROWSER_MAP.put("IE", CustomIEWebDriverProvider.class.getName());
		BROWSER_MAP.put("EDGE", CustomEdgeWebDriverProvider.class.getName());
		BROWSER_MAP.put("FIREFOX", CustomFirefoxWebDriverProvider.class.getName());
		BROWSER_MAP.put("CHROME", CustomChromeWebDriverProvider.class.getName());
	}
	private static final AShot ASHOT = new AShot()
			.shootingStrategy(new ViewportPastingDecoratorEx(new SimpleShootingStrategy())
					.withScrollTimeout(PropertyUtil.getPropertyInt("ashot.scrollTimeout", 100)));
	private static File ASHOT_FOLDER;

	@BeforeClass
	public static void beforeClass() throws Exception {
		// TODO review configuration
		// Configuration.fileDownload = FileDownloadMode.PROXY;
		Configuration.timeout = 5000;
		Configuration.pollingInterval = 1000;
		Configuration.startMaximized = true;
		Configuration.holdBrowserOpen = true;
		Configuration.fastSetValue = true;
		Configuration.savePageSource = true;
		Configuration.selectorMode = Configuration.SelectorMode.Sizzle;
		Configuration.baseUrl = PropertyUtil.getProperty("configuration.baseUrl").trim();
		Configuration.reportsFolder = new File(PropertyUtil.getProperty("configuration.reportsFolder"),
				(new SimpleDateFormat("yyyyMMddHHmmssSSS")).format(new Date())).getAbsolutePath() + "_"
				+ PropertyUtil.getProperty("configuration.browser", "CHROME").trim();
		CustomChromeWebDriverProvider.downloadDirectory = Configuration.reportsFolder;
		Configuration.browser = BROWSER_MAP.get(PropertyUtil.getProperty("configuration.browser", "CHROME").trim());

		ASHOT_FOLDER = new File(Configuration.reportsFolder, PropertyUtil.getProperty("ashot.folder", "ashot"));
		FileUtils.forceMkdir(ASHOT_FOLDER);

		final FileAppender<ILoggingEvent> fileAppender = (FileAppender<ILoggingEvent>) logger.getAppender("FILE");
		if (fileAppender.isStarted()) {
			fileAppender.stop();
		}
		fileAppender
				.setFile((new File(Configuration.reportsFolder, PropertyUtil.getProperty("logfile.name", "test.log")))
						.getAbsolutePath());
		fileAppender.start();
	}

	@AfterClass
	public static void afterClass() {
		Configuration.fileDownload = FileDownloadMode.HTTPGET;
		WebDriverRunner.getWebDriver().close();
	}

	@Test
	public void selenideTest() throws Exception {
		int index = 0;
		open("");
		logger.info("selenideTest start.\nopen page = {}", WebDriverRunner.url());
		screenshot(getIndexString(index) + "_open");
		final TestData testData = TestDataFactory.create(XLXSFILE);
		while (testData.hasNext()) {
			index++;
			final TestPage testPage = testData.next();
			logger.info("> test name = {}, current page = {} --", testPage.getName(), WebDriverRunner.url());
			setUpParameters(testPage);
			screenshot(getIndexString(index) + "-1_before " + testPage.getName());
			click(testPage);
			screenshot(getIndexString(index) + "-2_after " + testPage.getName());
		}
		screenshot(getIndexString(index + 1) + "_finish");
	}

	private String getIndexString(int index) {
		return StringUtils.leftPad(String.valueOf(index), 4, "0");
	}

	private final WebDriverWait wait = new WebDriverWait(WebDriverRunner.getWebDriver(), 30);
	private static final String JS_GET_SCROLL_HEIGHT = "return Math.max(document.body.scrollHeight, document.body.offsetHeight, document.documentElement.clientHeight, document.documentElement.scrollHeight, document.documentElement.offsetHeight) | 0;";

	private void screenshot(String fileName) throws IOException {
		Selenide.screenshot(fileName);
		wait.until(ExpectedConditions.javaScriptThrowsNoExceptions(JS_GET_SCROLL_HEIGHT));
		final Screenshot screenshot = ASHOT.takeScreenshot(WebDriverRunner.getWebDriver());
		ImageIO.write(screenshot.getImage(), "PNG", new File(ASHOT_FOLDER, fileName + "_ashot.png"));
	}

	private void click(TestPage testPage) throws FileNotFoundException, InterruptedException {
		final Element clickElement = testPage.getClickElement();
		logger.info("> click lname = {}, pname = {}, type = {}", clickElement.getlName(), clickElement.getpName(),
				clickElement.getAttr());
		final List<SelenideElement> elements = findElements(clickElement.getpName());
		if (!elements.isEmpty()) {
			// scroll to target element before action
			elements.get(0).scrollIntoView(true);
			if (Element.AType.DOWNLOAD.equals(clickElement.getAttr())) {
				elements.get(0).download();
			} else {
				elements.get(0).click();
			}
		} else {
			logger.error("> click element is not found. pname = {}", clickElement.getpName());
			throw new RuntimeException("click element is not found. pname=" + clickElement.getpName());
		}
	}

	private void setUpParameters(TestPage testPage) {
		List<SelenideElement> elements;
		for (Element element : testPage.getElementList()) {
			if (Element.AType.LINK.equals(element.getAttr()) || Element.AType.BUTTON.equals(element.getAttr())
					|| Element.AType.SUBMIT.equals(element.getAttr())
					|| Element.AType.DOWNLOAD.equals(element.getAttr())) {
				continue;
			}
			elements = findElements(element.getpName());
			if (CollectionUtils.isEmpty(elements)) {
				throw new RuntimeException("element is not found. name=" + element.getpName());
			}
			switch (element.getAttr()) {
			case RADIO:
				for (SelenideElement target : elements) {
					target.selectRadio(element.getInputValue());
				}
				break;
			case CHECKBOX:
				for (SelenideElement target : elements) {
					target.setSelected(Boolean.valueOf(element.getInputValue()));
				}
				break;
			case SELECT:
				for (SelenideElement target : elements) {
					target.selectOptionByValue(element.getInputValue());
				}
				break;
			default:
				for (SelenideElement target : elements) {
					target.val(element.getInputValue());
				}
				break;
			}
		}
	}

	private List<SelenideElement> findElements(String key) {
		final List<SelenideElement> elementList = new ArrayList<SelenideElement>();

		// find by id
		final SelenideElement elementById = $("#" + key);
		if (elementById != null && elementById.exists()) {
			elementList.add(elementById);
		} else {
			// find by name
			final ElementsCollection elementsByName = $$("[name=" + key + "]");
			if (!elementsByName.isEmpty()) {
				for (SelenideElement element : elementsByName) {
					elementList.add(element);
				}
			}
		}

		return elementList;
	}
}
