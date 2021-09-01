package nextstep.jwp.framework.http.response.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class FileUtilTest {

    @DisplayName("파일을 읽어 String으로 반환할 수 있다.")
    @Test
    void read() {
        final String fileName = "static/test.html";
        final ClassLoader classLoader = getClass().getClassLoader();
        final File file = new File(Objects.requireNonNull(classLoader.getResource(fileName)).getFile());

        final String readFile = FileUtil.read(file);

        assertThat(readFile).isEqualTo(
                "<!DOCTYPE html>\r\n" +
                "<html lang=\"en\">\r\n" +
                "<head>\r\n" +
                "    <meta charset=\"UTF-8\">\r\n" +
                "    <title>Title</title>\r\n" +
                "</head>\r\n" +
                "<body>\r\n" +
                "\r\n" +
                "</body>\r\n" +
                "</html>\r\n");
    }
}