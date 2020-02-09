package selenium.Pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class SignUpPage {
    private WebDriver driver;

    @FindBy(xpath = "//input[@formcontrolname=\"firstName\"]")
    private WebElement firstNameInput;

    @FindBy(xpath = "//input[@formcontrolname=\"lastName\"]")
    private WebElement lastNameInput;

    @FindBy(xpath = "//input[@formcontrolname=\"username\"]")
    private WebElement usernameInput;

    @FindBy(xpath = "//input[@formcontrolname=\"password\"]")
    private WebElement passwordInput;

    @FindBy(xpath = "//input[@formcontrolname=\"email\"]")
    private WebElement emailInput;

    @FindBy(xpath = "//button[@type=\"submit\"]")
    private WebElement submitButton;

    @FindBy(className = "centralError")
    private WebElement responseErrorMessage;

    public WebElement getSubmitButton() {
        return submitButton;
    }

    public WebElement getResponseErrorMessage() {
        return responseErrorMessage;
    }

    public SignUpPage(WebDriver webDriver){ this.driver = webDriver;}

    public WebElement getFirstNameInput() { return this.firstNameInput; }

    public WebElement getLastNameInput() { return this.lastNameInput; }

    public WebElement getUsernameInput() { return this.usernameInput; }

    public WebElement getPasswordInput() { return this.passwordInput; }

    public WebElement getEmailInput() { return this.emailInput; }

    public void ensureIsDisplayed() {
        (new WebDriverWait(driver, 10))
                .until(ExpectedConditions.visibilityOf(submitButton));
    }

    public void ensureResponseErrorMessageIsEqualToString(String text) {
        (new WebDriverWait(driver, 10))
                .until(ExpectedConditions.textToBe(By.className("centralError"), text));
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



    public void setPasswordInput(String value) {
        WebElement el = getPasswordInput();
        el.clear();
        el.sendKeys(value);
    }


    public void setEmailInput(String value) {
        WebElement el = getEmailInput();
        el.clear();
        el.sendKeys(value);
    }

    public void submitSignUp() {
        WebElement el = getSubmitButton();
        el.click();
    }

}
