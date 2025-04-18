package Parser.Database.models;
import java.math.BigDecimal;
import jakarta.persistence.*;
@Entity
@Table(name = "procurement_objects")
public class ProcurementObject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, columnDefinition = "TEXT")

    private String name;

    @Column(name = "type")
    private String type;

    @Column(name = "ktru_okpd2_codes")
    private String ktruOkpd2Codes;

    @Column(name = "quantity", precision = 19, scale = 3)

    private BigDecimal quantity;

    @Column(name = "unit", length = 20)
    private String unit;

    @Column(name = "price_per_unit", precision = 19, scale = 2)

    private BigDecimal pricePerUnit;

    @Column(name = "vat_rate", precision = 5, scale = 2)

    private BigDecimal vatRate;

    @Column(name = "country_of_origin", length = 100)
    private String countryOfOrigin;

    @Column(name = "total_amount", precision = 19, scale = 2)

    private BigDecimal totalAmount;

}
