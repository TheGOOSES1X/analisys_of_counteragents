package Parser.implementations.Parser223.Contracts;

import Parser.Database.models.ProcurementObject;
import Parser.implementations.Parser223.Contracts.Interfaces.IContractSubjectsModelFiller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ContractSubjectsModelFiller implements IContractSubjectsModelFiller {

    @Override
    public List<ProcurementObject>  fillSubjectModel(List<Map<String, String>>  rawData) {
        List<ProcurementObject> procurementObjects = new ArrayList<>();

        for (Map<String, String> row : rawData) {
            try {
                ProcurementObject obj = new ProcurementObject();

                obj.setName(row.get("name"));
                obj.setKtruOkpd2Codes(row.get("okpd2"));

                if (!row.get("quantity").isEmpty()) {
                    try {
                        obj.setQuantity(new BigDecimal(row.get("quantity").replace(",", ".")));
                    } catch (NumberFormatException e) {
                        System.out.println("Ошибка парсинга количества: " + row.get("quantity"));
                    }
                }

                obj.setUnit(row.get("unit"));

                if (!row.get("pricePerUnit").isEmpty()) {
                    try {
                        obj.setPricePerUnit(new BigDecimal(row.get("pricePerUnit")));
                    } catch (NumberFormatException e) {
                        System.out.println("Ошибка парсинга цены: " + row.get("pricePerUnit"));
                    }
                }

                obj.setCountryOfOrigin(row.get("countryOfOrigin"));

                if (!row.get("totalAmount").isEmpty()) {
                    try {
                        obj.setTotalAmount(new BigDecimal(row.get("totalAmount")));
                    } catch (NumberFormatException e) {
                        System.out.println("Ошибка парсинга суммы: " + row.get("totalAmount"));
                    }
                }

                // Логирование (опционально)
                System.out.printf(
                        "%-3s | %-60s | %-20s | %-15s | %-20s | %-25s | %-15s%n",
                        row.get("positionNumber"),
                        row.get("name").length() > 60 ? row.get("name").substring(0, 57) + "..." : row.get("name"),
                        row.get("okpd2"),
                        obj.getQuantity() != null ? obj.getQuantity().toString() : "",
                        obj.getPricePerUnit() != null ? obj.getPricePerUnit().toString() : "",
                        obj.getCountryOfOrigin(),
                        obj.getTotalAmount() != null ? obj.getTotalAmount().toString() : ""
                );

                procurementObjects.add(obj);
            } catch (Exception e) {
                System.out.println("Ошибка при создании объекта: " + e.getMessage());
            }
        }

        return procurementObjects;
    }
}