package com.mylive.project.loggy.youtrack;

//Coppies Exceptions from electra Exception Checker
public class ExceptionChecker {
    public static String [] strExceptions = {"NullPointerException",
    "AuthenticationException",
    "ArrayIndexOutOfBoundsException",
    "ConcurrentModificationException",
    "NoSuchMethodError",
    "ClassNotFoundException",
    "NoSuchElementException",
    "ClassCastException",
    "ArithmeticException",
    "OutOfMemoryError",
    "StackOverflowError",
    "IllegalMonitorStateException",
    "IllegalStateException",
    "AssertionError",
    "IllegalArgumentException",
    "NotSerializableException",
    "StringIndexOutOfBoundsException",
    "UnsupportedOperationException",
    "UnimplementedException",
    "LoginException",
    "SwsRuntimeException" };

    public static boolean checkAllowedException(String stack)
    {
        for(String exception : strExceptions)
        {
            if(stack.contains(exception))
                return true;
        }
        return false;
    }
}
