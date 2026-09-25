package org.apache.coyote.http11;

import org.apache.coyote.http11.request.requestline.RequestPath;
import org.apache.coyote.http11.request.requestline.RequestUri;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class RequestUriTest {
    @Test
    void 쿼리가_없으면_경로만_가진다() {
        final RequestUri uri = RequestUri.from("/index.html");

        assertThat(uri.getRequestPath()).isEqualTo(RequestPath.from("/index.html"));
        assertThat(uri.hasQueryParameters()).isFalse();
    }

    @Test
    void 경로와_쿼리_파라미터를_분리한다() {
        final RequestUri uri = RequestUri.from("/login?account=gugu&password=password");

        assertThat(uri.getRequestPath()).isEqualTo(RequestPath.from("/login"));
        assertThat(uri.getQueryParameter("account")).hasValue("gugu");
        assertThat(uri.getQueryParameter("password")).hasValue("password");
    }

    @Test
    void 물음표만_있으면_쿼리가_비어_있다() {
        final RequestUri uri = RequestUri.from("/login?");

        assertThat(uri.getRequestPath()).isEqualTo(RequestPath.from("/login"));
        assertThat(uri.hasQueryParameters()).isFalse();
    }

    @Test
    void 첫_번째_물음표에서만_분리한다() {
        final RequestUri uri = RequestUri.from("/search?q=what?");

        assertThat(uri.getRequestPath()).isEqualTo(RequestPath.from("/search"));
        assertThat(uri.getQueryParameter("q")).hasValue("what?");
    }

    @Test
    void 인코딩된_물음표는_경로의_일부다() {
        final RequestUri uri = RequestUri.from("/a%3Fb?x=1");

        assertThat(uri.getRequestPath()).isEqualTo(RequestPath.from("/a%3Fb"));
        assertThat(uri.getQueryParameter("x")).hasValue("1");
    }

    @Test
    void 값이_없는_파라미터는_빈_문자열이다() {
        final RequestUri uri = RequestUri.from("/login?account=&password");

        assertThat(uri.getQueryParameter("account")).hasValue("");
        assertThat(uri.getQueryParameter("password")).hasValue("");
    }

    @Test
    void 인코딩된_값을_디코딩한다() {
        final RequestUri uri = RequestUri.from("/login?account=%EA%B5%AC%EA%B5%AC");

        assertThat(uri.getQueryParameter("account")).hasValue("구구");
    }

    @Test
    void 쿼리_값의_인코딩된_슬래시는_허용한다() {
        final RequestUri uri = RequestUri.from("/login?redirect=%2Fmypage");

        assertThat(uri.getQueryParameter("redirect")).hasValue("/mypage");
    }
}
