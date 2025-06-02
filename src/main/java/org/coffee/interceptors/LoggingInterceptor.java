package org.coffee.interceptors;

import org.coffee.annotations.Logged;

import javax.annotation.Priority;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import java.io.Serializable;
import java.util.Arrays;
import java.util.logging.Logger;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.SimpleFormatter;

@Logged
@Interceptor
@Priority(Interceptor.Priority.APPLICATION)
public class LoggingInterceptor implements Serializable {
    private static final Logger LOGGER = Logger.getLogger(LoggingInterceptor.class.getName());
    
    static {
        try {
            FileHandler fileHandler = new FileHandler("application.log", true);

            SimpleFormatter formatter = new SimpleFormatter();
            fileHandler.setFormatter(formatter);

            LOGGER.addHandler(fileHandler);
            LOGGER.setUseParentHandlers(false);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @AroundInvoke
    public Object logMethod(InvocationContext ctx) throws Exception {
        long start = System.currentTimeMillis();
        String className = ctx.getTarget().getClass().getSimpleName();
        String methodName = ctx.getMethod().getName();
        String params = Arrays.toString(ctx.getParameters());
        LOGGER.info(() -> String.format("Entering %s.%s with parameters %s", className, methodName, params));
        try {
            Object result = ctx.proceed();
            long duration = System.currentTimeMillis() - start;
            LOGGER.info(() -> String.format("Exiting %s.%s, returned: %s, duration: %dms",
                    className, methodName, result, duration));
            return result;
        } catch (Exception e) {
            LOGGER.severe(() -> String.format("Exception in %s.%s: %s", className, methodName, e.getMessage()));
            throw e;
        }
    }
}