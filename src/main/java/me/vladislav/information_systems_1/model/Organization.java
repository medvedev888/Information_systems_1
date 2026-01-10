package me.vladislav.information_systems_1.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.util.Date;

@Entity
@Table(name = "organizations")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Cacheable
@org.hibernate.annotations.Cache(
        usage = CacheConcurrencyStrategy.READ_WRITE
)
public class Organization {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;

    @OneToOne
    @JoinColumn(name = "coordinates_id", nullable = false)
    private Coordinates coordinates;

    @Column(nullable = false)
    private Date creationDate;

    @OneToOne
    @JoinColumn(name = "official_address_id", nullable = false)
    private Address officialAddress;

    @Column(name = "annual_turnover")
    @org.hibernate.annotations.Check(constraints = "annual_turnover IS NULL OR annual_turnover > 0")
    private Double annualTurnover;

    @Column(name = "employees_count")
    @org.hibernate.annotations.Check(constraints = "employees_count > 0")
    private Integer employeesCount;

    @Column(name="rating", nullable = false)
    @org.hibernate.annotations.Check(constraints = "rating > 0")
    private Double rating;

    @Column
    private String fullName;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private OrganizationType type;

    @OneToOne
    @JoinColumn(name = "postal_address_id")
    private Address postalAddress;

    @PrePersist
    protected void onCreate() {
        creationDate = new Date();
    }
}