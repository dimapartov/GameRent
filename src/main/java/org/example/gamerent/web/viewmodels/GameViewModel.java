package org.example.gamerent.web.viewmodels;

public class GameViewModel {
    private Long id;
    private String name;
    private String description;
    private BrandViewModel brand;

    public GameViewModel() {
    }

    public GameViewModel(Long id, String name, String description, BrandViewModel brand) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.brand = brand;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public BrandViewModel getBrand() {
        return brand;
    }

    public void setBrand(BrandViewModel brand) {
        this.brand = brand;
    }

    @Override
    public String toString() {
        return "GameViewModel{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", brand=" + brand +
                '}';
    }
}