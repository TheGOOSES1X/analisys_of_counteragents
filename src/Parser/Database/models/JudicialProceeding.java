package Parser.Database.models;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Entity
@Table(name = "judicial_proceedings")
public class JudicialProceeding {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "case_number", columnDefinition = "TEXT")
    private String caseNumber;

    @Column(name = "judge", columnDefinition = "TEXT")
    private String judge;

    @Column(name = "current_instance", columnDefinition = "TEXT")
    private String currentInstance;

    @Column(name = "plaintiff", columnDefinition = "TEXT")
    private String plaintiff;

    @Column(name = "defendant", columnDefinition = "TEXT")
    private String defendant;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "status", columnDefinition = "TEXT")
    private String status;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;

    @Column(name = "reason", columnDefinition = "TEXT")  // Добавленное поле
    private String reason;


    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "duration")
    private Integer duration;


    @Column(name = "outcome", columnDefinition = "TEXT")
    private String outcome;


    @Transient
    private static final Pattern DURATION_PATTERN = Pattern.compile(
            "(?:(\\d+) год(?:а|ов)?)?\\s*(?:(\\d+) месяц(?:а|ев)?)?\\s*(?:(\\d+) д(?:ень|ня|ней))?"
    );


    // Constructors, getters, and setters
    public JudicialProceeding() {
    }

    // Getters and setters for all fields
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    // Метод для вычисления endDate на основе startDate и duration
    @PrePersist
    @PreUpdate
    private void calculateEndDate() {
        String duration = "";
        if (startDate != null && duration != null && !duration.isEmpty()) {
            try {
                Matcher matcher = DURATION_PATTERN.matcher(duration.toLowerCase());
                if (matcher.find()) {
                    int years = matcher.group(1) != null ? Integer.parseInt(matcher.group(1)) : 0;
                    int months = matcher.group(2) != null ? Integer.parseInt(matcher.group(2)) : 0;
                    int days = matcher.group(3) != null ? Integer.parseInt(matcher.group(3)) : 0;

                    this.endDate = startDate.plusYears(years)
                            .plusMonths(months)
                            .plusDays(days);
                }
            } catch (DateTimeParseException | NumberFormatException e) {
                // Логируем ошибку, но не прерываем выполнение
                System.err.println("Ошибка парсинга длительности: " + duration);
            }
        }
    }


    public String getCaseNumber() {
        return caseNumber;
    }

    public void setCaseNumber(String caseNumber) {
        this.caseNumber = caseNumber;
    }

    public String getJudge() {
        return judge;
    }

    public void setJudge(String judge) {
        this.judge = judge;
    }

    public String getCurrentInstance() {
        return currentInstance;
    }

    public void setCurrentInstance(String currentInstance) {
        this.currentInstance = currentInstance;
    }

    public String getPlaintiff() {
        return plaintiff;
    }

    public void setPlaintiff(String plaintiff) {
        this.plaintiff = plaintiff;
    }

    public String getDefendant() {
        return defendant;
    }

    public void setDefendant(String defendant) {
        this.defendant = defendant;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    public String getReason() {  // Геттер для нового поля
        return reason;
    }

    public void setReason(String reason) {  // Сеттер для нового поля
        this.reason = reason;
    }

    public String getOutcome() {
        return outcome;
    }

    public void setOutcome(String outcome) {
        this.outcome = outcome;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;

    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer durationDays) {
        this.duration = durationDays;
        calculateEndDate();
    }

}