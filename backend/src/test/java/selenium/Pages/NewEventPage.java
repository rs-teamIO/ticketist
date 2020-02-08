package selenium.Pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class NewEventPage extends BasePage{
    private WebDriver driver;

    @FindBy(xpath = "/html/body/app-root/app-event-form/div/div[1]/form/mat-form-field[1]/div/div[1]/div/input")
    private WebElement eventNameInput;

    @FindBy(xpath = "/html/body/app-root/app-event-form/div/div[1]/form/mat-form-field[1]/div/div[3]/div/mat-error")
    private WebElement eventNameError;

    @FindBy(xpath = "/html/body/app-root/app-event-form/div/div[1]/form/mat-form-field[2]/div/div[1]/div/mat-select")
    private WebElement categoryInputSelect;

    @FindBy(xpath = "/html/body/div[2]/div[2]/div/div/div/mat-option[1]")
    private WebElement categoryInputOption;

    @FindBy(xpath = "/html/body/app-root/app-event-form/div/div[1]/form/div[1]/mat-form-field[1]/div/div[1]/div[1]/input")
    private WebElement reservationDeadlineInput;

    @FindBy(xpath = "/html/body/app-root/app-event-form/div/div[1]/form/div[1]/mat-form-field[2]/div/div[1]/div[1]/input")
    private WebElement startDateInput;

    @FindBy(xpath = "/html/body/app-root/app-event-form/div/div[1]/form/div[1]/mat-form-field[3]/div/div[1]/div[1]/input")
    private WebElement endDateInput;

    @FindBy(xpath = "/html/body/app-root/app-event-form/div/div[1]/form/div[2]/mat-form-field[1]/div/div[1]/div/mat-select")
    private WebElement hoursInput;

    @FindBy(xpath = "/html/body/div[2]/div[2]/div/div/div/mat-option[3]")
    private WebElement hoursInputOption;

    @FindBy(xpath = "/html/body/app-root/app-event-form/div/div[1]/form/div[2]/mat-form-field[2]/div/div[1]/div/mat-select")
    private WebElement minutesInput;

    @FindBy(xpath = "/html/body/div[2]/div[2]/div/div/div/mat-option[1]")
    private WebElement minutesInputOption;

    @FindBy(xpath = "/html/body/app-root/app-event-form/div/div[1]/form/mat-form-field[3]/div/div[1]/div/input")
    private WebElement reservationLimitInput;

    @FindBy(xpath = "/html/body/app-root/app-event-form/div/div[1]/form/mat-form-field[4]/div/div[1]/div/textarea")
    private WebElement descriptionLimit;

    @FindBy(xpath = "/html/body/app-root/app-event-form/div/div[1]/form/button")
    private WebElement addEventButton;

    @FindBy(xpath = "/html/body/app-root/app-event-form/div/div[2]/div[1]/form/mat-form-field/div/div[1]/div/mat-select")
    private WebElement chooseVenueSelect;

    @FindBy(xpath = "/html/body/div[2]/div[2]/div/div/div/div/mat-option[1]")
    private WebElement chooseVenueSelectItem;

    @FindBy(xpath = "/html/body/app-root/app-event-form/div/div[2]/div[1]/form/div[1]/table")
    private WebElement sectorTable;

    @FindBy(xpath = "/html/body/app-root/app-event-form/div/div[2]/div[1]/form/div[1]/table/tr[2]/td[6]/input")
    private WebElement sector1Activate;

    @FindBy(xpath = "/html/body/app-root/app-event-form/div/div[2]/div[1]/form/div[1]/table/tr[2]/td[3]/input")
    private WebElement sector1Price;

    @FindBy(xpath = "/html/body/app-root/app-event-form/div/div[2]/div[1]/form/div[1]/table/tr[2]/td[5]/input")
    private WebElement sector1Numerated;

    @FindBy(xpath = "/html/body/app-root/app-event-form/div/div[2]/div[1]/form/div[1]/table/tr[2]/td[4]/input")
    private WebElement sector1Capacity;

    @FindBy(xpath = "/html/body/app-root/app-event-form/div/div[2]/div[1]/form/div[1]/table/tr[3]/td[5]/input")
    private WebElement sector2Activate;

    @FindBy(xpath = "/html/body/app-root/app-event-form/div/div[2]/div[1]/form/div[1]/table/tr[3]/td[3]/input")
    private WebElement sector2Price;

    public NewEventPage(WebDriver driver) {
        super(driver);
    }

    public void ensureTableSectorIsDisplayed() {
        ensureIsDisplayed(sectorTable);
    }

    public void ensureEventNameInputIsDisplayed() {
        ensureIsDisplayed(eventNameInput);

    }

    public void ensureVenueSelectIsDisplayed() {
        ensureIsDisplayed(chooseVenueSelect);
    }

    public WebElement getSubmitButton() {
        return addEventButton;
    }

    public void clickOnEventNameInput() {
        eventNameInput.click();
    }

    public void clickOnReservationLimit() {
        reservationLimitInput.click();
    }

    public void ensureEventNameErrorMessageIsDisplayed() {
        ensureIsDisplayed(eventNameError);
    }

    public WebElement getNewEventInputError() {
        return eventNameError;
    }

    public void enterEventName(String text) {
        enterText(eventNameInput, text);
    }

    public void chooseCategory() {
        clickElement(categoryInputSelect);
        clickElement(categoryInputOption);
    }

    public void enterReservationDeadline(String deadline) {
        enterText(reservationDeadlineInput, deadline);
    }

    public void enterStartDate(String deadline) {
        enterText(startDateInput, deadline);
    }

    public void enterEndDate(String deadline) {
        enterText(endDateInput, deadline);
    }

    public void chooseHours() {
        clickElement(hoursInput);
        clickElement(hoursInputOption);
    }

    public void chooseMinutes() {
        clickElement(minutesInput);
        clickElement(minutesInputOption);
    }

    public void enterReservationLimit(String text) {
        enterText(reservationLimitInput, text);
    }

    public void enterDescription(String text) {
        enterText(descriptionLimit, text);
    }

    public void chooseVenue() {
        clickElement(chooseVenueSelect);
        clickElement(chooseVenueSelectItem);
    }

    public void clickSector1() {
        clickElement(sector1Activate);
    }

    public void setSector1Price(String price) {
        enterText(sector1Price, price);
    }

    public void clickSector2() {
        clickElement(sector2Activate);
    }

    public void setSector2Price(String price) {
        enterText(sector2Price, price);
    }

    public void setSector1Numerated() {
        clickElement(sector1Numerated);
    }

    public void setSector1Capacity(String capacity) {
        enterText(sector1Capacity, capacity);
    }

    public void ensureSubmitIsDisplayed() {
        ensureIsDisplayed(addEventButton);
    }

    public void submitForm() {
        clickElement(addEventButton);
    }
}
