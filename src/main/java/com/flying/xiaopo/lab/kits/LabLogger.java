package com.flying.xiaopo.lab.kits;

import com.intellij.openapi.diagnostic.Logger;
import org.apache.log4j.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author wupanjie on 2017/10/26.
 */
public class LabLogger {

  private static Logger logger = Logger.getInstance("SnippetLab");

  private LabLogger() {
    //no instance
  }

  public boolean isDebugEnabled() {
    return true;
  }

  public static void debug(String s) {
    logger.debug(s);
  }

  public static void debug(@Nullable Throwable throwable) {
    logger.debug(throwable);
  }

  public static void debug(String s, @Nullable Throwable throwable) {
    logger.debug(s, throwable);
  }

  public static void info(String s) {
    logger.info(s);
  }

  public static void info(String s, @Nullable Throwable throwable) {
    logger.info(s);
  }

  public static void warn(String s, @Nullable Throwable throwable) {
    logger.warn(s, throwable);
  }

  public static void warn(String s) {
    logger.warn(s);
  }

  public static void error(String s, @Nullable Throwable throwable, @NotNull String... strings) {
    logger.error(s, throwable, strings);
  }

  public static void setLevel(Level level) {
    logger.setLevel(level);
  }
}
