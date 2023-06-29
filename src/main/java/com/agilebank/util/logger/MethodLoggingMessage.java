package com.agilebank.util.logger;

import org.aspectj.lang.JoinPoint;

import java.util.Arrays;

public class MethodLoggingMessage {

    public static String  msg(final String method, final Loc pointInMethod, final JoinPoint jp) {
        final String args = Arrays.toString(jp.getArgs());

        return ((pointInMethod == Loc.BEGIN) ? "Serving" : "Served")
                + " a "
                + method
                + " request via a call to "
                + jp.getSignature().getDeclaringTypeName()
                + "."
                + jp.getSignature().getName()
                + "("
                + ((args.length() == 0) ? "" : args.substring(1, args.length() - 1))
                + ")";
    }
}
