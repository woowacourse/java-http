package nextstep.jwp.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Parser {

	private static final String START_QUERY_STRING_DELIMITER = "?";
	private static final String QUERY_CONNECT_DELIMITER = "&";
	private static final String QUERY_VALUE_DELIMITER = "=";
	private static final int NAME_INDEX = 0;
	private static final int VALUE_INDEX = 1;
	private static final String GAP = " ";
	private static final String EXTENSION = ".";
	private static final String ROOT = "/";

	public static String findUrl(String url) {
		return url.split(GAP)[0];
	}

	public static Map<String, String> findParam(String url) {
		int startQueryIndex = findStartQueryString(url);
		if (startQueryIndex < 0) {
			return new HashMap<>();
		}
		String queryString = extractQueryString(url, startQueryIndex);
		return parseQueryString(queryString);
	}

	private static int findStartQueryString(String url) {
		return url.indexOf(START_QUERY_STRING_DELIMITER);
	}

	private static String extractQueryString(String url, int startQueryIndex) {
		return url.substring(startQueryIndex + VALUE_INDEX);
	}

	private static Map<String, String> parseQueryString(String queryString) {
		String[] values = queryString.split(QUERY_CONNECT_DELIMITER);
		Map<String, String> params = new HashMap<>();
		for (String param : values) {
			final String[] data = param.split(QUERY_VALUE_DELIMITER);
			params.put(data[NAME_INDEX], data[VALUE_INDEX]);
		}
		return params;
	}

	public static String convertResourceFileName(String path) {
		Objects.nonNull(path);
		final int index = path.indexOf(START_QUERY_STRING_DELIMITER);
		if (index > 0) {
			return path.split(START_QUERY_STRING_DELIMITER)[0];
		}
		return path;
	}

	public static String parseFileType(String fileName) {
		Objects.nonNull(fileName);
		return fileName.substring(fileName.indexOf(".")+1);
	}
}
