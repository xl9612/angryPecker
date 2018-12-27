package com.definesys.angrypecker.exception;

/**
 * 屠龙所有异常处理
 */
public class DragonException extends RuntimeException {

    public DragonException(){
        super();
    }

    public DragonException(String message){
        super(message);
    }
}
