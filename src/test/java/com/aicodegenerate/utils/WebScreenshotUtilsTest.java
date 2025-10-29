package com.aicodegenerate.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class WebScreenshotUtilsTest {

    @Test
    void saveWebPageScreenshot() {
        String webUrl = "https://www.baidu.com";
        String imagePath = WebScreenshotUtils.saveWebPageScreenshot(webUrl);
        Assertions.assertNotNull(imagePath);
    }
}