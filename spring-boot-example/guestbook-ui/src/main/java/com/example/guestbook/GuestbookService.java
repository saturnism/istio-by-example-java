// Copyright 2017 Google Inc.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

// TODO: High-level file comment.

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
package com.example.guestbook;

import io.vavr.control.Try;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by rayt on 5/1/17.
 */
public class GuestbookService {
  private static final Log log = LogFactory.getLog(GuestbookService.class);
  private final RestTemplate restTemplate;
  private final String endpoint;
  private final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

  public GuestbookService(RestTemplate restTemplate, String endpoint) {
    this.restTemplate = restTemplate;
    this.endpoint = endpoint;
  }

  public Map<String, String> add(String username, String message) {
    Map<String, String> payload = new HashMap<>();
    payload.put("username", username);
    payload.put("message", message);
    payload.put("timestamp", dateFormat.format(new Date()));

    // Istio can handle circuit breaking. Don't need to retry here.
    return restTemplate.postForObject(endpoint, payload, Map.class);
  }

  public List<Map> allFallback() {
    Map<String, String> bulkheadEntry = new HashMap<>();
    bulkheadEntry.put("username", "system");
    bulkheadEntry.put("message", "Guestbook Service is currenctly unavailable");
    return Arrays.asList(bulkheadEntry);
  }

  public List<Map> all() {
    try {
      Map response = restTemplate.getForObject(endpoint, Map.class);

      Map embedded = (Map) response.get("_embedded");
      List<Map> messages = (List<Map>) embedded.get("messages");
      return messages.stream()
          .filter(message -> message.containsKey("_links"))
          .map(message -> (Map) message.get("_links"))
          .filter(links -> links.containsKey("self"))
          .map(links -> (Map) links.get("self"))
          .map(self -> (String) self.get("href"))
          .map(href -> restTemplate.getForObject(href, Map.class))
          .collect(Collectors.toList());
    } catch (HttpStatusCodeException e) {
      // Istio would've performed circuit breaking and retries
      // but it doesn't handle bulkheads / returning default values on full failures.
      log.error("Error from Guestbook Service, falling back", e);
      return allFallback();
    }
  }
}
