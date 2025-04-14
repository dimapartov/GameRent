package org.example.gamerent.web.viewmodels.user_input;

public class GameCreationInputModel {

    private String name;
    private String description;
    private Long brandId;


    public GameCreationInputModel() {
    }

    public GameCreationInputModel(String name, String description, Long brandId) {
        this.name = name;
        this.description = description;
        this.brandId = brandId;
    }


    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Long getBrandId() {
        return brandId;
    }


    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setBrandId(Long brandId) {
        this.brandId = brandId;
    }

}