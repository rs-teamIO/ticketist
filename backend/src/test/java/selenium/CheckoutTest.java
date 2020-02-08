package selenium;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.PageFactory;
import selenium.Pages.CheckoutPage;
import selenium.Pages.EventsPage;
import selenium.Pages.LoginPage;
import selenium.Pages.MyReservationsPage;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

public class CheckoutTest {
    private static final String CART_TOTAL = "70.00";

    private WebDriver driver;

    LoginPage loginPage;
    EventsPage eventsPage;
    MyReservationsPage myReservationsPage;
    CheckoutPage checkoutPage;

    @Before
    public void setupSelenium() {
        System.setProperty("webdriver.chrome.driver", "src/test/resources/selenium-webdriver/chromedriver.exe");
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.navigate().to("http://localhost:4200/login");
        loginPage = PageFactory.initElements(driver, LoginPage.class);
        eventsPage = PageFactory.initElements(driver, EventsPage.class);
        myReservationsPage = PageFactory.initElements(driver, MyReservationsPage.class);
        checkoutPage = PageFactory.initElements(driver, CheckoutPage.class);
    }

    @After
    public void closeSelenium() {
        driver.quit();
    }

    @Test
    public void testCheckout() {
        /*
            This test needs to be run when first reservation for 2 EXIT tickets still exists
         */
        getToCheckout();
        testSuccessfulPayment();

    }

    private void getToCheckout() {
        // login
        loginPage.ensureIsDisplayed();
        loginPage.setUsernameInput("user2020");
        loginPage.setPasswordInput("123456");
        loginPage.ensureIsDisplayed();
        loginPage.submitLogin();

        //go to my reservations
        // ToDo ne radi
//        eventsPage.ensureMyReservationsButtonIsDisplayed();
//        eventsPage.navigateToMyReservations();

        //myReservationsPage.payFirstReservationInList();
        eventsPage.ensureEventsAreLoaded();
        driver.navigate().to("http://localhost:4200/checkout/1");
        assertEquals("http://localhost:4200/checkout/1", driver.getCurrentUrl());
    }

    private void testSuccessfulPayment() {
        // right items are to be payed
        checkoutPage.ensureTotalPriceIsDisplayed();
        assertThat("Total price should be 70$", checkoutPage.getTotalPrice().getText().contains(CART_TOTAL));

        // switch to paypal window
        checkoutPage.clickPaypalButton();
        String winHandleBefore = driver.getWindowHandle();
        Set<String> handle= driver.getWindowHandles();
        checkoutPage.ensureNumberOfWindowsIsEqual(2);
        for(String winHandle : driver.getWindowHandles()){
            driver.switchTo().window(winHandle);
        }

        // ToDo enter wrong email

        // enter right credentials
        checkoutPage.enterEmail("sb-st3c6518971@personal.example.com");
        checkoutPage.clickNextPaypalButton();
        checkoutPage.enterPassword("hd*}'c3V");
        checkoutPage.clickLoginPaypalButton();

        // same amount to be payed is in paypal cart
        checkoutPage.ensureCartTotalIsDisplayed();
        assertThat("Cart total should be 70$", checkoutPage.getCartTotal().getText().contains(CART_TOTAL));
        checkoutPage.ensurePaymentSubmitIsClickable();
        checkoutPage.clickPaymentSubmitButton();

        // confirm successful payment
        driver.switchTo().window(winHandleBefore);
        checkoutPage.ensureSwalAlertIsSuccessful("Payment successful!");
        checkoutPage.clickSwalOkButton();

        // redirected to front page
        assertEquals("http://localhost:4200/events", driver.getCurrentUrl());
    }
}
