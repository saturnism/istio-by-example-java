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

package com.example.istio;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.SpanTextMap;
import org.springframework.cloud.sleuth.instrument.web.HttpSpanInjector;
import org.springframework.cloud.sleuth.instrument.web.ZipkinHttpSpanInjector;
import org.springframework.util.StringUtils;

import java.lang.invoke.MethodHandles;
import java.util.Map;

/**
 * Created by rayt on 6/17/17.
 */
public class IstioHttpSpanInjector implements HttpSpanInjector {
  private static final String HEADER_DELIMITER = "-";
  private static final Log log = LogFactory.getLog(
      MethodHandles.lookup().lookupClass());

  @Override
  public void inject(Span span, SpanTextMap carrier) {
    setHeader(carrier, Span.TRACE_ID_NAME, span.traceIdString());
    setIdHeader(carrier, Span.SPAN_ID_NAME, span.getSpanId());
    setHeader(carrier, Span.SAMPLED_NAME, span.isExportable() ? Span.SPAN_SAMPLED : Span.SPAN_NOT_SAMPLED);
    setHeader(carrier, Span.SPAN_NAME_NAME, span.getName());
    setIdHeader(carrier, Span.PARENT_ID_NAME, getParentId(span));
    setHeader(carrier, Span.PROCESS_ID_NAME, span.getProcessId());
    setHeader(carrier, IstioTraceHeaderNames.REQUEST_ID_HEADER, span.getBaggageItem(IstioTraceHeaderNames.REQUEST_ID_HEADER));
    setHeader(carrier, IstioTraceHeaderNames.SPAN_CONTEXT_HEADER, span.getBaggageItem(IstioTraceHeaderNames.SPAN_CONTEXT_HEADER));
    setHeader(carrier, IstioTraceHeaderNames.USER_AGENT, span.getBaggageItem(IstioTraceHeaderNames.USER_AGENT));
    for (Map.Entry<String, String> entry : span.baggageItems()) {
      if (entry.getKey().equals(IstioTraceHeaderNames.REQUEST_ID_HEADER)) {
        continue;
      } else if (entry.getKey().equals(IstioTraceHeaderNames.SPAN_CONTEXT_HEADER)) {
        continue;
      }
      carrier.put(prefixedKey(entry.getKey()), entry.getValue());
    }
  }

  private String prefixedKey(String key) {
    if (key.startsWith(Span.SPAN_BAGGAGE_HEADER_PREFIX + HEADER_DELIMITER)) {
      return key;
    }
    return Span.SPAN_BAGGAGE_HEADER_PREFIX + HEADER_DELIMITER + key;
  }

  private Long getParentId(Span span) {
    return !span.getParents().isEmpty() ? span.getParents().get(0) : null;
  }

  private void setIdHeader(SpanTextMap carrier, String name, Long value) {
    if (value != null) {
      setHeader(carrier, name, Span.idToHex(value));
    }
  }
  private void setHeader(SpanTextMap carrier, String name, String value) {
    if (StringUtils.hasText(value)) {
      carrier.put(name, value);
    }
  }
}
