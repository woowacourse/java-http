package org.apache.coyote.http11.response.factory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.NoSuchElementException;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.element.HttpMethod;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class StaticResponseFactoryTest {

    @Test
    @DisplayName("URL 에 맞는 정적리소스를 매핑한다.")
    void getResponse() {
        StaticResponseFactory factory = new StaticResponseFactory();
        HttpResponse response = factory.getResponse(HttpMethod.GET, "/index.html");
        String bodyContext = response.getBody().getBodyContext();
        assertThat(bodyContext).contains("<!DOCTYPE html>");
    }

    @Test
    @DisplayName("URL 에 맞는 정적리소스가 없을 시 예외 발생한다.")
    void getResponse_Exception() {
        StaticResponseFactory factory = new StaticResponseFactory();
        assertThatThrownBy(() -> factory.getResponse(HttpMethod.GET, "/asdf"))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    @DisplayName("Files.probeContentType 학습테스트")
    void probe() {
        assertThat(probeContentType("/index.html")).isEqualTo("text/html");
        assertThat(probeContentType("/css/styles.css")).isEqualTo("text/css");
        assertThat(probeContentType("/assets/chart-area.js")).isEqualTo("text/javascript");
        assertThat(probeContentType("/awrhg")).isEqualTo(null);
    }

    private String probeContentType(String url) {
        try {
            return Files.probeContentType(Path.of(url));
        } catch (IOException e) {
            return "";
        }
    }
}
