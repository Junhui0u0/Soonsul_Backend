package com.example.soonsul.liquor.entity;

import lombok.*;

import javax.persistence.*;

@Getter
@AllArgsConstructor
@Builder
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name="liquor")
public class Liquor {

    @Id
    @Column(name = "liquor_id", nullable = false, unique = true)
    private String liquorId;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "ingredient")
    private String ingredient;

    @Column(name = "average_rating")
    private Double averageRating;

    @Column(name = "lowest_price")
    private Long lowestPrice;

    @Column(name = "alcohol")
    private Double alcohol;

    @Column(name = "capacity")
    private Integer capacity;

    @Column(name = "view_count")
    private Long viewCount;

    @Column(name = "region")
    private String region;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "liquor_category")
    private String liquorCategory;

    @Column(name = "brewery")
    private String brewery;

    @Column(name = "location")
    private String location;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "sale_place")
    private String salePlace;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "site_url")
    private String siteUrl;



    public void updateAverageRating(Double averageRating){
        this.averageRating= averageRating;
    }

    public void updateImageUrl(String imageUrl){
        this.imageUrl= imageUrl;
    }

    public void updateLiquor(String name, String ingredient, Long lowestPrice, String region,
                             Double alcohol, Integer capacity, String imageUrl, String liquorCategory, String location, String brewery,
                             String salePlace, String phoneNumber, String siteUrl, Double latitude, Double longitude){
        this.name= name;
        this.ingredient= ingredient;
        this.lowestPrice= lowestPrice;
        this.region= region;
        this.alcohol= alcohol;
        this.capacity= capacity;
        //this.imageUrl= imageUrl;
        this.liquorCategory= liquorCategory;
        this.location= location;
        this.brewery= brewery;
        this.salePlace= salePlace;
        this.phoneNumber= phoneNumber;
        this.siteUrl= siteUrl;
        this.latitude= latitude;
        this.longitude= longitude;
    }

}
