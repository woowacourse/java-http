package org.apache.coyote.http11;

import org.apache.coyote.http11.parser.Http11GetProcessor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;


class Http11GetProcessorTest {

    @Test
    void 존재하는_파일을_파싱할_수_있다() throws IOException {
        Http11GetProcessor http11GetProcessor = new Http11GetProcessor();

        org.assertj.core.api.Assertions
                .assertThat(http11GetProcessor.parse("/index.html")
                        .getParseContent())
                .hasSizeGreaterThan(0);
    }

    @Test
    void 존재하지_않는_파일일시_예외가_발생한다() {
        Http11GetProcessor http11GetProcessor = new Http11GetProcessor();

        org.assertj.core.api.Assertions
                .assertThatThrownBy(() -> http11GetProcessor.parse("GET /hello.html"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void get_요청을_처리할_수_있다() {
        Http11GetProcessor http11GetProcessor = new Http11GetProcessor();

        Assertions.assertDoesNotThrow(() -> http11GetProcessor.parse("/login?account=gugu&password=password"));
    }

    @Test
    void get_요청을_처리중에_실패시_예외가_발생한다() {
        Http11GetProcessor http11GetProcessor = new Http11GetProcessor();

        org.assertj.core.api.Assertions
                .assertThatThrownBy(() -> http11GetProcessor.parse("/login?account=tuda&password=password"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}

