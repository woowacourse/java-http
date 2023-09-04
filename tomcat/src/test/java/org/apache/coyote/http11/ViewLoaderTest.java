package org.apache.coyote.http11;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ViewLoaderTest {

    @DisplayName("view 이름에 맞는 파일을 읽고 반환한다.")
    @Test
    void from() {
        //given
        final String html = ViewLoader.from("/index.html");

        //then
        Assertions.assertThat(html).contains("<!DOCTYPE html>\n"
                + "<html lang=\"en\">\n"
                + "    <head>\n"
                + "        <meta charset=\"utf-8\" />\n"
                + "        <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\" />\n"
                + "        <meta name=\"viewport\" content=\"width=device-width, initial-scale=1, shrink-to-fit=no\" />\n"
                + "        <meta name=\"description\" content=\"\" />\n"
                + "        <meta name=\"author\" content=\"\" />\n"
                + "        <title>대시보드</title>\n"
                + "        <link href=\"css/styles.css\" rel=\"stylesheet\" />\n"
                + "        <script src=\"https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.3/js/all.min.js\" crossorigin=\"anonymous\"></script>\n"
                + "    </head>");
    }

    @DisplayName("view에 맞는 파일이 없으면 404 페이지를 반환한다.")
    @Test
    void from_no_file() {
        //given
        final String html = ViewLoader.from("sdsdsdsd.html");

        //then
        Assertions.assertThat(html).contains("<!DOCTYPE html>\n"
                + "<html lang=\"en\">\n"
                + "    <head>\n"
                + "        <meta charset=\"utf-8\" />\n"
                + "        <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\" />\n"
                + "        <meta name=\"viewport\" content=\"width=device-width, initial-scale=1, shrink-to-fit=no\" />\n"
                + "        <meta name=\"description\" content=\"\" />\n"
                + "        <meta name=\"author\" content=\"\" />\n"
                + "        <title>404 Error - SB Admin</title>\n"
                + "        <link href=\"css/styles.css\" rel=\"stylesheet\" />\n"
                + "        <script src=\"https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.3/js/all.min.js\" crossorigin=\"anonymous\"></script>\n"
                + "    </head>");
    }
}
