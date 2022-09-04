package org.apache.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class UrlUtilTest {

    @Test
    void urlJoin() {
        // given
        String left = "hello";
        String right = "world";
        // when
        String joined = UrlUtil.joinUrl(left, right);
        // then
        assertThat(joined).isEqualTo("hello/world");
    }

    @Test
    void urlJoinWithSlash() {
        // given
        String left = "hello";
        String right = "/world";
        // when
        String joined = UrlUtil.joinUrl(left, right);
        // then
        assertThat(joined).isEqualTo("hello/world");
    }

    @Test
    void urlJoinWithDoubleSlash() {
        // given
        String left = "hello";
        String right = "//world";
        // when
        String joined = UrlUtil.joinUrl(left, right);
        // then
        assertThat(joined).isEqualTo("hello/world");
    }

    @Test
    void urlJoinWithMultipleSlashes() {
        // given
        String left = "hello";
        String right = "world/multiple/slash.html";
        // when
        String joined = UrlUtil.joinUrl(left, right);
        // then
        assertThat(joined).isEqualTo("hello/world/multiple/slash.html");
    }
}
