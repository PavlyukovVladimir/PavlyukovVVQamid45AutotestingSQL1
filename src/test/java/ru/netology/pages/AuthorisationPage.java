package ru.netology.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.Keys;
import ru.netology.data.DataHelper;

import java.time.Duration;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.WebDriverConditions.url;


public class AuthorisationPage {
    private final SelenideElement loginElement = $("[data-test-id=login] input");
    private final SelenideElement loginSubElement = $("[data-test-id=login] .input__sub");
    //Поле обязательно для заполнения
    private final SelenideElement passwordElement = $("[data-test-id=password] input");
    private final SelenideElement passwordSubElement = $("[data-test-id=password] .input__sub");
    private final SelenideElement buttonElement = $("[data-test-id=action-login]");
    private final SelenideElement errorNotificationTitleElement = $("[data-test-id=error-notification] .notification__title");
    private final SelenideElement errorNotificationContentElement = $("[data-test-id=error-notification] .notification__content");
    private final SelenideElement h2Element = $("#root h2");
    private final SelenideElement paragraphElement = $("#root p.paragraph");

    public VerificationPage validAuthorisation(@NotNull DataHelper.Auth.Info user) {
        checkAuthorisationPage()    // нахожусь там где ожидаю?
                .fillForm(user)     // заполняю поля
                .clickSubmit();     // отправляю форму
        return new VerificationPage();
    }

    public AuthorisationPage invalidAuthorisation(@NotNull DataHelper.Auth.Info user, @NotNull String regex) {
        return checkAuthorisationPage()     // нахожусь там где ожидаю?
                .fillForm(user)             // заполняю поля
                .clickSubmit()              // отправляю форму
                .checkMessage(regex)        // проверяю что ошибка есть
                .checkAuthorisationPage();  // я еще там где был?
        // Тут бы какой-то таймаут выдержать и проверить что остался на странице... но думаю и так сойдет
    }

    public AuthorisationPage fillForm(@NotNull DataHelper.Auth.Info info) {
        String login = info.getLogin();
        // Проверки на null чтобы не заполнять поля т.к. "ничего" не может ничего заполнять
        if (login != null) {
            loginElement
                    .shouldBe(Condition.visible)
                    .press(Keys.CONTROL, "a", Keys.DELETE)
                    .setValue(login);
        }
        String pass = info.getPassword();
        if (pass != null) {
            passwordElement
                    .shouldBe(Condition.visible)
                    .press(Keys.CONTROL, "a", Keys.DELETE)
                    .setValue(pass);
        }
        return this;
    }

    public AuthorisationPage clickSubmit() {
        buttonElement.shouldBe(Condition.visible).click();
        return this;
    }

    public AuthorisationPage checkMessage(@NotNull String regexp) {
        errorNotificationTitleElement
                .shouldBe(Condition.visible)
                .shouldBe(Condition.text("Ошибка"), Duration.ofSeconds(2));
        errorNotificationContentElement
                .shouldBe(Condition.matchText(regexp), Duration.ofSeconds(2));
        return this;
    }

    public AuthorisationPage checkEmptyFields(boolean isLogin, boolean isPass) {
        if (isLogin) {
            loginSubElement
                    .shouldBe(Condition.visible)
                    .shouldBe(Condition.text("Поле обязательно для заполнения"));
        }
        if (isPass) {
            passwordSubElement
                    .shouldBe(Condition.visible)
                    .shouldBe(Condition.text("Поле обязательно для заполнения"));
        }
        return this;
    }

    public AuthorisationPage checkAuthorisationPage() {
        Selenide.webdriver().shouldHave(url(Configuration.baseUrl + "/"));
        h2Element
                .shouldBe(Condition.visible)
                .shouldBe(Condition.matchText(".*Интернет Банк.*"));
        paragraphElement
                .shouldBe(Condition.visible)
                .shouldBe(Condition.matchText(".*Мы гарантируем безопасность ваших данных.*"));
        return this;
    }
}
