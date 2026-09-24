package org.apache.coyote.http11;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RequestParametersTest {

    private static final String FORM_URLENCODED = "application/x-www-form-urlencoded";

    @Test
    void 쿼리_스트링의_파라미터를_읽는다() {
        final RequestParameters parameters = RequestParameters.of("account=gugu&password=password", null, "");

        assertThat(parameters.getParameter("account")).isEqualTo("gugu");
        assertThat(parameters.getParameter("password")).isEqualTo("password");
    }

    @Test
    void 폼_본문의_파라미터를_읽는다() {
        final RequestParameters parameters = RequestParameters.of("", FORM_URLENCODED, "account=gugu&password=password");

        assertThat(parameters.getParameter("account")).isEqualTo("gugu");
        assertThat(parameters.getParameter("password")).isEqualTo("password");
    }

    @Test
    void Content_Type에_charset이_붙어도_폼으로_읽는다() {
        final RequestParameters parameters = RequestParameters.of(
                "", FORM_URLENCODED + "; charset=UTF-8", "account=gugu");

        assertThat(parameters.getParameter("account")).isEqualTo("gugu");
    }

    @Test
    void 폼_형식이_아니면_본문을_파라미터로_읽지_않는다() {
        final RequestParameters parameters = RequestParameters.of("", "application/json", "account=gugu");

        assertThat(parameters.getParameter("account")).isNull();
    }

    @Test
    void 같은_이름이_쿼리와_본문에_있으면_쿼리를_우선한다() {
        final RequestParameters parameters = RequestParameters.of("account=admin", FORM_URLENCODED, "account=gugu");

        assertThat(parameters.getParameter("account")).isEqualTo("admin");
    }

    @Test
    void URL_인코딩된_값을_디코딩한다() {
        final RequestParameters parameters = RequestParameters.of("", FORM_URLENCODED, "email=ksc%40example.com");

        assertThat(parameters.getParameter("email")).isEqualTo("ksc@example.com");
    }

    @Test
    void 인코딩된_구분자는_값의_일부로_읽는다() {
        final RequestParameters parameters = RequestParameters.of("account=a%26b&password=p%3D1", null, "");

        assertThat(parameters.getParameter("account")).isEqualTo("a&b");
        assertThat(parameters.getParameter("password")).isEqualTo("p=1");
        assertThat(parameters.getParameter("b")).isNull();
    }

    @Test
    void 값이_없는_파라미터는_빈_문자열이다() {
        final RequestParameters parameters = RequestParameters.of("account=&password", null, "");

        assertThat(parameters.getParameter("account")).isEmpty();
        assertThat(parameters.getParameter("password")).isEmpty();
    }

    @Test
    void URL_인코딩_형식이_잘못되면_예외가_발생한다() {
        assertThatThrownBy(() -> RequestParameters.of("password=100%", null, ""))
                .isInstanceOf(HttpRequestParseException.class);
    }
}
