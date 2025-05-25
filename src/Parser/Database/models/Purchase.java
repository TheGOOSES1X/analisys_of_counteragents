package Parser.Database.models;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "purchases")
public class Purchase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "purchase_number", unique = true, columnDefinition = "text")
    private String purchaseNumber;

    @Column(name = "law", columnDefinition = "text")
    private String law;

    @Column(name = "initial_max_price", precision = 19, scale = 2)
    private BigDecimal initialMaxPrice;

    @Column(name = "currency", columnDefinition = "text")
    private String currency;

    @Column(name = "purchase_object", columnDefinition = "text")
    private String purchaseObject;

    @Column(name = "procurement_method", columnDefinition = "text")
    private String procurementMethod;

    @Column(name = "ikz", columnDefinition = "text")
    private String ikz;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "customer_id", nullable = true)
    private Customer customer;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "contract_id", unique = true) // добавьте unique=true
    private Contract contract;

    @Column(name = "executor", columnDefinition = "text")
    private String executor;

    @Column(name = "publication_date")
    private LocalDate publicationDate;

    @Column(name = "update_date")
    private LocalDate updateDate;

    @Column(name = "application_end_date")
    private LocalDate applicationEndDate;

    @Column(name = "auction_date")
    private LocalDate auctionDate;

    @Column(name = "complaints")
    private Integer complaints;

    @Column(name = "procurement_stage", columnDefinition = "text")
    private String procurementStage;


    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "purchase_id") // Это создаст столбец purchase_id в таблице procurement_objects
    private List<ProcurementObject> procurementObjects = new ArrayList<>();




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

    public LocalDate getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(LocalDate updateDate) {
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

    public Integer getComplaints() {
        return complaints;
    }

    public void setProcurementStage(String procurementStage) {
        this.procurementStage = procurementStage;
    }
    public void setComplaints(Integer Complaints) {
        this.complaints = Complaints;
    }
    public Contract getContract() {
        return contract;
    }

    public void setContract(Contract contract) {
        this.contract = contract;
    }
    public void addProcurementObject(ProcurementObject procurementObject) {
        procurementObjects.add(procurementObject);
    }

    public List<ProcurementObject> getProcurementObjects() {
        return procurementObjects;
    }


}