package MainAnalyzer;

import java.util.Date;

public class SupplierSummary {
    private Long contractId;
    private Date startDate;
    private Date endDate;
    private Integer contractDurationDays;
    private String inn;
    private String name;
    private Integer maxComplaints;
    private String reason;
    private String status;
    private Long totalJudicialProceedings;
    private String proceedingsDescriptions;
    private String okpd2Codes;

    // Конструктор, геттеры и сеттеры
    public SupplierSummary(Long contractId, Date startDate, Date endDate,
                           Integer contractDurationDays, String inn, String name,
                           Integer maxComplaints, String reason, String status,
                           Long totalJudicialProceedings, String proceedingsDescriptions,
                           String okpd2Codes) {
        this.contractId = contractId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.contractDurationDays = contractDurationDays;
        this.inn = inn;
        this.name = name;
        this.maxComplaints = maxComplaints;
        this.reason = reason;
        this.status = status;
        this.totalJudicialProceedings = totalJudicialProceedings;
        this.proceedingsDescriptions = proceedingsDescriptions;
        this.okpd2Codes = okpd2Codes;
    }

    // Геттеры и сеттеры для всех полей
    // ...
}