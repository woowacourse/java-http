package org.apache.coyote.http11.response;

import static org.assertj.core.api.Assertions.assertThat;

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

        String expectedHeader = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: 35 ",
                "");

        String actual = StaticFileResponseUtils.createResponse(filePath);

        assertThat(actual).contains(expectedHeader);
    }

    @Test
    void createResponseTest_whenLoadCss() {
        String filePath = "/test.css";

        String expectedHeader = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/css;charset=utf-8 ",
                "Content-Length: 19 ",
                "");

        String actual = StaticFileResponseUtils.createResponse(filePath);

        assertThat(actual).contains(expectedHeader);
    }
}
