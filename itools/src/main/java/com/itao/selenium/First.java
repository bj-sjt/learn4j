package com.itao.selenium;


import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class First {
    public static void main(String[] args) throws InterruptedException {
        ChromeDriver driver = new ChromeDriver();
        try {
            //driver.get("http://www.baidu.com");
            driver.navigate().to("http://www.baidu.com");
            //driver.manage().window().maximize();//最大化
            driver.manage().window().setPosition(new Point(0, 0));
            System.out.println(driver.getCurrentUrl());
            System.out.println(driver.getTitle());
            WebElement kw = driver.findElement(By.id("kw"));
            kw.click();
            kw.sendKeys("selenium");
            driver.findElement(By.id("su")).click();
            Thread.sleep(1000);
            driver.navigate().back();
            Thread.sleep(1000);
            driver.navigate().forward();
            Thread.sleep(1000);
            driver.navigate().refresh();
            Thread.sleep(5000);
        }finally {
            driver.quit();
        }

    }
}
