package Parser_EGRUL;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class EGRUL_member {
    private String INN;
    private String OGRNIP;
    private boolean isIP;
    private LocalDate OGRNIPData;  // Поле для хранения даты
    private LocalDate taxStartData;
    private String mainActivityCode;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public EGRUL_member(String INN, String OGRNIP, boolean isIP, String OGRNIPData, String taxStartData, String mainActivityCode) {
        this.INN = INN;
        this.OGRNIP = OGRNIP;
        this.isIP = isIP;
        this.OGRNIPData = LocalDate.parse(OGRNIPData, DATE_FORMATTER);
        this.taxStartData = LocalDate.parse(taxStartData, DATE_FORMATTER);
        this.mainActivityCode = mainActivityCode;
    }
}
