package Parser.Database.models;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Purchase {
    private Long id;
    private String purchaseNumber;
    private String law;
    private BigDecimal initialMaxPrice;
    private String currency;
    private String purchaseObject;
    private String procurementMethod;
    private String ikz;
    private Customer customer;
    private String executor;
    private LocalDate publicationDate;
    private LocalDateTime updateDate;
    private LocalDate applicationEndDate;
    private LocalDateTime auctionDate;
    private String procurementStage;

    private ProcurementObject procurementObject;

}