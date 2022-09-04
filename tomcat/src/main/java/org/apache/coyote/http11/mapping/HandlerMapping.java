package org.apache.coyote.http11.mapping;

import java.util.HashMap;
import java.util.Map;

import org.apache.coyote.http11.util.ResourceSearcher;

public class HandlerMapping {

	private static final HandlerMapping HANDLER_MAPPING = new HandlerMapping();
	private static final ResourceSearcher RESOURCE_SEARCHER = new ResourceSearcher();

	private Map<String, MappingResponse> value = new HashMap<>();

	private HandlerMapping() {
	}

	public static HandlerMapping getInstance() {
		return HANDLER_MAPPING;
	}

	public void add(final String url, final String resource, final String statusCode) {
		validateUrlIsNotDuplicated(url);

		final MappingResponse mappingResponse = new MappingResponse(resource, statusCode);
		value.put(url, mappingResponse);
	}

	public MappingResponse getResponse(final String url) {
		if (RESOURCE_SEARCHER.isFile(url)) {
			return new MappingResponse(url, "OK");
		}
		return value.getOrDefault(url, MappingResponse.notFound());
	}

	private void validateUrlIsNotDuplicated(final String url) {
		if (value.containsKey(url)) {
			throw new IllegalArgumentException(String.format("이미 등록된 url 입니다. [%s]", url));
		}
	}
}
