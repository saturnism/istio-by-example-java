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

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Created by rayt on 5/1/17.
 */
@RestController
@SessionAttributes("name")
public class RestServerController {
    private final HelloworldService helloworldService;
    private final GuestbookService guestbookService;

    public RestServerController(HelloworldService helloworldService, GuestbookService guestbookService) {
        this.helloworldService = helloworldService;
        this.guestbookService = guestbookService;
    }

    @RequestMapping(value = "/echo/{user}", method = RequestMethod.GET)
    public RestData echo(@PathVariable String user) {
        Map<String, String> greeting = helloworldService.greeting(user);
        List<Map> allMessages = guestbookService.all();
        RestData rd = new RestData(greeting, allMessages);
        return rd;
    }
}

class RestData {
    public Map<String, String> greeting;
    public  List<Map> allMessages;

    public RestData() {
    }

    public RestData(Map<String, String> greeting, List<Map> allMessages) {
        this.greeting = greeting;
        this.allMessages = allMessages;
    }
}
