/**
 *    Copyright 2009-2016 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.tianshouzhi.dragon.common.log;

import com.tianshouzhi.dragon.common.log.commons.JakartaCommonsLoggingImpl;
import com.tianshouzhi.dragon.common.log.jdk14.Jdk14LoggingImpl;
import com.tianshouzhi.dragon.common.log.log4j.Log4jImpl;
import com.tianshouzhi.dragon.common.log.log4j12.Log4j2Impl;
import com.tianshouzhi.dragon.common.log.slf4j.Slf4jImpl;
import com.tianshouzhi.dragon.common.log.stdout.StdOutImpl;

import java.lang.reflect.Constructor;

/**
 * @author tianshouzhi
 */
public final class LoggerFactory {

  /**
   * Marker to be used by logging implementations that support markers
   */
  public static final String MARKER = "DRAGON";

  private static Constructor<? extends Log> logConstructor;

  static {
    tryImplementation(Slf4jImpl.class);
    tryImplementation(JakartaCommonsLoggingImpl.class);
    tryImplementation(Log4j2Impl.class);
    tryImplementation(Log4jImpl.class);
    tryImplementation(Jdk14LoggingImpl.class);
    tryImplementation(StdOutImpl.class);
  }

  private LoggerFactory() {
    // disable construction
  }

  private static void tryImplementation(Class<? extends Log> implClass) {
    if (logConstructor == null) {
      try {
          Constructor<? extends Log> candidate = implClass.getConstructor(String.class);
          Log log = candidate.newInstance(LoggerFactory.class.getName());
          if (log.isDebugEnabled()) {
            log.debug("Logging initialized using '" + implClass + "' adapter.");
          }
          logConstructor = candidate;
      } catch (Throwable t) {
        // ignore
      }
    }
  }

  public static Log getLogger(Class<?> clazz) {
    return getLogger(clazz.getName());
  }

  public static Log getLogger(String logger) {
    try {
      return logConstructor.newInstance(logger);
    } catch (Throwable t) {
      throw new LogException("Error creating logger for logger " + logger + ".  Cause: " + t, t);
    }
  }
}
