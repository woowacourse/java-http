package org.apache.coyote.response;

public enum Protocol {

  HTTP_1_1("HTTP","1.1")
  ;

  private final String protocol;
  private final String version;

  Protocol(final String protocol, final String version) {
    this.protocol = protocol;
    this.version = version;
  }

  public String read() {
    return protocol + "/" + version + " ";
  }
}
