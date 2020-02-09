package selenium.Pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class VenuesPage {
    private WebDriver driver;

    @FindBy(xpath = "//*[@id=\"table\"]/tr[2]/td[1]")
    private WebElement name;

    @FindBy(xpath = "//*[@id=\"table\"]/tr[2]/td[5]")
    private WebElement status;

    @FindBy(xpath = "//*[@id=\"table\"]/tr[2]/td[6]")
    private WebElement statusButton;

    @FindBy(className = "mat-raised-button")
    private WebElement newButton;

    @FindBy(xpath = "//*[@id=\"table\"]/tr[2]/td[4]")
    private WebElement viewButton;

    @FindBy(xpath = "/html/body/app-root/app-venue-list/div/div/mat-paginator/div/div/div[2]/button[2]")
    private WebElement nextPage;


    @FindBy(xpath = "/html/body/app-root/app-venue-list/div/div/mat-paginator/div/div/div[2]/button[1]")
    private WebElement previousPage;

    public VenuesPage(WebDriver webDriver) { this.driver = webDriver; }

    public WebElement getName() { return this.name; }

    public WebElement getStatus() { return this.status; }

    public WebElement getStatusButton() { return this.statusButton; }

    public WebElement getNewButton() { return this.newButton; }

    public WebElement getViewButton() { return this.viewButton; }

    public WebElement getNextPage() { return this.nextPage; }

    public WebElement getPreviousPage() { return this.previousPage; }

    public void ensureIsDisplayed() {
        (new WebDriverWait(driver, 10))
                .until(ExpectedConditions.visibilityOf(newButton));
    }

    public void ensureIsDisplayed2() {
        (new WebDriverWait(driver, 10))
                .until(ExpectedConditions.visibilityOf(this.viewButton));
    }

    public void pressStatus(){
        WebElement el = getStatusButton();
        el.click();
    }

    public void pressNewButton(){
        WebElement el = getNewButton();
        el.click();
    }

    public void pressNextPage(){
        WebElement el = getNextPage();
        el.click();
    }

    public void pressPreviousPage(){
        WebElement el = getPreviousPage();
        el.click();
    }

    public void pressView(){
        WebElement el = getViewButton();
        el.click();
    }

    public void ensureStatusChanged(String text) {
        (new WebDriverWait(driver, 10))
                .until(ExpectedConditions.textToBe(By.xpath("//*[@id=\"table\"]/tr[2]/td[5]"),text));
    }
}
