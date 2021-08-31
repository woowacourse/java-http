package nextstep.jwp.domain;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class ResourceTest {

    @ParameterizedTest(name = "파일을 불러와 읽는다.")
    @MethodSource
    void getContent(String uri, String pageTitle) throws IOException {
        //given
        Resource resource = new Resource(uri);
        //when
        String content = resource.getContent();
        //then
        assertThat(content).contains(pageTitle);
    }

    static Stream<Arguments> getContent() {
        return Stream.of(
                Arguments.of("/index.html", "<title>대시보드</title>"),
                Arguments.of("/login.html", "<title>로그인</title>"),
                Arguments.of("/register.html", "<title>회원가입</title>"));
    }

    @ParameterizedTest(name = "Content-Type을 반환한다.")
    @MethodSource
    void getContentType(String uri, String expectedContentType) {
        //given
        Resource resource = new Resource(uri);
        //when
        String actualContentType = resource.getContentType();
        //then
        assertThat(actualContentType).isEqualTo(expectedContentType);
    }

    static Stream<Arguments> getContentType() {
        return Stream.of(
                Arguments.of("/index.html", ContentType.HTML.getContentType()),
                Arguments.of("/css/styles.css", ContentType.CSS.getContentType()),
                Arguments.of("/js/script.js", ContentType.JS.getContentType()),
                Arguments.of("/assets/img/error-404-monochrome.svg", ContentType.IMAGE.getContentType()));
    }
}