package org.apache.catalina;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import nextstep.jwp.controller.Controller;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class RequestMappingTest {

    @Mock
    Controller controller;

    @Mock
    HttpRequest mockHttpRequest;

    @Mock
    HttpResponse mockHttpResponse;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void 요청에_대한_응답을_처리한다() {
        // given
        RequestMapping requestMapping = new RequestMapping(List.of(controller));

        when(controller.canProcess(any()))
                .thenReturn(true);

        // when
        requestMapping.process(mockHttpRequest, mockHttpResponse);

        // then
        verify(controller, times(1)).service(mockHttpRequest, mockHttpResponse);
    }

}
