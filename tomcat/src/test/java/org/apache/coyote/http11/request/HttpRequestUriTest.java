package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.net.URI;
import org.apache.coyote.exception.UncheckedHttpException;
import org.apache.coyote.http11.component.FileExtension;
import org.apache.coyote.http11.file.FileDetails;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class HttpRequestUriTest {

    @DisplayName("확장자를 포함한 RequestURI로 파일 상세를 반환한다.")
    @ParameterizedTest
    @ValueSource(strings = {"login.html", "daon.css", "baeky.js"})
    void getFileDetails(String path) {
        //given
        HttpRequestUri httpRequestUri = new HttpRequestUri(URI.create(path));

        //when
        FileDetails fileDetails = httpRequestUri.getFileDetails();

        //then
        int delimiterIndex = path.lastIndexOf('.');
        String expectedFileName = path.substring(0, delimiterIndex);
        FileExtension expectedExtension = FileExtension.from(path.substring(delimiterIndex));

        assertAll(
                () -> assertThat(fileDetails.fileName()).isEqualTo(expectedFileName),
                () -> assertThat(fileDetails.extension()).isEqualTo(expectedExtension)
        );
    }

    @DisplayName("확장자가 없는 RequestURI로 파일 상세를 반환한다.")
    @ParameterizedTest
    @ValueSource(strings = {"/login", "/daon", "/baeky"})
    void getFileDetailsWithNoExtension(String path) {
        //given
        HttpRequestUri httpRequestUri = new HttpRequestUri(URI.create(path));

        //when
        FileDetails fileDetails = httpRequestUri.getFileDetails();

        //then
        assertAll(
                () -> assertThat(fileDetails.fileName()).isEqualTo(path),
                () -> assertThat(fileDetails.extension()).isEqualTo(FileExtension.HTML)
        );
    }

    @DisplayName("루트 경로로 요청하면 메인 페이지에 대한 상세를 반환한다.")
    @Test
    void getFileWithRootPath() {
        //given
        String path = "/";
        String expected = "/index";
        HttpRequestUri httpRequestUri = new HttpRequestUri(URI.create(path));

        //when
        FileDetails fileDetails = httpRequestUri.getFileDetails();

        //then
        assertAll(
                () -> assertThat(fileDetails.fileName()).isEqualTo(expected),
                () -> assertThat(fileDetails.extension()).isEqualTo(FileExtension.HTML)
        );
    }

    @DisplayName("요청 URI가 파일 확장자 구분자로 끝나면 예외가 발생한다.")
    @Test
    void getFileDetailsEndsWithDelimiter() {
        //given
        String path = "asmznxcvasdf.";
        HttpRequestUri httpRequestUri = new HttpRequestUri(URI.create(path));

        //when //then
        assertThatThrownBy(httpRequestUri::getFileDetails)
                .isInstanceOf(UncheckedHttpException.class)
                .hasMessageContaining("요청 URI는 확장자 구분자으로 끝날 수 없습니다.");
    }
}
