package org.apache.coyote.parser;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StaticFileParser {

  private static final Map<String, String> EXTENDER = Map.of(
      "js", "text/javascript",
      "html", "text/html",
      "css", "text/css",
      "ico", "image/x-icon"
  );
  private static final String REGEX = "\\.(css|html|js|ico)$";
  private static final Pattern PATTERN = Pattern.compile(REGEX);
  private static final String EMPTY_EXTENSION = "";

  private StaticFileParser() {
  }

  public static boolean isStaticFile(final String fileName) {
    return EXTENDER.containsKey(extractExtension(fileName));
  }

  public static String parsingFileType(final String fileName) {
    return EXTENDER.get(extractExtension(fileName));
  }

  private static String extractExtension(final String fileName) {
    final Matcher matcher = PATTERN.matcher(fileName);

    if (matcher.find()) {
      return matcher.group(1);
    }

    return EMPTY_EXTENSION;
  }
}
