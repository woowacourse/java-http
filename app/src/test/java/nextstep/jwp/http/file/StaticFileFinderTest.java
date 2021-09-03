package nextstep.jwp.http.file;

import nextstep.jwp.http.exception.HtmlNotFoundException;
import nextstep.jwp.http.exception.StaticFileNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class StaticFileFinderTest {

    @DisplayName("정적 파일 찾기에 성공한다.")
    @Test
    void findExistentFile() {
        StaticFileFinder staticFileReader = new StaticFileFinder("test.html");
        assertThatCode(staticFileReader::find).doesNotThrowAnyException();
    }

    @DisplayName("존재하지 않는 경로의 HTML 파일을 찾으면 예외가 발생한다.")
    @Test
    void findNonExistentHtmlFile() {
        StaticFileFinder staticFileReader = new StaticFileFinder("ggyool.html");
        assertThatThrownBy(staticFileReader::find)
                .isInstanceOf(HtmlNotFoundException.class)
                .hasMessageContaining("HTML 파일을 찾지 못했습니다");
    }

    @DisplayName("존재하지 않는 경로의 HTML 이외의 정적 파일을 찾으면 예외가 발생한다.")
    @Test
    void findNonExistentStaticFile() {
        StaticFileFinder staticFileReader = new StaticFileFinder("ggyool.css");
        assertThatThrownBy(staticFileReader::find)
                .isInstanceOf(StaticFileNotFoundException.class)
                .hasMessageContaining("정적 파일을 찾지 못했습니다");
    }
}
