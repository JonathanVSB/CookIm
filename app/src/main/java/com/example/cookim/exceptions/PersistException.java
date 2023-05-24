package com.example.cookim.exceptions;

/**
 * The PersistException class represents an exception that occurs during the persistence (data storage) process.
 * It extends the Exception class.
 */
public class PersistException extends Exception {

    private int code;

    /**
     * Constructs a new PersistException object with the specified message and code.
     *
     * @param message the detail message of the exception
     * @param code    the error code associated with the exception
     */
    PersistException(String message, int code) {
        super(message);
        this.code = code;
        this.toString();
    }

    /**
     * Constructs a new PersistException object with the specified code.
     *
     * @param code the error code associated with the exception
     */
    public PersistException(int code) {
        this.code = code;
    }

    /**
     * Returns the error code associated with the exception.
     *
     * @return the error code
     */
    public int getCode() {
        return code;
    }

    /**
     * Sets the error code for the exception.
     *
     * @param code the error code to set
     */
    public void setCode(int code) {
        this.code = code;
    }

    /**
     * Returns a string representation of the PersistException object.
     *
     * @return a string representation of the object
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("PersistException{");
        sb.append("message=").append(getMessage());
        sb.append("code=").append(code);
        sb.append('}');
        return sb.toString();
    }
}
