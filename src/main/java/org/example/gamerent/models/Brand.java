package org.example.gamerent.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import org.example.gamerent.models.base.IdCreatedModified;


@Entity
@Table(name = "brands")
public class Brand extends IdCreatedModified {

    private String name;
    private String description;
    private String imagePath;



}