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

    public void setId(Long id) {
        this.id = id;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPostalAddress(String postalAddress) {
        this.postalAddress = postalAddress;
    }

    public void setOgrn(String ogrn) {
        this.ogrn = ogrn;
    }

    public void setInn(String inn) {
        this.inn = inn;
    }

    public void setKpp(String kpp) {
        this.kpp = kpp;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
}