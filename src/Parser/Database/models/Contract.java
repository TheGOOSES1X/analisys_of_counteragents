package Parser.Database.models;

import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDate;
import jakarta.persistence.*;

@Entity
@Table(name = "contracts")
public class Contract {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;



    @Column(name = "inn", length = 12)
    private String inn;

    @Column(columnDefinition = "TEXT")
    private String subject;

    @Column(columnDefinition = "TEXT")
    private String specialization;

    @Column(name = "is_defense_order", columnDefinition = "TEXT")
    private String isDefenseOrder;

    @Column(name = "is_lifecycle_contract", columnDefinition = "TEXT")
    private String isLifecycleContract;

    @Column(name = "is_quantity_undefined", columnDefinition = "TEXT")
    private String isQuantityUndefined;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "execution_stages", columnDefinition = "TEXT")
    private String executionStages;

    @Column(columnDefinition = "TEXT")
    private String country;

    @Column(columnDefinition = "TEXT")
    private String address;

    @Column(name = "additional_address_info", columnDefinition = "TEXT")
    private String additionalAddressInfo;

    @Column(name = "quality_guarantee_required", columnDefinition = "TEXT")
    private String qualityGuaranteeRequired;

    @Column(name = "warranty_requirements", columnDefinition = "TEXT")
    private String warrantyRequirements;

    @Column(name = "manufacturer_warranty_requirements", columnDefinition = "TEXT")
    private String manufacturerWarrantyRequirements;

    @Column(name = "warranty_period", columnDefinition = "TEXT")
    private String warrantyPeriod;

    @Column(name = "warranty_guarantee_required", columnDefinition = "TEXT")
    private String warrantyGuaranteeRequired;

    @Column(name = "guarantee_type", columnDefinition = "TEXT")
    private String guaranteeType;

    @Column(name = "guarantee_percentage", precision = 5, scale = 2)
    private BigDecimal guaranteePercentage;

    @Column(name = "guarantee_amount", precision = 19, scale = 2)
    private BigDecimal guaranteeAmount;

    @Column(name = "guarantee_requirements", columnDefinition = "TEXT")
    private String guaranteeRequirements;

    @Column(name = "smp_subcontractors_required", columnDefinition = "TEXT")
    private String smpSubcontractorsRequired;

    @Column(name = "smp_subcontractors_exempt", columnDefinition = "TEXT")
    private String smpSubcontractorsExempt;

    @Column(name = "smp_subcontractors_percentage", precision = 5, scale = 2)
    private BigDecimal smpSubcontractorsPercentage;

    @Column(name = "smp_subcontractors_liability", columnDefinition = "TEXT")
    private String smpSubcontractorsLiability;

    @Column(name = "unilateral_termination_allowed", columnDefinition = "TEXT")
    private String unilateralTerminationAllowed;

    @Column(name = "budget_name", columnDefinition = "TEXT")
    private String budgetName;

    @Column(name = "budget_type", columnDefinition = "TEXT")
    private String budgetType;

    @Column(name = "municipality_code", columnDefinition = "TEXT")
    private String municipalityCode;

    @Column(name = "is_self_funded",  columnDefinition = "TEXT")
    private String isSelfFunded;

    @Column(name = "banking_support_info", columnDefinition = "TEXT")
    private String bankingSupportInfo;

    @Column(name = "price_indication_method", columnDefinition = "TEXT")
    private String priceIndicationMethod;

    @Column(name = "contract_price", precision = 19, scale = 2)
    private BigDecimal contractPrice;

    @Column(name = "including_vat", precision = 19, scale = 2)
    private BigDecimal includingVat;

    @Column(name = "treasury_guarantee_amount", precision = 19, scale = 2)
    private BigDecimal treasuryGuaranteeAmount;

    @Column(name = "price_formula", columnDefinition = "TEXT")
    private String priceFormula;

    @Column(columnDefinition = "TEXT")
    private String currency;

    @Column(name = "contract_right_price", precision = 19, scale = 2)
    private BigDecimal contractRightPrice;

    @Column(name = "advance_payment_available", columnDefinition = "TEXT")
    private String advancePaymentAvailable;

    @Column(name = "advance_percentage", precision = 5, scale = 2)
    private BigDecimal advancePercentage;

    @Column(name = "advance_amount", precision = 19, scale = 2)
    private BigDecimal advanceAmount;

    @Column(name = "tax_deduction_applied", columnDefinition = "TEXT")
    private String taxDeductionApplied;

    @Column(name = "penalty_deduction_applied", columnDefinition = "TEXT")
    private String penaltyDeductionApplied;

    @Column(name = "total_amount", precision = 19, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "advance_payment", precision = 19, scale = 2)
    private BigDecimal advancePayment;

    @Column(name = "contract_number", columnDefinition = "TEXT")
    private String contractNumber;

    @Column(name = "state_contract_id", columnDefinition = "TEXT")
    private String stateContractId;

