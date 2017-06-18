#!/bin/bash
###############################################################################
# Copyright 2015 Google Inc. All rights reserved.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
###############################################################################

kubectl delete svc helloworld-ui
kubectl delete deployment helloworld-ui

kubectl delete svc helloworld-service
kubectl delete deployment helloworld-service

kubectl delete svc guestbook-service
kubectl delete deployment guestbook-service

kubectl delete svc redis mysql
kubectl delete deployment redis mysql
kubectl delete pvc mysql-pvc

#kubectl delete storageclass standard
