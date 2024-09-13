package com.techcourse.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import org.apache.coyote.http11.Http11ResourceFinder;
import org.apache.coyote.http11.request.Http11Method;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.Http11StatusCode;
import org.apache.coyote.http11.response.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class StaticResourceControllerTest {

    private final StaticResourceController staticResourceController = new StaticResourceController();

    @Test
    @DisplayName("없는 페이지 조회 시 404 페이지로 이동")
    void doGet() throws IOException {
        HttpRequest request = makeRequest("/afdsadasd", new LinkedHashMap<>());
        HttpResponse response = new HttpResponse();

        staticResourceController.doGet(request, response);

        String responseAsString = new String(response.toBytes());
        Http11ResourceFinder resourceFinder = new Http11ResourceFinder();
        Path path = resourceFinder.find("/404.html");
        String expected = new String(Files.readAllBytes(path));

        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(Http11StatusCode.NOT_FOUND),
                () -> assertThat(responseAsString).contains(expected)
        );
    }

    private HttpRequest makeRequest(String uri, LinkedHashMap<String, String> body) {
        return new HttpRequest(
                Http11Method.GET,
                uri,
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                body
        );
    }
}
