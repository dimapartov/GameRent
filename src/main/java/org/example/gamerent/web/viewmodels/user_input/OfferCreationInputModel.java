package org.example.gamerent.web.viewmodels.user_input;

import org.example.gamerent.models.consts.OfferStatus;

import java.math.BigDecimal;


public class OfferCreationInputModel {

    private String description;
    private BigDecimal price;
    private OfferStatus status;
    private String photo;
    private String brand;
    private String gameName;


    public OfferCreationInputModel() {
    }



}