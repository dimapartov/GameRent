package org.example.gamerent.services.dto;

public class ReviewFiltersDTO {

    private String sortBy;


    public ReviewFiltersDTO() {
    }

    public ReviewFiltersDTO(String sortBy) {
        this.sortBy = sortBy;
    }


    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

}