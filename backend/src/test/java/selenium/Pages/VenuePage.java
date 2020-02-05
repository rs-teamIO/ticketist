package selenium.Pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class VenuePage extends BasePage {

    @FindBy(xpath = "//input[@formcontrolname=\"name\"]")
    private WebElement nameInput;

    @FindBy(xpath = "//input[@formcontrolname=\"street\"]")
    private WebElement streetInput;

    @FindBy(xpath = "//input[@formcontrolname=\"city\"]")
    private WebElement cityInput;

    @FindBy(xpath = "//button[3]")
    private WebElement venues;

    @FindBy(xpath = "//button[@id=\"view-on-map-button\"]")
    private WebElement viewMapButton;

    @FindBy(xpath = "//form//button[@id=\"add-venue-button\"]")
    private WebElement submitButton;

    @FindBy(className = "centralError")
    private WebElement responseErrorMessage;

    @FindBy(xpath = "//div[@id=\"swal2-content\"]")
    private WebElement swalContent;

    @FindBy(xpath = "//button[contains(@class, 'swal2-confirm')]")
    private WebElement swalOkButton;

    @FindBy(id = "sector-name")
    private WebElement sectorNameInput;

    @FindBy(id= "max-capacity")
    private WebElement maxCapacityinput;

    @FindBy(id = "add-sector-button")
    private WebElement addSectorButton;

    @FindBy(xpath = "//h2[@id=\"swal2-title\"]")
    private WebElement swalTitle;

    public VenuePage(WebDriver webDriver) { super(webDriver); }

    public WebElement getNameInput() { return this.nameInput; }

    public WebElement getStreetInput() { return this.streetInput; }

    public WebElement getCityInput() { return this.cityInput; }

    public WebElement getViewMapButton() { return this.viewMapButton; }

    public WebElement getVenues() { return this.venues; }

    public WebElement getSubmitButton() { return this.submitButton; }

    public WebElement getResponseErrorMessage() { return this.responseErrorMessage; }

    public WebElement getAddSectorButton() { return this.addSectorButton; }

    public void ensureSubmitButtonIsDisplayed() { ensureIsDisplayed(submitButton); }

    public void ensureResponseErrorMessageIsEqualToString(String text) {
        ensureTextToBe(By.className("centralError"), text);
    }

    public void ensureResponsePassed(){
        ensureTextToBe(By.className("centralError"), "");
    }

    public void ensureSwalAlertContentEquals(String text) { ensureElementContainsText(swalContent, text);}

    public void ensureAddSectorIsClickable() { ensureElementIsClickable(addSectorButton);}

    public void ensureSwalAlertIsSuccessful(String text) { ensureElementContainsText(swalTitle, text);}

    public void clickSwalOkButton() { clickElement(swalOkButton);}

    public void clickAddSectorButton() { clickElement(addSectorButton); }

    public void clickSubmitButton(){ clickElement(submitButton); }

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


    public void submitViewMap(){
        WebElement el = getViewMapButton();
        el.click();
    }

    public void pressVenues(){
        WebElement el = getVenues();
        el.click();
    }

    public void enterSectorName(String text) { enterText(sectorNameInput, text); }
    public void enterMaxCapacity(String number) { enterText(maxCapacityinput, number); }

    public void ensureIsDisplayed2() {
        ensureTextToBe(By.xpath("//input[@formcontrolname=\"name\"]"), "");
    }
}
