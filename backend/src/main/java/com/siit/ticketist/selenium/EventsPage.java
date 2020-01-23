package com.siit.ticketist.selenium;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class EventsPage {
    private WebDriver driver;

    @FindBy(xpath = "//form/button")
    private WebElement searchButton;

    public EventsPage(WebDriver driver) { this.driver = driver; }

    public WebElement getSearchButton() {
        return searchButton;
    }

    public void ensureIsDisplayed() {
        (new WebDriverWait(driver, 10))
                .until(ExpectedConditions.visibilityOf(searchButton));
    }
}
