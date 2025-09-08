package org.apache.coyote.http11.request.header;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

// 다음 단계를 위해 남겨놓은 코드.
// 리뷰 X
public record RequestHeaders(
        Map<String, List<String>> values
) {

    public static RequestHeaders fromRequest(String request) {
        // TODO: 객체지향적으로 리팩터링 시, 한 Request 에 같은 key 를 가지는 헤더가 여러 개 온 경우 합치는 로직 구현.
        // ex: Accept: text/css 헤더와 Accept: text/plain 이 같이 온 경우, 헤더 해석 결과는 Accept: text/css, text/plain 이 되어야 안정적임.
        // 참고: RFC 7230 3.2.2
//        System.out.println("parseHeaderLines(request) = " + parseHeaderLines(request));
        Map<String, List<String>> headersMap = parseHeaderLines(request).stream()
                .map(RequestHeaders::parseFromHeaderLine)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (oldHeader, newHeader) -> oldHeader // 우선 첫 번째 헤더만 사용하도록 구현.
                ));
        return new RequestHeaders(headersMap);
    }

    private static List<String> parseHeaderLines(String request) {
        return Arrays.stream(request.split(System.lineSeparator()))
                .skip(1)
                .takeWhile(line -> !line.isEmpty())
                .toList();
    }

    private static Entry<String, List<String>> parseFromHeaderLine(String headerLine) {
        String[] parts = headerLine.split(": ", 2);

        String key = parts[0].trim();
        List<String> value = Arrays.stream(parts[1].split(","))
                .map(String::trim)
                .toList();
        return Map.entry(key, value);
    }
}
