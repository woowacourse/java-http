package org.apache.coyote.http11.request.requestLine.requestUri;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RequestUriTest {

    @Test
    @DisplayName("문자열로 주어진 요청 uri를 리소스 경로, 쿼리 파라미터로 분리하여 RequestUri를 생성한다.")
    void requestUriStringToRequestUri() {
        // given
        final String givenResourcePath = "/members";
        final String givenQueryString = "name=encho&age=23";
        final String givenUri = givenResourcePath + "?" + givenQueryString;

        final ResourcePath expectResourcePath = ResourcePath.from(givenResourcePath);
        final QueryParameters expectQueryParameters = QueryParameters.from(givenQueryString);

        // when
        final RequestUri actual = RequestUri.from(givenUri);

        // then
        SoftAssertions.assertSoftly(softAssertions -> {
            softAssertions.assertThat(actual.getResourcePath()).isEqualTo(expectResourcePath);
            softAssertions.assertThat(actual.getQueryParameters()).isEqualTo(expectQueryParameters);
        });
    }
}
