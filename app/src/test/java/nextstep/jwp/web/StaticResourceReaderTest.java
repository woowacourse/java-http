package nextstep.jwp.web;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("정적 파일을 읽는 로직을 테스트한다.")
class StaticResourceReaderTest {

    @DisplayName("/css/styles.css 정적 파일 읽는 로직을 테스트한다. - 성공")
    @Test
    void content_stylesCss_Success() throws IOException {
        // given
        String targetResource = "/css/styles.css";
        String expected = readFile(targetResource);

        // when
        String actual = new StaticResourceReader(targetResource).content();

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("404.html 정적 파일 읽는 로직을 테스트한다. - 성공")
    @Test
    void content_404html_Success() throws IOException {
        // given
        String targetResource = "/404.html";
        String expected = readFile(targetResource);

        // when
        String actual = new StaticResourceReader(targetResource).content();

        // then
        assertThat(actual).isEqualTo(expected);
    }

    private String readFile(String path) throws IOException {
        InputStream in = getClass().getResourceAsStream("/static" + path);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));

        StringBuilder sb = new StringBuilder();

        while (bufferedReader.ready()) {
            sb.append(bufferedReader.readLine())
                .append("\n");
        }

        return sb.toString();
    }
}
