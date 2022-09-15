package org.springframework.servlet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.lang.annotation.Annotation;
import org.apache.http.BasicHttpRequest;
import org.apache.http.HttpRequest;
import org.apache.http.info.HttpMethod;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.annotation.RequestMapping;

class RequestMappingInfoTest {

    @DisplayName("RequestMappingInfo.EMPTY의 HttpMethod와 uri는 모두 null이다")
    @Test
    void empty_requestMappingInfo_contains_null_method_and_uri() {
        // given
        final RequestMappingInfo empty = RequestMappingInfo.EMPTY;

        // when
        final HttpMethod httpMethod = empty.getHttpMethod();
        final String uri = empty.getUri();

        // then
        assertAll(
                () -> assertThat(httpMethod).isNull(),
                () -> assertThat(uri).isNull()
        );
    }

    @DisplayName("RequestMapping 애너테이션 정보를 통해 RequestMappingInfo를 생성할 수 있다")
    @Test
    void create_requestMappingInfo_from_requestMapping_annotation() {
        // given
        final HttpMethod httpMethod = HttpMethod.GET;
        final String uri = "/";
        final RequestMapping requestMapping = createRequestMapping(httpMethod, uri);

        // when
        final RequestMappingInfo actual = RequestMappingInfo.from(requestMapping);

        // then
        assertAll(
                () -> assertThat(actual.getHttpMethod()).isEqualTo(httpMethod),
                () -> assertThat(actual.getUri()).isEqualTo(uri)
        );
    }

    private static RequestMapping createRequestMapping(final HttpMethod httpMethod, final String uri) {
        return new RequestMapping() {
            @Override
            public HttpMethod method() {
                return httpMethod;
            }

            @Override
            public String uri() {
                return uri;
            }

            @Override
            public Class<? extends Annotation> annotationType() {
                return RequestMapping.class;
            }
        };
    }

    @DisplayName("HttpRequest정보를 파싱하여 RequestMappingInfo를 생성할 수 있다")
    @Test
    void create_requestMappingInfo_from_httpRequest() {
        // given
        final HttpMethod expectedHttpMethod = HttpMethod.GET;
        final String expectedUri = "/";
        final HttpRequest httpRequest = new BasicHttpRequest(expectedHttpMethod, expectedUri, null, null, null);

        // when
        final RequestMappingInfo requestMappingInfo = RequestMappingInfo.from(httpRequest);
        final HttpMethod actualHttpMethod = requestMappingInfo.getHttpMethod();
        final String actualUri = requestMappingInfo.getUri();

        // then
        assertAll(
                () -> assertThat(actualHttpMethod).isEqualTo(expectedHttpMethod),
                () -> assertThat(actualUri).isEqualTo(expectedUri)
        );
    }

    @DisplayName("같은 HttpMethod, Uri 정보를 담은 RequestMapping은 equals에 true가 반환된다")
    @Test
    void return_true_on_equals_based_on_httpMethod_and_uri() {
        // given
        final HttpMethod httpMethod = HttpMethod.GET;
        final String uri = "/";
        final HttpRequest httpRequest = new BasicHttpRequest(httpMethod, uri, null, null, null);
        final HttpRequest anotherHttpRequest = new BasicHttpRequest(httpMethod, uri, null, null, null);

        // when
        final RequestMappingInfo requestMappingInfo = RequestMappingInfo.from(httpRequest);
        final RequestMappingInfo anotherRequestMappingInfo = RequestMappingInfo.from(anotherHttpRequest);

        // then
        assertAll(
                () -> assertThat(httpRequest).isNotSameAs(anotherHttpRequest),
                () -> assertThat(requestMappingInfo).isNotSameAs(anotherRequestMappingInfo),
                () -> assertThat(requestMappingInfo).isEqualTo(anotherRequestMappingInfo)
        );
    }
}
