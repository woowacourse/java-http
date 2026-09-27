package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RegisterControllerTest {

    @Test
    @DisplayName("아이디가 2자 미만이면 회원가입하지 않는다")
    void doesNotRegisterWhenAccountIsShorterThanTwoCharacters() throws Exception {
        assertNotRegistered("a");
    }

    @Test
    @DisplayName("아이디가 20자를 초과하면 회원가입하지 않는다")
    void doesNotRegisterWhenAccountIsLongerThanTwentyCharacters() throws Exception {
        final String account = "account-" + UUID.randomUUID().toString().replace("-", "");
        assertNotRegistered(account);
    }

    @Test
    @DisplayName("아이디가 없으면 회원가입하지 않는다")
    void doesNotRegisterWithoutAccount() throws Exception {
        assertThat(postRegister("password=password&email=user@example.com"))
                .contains("Location: /register?error=registration-failed ");
        assertRejected("account=++&password=password&email=user@example.com", "  ");
    }

    @Test
    @DisplayName("비밀번호가 없거나 공백이면 회원가입하지 않는다")
    void doesNotRegisterWithoutPassword() throws Exception {
        final String missingPasswordAccount = newAccount();
        assertRejected("account=" + missingPasswordAccount + "&email=user@example.com", missingPasswordAccount);

        final String blankPasswordAccount = newAccount();
        assertRejected("account=" + blankPasswordAccount + "&password=++&email=user@example.com", blankPasswordAccount);
    }

    @Test
    @DisplayName("이메일이 없거나 공백이면 회원가입하지 않는다")
    void doesNotRegisterWithoutEmail() throws Exception {
        final String missingEmailAccount = newAccount();
        assertRejected("account=" + missingEmailAccount + "&password=password", missingEmailAccount);

        final String blankEmailAccount = newAccount();
        assertRejected("account=" + blankEmailAccount + "&password=password&email=++", blankEmailAccount);
    }

    @Test
    @DisplayName("이메일 형식이 올바르지 않으면 회원가입하지 않는다")
    void doesNotRegisterWithInvalidEmail() throws Exception {
        final String account = newAccount();
        assertRejected("account=" + account + "&password=password&email=user.example.com", account);

        final String accountWithoutDomainSuffix = newAccount();
        assertRejected("account=" + accountWithoutDomainSuffix + "&password=password&email=user@example",
                accountWithoutDomainSuffix);
    }

    @Test
    @DisplayName("이미 있는 아이디로 가입하면 기존 사용자를 보존한다")
    void doesNotOverwriteExistingAccount() throws Exception {
        final String account = newAccount();
        InMemoryUserRepository.save(new User(account, "original-password", "original@example.com"));

        final String response = postRegister("account=" + account + "&password=new-password&email=new@example.com");

        assertThat(response).contains("Location: /register?error=registration-failed ");
        assertThat(InMemoryUserRepository.findByAccount(account)).hasValueSatisfying(user ->
                assertThat(user.checkPassword("original-password")).isTrue());
    }

    @Test
    @DisplayName("올바른 회원 정보는 저장한다")
    void registersValidUser() throws Exception {
        final String account = newAccount();

        final String response = postRegister("account=" + account
                + "&password=password&email=user@mail.example.com");

        assertThat(response).contains("Location: /index.html ");
        assertThat(InMemoryUserRepository.findByAccount(account)).isPresent();
    }

    @Test
    @DisplayName("아이디 길이가 2자 또는 20자이면 회원가입한다")
    void registersAccountAtLengthLimits() throws Exception {
        assertRegistered("ab");
        assertRegistered("abcdefghijklmnopqrst");
    }

    private void assertRegistered(final String account) throws Exception {
        final String response = postRegister("account=" + account
                + "&password=password&email=user@example.com");
        assertThat(response).contains("Location: /index.html ");
        assertThat(InMemoryUserRepository.findByAccount(account)).isPresent();
    }

    private void assertNotRegistered(final String account) throws Exception {
        final String body = "account=" + account + "&password=password&email=user@example.com";
        assertRejected(body, account);
    }

    private void assertRejected(final String body, final String account) throws Exception {
        assertThat(postRegister(body)).contains("Location: /register?error=registration-failed ");
        assertThat(InMemoryUserRepository.findByAccount(account)).isEmpty();
    }

    private String postRegister(final String body) throws Exception {
        final byte[] requestBytes = createRequest(body);
        final HttpRequest request = new HttpRequest(new ByteArrayInputStream(requestBytes));
        final HttpResponse response = new HttpResponse(request.getHttpVersion());

        new RegisterController().service(request, response);

        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        response.write(output);
        return output.toString(StandardCharsets.UTF_8);
    }

    private String newAccount() {
        return "user" + UUID.randomUUID().toString().replace("-", "").substring(0, 12);
    }

    private byte[] createRequest(final String body) {
        final int contentLength = body.getBytes(StandardCharsets.UTF_8).length;
        final String rawRequest = String.join("\r\n",
                "POST /register HTTP/1.1",
                "Content-Length: " + contentLength,
                "",
                body);
        return rawRequest.getBytes(StandardCharsets.UTF_8);
    }
}
