package com.girigiri.controller.utils;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * Created by JianGuo on 6/28/16.
 * POJO used in json format representing bad request error message
 */
@JsonPropertyOrder({"entity", "message", "invalidValue", "property"})
public class ErrorMessage {


    private String property;
    private String entity;
    private String invalidValue;
    private String message;


    public ErrorMessage() {

    }

    public ErrorMessage(String message, String invalidValue, String entity, String property) {
        this.message = message;
        this.invalidValue = invalidValue;
        this.entity = entity;
        this.property = property;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }

    public void setInvalidValue(String invalidValue) {
        this.invalidValue = invalidValue;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getInvalidValue() {
        return invalidValue;
    }

    public String getMessage() {
        return message;
    }
}