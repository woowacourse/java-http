package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ViewResolverTest {

    @DisplayName("파일명으로 페이지를 찾아 반환한다.")
    @Test
    void resolveViewNameSuccess() throws IOException {
        String viewName = "/login.html";

        String actual = ViewResolver.getInstance().resolveViewName(viewName);

        URL resource = getClass().getClassLoader().getResource("static/login.html");
        String expected = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("일치하는 파일명이 없으면 404 페이지를 반환한다.")
    @Test
    void resolveViewNameFailure() throws IOException {
        String viewName = "/wrong-view-name.html";

        String actual = ViewResolver.getInstance().resolveViewName(viewName);

        URL resource = getClass().getClassLoader().getResource("static/404.html");
        String expected = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(actual).isEqualTo(expected);
    }
}
