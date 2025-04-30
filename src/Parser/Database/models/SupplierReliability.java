package Parser.Database.models;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "supplier_reliability")
public class SupplierReliability {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;

    // Основные сведения
    @Column(name = "name", columnDefinition = "TEXT")
    private String name; // Наименование/ФИО

    @Column(name = "inn", columnDefinition = "TEXT")
    private String inn; // ИНН

    @Column(name = "authority", columnDefinition = "TEXT")
    private String authority; // Уполномоченный орган, осуществивший включение сведений в РНП 44-ФЗ

    @Column(name = "reason", columnDefinition = "TEXT")
    private String reason; // Причина для внесения в РНП 44-ФЗ

    @Column(name = "registry_number", columnDefinition = "TEXT")
    private String registryNumber; // Реестровый номер

    @Column(name = "inclusion_date")
    private LocalDate inclusionDate; // Дата включения сведений в РНП 44-ФЗ

    @Column(name = "status", columnDefinition = "TEXT")
    private String status; // Статус записи

    @Column(name = "exclusion_date")
    private LocalDate exclusionDate; // Дата исключения

    // Дополнительные сведения
    @Column(name = "entity_type", columnDefinition = "TEXT")
    private String entityType; // ТИП ЛИЦА

    @Column(name = "eruz_number", columnDefinition = "TEXT")
    private String eruzNumber; // Номер ЕРУЗ

    @Column(name = "person_inn", columnDefinition = "TEXT")
    private String personInn; // ИНН ЛИЦА

    @Column(name = "law", columnDefinition = "TEXT")
    private String law; // Закон

    @Column(name = "record_number", columnDefinition = "TEXT")
    private String recordNumber; // Номер записи

    @Column(name = "update_date")
    private LocalDate updateDate; // Дата обновления

    @Column(name = "planned_exclusion_date")
    private LocalDate plannedExclusionDate; // Планируемая дата исключения

    // Конструкторы
    public SupplierReliability() {
    }

    // Геттеры и сеттеры
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInn() {
        return inn;
    }

    public void setInn(String inn) {
        this.inn = inn;
    }

    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getRegistryNumber() {
        return registryNumber;
    }

    public void setRegistryNumber(String registryNumber) {
        this.registryNumber = registryNumber;
    }

    public LocalDate getInclusionDate() {
        return inclusionDate;
    }

    public void setInclusionDate(LocalDate inclusionDate) {
        this.inclusionDate = inclusionDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDate getExclusionDate() {
        return exclusionDate;
    }

    public void setExclusionDate(LocalDate exclusionDate) {
        this.exclusionDate = exclusionDate;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public String getEruzNumber() {
        return eruzNumber;
    }

    public void setEruzNumber(String eruzNumber) {
        this.eruzNumber = eruzNumber;
    }

    public String getPersonInn() {
        return personInn;
    }

    public void setPersonInn(String personInn) {
        this.personInn = personInn;
    }

    public String getLaw() {
        return law;
    }

    public void setLaw(String law) {
        this.law = law;
    }

    public String getRecordNumber() {
        return recordNumber;
    }

    public void setRecordNumber(String recordNumber) {
        this.recordNumber = recordNumber;
    }

    public LocalDate getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(LocalDate updateDate) {
        this.updateDate = updateDate;
    }

    public LocalDate getPlannedExclusionDate() {
        return plannedExclusionDate;
    }

    public void setPlannedExclusionDate(LocalDate plannedExclusionDate) {
        this.plannedExclusionDate = plannedExclusionDate;
    }
}