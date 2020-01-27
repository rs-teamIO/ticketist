package selenium.Pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;

public class MyReservationsPage {
    private WebDriver driver;

    @FindBy(css = "button.mat-paginator-navigation-previous")
    private WebElement previousPageButton;

    @FindBy(css = "button.mat-paginator-navigation-next")
    private WebElement nextPageButton;

    @FindBy(xpath = "//app-reservation-item[1]/div/div[4]/button[1]")
    private WebElement firstReservationPayNowButton;

    @FindBy(xpath = "//app-reservation-item[1]/div/div[4]/button[2]")
    private WebElement firstReservationCancelButton;

    @FindBy(xpath = "//div[@class=\"outer-container\"]")
    private List<WebElement> reservationsList;

    public MyReservationsPage(WebDriver driver) {
        this.driver = driver;
    }

    public void ensureIsDisplayed() {
        (new WebDriverWait(driver, 10))
                .until(ExpectedConditions.visibilityOf(nextPageButton));
    }

    public void ensureWantedPageLabelIsDisplayed(String pageLabel) {
        (new WebDriverWait(driver, 10))
                .until(ExpectedConditions.textToBe(By.cssSelector("div.mat-paginator-range-label"), pageLabel));
    }

    public WebElement getPreviousPageButton() {
        return previousPageButton;
    }

    public WebElement getNextPageButton() {
        return nextPageButton;
    }

    public void payFirstReservationInList() {
        firstReservationPayNowButton.click();
    }

    public void cancelFirstReservationInList() {
        firstReservationCancelButton.click();
    }

    public void moveToPreviousPage() {
        previousPageButton.click();
    }

    public void moveToNextPage() {
        nextPageButton.click();
    }

    public int getNumberOfReservationsOnPage() {
        return reservationsList.size();
    }
}
