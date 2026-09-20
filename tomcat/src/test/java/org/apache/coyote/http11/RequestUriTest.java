package org.apache.coyote.http11;

import org.apache.coyote.http11.request.RequestUri;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class RequestUriTest {
    @Test
    void 쿼리가_없으면_경로만_가진다() {
        final RequestUri uri = RequestUri.from("/index.html");

        assertThat(uri.getPath()).isEqualTo("/index.html");
        assertThat(uri.getQueryParameter("account")).isEmpty();
    }

    @Test
    void 경로와_쿼리_파라미터를_분리한다() {
        final RequestUri uri = RequestUri.from("/login?account=gugu&password=password");

        assertThat(uri.getPath()).isEqualTo("/login");
        assertThat(uri.getQueryParameter("account")).hasValue("gugu");
        assertThat(uri.getQueryParameter("password")).hasValue("password");
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
}
