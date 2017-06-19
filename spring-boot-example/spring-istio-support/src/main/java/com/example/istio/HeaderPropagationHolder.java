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
package com.example.istio;

import java.util.*;

/**
 * Created by rayt on 6/18/17.
 */
public final class HeaderPropagationHolder {
  private static final ThreadLocal<Map<String, String>> headers = ThreadLocal.withInitial(() -> {
    return new HashMap<>();
  });

  private HeaderPropagationHolder() {
  }

  public static void put(String header, String value) {
    headers.get().put(header, value);
  }

  public static String get(String header) {
    return headers.get().get(header);
  }

  public static Set<Map.Entry<String, String>> entries() {
    return headers.get().entrySet();
  }

  public static Map<String, String> all() {
    Map<String, String> map = new TreeMap<>(new Comparator<String>() {
      @Override
      public int compare(String o1, String o2) {
        return o1.toLowerCase().compareTo(o2.toLowerCase());
      }
    });
    for (Map.Entry<String, String> entry : headers.get().entrySet()) {
      map.put(entry.getKey(), entry.getValue());
    }
    return Collections.unmodifiableMap(map);
  }

}
