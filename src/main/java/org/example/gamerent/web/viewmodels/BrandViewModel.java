package org.example.gamerent.web.viewmodels;

public class BrandViewModel {

    private String name;
    private String description;
    private String photo;


    public BrandViewModel() {
    }


    public BrandViewModel(String name, String description, String photo) {
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