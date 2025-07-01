package Parser.Database.models;

import jakarta.persistence.*;
import org.hibernate.annotations.NaturalId;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "suppliers")
public class Supplier {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "type", columnDefinition = "TEXT")
    private String type;

    @NaturalId
    @Column(name = "name", columnDefinition = "TEXT")
    private String name;

    @Column(name = "country_name", columnDefinition = "TEXT")
    private String countryName;

    @Column(name = "country_code", columnDefinition = "TEXT")
    private String countryCode;

    @Column(name = "address", columnDefinition = "TEXT")
    private String address;

    @Column(name = "postal_address", columnDefinition = "TEXT")
    private String postalAddress;

    @Column(name = "ogrn", length = 13)
    private String ogrn;

    @Column(name = "inn", columnDefinition = "TEXT",unique = true)
    private String inn;

    @Column(name = "kpp", length = 9)
    private String kpp;

    @Column(name = "status", columnDefinition = "TEXT")
    private String status;

    @Column(name = "email", columnDefinition = "TEXT")
    private String email;

    @Column(name = "phone" , columnDefinition = "TEXT")
    private String phone;

    @OneToMany(mappedBy = "supplier", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Contract> contracts = new ArrayList<>();


    @OneToMany(mappedBy = "supplier", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<JudicialProceeding> judicialProceedings = new ArrayList<>();


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

    public String getName() {
        return name;
    }
    // Геттеры
    public Long getId() {
        return id;
    }

    public String getType() {
        return type;
    }


    public String getCountryName() {
        return countryName;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public String getAddress() {
        return address;
    }

    public String getPostalAddress() {
        return postalAddress;
    }

    public String getOgrn() {
        return ogrn;
    }

    public String getInn() {
        return inn;
    }

    public String getKpp() {
        return kpp;
    }

    public String getStatus() {
        return status;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public List<Contract> getContracts() {
        return contracts;
    }
    public List<JudicialProceeding> getJudicialProceedings() {
        return judicialProceedings;
    }

    public void setJudicialProceedings(List<JudicialProceeding> judicialProceedings) {
        this.judicialProceedings = judicialProceedings;
    }

}