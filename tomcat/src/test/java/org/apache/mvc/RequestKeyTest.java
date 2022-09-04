package org.apache.mvc;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.annotation.Annotation;
import org.apache.coyote.http11.request.RequestMethod;
import org.apache.mvc.annotation.RequestMapping;
import org.apache.mvc.handlerchain.RequestKey;
import org.junit.jupiter.api.Test;

class RequestKeyTest {

    @Test
    void createRequestKeyByAnnotation() {
        // given
        RequestMapping mappingAnnotation = new RequestMapping() {
            @Override
            public Class<? extends Annotation> annotationType() {
                return RequestMapping.class;
            }

            @Override
            public String value() {
                return "/";
            }

            @Override
            public RequestMethod method() {
                return RequestMethod.GET;
            }
        };
        // when
        RequestKey key = RequestKey.from(mappingAnnotation);

        // then
        assertThat(key).isNotNull(); //todo : elaborate this
    }

    @Test
    void equalsWithSamePathAndMethod() {
        // given
        RequestKey keyA = new RequestKey(RequestMethod.GET, "/");
        RequestKey keyB = new RequestKey(RequestMethod.GET, "/");

        // when
        boolean isEqual = keyA.equals(keyB);

        // then
        assertThat(isEqual).isTrue();
    }
}
