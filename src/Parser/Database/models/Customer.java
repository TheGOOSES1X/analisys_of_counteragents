package Parser.Database.models;

import jakarta.persistence.*;

import javax.swing.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "customers")
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "full_name", columnDefinition = "TEXT")
    private String fullName;

    @Column(name = "short_name")
    private String shortName;

    @Column(name = "consolidated_register_code")
    private String consolidatedRegisterCode;

    @Column(name = "registration_date")
    private LocalDate registrationDate;

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;

    @Column(name = "inn")
    private String inn;

    @Column(name = "kpp")
    private String kpp;

    @Column(name = "ogrn")
    private String ogrn;

    @Column(name = "oktmo")
    private String oktmo;

    @Column(name = "location")
    private String location;

    @Column(name = "iku")
    private String iku;

    @Column(name = "iku_assignment_date")
    private LocalDate ikuAssignmentDate;

    @Column(name = "okfs_code")
    private String okfsCode;

    @Column(name = "ownership_form_name")
    private String ownershipFormName;

    @Column(name = "okopf_code")
    private String okopfCode;

    @Column(name = "legal_form_name")
    private String legalFormName;

    @Column(name = "organization_authorities", columnDefinition = "TEXT")
    private String organizationAuthorities;

    @Column(name = "unique_registration_number")
    private String uniqueRegistrationNumber;

    @Column(name = "tax_registration_date")
    private LocalDate taxRegistrationDate;

    @Column(name = "organization_type")
    private String organizationType;

    @Column(name = "organization_level")
    private String organizationLevel;

    @Column(name = "okved", columnDefinition = "TEXT")
    private String okved;

    @Column(name = "consolidated_register_code_alt")
    private String consolidatedRegisterCodeAlt;

    @Column(name = "authorized_organization_name")
    private String authorizedOrganizationName;

    @Column(name = "phone")
    private String phone;

    @Column(name = "fax")
    private String fax;

    @Column(name = "postal_address")
    private String postalAddress;

    @Column(name = "email")
    private String email;

    @Column(name = "website")
    private String website;

    @Column(name = "contact_person")
    private String contactPerson;

    @Column(name = "time_zone")
    private String timeZone;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Purchase> purchases = new ArrayList<>();

    // Геттеры и сеттеры

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getConsolidatedRegisterCode() {
        return consolidatedRegisterCode;
    }

    public void setConsolidatedRegisterCode(String consolidatedRegisterCode) {
        this.consolidatedRegisterCode = consolidatedRegisterCode;
    }

    public LocalDate getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDate registrationDate) {
        this.registrationDate = registrationDate;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public String getInn() {
        return inn;
    }

    public void setInn(String inn) {
        this.inn = inn;
    }

    public String getKpp() {
        return kpp;
    }

    public void setKpp(String kpp) {
        this.kpp = kpp;
    }

    public String getOgrn() {
        return ogrn;
    }

    public void setOgrn(String ogrn) {
        this.ogrn = ogrn;
    }

    public String getOktmo() {
        return oktmo;
    }

    public void setOktmo(String oktmo) {
        this.oktmo = oktmo;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getIku() {
        return iku;
    }

    public void setIku(String iku) {
        this.iku = iku;
    }

    public LocalDate getIkuAssignmentDate() {
        return ikuAssignmentDate;
    }

    public void setIkuAssignmentDate(LocalDate ikuAssignmentDate) {
        this.ikuAssignmentDate = ikuAssignmentDate;
    }

    public String getOkfsCode() {
        return okfsCode;
    }

    public void setOkfsCode(String okfsCode) {
        this.okfsCode = okfsCode;
    }

    public String getOwnershipFormName() {
        return ownershipFormName;
    }

    public void setOwnershipFormName(String ownershipFormName) {
        this.ownershipFormName = ownershipFormName;
    }

    public String getOkopfCode() {
        return okopfCode;
    }

    public void setOkopfCode(String okopfCode) {
        this.okopfCode = okopfCode;
    }

    public String getLegalFormName() {
        return legalFormName;
    }

    public void setLegalFormName(String legalFormName) {
        this.legalFormName = legalFormName;
    }

    public String getOrganizationAuthorities() {
        return organizationAuthorities;
    }

    public void setOrganizationAuthorities(String organizationAuthorities) {
        this.organizationAuthorities = organizationAuthorities;
    }

    public String getUniqueRegistrationNumber() {
        return uniqueRegistrationNumber;
    }

    public void setUniqueRegistrationNumber(String uniqueRegistrationNumber) {
        this.uniqueRegistrationNumber = uniqueRegistrationNumber;
    }

    public LocalDate getTaxRegistrationDate() {
        return taxRegistrationDate;
    }

    public void setTaxRegistrationDate(LocalDate taxRegistrationDate) {
        this.taxRegistrationDate = taxRegistrationDate;
    }

    public String getOrganizationType() {
        return organizationType;
    }

    public void setOrganizationType(String organizationType) {
        this.organizationType = organizationType;
    }

    public String getOrganizationLevel() {
        return organizationLevel;
    }

    public void setOrganizationLevel(String organizationLevel) {
        this.organizationLevel = organizationLevel;
    }

    public String getOkved() {
        return okved;
    }

    public void setOkved(String okved) {
        this.okved = okved;
    }

    public String getConsolidatedRegisterCodeAlt() {
        return consolidatedRegisterCodeAlt;
    }

    public void setConsolidatedRegisterCodeAlt(String consolidatedRegisterCodeAlt) {
        this.consolidatedRegisterCodeAlt = consolidatedRegisterCodeAlt;
    }

    public String getAuthorizedOrganizationName() {
        return authorizedOrganizationName;
    }

    public void setAuthorizedOrganizationName(String authorizedOrganizationName) {
        this.authorizedOrganizationName = authorizedOrganizationName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getPostalAddress() {
        return postalAddress;
    }

    public void setPostalAddress(String postalAddress) {
        this.postalAddress = postalAddress;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }
    public List<Purchase> getPurchases() {
        return new ArrayList<>(purchases); // Возвращает копию
    }
    public void setPurchases(List<Purchase> purchases) {
        if (purchases == null) {
            this.purchases.clear();
        } else {
            this.purchases = new ArrayList<>(purchases);
            // Обновляем обратные ссылки
            this.purchases.forEach(p -> p.setCustomer(this));
        }
    }
}
