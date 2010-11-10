//
// Groovy Liquibase ChangeLog
//
// Copyright (C) 2010 Tim Berglund
// http://augusttechgroup.com
// Littleton, CO
//
// Licensed under the Apache License 2.0
//

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