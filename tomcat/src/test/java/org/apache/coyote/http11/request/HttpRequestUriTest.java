package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import java.util.List;
import org.apache.coyote.http11.component.HttpMethod;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

class HttpRequestUriTest {

    private ListAppender<ILoggingEvent> listAppender;

    @BeforeEach
    void setup() {
        listAppender = new ListAppender<>();
        Logger logger = (Logger) LoggerFactory.getLogger(HttpRequestUri.class);
        logger.addAppender(listAppender);
        listAppender.start();
    }

    @DisplayName("로그인 처리시 쿼리 파리미터를 이용하여 조회한 유저 정보를 로그에 기록한다.")
    @Test
    void processParams() {
        //given
        String expectedLog = "user : User{id=1, account='gugu', email='hkkang@woowahan.com', password='password'}";
        String path = "/login?account=gugu&password=password";
        HttpRequestUri requestUri = HttpRequestUriParser.parse(path);

        //when
        requestUri.processParams(HttpMethod.GET);

        //then
        List<ILoggingEvent> testLogs = listAppender.list;
        List<String> messages = testLogs.stream()
                .map(ILoggingEvent::getFormattedMessage)
                .toList();
        assertThat(messages).isNotEmpty().anyMatch(message -> message.contains(expectedLog));
    }

    @DisplayName("쿼리 파리미터가 없다면 로그에 아무것도 기록되지 않는다.")
    @Test
    void processQueryParamsWithoutEmptyParams() {
        //given
        String expectedLog = "user : User{id=1, account='gugu', email='hkkang@woowahan.com', password='password'}";
        String path = "/login";
        HttpRequestUri requestUri = HttpRequestUriParser.parse(path);

        //when
        requestUri.processParams(HttpMethod.GET);

        //then
        List<ILoggingEvent> testLogs = listAppender.list;
        List<String> messages = testLogs.stream()
                .map(ILoggingEvent::getFormattedMessage)
                .toList();
        assertThat(messages).noneMatch(message -> message.contains(expectedLog));
    }
}
