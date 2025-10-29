package com.aicodegenerate.utils;

import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.aicodegenerate.exception.BusinessException;
import com.aicodegenerate.exception.ErrorCode;
import io.github.bonigarcia.wdm.WebDriverManager;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.time.Duration;
import java.util.UUID;

@Slf4j
public class WebScreenshotUtils {

    private static final WebDriver webdriver;

    //全局静态初始化，避免重复初始化驱动程序
    static {
        final int DEFAULT_WIDTH = 1600;
        final int DEFAULT_HEIGHT = 900;
        webdriver = initChromeDriver(DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    /**
     * 退出时销毁
     */
    @PreDestroy
    public void destory() {
        webdriver.quit();
    }

    /**
     * 网页截图
     */
    public static String saveWebPageScreenshot(String webUrl) {
        //非空校验
        if (StrUtil.isBlank(webUrl)) {
            log.error("网页截图失败，url为空");
            return null;
        }
        String compressImagePath = null;
        try {
            //创建临时目录
            String screenshotDir = System.getProperty("user.dir") + "/tmp/screenshots" + UUID.randomUUID().toString().substring(0, 8);
            FileUtil.mkdir(screenshotDir);
            //图片后缀
            final String imageSuffix = ".png";
            String imagePath = screenshotDir + File.separator + RandomUtil.randomString(5) + imageSuffix;
            //访问网页
            webdriver.get(webUrl);
            //等待页面加载
            waitForPageLoad(webdriver);
            //截图
            byte[] screenshotByte = ((TakesScreenshot) webdriver).getScreenshotAs(OutputType.BYTES);
            //保存截图
            savaImage(screenshotByte, imagePath);
            log.info("网页截图保存成功，路径为： {}", imagePath);
            //压缩图片
            final String compressImageSuffix = "_compressed.jpg";
            compressImagePath = screenshotDir + File.separator + RandomUtil.randomString(5) + compressImageSuffix;
            compressImage(imagePath, compressImagePath);
            log.info("压缩截图保存成功，路径为： {}", compressImagePath);
            //节约空间的话，删除压缩前的截图,
            //FileUtil.del(imagePath);
            return compressImagePath;
        } catch (WebDriverException e) {
            log.error("网页截图失败，webUrl：{} 错误信息为： {}", webUrl, e.getMessage());
            return null;
        }
    }

    ;


    /**
     * 初始化 Chrome 浏览器驱动
     */
    private static WebDriver initChromeDriver(int width, int height) {
        try {
            // 自动管理 ChromeDriver
            WebDriverManager.chromedriver().setup();
            // 配置 Chrome 选项
            ChromeOptions options = new ChromeOptions();
            // 无头模式
            options.addArguments("--headless");
            // 禁用GPU（在某些环境下避免问题）
            options.addArguments("--disable-gpu");
            // 禁用沙盒模式（Docker环境需要）
            options.addArguments("--no-sandbox");
            // 禁用开发者shm使用
            options.addArguments("--disable-dev-shm-usage");
            // 设置窗口大小
            options.addArguments(String.format("--window-size=%d,%d", width, height));
            // 禁用扩展
            options.addArguments("--disable-extensions");
            // 设置用户代理
            options.addArguments("--user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");
            // 创建驱动
            WebDriver driver = new ChromeDriver(options);
            // 设置页面加载超时
            driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
            // 设置隐式等待
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
            return driver;
        } catch (Exception e) {
            log.error("初始化 Chrome 浏览器失败", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "初始化 Chrome 浏览器失败");
        }
    }

    private static void savaImage(byte[] imageBytes, String imagePath) {
        try {
            FileUtil.writeBytes(imageBytes, imagePath);
        } catch (Exception e) {
            log.error("保存图片失败, 路径为： {}", imagePath, e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "保存图片失败");
        }
    }

    /**
     * 压缩图片
     */
    private static void compressImage(String originImagePath, String compressImagePath) {
        //图片压缩质量（0.1 * 100%）
        final float COMPRESSION_QUALITY = 0.3f;
        try {
            ImgUtil.compress(FileUtil.file(originImagePath), FileUtil.file(compressImagePath), COMPRESSION_QUALITY);
        } catch (Exception e) {
            log.error("压缩图片失败, 路径为： {} -> {}", originImagePath, compressImagePath, e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "压缩图片失败");
        }
    }

    /**
     * 等待页面加载完成
     */
    private static void waitForPageLoad(WebDriver driver) {
        try {
            //等待页面加载完成
            WebDriverWait webDriverWait = new WebDriverWait(driver, Duration.ofSeconds(10));
            //等待document.readyState 为 complete
            webDriverWait.until(webDriver -> ((JavascriptExecutor) webDriver)
                    .executeScript("return document.readyState")
                    .equals("complete")
            );
            //额外等待一段时间，以确保动态内容加载完成
            Thread.sleep(1000);
        } catch (Exception e) {
            log.error("等待页面加载时错误, 继续执行截图", e);
        }
    }
}
