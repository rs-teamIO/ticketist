package selenium.Pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class EventDetailsPage extends BasePage {

    @FindBy(id = "reserve-button")
    private WebElement reserveButton;

    @FindBy(id = "buy-button")
    private WebElement buyButton;

    @FindBy(id = "date-select")
    private WebElement dateSelect;

    @FindBy(xpath = "//span[@class=\"mat-option-text\" and contains(text(), 'Day 2')]")
    private WebElement datum;

    @FindBy(id = "sector-select")
    private WebElement sectorSelect;

    @FindBy(xpath = "//span[@class=\"mat-option-text\" and contains(text(), 'Zapad')]")
    private WebElement zapad;

    @FindBy(id = "counter-button-plus")
    private WebElement counterButtonPlus;

    @FindBy(xpath = "//div[@id=\"swal2-content\"]")
    private WebElement warningSwalContent;

    public EventDetailsPage(WebDriver driver) {
        super(driver);
    }

    public WebElement getReserveButton() { return reserveButton; }

    public WebElement getBuyButton() { return buyButton; }

    public void ensureReserveIsClickable() { ensureElementIsClickable(reserveButton); }

    public void ensureSwalAlertShowsWarning(String text) { ensureElementContainsText(warningSwalContent, text);}

    public void clickDateSelect() { clickElement(dateSelect); }

    public void clickDatum() { clickElement(datum); }

    public void clickSectorSelect() { clickElement(sectorSelect); }

    public void clickZapad() { clickElement(zapad); }

    public void clickPlusButton() { clickElement(counterButtonPlus); }

    public void clickReserveButton() { clickElement(reserveButton); }

    public void clickBuyButton() { clickElement(buyButton); }

}
