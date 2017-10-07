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

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.annotation.SessionScope;

import java.util.Map;

/**
 * Created by rayt on 5/1/17.
 */
@Controller
@SessionAttributes("name")
public class HelloworldUiController {
  private final HelloworldService helloworldService;
  private final GuestbookService guestbookService;

  public HelloworldUiController(HelloworldService helloworldService, GuestbookService guestbookService) {
    this.helloworldService = helloworldService;
    this.guestbookService = guestbookService;
  }

  @GetMapping("/")
  public String index(Model model) {
    if (model.containsAttribute("name")) {
      String name = (String) model.asMap().get("name");
      Map<String, String> greeting = helloworldService.greeting(name);
      model.addAttribute("greeting", greeting);
    }

    model.addAttribute("messages", guestbookService.all());

    return "index";
  }

  @PostMapping("/greet")
  public String greet(@RequestParam String name, @RequestParam String message, Model model) {
    model.addAttribute("name", name);
    if (message != null && !message.trim().isEmpty()) {
      guestbookService.add(name, message);
    }

    return "redirect:/";
  }
}
