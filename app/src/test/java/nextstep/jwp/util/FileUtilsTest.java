package nextstep.jwp.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

class FileUtilsTest {

    @DisplayName("파일을 정상적으로 읽어온다.")
    @Test
    void readFileOfUrl() throws IOException {
        // given
        String indexBody = FileUtils.readFileOfUrl("/index.html");
        String loginBody = FileUtils.readFileOfUrl("/login.html");
        String registerBody = FileUtils.readFileOfUrl("/register.html");
        String unAuthorizedBody = FileUtils.readFileOfUrl("/401.html");
        String notFoundBody = FileUtils.readFileOfUrl("/404.html");
        String serverErrorBody = FileUtils.readFileOfUrl("/500.html");

        // then
        assertThat(indexBody).isNotEmpty();
        assertThat(loginBody).isNotEmpty();
        assertThat(registerBody).isNotEmpty();
        assertThat(unAuthorizedBody).isNotEmpty();
        assertThat(notFoundBody).isNotEmpty();
        assertThat(serverErrorBody).isNotEmpty();
    }

    @DisplayName("url의 절대 경로를 검색한다.")
    @Test
    void getAbsolutePath() {
        String absolutePath = FileUtils.getAbsolutePath("/index.html");
        String staticPath = getClass().getClassLoader().getResource("static").getFile();
        assertThat(absolutePath).isEqualTo(staticPath + "/index.html");
    }

    @DisplayName("static 폴더 하위에 존재하는 파일인지 확인한다.")
    @Test
    void isStaticFile() {
        boolean svgIsStatic = FileUtils.isStaticFile("/assets/img/error-404-monochrome.svg");
        boolean cssIsStatic = FileUtils.isStaticFile("/css/styles.css");
        boolean jsIsStatic = FileUtils.isStaticFile("/js/scripts.js");
        boolean icoIsStatic = FileUtils.isStaticFile("/favicon.ico");
        boolean htmlIsStatic = FileUtils.isStaticFile("/login.html");
        assertThat(svgIsStatic).isTrue();
        assertThat(cssIsStatic).isTrue();
        assertThat(jsIsStatic).isTrue();
        assertThat(icoIsStatic).isTrue();
        assertThat(htmlIsStatic).isTrue();
    }

    @DisplayName("절대 경로로부터 상대 경로를 반환한다. (static을 기준으로 이후의 경로 ==> 'static/~')")
    @Test
    void getRelativePath() {
        String svgAbsolutePath = FileUtils.getAbsolutePath("/assets/img/error-404-monochrome.svg");
        String cssAbsolutePath = FileUtils.getAbsolutePath("/css/styles.css");
        String jsAbsolutePath = FileUtils.getAbsolutePath("/js/scripts.js");
        String icoAbsolutePath = FileUtils.getAbsolutePath("/favicon.ico");
        String htmlAbsolutePath = FileUtils.getAbsolutePath("/login.html");
        assertThat(FileUtils.getRelativePath(svgAbsolutePath)).isEqualTo("/assets/img/error-404-monochrome.svg");
        assertThat(FileUtils.getRelativePath(cssAbsolutePath)).isEqualTo("/css/styles.css");
        assertThat(FileUtils.getRelativePath(jsAbsolutePath)).isEqualTo("/js/scripts.js");
        assertThat(FileUtils.getRelativePath(icoAbsolutePath)).isEqualTo("/favicon.ico");
        assertThat(FileUtils.getRelativePath(htmlAbsolutePath)).isEqualTo("/login.html");
    }
}