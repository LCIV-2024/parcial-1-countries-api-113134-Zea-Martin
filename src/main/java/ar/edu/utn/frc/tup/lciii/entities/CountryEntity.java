package ar.edu.utn.frc.tup.lciii.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Entity
@Table(name = "countries")
public class CountryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code;
    private String name;
    private long population;
    private double area;
}
