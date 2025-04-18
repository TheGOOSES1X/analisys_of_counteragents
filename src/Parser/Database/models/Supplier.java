package Parser.Database.models;

import jakarta.persistence.*;

@Entity
@Table(name = "suppliers")
public class Supplier {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "type")
    private String type;

    @Column(name = "name", nullable = false)

    private String name;

    @Column(name = "country_name")
    private String countryName;

    @Column(name = "country_code", length = 3)
    private String countryCode;

    @Column(name = "address", columnDefinition = "TEXT")
    private String address;

    @Column(name = "postal_address", columnDefinition = "TEXT")
    private String postalAddress;

    @Column(name = "ogrn", length = 13)
    private String ogrn;

    @Column(name = "inn", length = 12)
    private String inn;

    @Column(name = "kpp", length = 9)
    private String kpp;

    @Column(name = "status")
    private String status;

    @Column(name = "email")
    private String email;

    @Column(name = "phone")
    private String phone;
}