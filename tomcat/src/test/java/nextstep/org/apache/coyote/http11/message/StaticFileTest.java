package nextstep.org.apache.coyote.http11.message;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.apache.catalina.exception.file.NoExistFileException;
import org.apache.coyote.http11.message.StaticFile;
import org.junit.jupiter.api.Test;

public class StaticFileTest {

    @Test
    void getBody() {
        final StaticFile staticFile = new StaticFile("invalidFile.html");
        assertThatThrownBy(staticFile::getBody)
                .isInstanceOf(NoExistFileException.class);
    }
}
