package com.siit.ticketist.selenium;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class ProfilePage {
    private WebDriver driver;

    @FindBy(xpath = "//input[@formcontrolname=\"firstName\"]")
    private WebElement firstNameInput;

    @FindBy(xpath = "//input[@formcontrolname=\"lastName\"]")
    private WebElement lastNameInput;

    @FindBy(xpath = "//input[@formcontrolname=\"username\"]")
    private WebElement usernameInput;

    @FindBy(xpath = "//input[@formcontrolname=\"email\"]")
    private WebElement emailInput;

    @FindBy(xpath = "//input[@formcontrolname=\"oldPassword\"]")
    private WebElement oldPasswordInput;

    @FindBy(xpath = "//input[@formcontrolname=\"newPassword\"]")
    private WebElement newPasswordInput;

    @FindBy(xpath = "//input[@formcontrolname=\"newPasswordRepeat\"]")
    private WebElement newPasswordRepeatInput;

    @FindBy(xpath = "//button[@type=\"submit\"]")
    private WebElement updateButton;

    @FindBy(className = "centralError")
    private WebElement responseErrorMessage;

    public WebElement getUpdateButton() {
        return updateButton;
    }

    public WebElement getResponseErrorMessage() {
        return responseErrorMessage;
    }

    public ProfilePage(WebDriver webDriver){ this.driver = webDriver;}

    public WebElement getFirstNameInput() { return this.firstNameInput; }

    public WebElement getLastNameInput() { return this.lastNameInput; }

    public WebElement getUsernameInput() { return this.usernameInput; }

    public WebElement getOldPasswordInput() { return this.oldPasswordInput; }

    public WebElement getNewPasswordInput() { return this.newPasswordInput; }

    public WebElement getNewPasswordRepeatInput() { return this.newPasswordRepeatInput; }

    public WebElement getEmailInput() { return this.emailInput; }

    public void ensureIsDisplayed() {
        (new WebDriverWait(driver, 10))
                .until(ExpectedConditions.visibilityOf(updateButton));
    }

    public void ensureResponseErrorMessageIsEqualToString(String text) {
        (new WebDriverWait(driver, 10))
                .until(ExpectedConditions.textToBe(By.className("centralError"), text));
    }

    public void ensureResponsePassed(){
        (new WebDriverWait(driver, 10))
                .until(ExpectedConditions.textToBe(By.xpath("//input[@formcontrolname=\"oldPassword\"]"), ""));
    }

    public void setFirstNameInput(String value) {
        WebElement el = getFirstNameInput();
        el.clear();
        el.sendKeys(value);
    }


    public void setLastNameInput(String value) {
        WebElement el = getLastNameInput();
        el.clear();
        el.sendKeys(value);
    }


    public void setUsernameInput(String value) {
        WebElement el = getUsernameInput();
        el.clear();
        el.sendKeys(value);
    }


    public void setOldPasswordInput(String value) {
        WebElement el = getOldPasswordInput();
        el.clear();
        el.sendKeys(value);
    }


    public void setNewPasswordInput(String value) {
        WebElement el = getNewPasswordInput();
        el.clear();
        el.sendKeys(value);
    }


    public void setNewPasswordRepeatInput(String value) {
        WebElement el = getNewPasswordRepeatInput();
        el.clear();
        el.sendKeys(value);
    }

    public void setEmailInput(String value) {
        WebElement el = getEmailInput();
        el.clear();
        el.sendKeys(value);
    }

    public void submitUpdate() {
        WebElement el = getUpdateButton();
        el.click();
    }

}
