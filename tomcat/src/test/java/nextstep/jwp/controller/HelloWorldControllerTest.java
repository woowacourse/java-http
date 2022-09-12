package nextstep.jwp.controller;

import org.apache.http.ContentType;
import org.apache.http.HttpMethod;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.StatusCode;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HelloWorldControllerTest {

    @Test
    @DisplayName("Response로 \"Hello world!\" 를 반환한다.")
    void handleGet() {
        HttpRequest httpRequest = new HttpRequest(HttpMethod.GET, "/", "/", null, null, null);
        HelloWorldController helloWorldController = new HelloWorldController();

        HttpResponse actual = helloWorldController.handleGet(httpRequest);

        HttpResponse expected = HttpResponse.of(StatusCode.OK, ContentType.TEXT_PLAIN, "Hello world!");
        Assertions.assertThat(actual).usingRecursiveComparison()
            .isEqualTo(expected);
    }
}
