package com.daniel.pfm.enums;

public enum TransactionType {

    INCOME("income"),
    EXPENSE("expense");

    private final String value;

    TransactionType(String value){
        this.value = value;
    }

    public String getValue(){
        return value;
    }

}
