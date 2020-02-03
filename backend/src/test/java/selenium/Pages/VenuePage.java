package selenium.Pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class VenuePage {
    private WebDriver driver;

    @FindBy(xpath = "//input[@formcontrolname=\"name\"]")
    private WebElement nameInput;

    @FindBy(xpath = "//input[@formcontrolname=\"street\"]")
    private WebElement streetInput;

    @FindBy(xpath = "//input[@formcontrolname=\"city\"]")
    private WebElement cityInput;

    @FindBy(xpath = "//button[3]")
    private WebElement venues;

    @FindBy(xpath = "/html/body/app-root/app-venue-form/div/div/app-venue-form-basic/div/mat-card/mat-card-content/button")
    private WebElement viewMapButton;

    @FindBy(xpath = "//form/button")
    private WebElement submitButton;

    @FindBy(className = "centralError")
    private WebElement responseErrorMessage;

    public VenuePage(WebDriver webDriver) { this.driver = webDriver; }

    public WebElement getNameInput() { return this.nameInput; }

    public WebElement getStreetInput() { return this.streetInput; }

    public WebElement getCityInput() { return this.cityInput; }

    public WebElement getViewMapButton() { return this.viewMapButton; }

    public WebElement getVenues() { return this.venues; }

    public WebElement getSubmitButton() { return this.submitButton; }

    public WebElement getResponseErrorMessage() { return this.responseErrorMessage; }

    public void ensureIsDisplayed() {
        (new WebDriverWait(driver, 10))
                .until(ExpectedConditions.visibilityOf(submitButton));
    }

    public void ensureResponseErrorMessageIsEqualToString(String text) {
        (new WebDriverWait(driver, 10))
                .until(ExpectedConditions.textToBe(By.className("centralError"), text));
    }

    public void ensureResponsePassed(){
        (new WebDriverWait(driver, 10))
                .until(ExpectedConditions.textToBe(By.className("centralError"), ""));
    }

    public void setNameInput(String value) {
        WebElement el = getNameInput();
        el.clear();
        el.sendKeys(value);
    }

    public void setStreetInput(String value) {
        WebElement el = getStreetInput();
        el.clear();
        el.sendKeys(value);
    }

    public void setCityInput(String value) {
        WebElement el = getCityInput();
        el.clear();
        el.sendKeys(value);
    }

    public void submitButton(){
        WebElement el = getSubmitButton();
        el.click();
    }

    public void submitViewMap(){
        WebElement el = getViewMapButton();
        el.click();
    }

    public void pressVenues(){
        WebElement el = getVenues();
        el.click();
    }

    public void ensureIsDisplayed2() {
        (new WebDriverWait(driver, 10))
                .until(ExpectedConditions.textToBe(By.xpath("//input[@formcontrolname=\"name\"]"), ""));
    }
}
