package com.example.soonsul.liquor.entity;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@AllArgsConstructor
@Builder
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name="evaluation_number")
public class EvaluationNumber {

    @Id
    @Column(name = "liquor_id", nullable = false, unique = true)
    private String liquorId;

    @Column(name = "average_rating")
    private Integer averageRating;

    @Column(name = "sweetness")
    private Integer sweetness;

    @Column(name = "acidity")
    private Integer acidity;

    @Column(name = "carbonic_acid")
    private Integer carbonicAcid;

    @Column(name = "heavy")
    private Integer heavy;

    @Column(name = "scent")
    private Integer scent;

    @Column(name = "density")
    private Integer density;


    public void addAverageRating(CalculationType cType){
        switch (cType){
            case ADD:
                this.averageRating++;
                break;
            case SUB:
                this.averageRating--;
                break;
        }
    }


    public void updateFlavor(FlavorType fType, CalculationType cType){
        switch (fType){
            case SWEETNESS:
                if(cType.equals(CalculationType.ADD)) this.sweetness++;
                else this.sweetness--;
                break;
            case ACIDITY:
                if(cType.equals(CalculationType.ADD)) this.acidity++;
                else this.acidity--;
                break;
            case CARBONIC_ACID:
                if(cType.equals(CalculationType.ADD)) this.carbonicAcid++;
                else this.carbonicAcid--;
                break;
            case HEAVY:
                if(cType.equals(CalculationType.ADD)) this.heavy++;
                else this.heavy--;
                break;
            case SCENT:
                if(cType.equals(CalculationType.ADD)) this.scent++;
                else this.scent--;
                break;
            case DENSITY:
                if(cType.equals(CalculationType.ADD)) this.density++;
                else this.density--;
                break;
        }
    }


    public Integer getFlavor(FlavorType fType){
        switch (fType){
            case SWEETNESS:
                return this.sweetness;
            case ACIDITY:
                return this.acidity;
            case CARBONIC_ACID:
                return this.carbonicAcid;
            case HEAVY:
                return this.heavy;
            case SCENT:
                return this.scent;
            case DENSITY:
                return this.density;
            default:
                return 1;
        }
    }
}
