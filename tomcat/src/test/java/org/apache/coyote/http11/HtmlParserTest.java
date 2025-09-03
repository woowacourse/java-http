package org.apache.coyote.http11;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


class HtmlParserTest {

    @Test
    void 확장자가_html_이면_파싱_가능하다(){
        HtmlParser htmlParser = new HtmlParser();

        assertAll(
                () -> assertTrue(htmlParser.isParseAble("hello.html")),
                () -> assertFalse(htmlParser.isParseAble("hello.csv"))
        );
    }

    @Test
    void 존재하는_html_파일을_파싱할_수_있다() throws IOException {
        HtmlParser htmlParser = new HtmlParser();

        org.assertj.core.api.Assertions
                .assertThat(htmlParser.parseContent("/index.html"))
                .hasSizeGreaterThan(0);
    }

    @Test
    void 존재하지_않는_html_파일일시_예외가_발생한다(){
        HtmlParser htmlParser = new HtmlParser();

        org.assertj.core.api.Assertions
                .assertThatThrownBy(() -> htmlParser.parseContent("hello.html"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}

