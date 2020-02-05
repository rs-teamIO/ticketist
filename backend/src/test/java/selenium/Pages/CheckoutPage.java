package selenium.Pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class CheckoutPage extends BasePage {

    @FindBy(id = "paypal")
    private WebElement paypalButton;

    @FindBy(xpath = "//mat-card-actions/p/b")
    private WebElement totalPrice;

    @FindBy(name = "login_email")
    private WebElement emailInput;

    @FindBy(name = "login_password")
    private WebElement passwordInput;

    @FindBy(name = "btnNext")
    private WebElement nextPaypalButton;

    @FindBy(name = "btnLogin")
    private WebElement loginPaypalButton;

    @FindBy(xpath = "//span[@data-testid=\"header-cart-total\"]")
    private WebElement cartTotal;

    @FindBy(name = "payment-submit-btn")
    private WebElement paymentSubmitButton;

    @FindBy(xpath = "//h2[@id=\"swal2-title\"]")
    private WebElement swalTitle;

    @FindBy(xpath = "//div[@class=\"swal2-actions\"]/button")
    private WebElement swalOkButton;

    public CheckoutPage(WebDriver driver) { super(driver); }

    public WebElement getTotalPrice() { return totalPrice;}
    public WebElement getCartTotal() { return cartTotal; }
    public WebElement getPaymentSubmitButton() { return paymentSubmitButton; }

    public void ensureTotalPriceIsDisplayed() { ensureIsDisplayed(totalPrice); }
    public void ensureCartTotalIsDisplayed() { ensureIsDisplayed(cartTotal); }

    public void clickPaypalButton() { clickElement(paypalButton); }

    public void clickNextPaypalButton() { clickElement(nextPaypalButton); }

    public void clickPaymentSubmitButton() { clickElement(paymentSubmitButton); }

    public void clickSwalOkButton() { clickElement(swalOkButton);}

    public void clickLoginPaypalButton() { clickElement(loginPaypalButton); }

    public void ensureNumberOfWindowsIsEqual(int number) {
        ensureNumberOfWindowsEquals(number);
    }

    public void ensurePaymentSubmitIsClickable() { ensureElementIsClickable(paymentSubmitButton);}

    public void ensureSwalAlertIsSuccessful(String text) { ensureElementContainsText(swalTitle, text);}

    public void enterEmail(String text) { enterText(emailInput, text); }

    public void enterPassword(String text) { enterText(passwordInput, text); }


}
