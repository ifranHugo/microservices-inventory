package com.appEscalable.inventory_service.model.dtos;

public record BaseResponse (String[] errorMessage){

    public boolean hasError(){
        return errorMessage != null && errorMessage.length > 0;
    }

}
