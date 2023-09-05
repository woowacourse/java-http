package org.apache.coyote.session;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertAll;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class CookiesTest {

    @Test
    void 쿠키_헤더값을_이용해서_생성에_성공한다() {
        // given
        final String cookieValues = "JSESSIONID=" + UUID.randomUUID();

        // expect
        assertThatCode(() -> Cookies.from(cookieValues))
                .doesNotThrowAnyException();
    }

    @Test
    void 세션_아이디를_이용해서_쿠키_생성에_성공한다() {
        // given
        final String sessionId = UUID.randomUUID().toString();

        // expect
        assertThatCode(() -> Cookies.ofJSessionId(sessionId))
                .doesNotThrowAnyException();
    }

    @Test
    void 쿠키에_있는_모든_키값을_가져온다() {
        // given
        final String cookieValues = "name=testName;title=testTitle;content=testContent";

        // when
        final Cookies cookies = Cookies.from(cookieValues);

        // then
        assertThat(cookies.cookieNames()).containsExactlyInAnyOrder("name", "title", "content");
    }

    @Test
    void 쿠키_키값을_이용해서_쿠키값을_가져온다() {
        // given
        final String cookieValues = "name=testName;title=testTitle;content=testContent";
        final Cookies cookies = Cookies.from(cookieValues);

        // when
        final String name = cookies.getCookieValue("name");
        final String title = cookies.getCookieValue("title");
        final String content = cookies.getCookieValue("content");

        // then
        assertAll(
                () -> assertThat(name).isEqualTo("testName"),
                () -> assertThat(title).isEqualTo("testTitle"),
                () -> assertThat(content).isEqualTo("testContent")

        );
    }
}
