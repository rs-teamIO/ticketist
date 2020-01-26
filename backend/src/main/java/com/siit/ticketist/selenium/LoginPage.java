package com.siit.ticketist.selenium;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class LoginPage {
    private WebDriver driver;

    @FindBy(xpath = "//input[@formcontrolname=\"username\"]")
    private WebElement usernameInput;

    @FindBy(xpath = "//mat-error[@id=\"mat-error-0\"]")
    private WebElement usernameErrorMessage;

    @FindBy(xpath = "//input[@formcontrolname=\"password\"]")
    private WebElement passwordInput;

    @FindBy(xpath = "//mat-error[@id=\"mat-error-2\"]")
    private WebElement passwordErrorMessage;

    @FindBy(xpath = "//button[@type=\"submit\"]")
    private WebElement loginButton;

    @FindBy(className = "centralError")
    private WebElement responseErrorMessage;

    public LoginPage(WebDriver driver) { this.driver = driver; }

    public WebElement getUsernameInput() {
        return usernameInput;
    }

    public WebElement getUsernameErrorMessage() {
        return usernameErrorMessage;
    }

    public WebElement getPasswordInput() {
        return passwordInput;
    }

    public WebElement getPasswordErrorMessage() {
        return passwordErrorMessage;
    }

    public WebElement getLoginButton() {
        return loginButton;
    }

    public WebElement getResponseErrorMessage() {
        return responseErrorMessage;
    }

    public void ensureIsDisplayed() {
        (new WebDriverWait(driver, 10))
                .until(ExpectedConditions.visibilityOf(loginButton));
    }

    public void ensureResponseErrorMessageIsEqualToString(String text) {
        (new WebDriverWait(driver, 10))
                .until(ExpectedConditions.textToBe(By.className("centralError"), text));
    }

    public void setUsernameInput(String value) {
        WebElement el = getUsernameInput();
        el.clear();
        el.sendKeys(value);
    }

    public void setPasswordInput(String value) {
        WebElement el = getPasswordInput();
        el.clear();
        el.sendKeys(value);
    }

    public void resetUsernameInput() {
        WebElement el = getUsernameInput();
        el.clear();
        el.sendKeys("A");
        el.sendKeys(Keys.BACK_SPACE);
    }

    public void resetPasswordInput() {
        WebElement el = getPasswordInput();
        el.clear();
        el.sendKeys("A");
        el.sendKeys(Keys.BACK_SPACE);
    }

    public void submitLogin() {
        WebElement el = getLoginButton();
        el.click();
    }

}
