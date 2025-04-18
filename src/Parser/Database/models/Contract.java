package Parser.Database.models;

import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDate;
import jakarta.persistence.*;


import java.math.BigDecimal;
@Entity
@Table(name = "contracts")
public class Contract {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "subject", columnDefinition = "TEXT")
    private String subject;

    @Column(name = "specialization")
    private String specialization;

    @Column(name = "is_defense_order", nullable = false)
    private boolean isDefenseOrder = false;

    @Column(name = "is_lifecycle_contract", nullable = false)
    private boolean isLifecycleContract = false;

    @Column(name = "is_quantity_undefined", nullable = false)
    private boolean isQuantityUndefined = false;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "execution_stages", columnDefinition = "TEXT")
    private String executionStages;

    @Column(name = "country", length = 100)
    private String country;

    @Column(name = "address", columnDefinition = "TEXT")
    private String address;

    @Column(name = "additional_address_info", columnDefinition = "TEXT")
    private String additionalAddressInfo;

    @Column(name = "quality_guarantee_required", nullable = false)
    private boolean qualityGuaranteeRequired = false;

    @Column(name = "warranty_requirements", columnDefinition = "TEXT")
    private String warrantyRequirements;

    @Column(name = "manufacturer_warranty_requirements", columnDefinition = "TEXT")
    private String manufacturerWarrantyRequirements;

    @Column(name = "warranty_period", length = 100)
    private String warrantyPeriod;

    @Column(name = "warranty_guarantee_required", nullable = false)
    private boolean warrantyGuaranteeRequired = false;

    @Column(name = "guarantee_type", length = 50)
    private String guaranteeType;

    @Column(name = "guarantee_percentage", precision = 5, scale = 2)

    private BigDecimal guaranteePercentage;

    @Column(name = "guarantee_amount", precision = 19, scale = 2)

    private BigDecimal guaranteeAmount;

    @Column(name = "guarantee_requirements", columnDefinition = "TEXT")
    private String guaranteeRequirements;

    @Column(name = "smp_subcontractors_required", nullable = false)
    private boolean smpSubcontractorsRequired = false;

    @Column(name = "smp_subcontractors_exempt", nullable = false)
    private boolean smpSubcontractorsExempt = false;

    @Column(name = "smp_subcontractors_percentage", precision = 5, scale = 2)

    private BigDecimal smpSubcontractorsPercentage;

    @Column(name = "smp_subcontractors_liability", nullable = false)
    private boolean smpSubcontractorsLiability = false;

    @Column(name = "unilateral_termination_allowed", nullable = false)
    private boolean unilateralTerminationAllowed = false;

    @Column(name = "budget_name", length = 255)
    private String budgetName;

    @Column(name = "budget_type", length = 100)
    private String budgetType;

    @Column(name = "municipality_code", length = 20)
    private String municipalityCode;

    @Column(name = "is_self_funded", nullable = false)
    private boolean isSelfFunded = false;

    @Column(name = "banking_support_info", columnDefinition = "TEXT")
    private String bankingSupportInfo;

    @Column(name = "price_indication_method", length = 100)
    private String priceIndicationMethod;

    @Column(name = "contract_price", precision = 19, scale = 2)

    private BigDecimal contractPrice;

    @Column(name = "including_vat", precision = 19, scale = 2)

    private BigDecimal includingVat;

    @Column(name = "treasury_guarantee_amount", precision = 19, scale = 2)

    private BigDecimal treasuryGuaranteeAmount;

    @Column(name = "price_formula", columnDefinition = "TEXT")
    private String priceFormula;

    @Column(name = "currency", length = 3)
    private String currency;

    @Column(name = "contract_right_price", precision = 19, scale = 2)

    private BigDecimal contractRightPrice;

    @Column(name = "advance_payment_available", nullable = false)
    private boolean advancePaymentAvailable = false;

    @Column(name = "advance_percentage", precision = 5, scale = 2)

    private BigDecimal advancePercentage;

    @Column(name = "advance_amount", precision = 19, scale = 2)

    private BigDecimal advanceAmount;

    @Column(name = "tax_deduction_applied", nullable = false)
    private boolean taxDeductionApplied = false;

    @Column(name = "penalty_deduction_applied", nullable = false)
    private boolean penaltyDeductionApplied = false;

    @Column(name = "total_amount", precision = 19, scale = 2)

    private BigDecimal totalAmount;

    @Column(name = "advance_payment", precision = 19, scale = 2)

    private BigDecimal advancePayment;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "procurement_object_id", nullable = true)
    private ProcurementObject procurementObject;

    @ManyToOne
    @JoinColumn(name = "supplier_id", nullable = true)
    private Supplier supplier;

    @OneToOne(mappedBy = "contract")
    private Purchase purchase;
}