package org.apache.coyote.http11.pageController;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PageControllerMapperTest {
    @Test
    void mappedController() {
        assertThat(PageControllerMapper.getPageController("/login")).isInstanceOf(LoginController.class);
    }

    @Test
    void sameInstanceForEveryRequest() {
        assertThat(PageControllerMapper.getPageController("/login"))
                .isSameAs(PageControllerMapper.getPageController("/login"));
    }

    @Test
    void staticResourceControllerByDefault() {
        assertThat(PageControllerMapper.getPageController("/index.html"))
                .isInstanceOf(StaticResourceController.class);
    }
}
