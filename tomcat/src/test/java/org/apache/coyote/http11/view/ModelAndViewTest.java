package org.apache.coyote.http11.view;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.coyote.http11.handler.ApiHandler.ApiHandlerResponse;
import org.apache.coyote.http11.handler.FileHandler.FileHandlerResponse;
import org.apache.coyote.http11.httpmessage.ContentType;
import org.apache.coyote.http11.httpmessage.request.Headers;
import org.apache.coyote.http11.httpmessage.response.HttpStatus;
import org.junit.jupiter.api.Test;

class ModelAndViewTest {

    @Test
    void ApiHandlerResponse를_전달받았을_때_상태가_FOUND라면_ApiHandlerResponse내부_값을_그대로_사용한다() throws IOException {
        // given
        ApiHandlerResponse response = ApiHandlerResponse.of(HttpStatus.FOUND, Map.of(), "/index.html",
                ContentType.HTML);

        // when
        ModelAndView modelAndView = ModelAndView.of(response);

        // then
        assertThat(modelAndView).extracting("httpStatus", "headers", "view", "contentType")
                .containsExactly(HttpStatus.FOUND, new Headers(Map.of()), "/index.html", ContentType.HTML);
    }

    @Test
    void ApiHandlerResponse를_전달받았을_때_FOUND가_아니고_body에_빈_값이_존재하면_ApiHandlerResponse내부_값을_그대로_사용한다()
            throws IOException {
        // given
        ApiHandlerResponse response = ApiHandlerResponse.of(HttpStatus.OK, Map.of(), "", ContentType.HTML);

        // when
        ModelAndView modelAndView = ModelAndView.of(response);

        // then
        assertThat(modelAndView).extracting("httpStatus", "headers", "view", "contentType")
                .containsExactly(HttpStatus.OK, new Headers(Map.of()), "", ContentType.HTML);
    }


    @Test
    void ApiHandlerResponse를_전달받았을_때_FOUND가_아니고_body에_파일이_존재하지않으면_ApiHandlerResponse내부_값을_그대로_사용한다()
            throws IOException {
        // given
        ApiHandlerResponse response = ApiHandlerResponse.of(HttpStatus.OK, Map.of(), "hello world!", ContentType.HTML);

        // when
        ModelAndView modelAndView = ModelAndView.of(response);

        // then
        assertThat(modelAndView).extracting("httpStatus", "headers", "view", "contentType")
                .containsExactly(HttpStatus.OK, new Headers(Map.of()), "hello world!", ContentType.HTML);
    }

    @Test
    void ApiHandlerResponse를_전달받았을_때_FOUND가_아니고_body에_파일이_존재하면_파일을_읽어온다() throws IOException {
        // given
        ApiHandlerResponse response = ApiHandlerResponse.of(HttpStatus.OK, Map.of(), "/index.html", ContentType.HTML);

        // when
        ModelAndView modelAndView = ModelAndView.of(response);

        // then
        URL resource = getClass().getClassLoader().getResource("static/index.html");
        String expect = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(modelAndView).extracting("httpStatus", "headers", "view", "contentType")
                .containsExactly(HttpStatus.OK, new Headers(Map.of()), expect, ContentType.HTML);
    }

    @Test
    void FileHandlerResponse를_전달받았을_때_body내부의_파일이_빈값이면_404_html을_반환한다() throws IOException {
        // given
        FileHandlerResponse response = new FileHandlerResponse(HttpStatus.OK, "");

        // when
        ModelAndView modelAndView = ModelAndView.of(response);

        // then
        assertThat(modelAndView).usingRecursiveComparison()
                .isEqualTo(ModelAndView.of(new FileHandlerResponse(HttpStatus.NOT_FOUND, "/404.html")));
    }

    @Test
    void FileHandlerResponse를_전달받았을_때_body내부의_파일이_존재하지않는_피일이면_404_html을_반환한다() throws IOException {
        // given
        FileHandlerResponse response = new FileHandlerResponse(HttpStatus.OK, "/invalidFile.html");

        // when
        ModelAndView modelAndView = ModelAndView.of(response);

        // then
        assertThat(modelAndView).usingRecursiveComparison()
                .isEqualTo(ModelAndView.of(new FileHandlerResponse(HttpStatus.NOT_FOUND, "/404.html")));
    }

    @Test
    void FileHandlerResponse를_전달받았을_때_body내부의_파일이_존재하는_피일이면_파일내용을_반환한다() throws IOException {
        // given
        FileHandlerResponse response = new FileHandlerResponse(HttpStatus.OK, "/index.html");

        // when
        ModelAndView modelAndView = ModelAndView.of(response);

        // then
        URL resource = getClass().getClassLoader().getResource("static/index.html");
        String expect = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(modelAndView).extracting("httpStatus", "headers", "view", "contentType")
                .containsExactly(HttpStatus.OK, new Headers(new LinkedHashMap<>()), expect, ContentType.HTML);
    }
}
