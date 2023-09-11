package org.apache.coyote.http11.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class IOUtils {


  private static final String PREFIX_STATIC_PATH = "static";
  private static final ClassLoader CLASS_LOADER = ClassLoader.getSystemClassLoader();

  private IOUtils() {
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

  public static List<String> readWhileEmptyLine(final BufferedReader br) throws IOException {
    final List<String> lines = new ArrayList<>();
    String line;
    while (!(line = br.readLine()).isEmpty()) {
      lines.add(line);
    }
    return lines;
  }

  public static String readAsContentLength(
      final BufferedReader br,
      final String contentLength
  ) throws IOException {
    if (contentLength == null) {
      return "";
    }
    final int length = Integer.parseInt(contentLength);
    final char[] buffer = new char[length];
    br.read(buffer);
    return URLDecoder.decode(new String(buffer), StandardCharsets.UTF_8);
  }
}
