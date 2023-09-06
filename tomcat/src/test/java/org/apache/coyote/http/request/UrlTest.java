package org.apache.coyote.http.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class UrlTest {

    @Test
    void from_메서드는_url을_전달하면_Url을_초기화한다() {
        final String url = "/login";

        assertThatCode(() -> Url.from(url)).doesNotThrowAnyException();
    }

    @Test
    void matchesByPath_메서드는_path와_rootContextPath를_전달할때_path가_일치하면_true를_반환한다() {
        final String path = "/hello/login";
        final String equalTargetPath = "/login";
        final String contextPath = "/hello";
        final Url url = Url.from(path);

        final boolean actual = url.matchesByPathExcludingRootContextPath(equalTargetPath, contextPath);

        assertThat(actual).isTrue();
    }

    @Test
    void matchesByPath_메서드는_path와_rootContextPath를_전달할때_path가_일치하지_않으면_false를_반환한다() {
        final String path = "/hello/login";
        final String notEqualTargetPath = "/loginabc";
        final String contextPath = "/hello";
        final Url url = Url.from(path);

        final boolean actual = url.matchesByPathExcludingRootContextPath(notEqualTargetPath, contextPath);

        assertThat(actual).isFalse();
    }

    @Test
    void isWelcomePageUrl_메서드는_url이_welcome_page인_경우_true를_반환한다() {
        final String path = "/";
        final String contextPath = "/";
        final Url url = Url.from(path);

        final boolean actual = url.isWelcomePageUrl(contextPath);

        assertThat(actual).isTrue();
    }

    @Test
    void isWelcomePageUrl_메서드는_url이_welcome_page가_아닌_경우_false를_반환한다() {
        final String path = "/login";
        final String contextPath = "/";
        final Url url = Url.from(path);

        final boolean actual = url.isWelcomePageUrl(contextPath);

        assertThat(actual).isFalse();
    }

    @Test
    void isStaticResource_메서드는_url이_정적_리소스_요청인_경우_true를_반환한다() {
        final String path = "/index.html";
        final Url url = Url.from(path);

        final boolean actual = url.isStaticResource();

        assertThat(actual).isTrue();
    }

    @Test
    void isStaticResource_메서드는_url이_정적_리소스_요청이_아닌_경우_false를_반환한다() {
        final String path = "/login";
        final Url url = Url.from(path);

        final boolean actual = url.isStaticResource();

        assertThat(actual).isFalse();
    }

    @Test
    void startsWithRootContextPath_메서드는_path가_rootContextPath로_시작하면_true를_반환한다() {
        final String path = "/login";
        final Url url = Url.from(path);
        final String contextPath = "/";

        final boolean actual = url.startsWithRootContextPath(contextPath);

        assertThat(actual).isTrue();
    }

    @Test
    void startsWithRootContextPath_메서드는_path가_rootContextPath로_시작하지_않으면_false를_반환한다() {
        final String path = "/login";
        final Url url = Url.from(path);
        final String contextPath = "/hello";

        final boolean actual = url.startsWithRootContextPath(contextPath);

        assertThat(actual).isFalse();
    }
}
