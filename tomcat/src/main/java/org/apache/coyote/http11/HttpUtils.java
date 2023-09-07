package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

public class HttpUtils {

  private static final String PREFIX_STATIC_PATH = "static";
  private static final ClassLoader CLASS_LOADER = ClassLoader.getSystemClassLoader();

  private HttpUtils() {
  }

  public static String readContentsFromFile(final String url) throws IOException {
    final URL resource = CLASS_LOADER.getResource(PREFIX_STATIC_PATH + url);
    if (isInvalidFile(resource)) {
      return "Hello world!";
    }

    final File file = new File(resource.getFile());
    return new String(Files.readAllBytes(file.toPath()));
  }

  private static boolean isInvalidFile(final URL resource) {
    return resource == null || new File(resource.getFile()).isDirectory();
  }

  public static BufferedReader bufferingInputStream(final InputStream inputStream) {
    final InputStreamReader reader = new InputStreamReader(inputStream);
    return new BufferedReader(reader);
  }

  public static String getContentType(final String accept) {
    if (accept == null) {
      return "text/html";
    }
    return accept.split(",")[0];
  }

  public static Map<String, String> parseParam(final String queryString) {
    final Map<String, String> params = new HashMap<>();
    for (final String query : URLDecoder.decode(queryString, StandardCharsets.UTF_8).split("&")) {
      final String[] tokens = query.split("=", 2);
      params.put(tokens[0], tokens[1]);
    }
    return params;
  }
}
