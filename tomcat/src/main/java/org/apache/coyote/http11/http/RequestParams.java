package org.apache.coyote.http11.http;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.coyote.http11.http.header.ContentType;

public class RequestParams {

	private static final String QUERY_STRING_DELIMITER = "\\?";
	private static final int PARAMS_INDEX = 1;

	private static final String PARAM_KEY_VALUE_DELIMITER = "=";
	private static final int PARAM_KEY_INDEX = 0;
	private static final int PARAM_VALUE_INDEX = 1;

	private static final String PARAM_DELIMITER = "&";

	private static final String EMPTY = "";

	private final Map<String, String> requestParams;

	public RequestParams(ContentType contentType, String url, String body) {
		List<String> requestParams = new ArrayList<>();
		checkQueryString(url, requestParams);
		checkBody(contentType, body, requestParams);
		this.requestParams = convertToMap(requestParams);
	}

	private void checkQueryString(String url, List<String> requestParams) {
		if (url.contains(QUERY_STRING_DELIMITER)) {
			String[] params = url.split(QUERY_STRING_DELIMITER)[PARAMS_INDEX]
				.split(PARAM_DELIMITER);
			requestParams.addAll(List.of(params));
		}
	}

	private void checkBody(ContentType contentType, String body, List<String> requestParams) {
		if (ContentType.FORM_DATA == contentType) {
			String[] params = body.split(PARAM_DELIMITER);
			requestParams.addAll(List.of(params));
		}
	}

	private Map<String, String> convertToMap(List<String> requestParams) {
		return requestParams.stream()
			.filter(param -> param.contains(PARAM_KEY_VALUE_DELIMITER))
			.collect(Collectors.toMap(
				line -> line.split(PARAM_KEY_VALUE_DELIMITER)[PARAM_KEY_INDEX],
				line -> line.split(PARAM_KEY_VALUE_DELIMITER)[PARAM_VALUE_INDEX]
			));
	}

	public String getOrDefault(String key) {
		return requestParams.getOrDefault(key, EMPTY);
	}
}
