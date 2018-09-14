/*
 * Copyright 2015 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.jdk;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;

public class WorkServer {
  static final String[] TRACE_HEADERS = new String[] {"x-request-id", "x-b3-traceid", "x-b3-spanid", "x-b3-parentspanid", "x-b3-sampled", "x-b3-flags", "x-ot-span-context"};

  public static void main(String[] args) throws IOException {
    String endpoint = System.getenv().getOrDefault("ENDPOINT", "http://localhost:8081/meet");
    HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
    server.setExecutor(Executors.newCachedThreadPool());
    String hostName = InetAddress.getLocalHost().getHostName();

    server.createContext("/work", exchange -> {
      long start = System.currentTimeMillis();

      // Create a new HttpClient
      // Create a new HttpRequest - set HTTP 1.1 and User Agent

      // ADD Trace Headers!

      int meetings = 0;

      // Do some work!

      long end = System.currentTimeMillis();

      String response = "Working for " + (end - start) + "ms, attended " + meetings + " meetings at " + hostName + "\n";
      byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
      exchange.sendResponseHeaders(200, bytes.length);
      OutputStream os = exchange.getResponseBody();

      os.write(bytes);
      os.close();
    });

    server.start();
  }
}
