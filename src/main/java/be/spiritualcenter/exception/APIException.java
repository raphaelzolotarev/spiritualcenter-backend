package be.spiritualcenter.exception;
/*
 * @author Raphael Zolotarev
 * @version 1.0
 * @license Copyright (c) 2025 www.zolotarev.eu
 * @since 03/03/2025
 */
public class APIException extends RuntimeException{
    public APIException(String msg){
        super(msg);
    }
}
