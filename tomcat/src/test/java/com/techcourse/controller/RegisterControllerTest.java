package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class RegisterControllerTest {

    @TestFactory
    Stream<DynamicTest> rejectsMissingOrBlankRequiredFields() {
        return Stream.of("account", "password", "email").flatMap(field ->
                Stream.of(null, "", "   ").map(value -> DynamicTest.dynamicTest(
                        field + "=" + (value == null ? "missing" : "[" + value + "]"),
                        () -> assertInvalidRegistration(field, value))));
    }

    private void assertInvalidRegistration(final String field, final String value) throws Exception {
        final Map<String, String> parameters = new HashMap<>(Map.of(
                "account", "invalid-" + field + "-" + (value == null ? "missing" : value.length()),
                "password", "password",
                "email", "test@example.com"));
        if (value == null) {
            parameters.remove(field);
        } else {
            parameters.put(field, value);
        }
        final String body = parameters.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8))
                .collect(java.util.stream.Collectors.joining("&"));
        final String rawRequest = "POST /register HTTP/1.1\r\n"
                + "Content-Type: application/x-www-form-urlencoded\r\n"
                + "Content-Length: " + body.getBytes(StandardCharsets.UTF_8).length + "\r\n\r\n" + body;
        final HttpRequest request = HttpRequest.read(new ByteArrayInputStream(
                rawRequest.getBytes(StandardCharsets.UTF_8))).orElseThrow();
        final var response = new HttpResponse();

        new RegisterController().service(request, response);

        final var output = new ByteArrayOutputStream();
        response.writeTo(output);
        assertThat(output.toString(StandardCharsets.UTF_8))
                .startsWith("HTTP/1.1 400 Bad Request")
                .doesNotContain("Location:");
        final String account = parameters.get("account");
        if (account != null) {
            assertThat(InMemoryUserRepository.findByAccount(account)).isEmpty();
        }
    }
}
