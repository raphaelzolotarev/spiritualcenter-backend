package be.spiritualcenter.enums;
/*
 * @author Raphael Zolotarev
 * @version 1.0
 * @license Copyright (c) 2025 www.zolotarev.eu
 * @since 03/03/2025
 */
public enum VerificationType {
    ACCOUNT("ACCOUNT"),
    PASSWORD("PASSWORD");

    private final String type;
    VerificationType(String type){
        this.type = type;
    }
    public String getType(){
        return this.type.toLowerCase();
    }
}