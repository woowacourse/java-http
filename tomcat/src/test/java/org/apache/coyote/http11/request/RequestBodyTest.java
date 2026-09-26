package org.apache.coyote.http11.request;

import org.apache.coyote.http11.exception.BadRequestException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class RequestBodyTest {
    private static final Optional<String> FORM = Optional.of("application/x-www-form-urlencoded");

    @Test
    void form_본문의_파라미터를_파싱한다() {
        final RequestBody body = RequestBody.of(bytes("account=gugu&password=password"), FORM);

        assertThat(body.getParameter("account")).hasValue("gugu");
        assertThat(body.getParameter("password")).hasValue("password");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "application/x-www-form-urlencoded; charset=UTF-8",
            "Application/X-WWW-Form-Urlencoded",
            "application/x-www-form-urlencoded ; charset=UTF-8",
    })
    void 대소문자와_파라미터에_관계없이_form을_인식한다(final String contentType) {
        final RequestBody body = RequestBody.of(bytes("a=1"), Optional.of(contentType));

        assertThat(body.getParameter("a")).hasValue("1");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "application/json",
            "text/plain",
            "multipart/form-data",
            "application/x-www-form-urlencoded-evil",   // 접두사만 같은 다른 타입
    })
    void form이_아니면_파라미터가_없다(final String contentType) {
        final RequestBody body = RequestBody.of(bytes("a=1"), Optional.of(contentType));

        assertThat(body.hasParameters()).isFalse();
    }

    @Test
    void Content_Type이_없으면_파라미터가_없다() {
        final RequestBody body = RequestBody.of(bytes("a=1"), Optional.empty());

        assertThat(body.hasParameters()).isFalse();
    }

    @Test
    void 인코딩된_한글을_디코딩한다() {
        final RequestBody body = RequestBody.of(bytes("account=%EA%B5%AC%EA%B5%AC"), FORM);

        assertThat(body.getParameter("account")).hasValue("구구");
    }

    @Test
    void 인코딩되지_않은_비ASCII_바이트는_거부한다() {
        final byte[] raw = "account=구구".getBytes(StandardCharsets.UTF_8);

        assertThatThrownBy(() -> RequestBody.of(raw, FORM))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void 빈_본문은_파라미터가_없다() {
        assertThat(RequestBody.of(new byte[0], FORM).hasParameters()).isFalse();
    }

    private static byte[] bytes(final String value) {
        return value.getBytes(StandardCharsets.US_ASCII);
    }
}