package ru.netology.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import org.jetbrains.annotations.NotNull;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.WebDriverConditions.url;


public class VerificationPage {
    private final SelenideElement codeElement = $("[data-test-id=code] input");
    private final SelenideElement buttonElement = $("[data-test-id=action-verify]");
    private final SelenideElement errorNotificationTitleElement = $("[data-test-id=error-notification] .notification__title");
    private final SelenideElement errorNotificationContentElement = $("[data-test-id=error-notification] .notification__content");
    private final SelenideElement h2Element = $("#root h2");
    private final SelenideElement paragraphElement = $("#root .paragraph");

    public PersonalAccountPage validVerify(String verificationCode) {
        checkVerificationPage()
                .fillForm(verificationCode)
                .clickSubmit();
        return new PersonalAccountPage();
    }

    public VerificationPage invalidVerify(String verificationCode) {
        return checkVerificationPage()
                .fillForm(verificationCode)
                .clickSubmit()
                .checkErrorMessage(".*Неверно указан код! Попробуйте ещё раз\\.$")
                .checkVerificationPage();
    }

    public VerificationPage fillForm(String verificationCode) {
        if (verificationCode != null) codeElement.shouldBe(Condition.visible).setValue(verificationCode);
        return this;
    }

    public VerificationPage clickSubmit() {
        buttonElement.shouldBe(Condition.visible).click();
        return this;
    }

    public VerificationPage checkErrorMessage(@NotNull String regex) {
        errorNotificationTitleElement
                .shouldBe(Condition.visible)
                .shouldBe(Condition.text("Ошибка"));
        // "Неверно указан код! Попробуйте ещё раз."
        errorNotificationContentElement
                .shouldBe(Condition.matchText(regex));
        return this;
    }

    public VerificationPage checkVerificationPage() {
        Selenide.webdriver().shouldHave(url(Configuration.baseUrl + "/verification"));
        h2Element
                .shouldBe(Condition.visible)
                .shouldBe(Condition.matchText(".*Интернет Банк.*"));
        paragraphElement
                .shouldBe(Condition.visible)
                .shouldBe(Condition.matchText(".*Необходимо подтверждение.*"));
        return this;
    }
}
