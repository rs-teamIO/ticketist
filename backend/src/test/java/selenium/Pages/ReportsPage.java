package selenium.Pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class ReportsPage extends BasePage {

    @FindBy(xpath = "//app-report-chart//ngx-charts-legend")
    private WebElement chartLegend;

    @FindBy(xpath = "//ul[@class=\"legend-labels\"]/li[1]//span[contains(text(), 'Stark Arena')]")
    private WebElement starkArenaButton;

    @FindBy(id = "all-venues-button")
    private WebElement allVenuesButton;

    @FindBy(xpath = "//table")
    private WebElement eventsTable;

    @FindBy(xpath = "//td[contains(@class, 'event-name-td')]")
    private WebElement eventNameTd;

    public ReportsPage(WebDriver driver) { super(driver); }


    public void ensureChartLegendIsDisplayed() { ensureIsDisplayed(chartLegend); }

    public void ensureEventsTableIsDisplayed() { ensureIsDisplayed(eventsTable); }

    public void ensureTableDataIsDisplayed() { ensureIsDisplayed(eventNameTd); }

    public void clickOnFirstVenueName() { clickElement(starkArenaButton); }

    public void clickOnAllVenuesButton() { clickElement(allVenuesButton); }
}
