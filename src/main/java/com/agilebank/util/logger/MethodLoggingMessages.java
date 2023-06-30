package com.agilebank.util.logger;

import java.util.Arrays;
import org.aspectj.lang.JoinPoint;

public class MethodLoggingMessages {

  public static String msg(Loc pointInMethod, JoinPoint jp) {
    String args = Arrays.toString(jp.getArgs());
    return ((pointInMethod == Loc.BEGIN) ? "Making" : "Completed")
        + " the call " + jp.getSignature().toShortString()
        + " with arguments: "
        + ((args.length() == 2) ? "()" : args.substring(1, args.length() - 1)); // args.length == 2 means that args = "[]", i.e no args
  }
  
  public static String msg(JoinPoint jp, Class<? extends Throwable> throwable){
    return "Method " + jp.getSignature().toShortString() + " threw an instance of " + throwable.getSimpleName() + "!";
  }
}
