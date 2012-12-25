package ru.histone.utils;

/**
 * @author sazonovkirill@gmail.com
 * @version $Id: AssertException.java 108 2012-12-22 07:59:28Z sazonovkirill $
 */
public class AssertException extends RuntimeException {
    public AssertException() {
    }

    public AssertException(String message) {
        super(message);
    }

    public AssertException(String message, Throwable cause) {
        super(message, cause);
    }

    public AssertException(Throwable cause) {
        super(cause);
    }
}
