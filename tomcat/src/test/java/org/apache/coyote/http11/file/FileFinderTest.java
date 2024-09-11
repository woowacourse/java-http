package org.apache.coyote.http11.file;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Objects;
import org.apache.coyote.exception.UncheckedHttpException;
import org.apache.coyote.http11.component.FileExtension;
import org.apache.coyote.http11.component.HttpHeaders;
import org.apache.coyote.http11.fixture.HttpRequestFixture;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class FileFinderTest {

    @DisplayName("응답 본문에 요쳥 라인의 경로를 참고하여 정적 파일 내용을 추가한다.")
    @Test
    void resolve() throws IOException {
        //given
        HttpRequest request = HttpRequestFixture.getGetRequest("/login");
        HttpResponse response = new HttpResponse(request);
        FileFinder fileFinder = new FileFinder(request);

        //when
        fileFinder.resolve(response);

        //then
        URL resource = getClass().getClassLoader().getResource("static/login.html");
        File file = new File(Objects.requireNonNull(resource).getFile());
        String expected = new String(Files.readAllBytes(file.toPath()));
        assertAll(
                () -> assertThat(response.getBody()).isEqualTo(expected),
                () -> assertThat(response.getHeaders())
                        .containsEntry(HttpHeaders.CONTENT_TYPE, FileExtension.HTML.getMediaType()),
                () -> assertThat(response.getHeaders())
                        .containsEntry(HttpHeaders.CONTENT_LENGTH, String.valueOf(expected.getBytes().length))
        );
    }

    @DisplayName("요청 경로와 동일한 정적 파일이 존재하지 않으면 예외가 발생한다.")
    @Test
    void resolveThrowsException() {
        //given
        HttpRequest request = HttpRequestFixture.getGetRequest("/아무거나");
        HttpResponse response = new HttpResponse(request);
        FileFinder fileFinder = new FileFinder(request);

        //when //then
        assertThatThrownBy(() -> fileFinder.resolve(response))
                .isInstanceOf(UncheckedHttpException.class)
                .hasMessageContaining("해당 경로에 파일이 존재하지 않습니다.");
    }
}
