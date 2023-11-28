package org.zb.plugin.putil;


import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.MDC;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * 多线程执行工具类
 *
 * @author :
 * @date :  2021-05-21
 */
public class CompletableUtil {

    public static final String TRACE_ID = "bizId";

    public static ThreadPoolExecutor defaultExecutor;

    private static final ThreadLocal<Object> CACHE_LOCAL = new ThreadLocal<>();

    /**
     * 异步执行单任务
     */
    public static CompletableFuture<Void> runSingleAsync(Supplier fun) {
        if (defaultExecutor == null) {
            initExecutor();
        }
        return runSingleAsync(fun, defaultExecutor);
    }

    /**
     * 异步执行单任务, 指定 线程池
     */
    public static CompletableFuture<Void> runSingleAsync(Supplier fun, Executor executor) {
        Object cacheObject = getCacheLocal();
        String mastNo = UUID.randomUUID().toString().replaceAll("-", "");
        logQueueSize(executor);
        return CompletableFuture.runAsync(() -> {
            try {
                Map<String, String> contextMap = MDC.getCopyOfContextMap();
                setContent(contextMap, cacheObject, mastNo);
                fun.get();
            } finally {
                MDC.clear();
                remove();
            }
        }, executor);
    }

    private static void logQueueSize(Executor executor) {

    }

    /**
     * 多线程异步执行批量任务，等待全部任务执行完
     */
    public static <P> void runAndWaitAsync(List<P> list, Consumer<P> fun) {
        if (defaultExecutor == null) {
            initExecutor();
        }
        runAndWaitAsync(list, fun, defaultExecutor);

    }

    /**
     * 多线程异步执行批量任务，等待全部任务执行完
     */
    public static <P> void runAndWaitAsync(List<P> list, Consumer<P> fun, Executor executor) {
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        if (list.size() == 1) {
            P p = list.get(0);
            fun.accept(p);
            return;
        }
        Object cacheObject = getCacheLocal();
        String mastNo = UUID.randomUUID().toString().replaceAll("-", "");
        logQueueSize(executor);
        try {
            CompletableFuture.allOf(list.stream().map(t -> CompletableFuture.runAsync(() -> {
                try {
                    Map<String, String> contextMap = MDC.getCopyOfContextMap();
                    setContent(contextMap, cacheObject, mastNo);
                    fun.accept(t);
                } finally {
                    MDC.clear();
                    remove();
                }
            }, executor)).toArray(CompletableFuture[]::new)).join();
        } catch (Exception e) {
            throw getBaseException(e);
        }

    }

    public static ThreadPoolExecutor getDefaultExecutor() {
        if (defaultExecutor == null) {
            initExecutor();
        }
        return defaultExecutor;
    }

    private static synchronized void initExecutor() {
        if (defaultExecutor == null) {
            defaultExecutor = new ThreadPoolExecutor(10, 15,
                    60, TimeUnit.SECONDS, new LinkedBlockingQueue<>(1000), new ThreadFactoryBuilder()
                    .setNameFormat("Executor-%s").build(), new ThreadPoolExecutor.CallerRunsPolicy());
        }

    }

    /**
     * 主线程 设置传递对象，子线程使用。  使用方法， 必须主动调用 remove()
     */
    public static void setCacheLocal(Object o) {
        if (o != null) {
            CACHE_LOCAL.set(o);
        }

    }

    public static Object getCacheLocal() {
        return CACHE_LOCAL.get();
    }

    public static void remove() {
        CACHE_LOCAL.remove();
    }


    private static void setContent(Map<String, String> contextMap, Object cacheObject, String mastNo) {
        if (contextMap == null) {
            contextMap = new HashMap<>(8);
        }
        if (!contextMap.containsKey(TRACE_ID)) {
            contextMap.put(TRACE_ID, mastNo + "-" + RandomStringUtils.randomAlphanumeric(6));
        }
        MDC.setContextMap(contextMap);
        setCacheLocal(cacheObject);
    }

    public static String getRealMessage(Throwable e) {
        // 如果e不为空，则去掉外层的异常包装
        while (e != null) {
            Throwable cause = e.getCause();
            if (cause == null) {
                return e.getMessage();
            }
            e = cause;
        }
        return "";
    }

    public static RuntimeException getBaseException(Throwable e) {
        // 如果e不为空，则去掉外层的异常包装
        while (e != null) {
            if (e instanceof CompletionException) {
                Throwable cause = e.getCause();
                if (cause == null) {
                    return new RuntimeException(e.getMessage());
                }
                e = cause;
            } else {
                if (e instanceof RuntimeException) {
                    return (RuntimeException) e;
                } else {
                    return new RuntimeException(getRealMessage(e));
                }
            }

        }
        return null;
    }
}
