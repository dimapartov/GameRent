package org.example.gamerent.web.viewmodels.user_input;

public class BrandCreationInputModel {

    private String name;
    private String description;
    private String photo;


    public BrandCreationInputModel() {
    }

    public BrandCreationInputModel(String name, String description, String photo) {
        this.name = name;
        this.description = description;
        this.photo = photo;
    }


    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getPhoto() {
        return photo;
    }
    public void setPhoto(String photo) {
        this.photo = photo;
    }

}