package org.apel.show.attach.service.util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 日志工具类
 * @author wangbowen
 *  @date 2015年12月11日
 * @version 1.0
 */
public class LoggerUtils {
    /**
     * log对象
     */
//    private static org.apache.log4j.Logger log = org.apache.log4j.Logger.getRootLogger();
    private static Logger log = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
    /**
     * 日志信息
     * @param message 输出信息
     */
    final public static void info(String message) {
        log.info(message);
    }
    /**
     * 调试信息
     * @param message 输出信息
     */
    final public static void debug(String message) {
        log.debug(message);
    }
    /**
     * 错误信息
     * @param message 输出信息
     */
    final public static void error(String message) {
        log.error(message);
    }
    
    /**
     * 错误信息
     * @param message 输出信息
     */
    final public static void error(String message,Throwable e) {
        log.error(message,e);
    }
    /**
     * 警告信息
     * @param message 输出信息
     */
    final public static void warn(String message,Throwable e) {
        log.warn(message,e);
    }
    /**
     * 警告信息
     * @param message 输出信息
     */
    final public static void warn(String message) {
    	log.warn(message);
    }
    /**
     * 严重错误信息
     * @param message 输出信息
     */
    final public static void fatal(String message) {
//        log.fatal(message);
    }

}