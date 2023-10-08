package com.greenjon902.hisdoc.webDriver;

import java.util.HashMap;
import java.util.Map;

public record WebDriverConfig(Map<String, PageRenderer> pageRenderers, int port, int backlog, int stopDelay) {

}
