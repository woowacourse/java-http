package org.apache.coyote.http11.request.header;

public class Resource {

	private static final String URL_PARAMS_DELIMITER = "?";

	private final String url;
	private final QueryParams value;

	private Resource(final String url, final QueryParams value) {
		this.url = url;
		this.value = value;
	}

	public static Resource from(final String resource) {
		int startParams = resource.indexOf(URL_PARAMS_DELIMITER);
		if (startParams == -1) {
			return new Resource(resource, QueryParams.empty());
		}
		return new Resource(
			resource.substring(0, startParams),
			QueryParams.from(resource.substring(startParams + 1))
		);
	}

	public String getUrl() {
		return url;
	}
}
