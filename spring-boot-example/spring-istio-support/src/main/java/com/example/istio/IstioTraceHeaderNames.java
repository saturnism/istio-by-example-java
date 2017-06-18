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

/**
 * Created by rayt on 6/17/17.
 */
public final class IstioTraceHeaderNames {
  public static String REQUEST_ID_HEADER = "X-Request-Id";
  public static String SPAN_CONTEXT_HEADER = "X-OT-Span-Context";

  // Other headers are defined in Span already

  private IstioTraceHeaderNames() {}
}
