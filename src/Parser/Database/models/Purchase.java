package Parser.Database.models;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "purchases")
public class Purchase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "purchase_number", unique = true)
    private String purchaseNumber;

    @Column(name = "law")
    private String law;

    @Column(name = "initial_max_price", precision = 19, scale = 2)
    private BigDecimal initialMaxPrice;

    @Column(name = "currency")
    private String currency;

    @Column(name = "purchase_object")
    private String purchaseObject;

    @Column(name = "procurement_method")
    private String procurementMethod;

    @Column(name = "ikz")
    private String ikz;

    @OneToOne
    @JoinColumn(name = "customer_id", nullable = true) // Разрешаем null, если customer неизвестен
    private Customer customer;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "contract_id") // внешний ключ в таблице purchases
    private Contract contract;

    @Column(name = "executor")
    private String executor;

    @Column(name = "publication_date")
    private LocalDate publicationDate;

    @Column(name = "update_date")
    private LocalDateTime updateDate;

    @Column(name = "application_end_date")
    private LocalDate applicationEndDate;

    @Column(name = "auction_date")
    private LocalDate auctionDate;

    @Column(name = "procurement_stage")
    private String procurementStage;




    // Геттеры и сеттеры
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPurchaseNumber() {
        return purchaseNumber;
    }

    public void setPurchaseNumber(String purchaseNumber) {
        this.purchaseNumber = purchaseNumber;
    }

    public String getLaw() {
        return law;
    }

    public void setLaw(String law) {
        this.law = law;
    }

    public BigDecimal getInitialMaxPrice() {
        return initialMaxPrice;
    }

    public void setInitialMaxPrice(BigDecimal initialMaxPrice) {
        this.initialMaxPrice = initialMaxPrice;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getPurchaseObject() {
        return purchaseObject;
    }

    public void setPurchaseObject(String purchaseObject) {
        this.purchaseObject = purchaseObject;
    }

    public String getProcurementMethod() {
        return procurementMethod;
    }

    public void setProcurementMethod(String procurementMethod) {
        this.procurementMethod = procurementMethod;
    }

    public String getIkz() {
        return ikz;
    }

    public void setIkz(String ikz) {
        this.ikz = ikz;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public String getExecutor() {
        return executor;
    }

    public void setExecutor(String executor) {
        this.executor = executor;
    }

    public LocalDate getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(LocalDate publicationDate) {
        this.publicationDate = publicationDate;
    }

    public LocalDateTime getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(LocalDateTime updateDate) {
        this.updateDate = updateDate;
    }

    public LocalDate getApplicationEndDate() {
        return applicationEndDate;
    }

    public void setApplicationEndDate(LocalDate applicationEndDate) {
        this.applicationEndDate = applicationEndDate;
    }

    public LocalDate getAuctionDate() {
        return auctionDate;
    }

    public void setAuctionDate(LocalDate auctionDate) {
        this.auctionDate = auctionDate;
    }

    public String getProcurementStage() {
        return procurementStage;
    }

    public void setProcurementStage(String procurementStage) {
        this.procurementStage = procurementStage;
    }

    public Contract getContract() {
        return contract;
    }

    public void setContract(Contract contract) {
        this.contract = contract;
    }
}