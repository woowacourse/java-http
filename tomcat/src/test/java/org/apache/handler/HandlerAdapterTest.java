package org.apache.handler;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class HandlerAdapterTest {

    @ParameterizedTest
    @ValueSource(strings = {"/", "/login", "/register"})
    void 요청에_해당하는_핸들러를_반환한다(String url) {
        RequestHandler requestHandler = HandlerAdapter.findRequestHandler(url);

        assertThat(requestHandler).isNotNull();
    }

    @Test
    void 요청에_해당하는_핸들러가_없으면_파일_핸들러를_반환한다() {
        String url = "/notFound";

        RequestHandler requestHandler = HandlerAdapter.findRequestHandler(url);

        assertThat(requestHandler).isInstanceOf(FileHandler.class);
    }
}
