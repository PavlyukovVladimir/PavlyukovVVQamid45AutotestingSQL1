package ru.netology.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.WebDriverConditions.url;


public class PersonalAccountPage {
    private final SelenideElement h2Element = $("h2[data-test-id=dashboard]");

    public PersonalAccountPage checkPersonalAccount() {
        Selenide.webdriver().shouldHave(url(Configuration.baseUrl + "/dashboard"));
        h2Element
                .shouldBe(Condition.visible)
                .shouldBe(Condition.matchText(".*Личный.кабинет.*"));
        return this;
    }
}
