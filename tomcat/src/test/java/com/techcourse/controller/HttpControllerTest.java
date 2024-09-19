package com.techcourse.controller;

import static org.junit.jupiter.api.Assertions.*;

import com.techcourse.exception.UnsupportedHttpMethodException;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import support.FakeRequests;

class HttpControllerTest {

    @DisplayName("지원하지 않는 http 메서드를 service할 땐 예외가 발생한다")
    @Test
    void service() {
        HttpController httpController = new HttpController("/") {
            @Override
            public void service(HttpRequest request, HttpResponse response) throws Exception {
                super.service(request, response);
            }
        };

        Assertions.assertThatThrownBy(()->httpController.service(new HttpRequest(FakeRequests.validPutRequest),new HttpResponse()))
                .isInstanceOf(UnsupportedHttpMethodException.class);
    }
}
