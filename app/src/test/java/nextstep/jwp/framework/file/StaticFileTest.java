package nextstep.jwp.framework.file;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class StaticFileTest {

    @DisplayName("정적 파일을 바이트 배열로 변환한다.")
    @Test
    void toBytes() {
        // given
        StaticFileFinder staticFileFinder = new StaticFileFinder("test.html");
        StaticFile staticFile = staticFileFinder.find();

        // when
        byte[] bytes = staticFile.toBytes();

        // then
        String htmlText = new String(bytes);
        assertThat(htmlText).contains("test title", "test body");
    }
}
