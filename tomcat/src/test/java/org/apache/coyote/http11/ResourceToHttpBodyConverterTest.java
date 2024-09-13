package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import org.apache.coyote.http11.message.HttpHeaderName;
import org.apache.coyote.http11.message.response.HttpResponse;
import org.apache.coyote.http11.message.response.HttpStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ResourceToHttpBodyConverterTest {

    @Test
    @DisplayName("Resource를 HttpResponse로 변환한다.")
    void covertTest() throws IOException {
        // given
        URL resource = getClass().getClassLoader().getResource("static/index.html");
        byte[] body = Files.readAllBytes(new File(resource.getFile()).toPath());

        // when
        HttpResponse response = ResourceToHttpBodyConverter.convert(resource);

        // then
        assertAll(
                () -> assertThat(response.getStatus()).isEqualTo(HttpStatus.OK),
                () -> assertThat(response.getFieldByHeaderName(HttpHeaderName.CONTENT_TYPE))
                        .contains("text/html;charset=utf-8"),
                () -> assertThat(response.getBody().length).isEqualTo(body.length),
                () -> assertThat(response.getBody()).isEqualTo(body)
        );
    }

    @Test
    @DisplayName("Resource가 존재하지 않을 때, 예외를 던진다.")
    void covertWithNullResourceTest() throws IOException {
        // given
        URL resource = getClass().getClassLoader().getResource("static/not-exist.html");

        // when & then
        assertThatThrownBy(() -> ResourceToHttpBodyConverter.convert(resource))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("해당 파일이 존재하지 않습니다.");
    }
}
