package ru.netology;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.junit5.ScreenShooterExtension;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.netology.data.DataHelper.Exec.DBContainerControl;
import ru.netology.data.DataHelper.Exec.JarControl;
import ru.netology.pages.AuthorisationPage;

import static ru.netology.data.DataHelper.Auth.*;
import static ru.netology.data.DataHelper.Verify.getVerificationCode;


@ExtendWith({ScreenShooterExtension.class})
public class AuthTest {
    private static final boolean base_auto_create = true;
    private static final boolean base_auto_drop = true;
    private AuthorisationPage page;
    private static JarControl jarControl;

    @BeforeAll
    public static void allStart() {
        if (base_auto_create) {
            DBContainerControl.start();
            jarControl = new JarControl();
            jarControl.start();
        }
    }

    @AfterAll
    public static void allStop() {
        if (base_auto_drop) {
            jarControl.stop();
            DBContainerControl.stop();
        }
    }

    @BeforeEach
    void setUp() {
        Configuration.browser = "chrome";
        Configuration.baseUrl = "http://localhost:9999";
        Configuration.holdBrowserOpen = true;  // false не оставляет браузер открытым по завершению теста
        Configuration.reportsFolder = "build/reports/tests/test/screenshoots";
        Selenide.open("");
        page = new AuthorisationPage();
    }

    @DisplayName("Активен, креды верны")
    @Test
    void activeUserTest() {
        Info info = setCredentials(
                getAuthorisationInfo(), AuthStatuses.active);
        page
                .validAuthorisation(info)
                .validVerify(getVerificationCode(info))
                .checkPersonalAccount();
    }

    @DisplayName("Активен, все креды не верны")
    @Test
    void activeBadCredentialsTest() {
        page
                .invalidAuthorisation(
                        breakCredentials(
                                setCredentials(getAuthorisationInfo(), AuthStatuses.active),
                                BreakCredentialsType.BOTH
                        ),
                        ".*Неверно указан логин или пароль.*"
                );
    }

    @DisplayName("Заблокирован, креды верны")
    @Test
    void blockedUserTest() {
        page
                .invalidAuthorisation(
                        setCredentials(getAuthorisationInfo(), AuthStatuses.blocked),
                        ".*Пользователь заблокирован.*"
                );
    }

    @DisplayName("Заблокирован, все креды не верны")
    @Test
    void blockedBadCredentialsTest() {
        page
                .invalidAuthorisation(
                        breakCredentials(
                                setCredentials(getAuthorisationInfo(), AuthStatuses.blocked),
                                BreakCredentialsType.BOTH
                        ),
                        ".*Неверно указан логин или пароль.*"
                );
    }

    @DisplayName("Неизвестный пользователь")
    @Test
    void unknownUserTest() {
        page
                .invalidAuthorisation(
                        getAuthorisationInfo(),
                        ".*Неверно указан логин или пароль.*"
                );
    }

    @DisplayName("Активен, только пароль не верный")
    @Test
    void activeUserBadPassTest() {
        page
                .invalidAuthorisation(
                        breakCredentials(
                                setCredentials(getAuthorisationInfo(), AuthStatuses.active),
                                BreakCredentialsType.PASSWORD
                        ),
                        ".*Неверно указан логин или пароль.*"
                );
    }

    @DisplayName("Активен, только логин не верный")
    @Test
    void activeUserBadLoginTest() {
        page
                .invalidAuthorisation(
                        breakCredentials(
                                setCredentials(getAuthorisationInfo(), AuthStatuses.active),
                                BreakCredentialsType.LOGIN
                        ),
                        ".*Неверно указан логин или пароль.*"
                );
    }

    @DisplayName("Заблокирован, только пароль не верный")
    @Test
    void blockedUserBadPassTest() {
        page
                .invalidAuthorisation(
                        breakCredentials(
                                setCredentials(getAuthorisationInfo(), AuthStatuses.blocked),
                                BreakCredentialsType.PASSWORD
                        ),
                        ".*Неверно указан логин или пароль.*"
                );
    }

    @DisplayName("Заблокирован, только логин не верный")
    @Test
    void blockedUserBadLoginTest() {
        page
                .invalidAuthorisation(
                        breakCredentials(
                                setCredentials(getAuthorisationInfo(), AuthStatuses.blocked),
                                BreakCredentialsType.LOGIN
                        ),
                        ".*Неверно указан логин или пароль.*"
                );
    }

    @DisplayName("3 раза пробуем зайти с неверным паролем")
    @Test
    void activeUserBadMultiLoginTest() {
        Info info =
                setCredentials(getAuthorisationInfo(), AuthStatuses.active);

        for (int i = 0; i < 3; i++) {
            page
                    .invalidAuthorisation(
                            breakCredentials(
                                    info,
                                    BreakCredentialsType.PASSWORD
                            ),
                            ".*Неверно указан логин или пароль.*"
                    );
            Selenide.sleep(2000);
        }

        page.invalidAuthorisation(
                info,
                ".*Пользователь заблокирован.*"
        );
    }

    @DisplayName("Пробуем отправить пустую форму")
    @Test
    void emptyFormLoginTest() {
        page
                .clickSubmit()
                .checkEmptyFields(true, true);
    }

}
