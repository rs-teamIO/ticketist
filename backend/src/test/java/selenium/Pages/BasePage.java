package selenium.Pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class BasePage {

    private WebDriver driver;

    public BasePage(WebDriver driver) {
        this.driver = driver;
    }

    protected void ensureIsDisplayed(WebElement element) {
        (new WebDriverWait(driver, 10))
                .until(ExpectedConditions.visibilityOf(element));
    }

    protected void ensureIsDisplayed(By by) {
        (new WebDriverWait(driver, 10))
                .until(ExpectedConditions.presenceOfElementLocated(by));
    }

    protected void ensureTextIsEntered(WebElement element, String text) {
        (new WebDriverWait(driver, 10))
                .until(ExpectedConditions.textToBePresentInElementValue(
                        element, text));
    }

    protected void ensureElementContainsText(WebElement element, String text) {
        (new WebDriverWait(driver, 10))
                .until(ExpectedConditions.textToBePresentInElement(
                        element, text));
    }

    protected void ensureTextToBe(By by, String text) {
        (new WebDriverWait(driver, 10))
                .until(ExpectedConditions.textToBe(by, text));
    }

    protected void ensureNumberOfWindowsEquals(int number) {
        (new WebDriverWait(driver, 10))
                .until(ExpectedConditions.numberOfWindowsToBe(number));
    }

    protected void ensureElementIsClickable(WebElement element) {
        (new WebDriverWait(driver, 10))
                .until(ExpectedConditions.elementToBeClickable(element));
    }

    protected boolean isVisible(By by) {
        try {
            driver.findElement(by);
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    protected void clickElement(WebElement element) {
        ensureIsDisplayed(element);
        element.click();
    }

    protected void clear(WebElement formField) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].value=\"\";", formField);
    }

    protected void enterText(WebElement element, String text) {
        ensureIsDisplayed(element);
        clear(element);
        element.sendKeys(text);
        ensureTextIsEntered(element, text);
    }
}
