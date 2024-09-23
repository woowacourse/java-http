package org.apache.coyote.controller;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;
import static utils.TestFixtures.GET;
import static utils.TestFixtures.buildHttpRequest;

class RequestMappingTest {

    @CsvSource({"/login, /login.html", "/register, /register.html", "/fake, /404.html"})
    @ParameterizedTest
    @DisplayName("요청에 일치하는 컨트롤러로 GET 요청을 처리한다.")
    void getMapping(String path, String location) throws Exception {
        // given
        final HttpRequest httpRequest = buildHttpRequest(GET, path, "");
        RequestMapping requestMapping = RequestMapping.getInstance();

        // when
        HttpResponse httpResponse = requestMapping.dispatch(httpRequest);

        // then
        assertThat(httpResponse.getResponseHeader()).containsEntry("Location", location);
    }
}
