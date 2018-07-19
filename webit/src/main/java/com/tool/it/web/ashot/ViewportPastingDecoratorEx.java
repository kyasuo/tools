package com.tool.it.web.ashot;

import org.openqa.selenium.JavascriptExecutor;

import ru.yandex.qatools.ashot.shooting.ShootingStrategy;
import ru.yandex.qatools.ashot.shooting.ViewportPastingDecorator;

public class ViewportPastingDecoratorEx extends ViewportPastingDecorator {

	private static final long serialVersionUID = 1L;

	public ViewportPastingDecoratorEx(ShootingStrategy strategy) {
		super(strategy);
	}

	@Override
	protected int getCurrentScrollY(JavascriptExecutor js) {
		return ((Number) js
				.executeScript("return window.scrollY || window.pageYOffset || document.documentElement.scrollTop;"))
						.intValue();
	}

}
