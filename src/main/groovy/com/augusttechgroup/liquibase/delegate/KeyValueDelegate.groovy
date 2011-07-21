/*
 * Copyright 2011 Tim Berglund
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.augusttechgroup.liquibase.delegate


/**
 * A general-purpose delgate class to provide key/value support in a builder.
 * 
 * @author Tim Berglund
 */
class KeyValueDelegate
{
  def map = [:]
  
  void methodMissing(String name, args) {
    if(args != null && args.size() == 1) {
      map[name] = args[0]
    }
    else {
      map[name] = args
    }
  }
}