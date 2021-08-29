package nextstep.jwp.framework.http.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import nextstep.jwp.framework.http.HttpMethod;
import nextstep.jwp.framework.http.HttpRequest;
import nextstep.jwp.framework.http.HttpVersion;
import nextstep.jwp.framework.http.RequestLine;
import org.assertj.core.api.ThrowableAssert;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

class RequestLineParserTest {

    @Test
    @DisplayName("리퀘스트 라인 파싱 테스트")
    void requestLineParsingTest() throws IOException {

        // given
        String givenLine = "GET / HTTP/1.1";
        HttpParser httpParser = new RequestLineParser(new BufferedReader(new StringReader(givenLine)));

        // when
        final HttpRequest httpRequest = httpParser.parse().buildRequest();

        //then

        final RequestLine requestLine = new RequestLine(HttpMethod.GET, "/", HttpVersion.HTTP_1_1);
        HttpRequest expected = new HttpRequest.Builder().requestLine(requestLine)
                                                        .build();

        assertThat(httpRequest).usingRecursiveComparison().isEqualTo(expected);
    }

    @DisplayName("토큰이 4개 이상 시 예외 발생 테스트")
    @Test
    void manyTokenParsingTest() {

        // given
        String givenLine = "GET / HTTP/1.1 Token";
        HttpParser httpParser = new RequestLineParser(new BufferedReader(new StringReader(givenLine)));

        // when
        ThrowableAssert.ThrowingCallable callable = httpParser::parse;

        //then
        assertThatIllegalArgumentException().isThrownBy(callable);
    }

    @DisplayName("리퀘스트 라인이 없을 경우 예외 발생 테스트")
    @ParameterizedTest
    @ValueSource(strings = {"", " ", "  "})
    void emptyRequestLineTest(String givenLine) {

        // given
        HttpParser httpParser = new RequestLineParser(new BufferedReader(new StringReader(givenLine)));

        // when
        ThrowableAssert.ThrowingCallable callable = httpParser::parse;

        //then
        assertThatIllegalArgumentException().isThrownBy(callable);
    }
}