//    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
//    @JoinColumn(name = "procurement_object_id", nullable = true)
//    private ProcurementObject procurementObject;

    @ManyToOne
    @JoinColumn(name = "supplier_id", nullable = true)
    private Supplier supplier;

    @OneToOne(mappedBy = "contract")
    private Purchase purchase;

    // Сеттеры для новых полей
    public void setContractNumber(String contractNumber) {
        this.contractNumber = contractNumber;
    }

    public void setStateContractId(String stateContractId) {
        this.stateContractId = stateContractId;
    }



    public void setId(Long id) {
        this.id = id;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public void setDefenseOrder(String defenseOrder) {
        isDefenseOrder = defenseOrder;
    }

    public void setLifecycleContract(String lifecycleContract) {
        isLifecycleContract = lifecycleContract;
    }

    public void setQuantityUndefined(String quantityUndefined) {
        isQuantityUndefined = quantityUndefined;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public void setExecutionStages(String executionStages) {
        this.executionStages = executionStages;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setAdditionalAddressInfo(String additionalAddressInfo) {
        this.additionalAddressInfo = additionalAddressInfo;
    }

    public void setQualityGuaranteeRequired(String qualityGuaranteeRequired) {
        this.qualityGuaranteeRequired = qualityGuaranteeRequired;
    }

    public void setWarrantyRequirements(String warrantyRequirements) {
        this.warrantyRequirements = warrantyRequirements;
    }

    public void setManufacturerWarrantyRequirements(String manufacturerWarrantyRequirements) {
        this.manufacturerWarrantyRequirements = manufacturerWarrantyRequirements;
    }

    public void setWarrantyPeriod(String warrantyPeriod) {
        this.warrantyPeriod = warrantyPeriod;
    }

    public void setWarrantyGuaranteeRequired(String warrantyGuaranteeRequired) {
        this.warrantyGuaranteeRequired = warrantyGuaranteeRequired;
    }

    public void setGuaranteeType(String guaranteeType) {
        this.guaranteeType = guaranteeType;
    }

    public void setGuaranteePercentage(BigDecimal guaranteePercentage) {
        this.guaranteePercentage = guaranteePercentage;
    }

    public void setGuaranteeAmount(BigDecimal guaranteeAmount) {
        this.guaranteeAmount = guaranteeAmount;
    }

    public void setGuaranteeRequirements(String guaranteeRequirements) {
        this.guaranteeRequirements = guaranteeRequirements;
    }

    public void setSmpSubcontractorsRequired(String smpSubcontractorsRequired) {
        this.smpSubcontractorsRequired = smpSubcontractorsRequired;
    }

    public void setSmpSubcontractorsExempt(String smpSubcontractorsExempt) {
        this.smpSubcontractorsExempt = smpSubcontractorsExempt;
    }

    public void setSmpSubcontractorsPercentage(BigDecimal smpSubcontractorsPercentage) {
        this.smpSubcontractorsPercentage = smpSubcontractorsPercentage;
    }

    public void setSmpSubcontractorsLiability(String smpSubcontractorsLiability) {
        this.smpSubcontractorsLiability = smpSubcontractorsLiability;
    }

    public void setUnilateralTerminationAllowed(String unilateralTerminationAllowed) {
        this.unilateralTerminationAllowed = unilateralTerminationAllowed;
    }

    public void setBudgetName(String budgetName) {
        this.budgetName = budgetName;
    }

    public void setBudgetType(String budgetType) {
        this.budgetType = budgetType;
    }

    public void setMunicipalityCode(String municipalityCode) {
        this.municipalityCode = municipalityCode;
    }

    public void setSelfFunded(String selfFunded) {
        isSelfFunded = selfFunded;
    }

    public void setBankingSupportInfo(String bankingSupportInfo) {
        this.bankingSupportInfo = bankingSupportInfo;
    }

    public void setPriceIndicationMethod(String priceIndicationMethod) {
        this.priceIndicationMethod = priceIndicationMethod;
    }

    public void setContractPrice(BigDecimal contractPrice) {
        this.contractPrice = contractPrice;
    }

    public void setIncludingVat(BigDecimal includingVat) {
        this.includingVat = includingVat;
    }

    public void setTreasuryGuaranteeAmount(BigDecimal treasuryGuaranteeAmount) {
        this.treasuryGuaranteeAmount = treasuryGuaranteeAmount;
    }

    public void setPriceFormula(String priceFormula) {
        this.priceFormula = priceFormula;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public void setContractRightPrice(BigDecimal contractRightPrice) {
        this.contractRightPrice = contractRightPrice;
    }

    public void setAdvancePaymentAvailable(String advancePaymentAvailable) {
        this.advancePaymentAvailable = advancePaymentAvailable;
    }

    public void setAdvancePercentage(BigDecimal advancePercentage) {
        this.advancePercentage = advancePercentage;
    }

    public void setAdvanceAmount(BigDecimal advanceAmount) {
        this.advanceAmount = advanceAmount;
    }

    public void setTaxDeductionApplied(String taxDeductionApplied) {
        this.taxDeductionApplied = taxDeductionApplied;
    }

    public void setPenaltyDeductionApplied(String penaltyDeductionApplied) {
        this.penaltyDeductionApplied = penaltyDeductionApplied;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public void setAdvancePayment(BigDecimal advancePayment) {
        this.advancePayment = advancePayment;
    }



    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    public void setPurchase(Purchase purchase) {
        this.purchase = purchase;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public Long getId() {
        return id;
    }


    public void setInn(String inn) {
        this.inn = inn;
    }

    public Object getContractNumber() {
        return contractNumber;
    }


//    public ProcurementObject getProcurementObject() {
//        return procurementObject;
//    }
}

