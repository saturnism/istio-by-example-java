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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.SpanTextMap;
import org.springframework.cloud.sleuth.instrument.web.HttpSpanExtractor;
import org.springframework.cloud.sleuth.util.TextMapUtil;
import org.springframework.util.StringUtils;

import java.lang.invoke.MethodHandles;
import java.util.Map;

/**
 * Created by rayt on 6/17/17.
 */
public class IstioHttpSpanExtractor implements HttpSpanExtractor {
  static final String URI_HEADER = "X-Span-Uri";
  private static final Log log = LogFactory.getLog(
      MethodHandles.lookup().lookupClass());
  private static final String HEADER_DELIMITER = "-";
  private static final String HTTP_COMPONENT = "http";

  public IstioHttpSpanExtractor() {
  }

  @Override
  public Span joinTrace(SpanTextMap textMap) {
    Map<String, String> carrier = TextMapUtil.asMap(textMap);
    if (carrier.get(Span.TRACE_ID_NAME) == null) {
      // can't build a Span without trace id
      return null;
    }
    try {
      String uri = carrier.get(URI_HEADER);
      boolean skip = Span.SPAN_NOT_SAMPLED.equals(carrier.get(Span.SAMPLED_NAME));
      long spanId = spanId(carrier);
      return buildParentSpan(carrier, uri, skip, spanId);
    } catch (Exception e) {
      log.error("Exception occurred while trying to extract span from carrier", e);
      return null;
    }
  }

  private long spanId(Map<String, String> carrier) {
    String spanId = carrier.get(Span.SPAN_ID_NAME);
    if (spanId == null) {
      if (log.isDebugEnabled()) {
        log.debug("Request is missing a span id but it has a trace id. We'll assume that this is "
            + "a root span with span id equal to the lower 64-bits of the trace id");
      }
      return Span.hexToId(carrier.get(Span.TRACE_ID_NAME));
    } else {
      return Span.hexToId(spanId);
    }
  }

  private Span buildParentSpan(Map<String, String> carrier, String uri, boolean skip, long spanId) {
    String traceId = carrier.get(Span.TRACE_ID_NAME);
    Span.SpanBuilder span = Span.builder()
        .traceIdHigh(traceId.length() == 32 ? Span.hexToId(traceId, 0) : 0)
        .traceId(Span.hexToId(traceId))
        .spanId(spanId);

    String processId = carrier.get(Span.PROCESS_ID_NAME);
    String parentName = carrier.get(Span.SPAN_NAME_NAME);

    if (StringUtils.hasText(parentName)) {
      span.name(parentName);
    } else {
      span.name(HTTP_COMPONENT + ":/parent" + uri);
    }
    if (StringUtils.hasText(processId)) {
      span.processId(processId);
    }
    if (carrier.containsKey(Span.PARENT_ID_NAME)) {
      span.parent(Span.hexToId(carrier.get(Span.PARENT_ID_NAME)));
    }
    span.remote(true);
    boolean debug = Span.SPAN_SAMPLED.equals(carrier.get(Span.SPAN_FLAGS));
    if (debug) {
      span.exportable(true);
    } else if (skip) {
      span.exportable(false);
    }
    for (Map.Entry<String, String> entry : carrier.entrySet()) {
      if (entry.getKey().startsWith(Span.SPAN_BAGGAGE_HEADER_PREFIX + HEADER_DELIMITER)) {
        span.baggage(unprefixedKey(entry.getKey()), entry.getValue());
      }
    }

    String requestId = carrier.get(IstioTraceHeaderNames.REQUEST_ID_HEADER);
    String spanContext = carrier.get(IstioTraceHeaderNames.SPAN_CONTEXT_HEADER);
    span.baggage(IstioTraceHeaderNames.REQUEST_ID_HEADER, requestId);
    span.baggage(IstioTraceHeaderNames.SPAN_CONTEXT_HEADER, spanContext);

    Span spanInstance = span.build();
    return spanInstance;
  }

  private String unprefixedKey(String key) {
    return key.substring(key.indexOf(HEADER_DELIMITER) + 1);
  }
}
