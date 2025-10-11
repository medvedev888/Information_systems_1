package me.vladislav.information_systems_1.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Table(name = "coordinates")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Organization {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;

    @OneToOne
    @org.hibernate.annotations.Check(constraints = "coordinates IS NOT NULL")
    private Coordinates coordinates;

    @Column(nullable = false)
    private Date creationDate;

    @OneToOne
    @org.hibernate.annotations.Check(constraints = "official_address IS NOT NULL")
    private Address officialAddress;

    @Column
    @org.hibernate.annotations.Check(constraints = "annual_turnover IS NULL OR annual_turnover > 0")
    private Double annualTurnover;

    @Column
    @org.hibernate.annotations.Check(constraints = "employees_count > 0")
    private Integer employeesCount;

    @Column(nullable = false)
    @org.hibernate.annotations.Check(constraints = "rating > 0")
    private Double rating;

    @Column
    private String fullName;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private OrganizationType type;

    @OneToOne
    private Address postalAddress;

    @PrePersist
    protected void onCreate() {
        creationDate = new Date();
    }
}