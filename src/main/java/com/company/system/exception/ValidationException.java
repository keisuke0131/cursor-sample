package com.company.system.exception;

/**
 * バリデーションエラーの例外
 */
public class ValidationException extends RuntimeException {

    public ValidationException(String message) {
        super(message);
    }
}

