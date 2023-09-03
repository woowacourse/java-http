package org.apache.coyote.http11;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.session.SessionManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class HttpResponseMakerTest {

    @Mock
    private HttpRequestParser mockParser;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void 로그인_성공_시에_요청에_해당하는_응답을_반환한다() throws IOException {
        // given
        when(mockParser.getHttpRequestFirstLineInfo()).thenReturn(
                HttpRequestFirstLineInfo.from("POST /login HTTP/1.1"));
        when(mockParser.getBody())
                .thenReturn(
                        Map.of(
                                "account", "testuser",
                                "password", "password123",
                                "email", "email")
                );

        InMemoryUserRepository.save(new User("testuser", "password123", "email"));

        // when
        String response = HttpResponseMaker.makeFrom(mockParser, new SessionManager());

        // then
        assertTrue(response.contains("Location: /index.html"));
    }

    @Test
    void 로그인_실패_시에_요청에_해당하는_응답을_반환한다() throws IOException {
        // given
        when(mockParser.getHttpRequestFirstLineInfo()).thenReturn(
                HttpRequestFirstLineInfo.from("POST /login HTTP/1.1"));
        when(mockParser.getBody())
                .thenReturn(
                        Map.of(
                                "account", "testuser",
                                "password", "password456",
                                "email", "email")
                );

        InMemoryUserRepository.save(new User("testuser", "password123", "email"));

        // when
        String response = HttpResponseMaker.makeFrom(mockParser, new SessionManager());

        // then
        assertTrue(response.contains("Location: /401.html"));
    }

    @Test
    void 회원_정보가_없을_시에_예외를_발생시킨다() {
        // given
        when(mockParser.getHttpRequestFirstLineInfo()).thenReturn(
                HttpRequestFirstLineInfo.from("POST /login HTTP/1.1"));
        when(mockParser.getBody())
                .thenReturn(
                        Map.of(
                                "account", "test",
                                "password", "password123",
                                "email", "email")
                );

        InMemoryUserRepository.save(new User("testuser", "password123", "email"));

        // expect
        SessionManager sessionManager = new SessionManager();
        Assertions.assertThatThrownBy(() -> HttpResponseMaker.makeFrom(mockParser, sessionManager))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("회원 정보가 존재하지 않습니다.");
    }
}

