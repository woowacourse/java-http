package org.apache.coyote.http11.response;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.junit.jupiter.api.Test;

class StaticFileResponseUtilsTest {

    @Test
    void isExistFileTest_whenFileExist_returnTrue() {
        String filePath = "/test.html";

        boolean actual = StaticFileResponseUtils.isExistFile(filePath);

        assertThat(actual).isTrue();
    }

    @Test
    void isExistFileTest_whenFileNotExist_returnFalse() {
        String filePath = "/not-exist.html";

        boolean actual = StaticFileResponseUtils.isExistFile(filePath);

        assertThat(actual).isFalse();
    }

    @Test
    void createResponseTest_whenLoadHtml() {
        String filePath = "/test.html";
        int expectedStatus = 200;
        Map<String, String> expectedHeaders = Map.of(
                "Content-Type", "text/html;charset=utf-8",
                "Content-Length", "35");

        HttpResponse actual = StaticFileResponseUtils.createResponse(filePath);

        assertThat(actual.getStatusCode()).isEqualTo(expectedStatus);
        assertThat(actual.getHeaders()).isEqualTo(expectedHeaders);
    }

    @Test
    void createResponseTest_whenLoadCss() {
        String filePath = "/test.css";
        int expectedStatus = 200;
        Map<String, String> expectedHeaders = Map.of(
                "Content-Type", "text/css;charset=utf-8",
                "Content-Length", "19");

        HttpResponse actual = StaticFileResponseUtils.createResponse(filePath);

        assertThat(actual.getStatusCode()).isEqualTo(expectedStatus);
        assertThat(actual.getHeaders()).isEqualTo(expectedHeaders);
    }
}
