package Parser.implementations.Parser223;

import Parser.Database.models.Supplier;
import java.util.Map;

public class SupplierModelFiller {

    public void fillSupplierModelFromParticipantData(Supplier supplier, Map<String, Object> participantData) {
        try {
            // Set basic information
            supplier.setName((String) participantData.get("name"));
            supplier.setAddress((String) participantData.get("address"));

            // Set organization details
            supplier.setInn((String) participantData.get("inn"));
            supplier.setKpp((String) participantData.get("kpp"));
            supplier.setOgrn((String) participantData.get("ogrn"));

            // Set default values for Russia (since all examples are Russian companies)
            supplier.setCountryName("Российская Федерация");
            supplier.setCountryCode("643"); // OKSM code for Russia

            // Set type as legal entity by default
            supplier.setType("Юридическое лицо");

            // If address contains postal code, extract it to postalAddress
            String address = supplier.getAddress();
            if (address != null && address.matches("^\\d{6}.*")) {
                supplier.setPostalAddress(address.substring(0, 6)); // Extract postal code
            }

            // Set status as active by default
            supplier.setStatus("Активен");

        } catch (Exception e) {
            System.err.println("Ошибка при заполнении модели поставщика из данных участника: " + e.getMessage());
        }
    }

    public void fillSupplierModelWithBidInfo(Supplier supplier, Map<String, Object> participantData) {
        try {
            // This method can be used to add bid-specific information to the supplier
            // For example, you might want to store the bid number or prices in additional fields

            // If you need to store bid-specific prices, you could add them to a separate entity
            // or as additional fields in the Supplier model if they're relevant

            // Example of how you might handle this:
            Double priceWithVAT = (Double) participantData.get("priceWithVAT");
            Double priceWithoutVAT = (Double) participantData.get("priceWithoutVAT");
            String vatRate = (String) participantData.get("vatRate");
            String bidNumber = (String) participantData.get("bidNumber");

            // You could store these in custom fields or related entities if needed
            // For now, we'll just print them for demonstration
            System.out.println("Bid info for supplier " + supplier.getName() +
                    ": bidNumber=" + bidNumber +
                    ", priceWithVAT=" + priceWithVAT +
                    ", priceWithoutVAT=" + priceWithoutVAT +
                    ", vatRate=" + vatRate);

        } catch (Exception e) {
            System.err.println("Ошибка при добавлении информации о заявке к поставщику: " + e.getMessage());
        }
    }
}