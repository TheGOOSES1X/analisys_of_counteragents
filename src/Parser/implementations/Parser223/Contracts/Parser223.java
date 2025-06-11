package Parser.implementations.Parser223.Contracts;



import Parser.Database.models.*;
import Parser.implementations.Parser223.Contracts.Interfaces.*;
import org.openqa.selenium.WebDriver;
import java.util.*;

public class Parser223 implements IContractParser {

    private final IUrlNavigator urlNavigator;
    private final IContractModelFiller contractModelFiller;
    private final IContractSubjectsParser subjectsParser;
    private final IContractSubjectsModelFiller modelFillermodelFiller;
    public Parser223() {

        this.urlNavigator = new UrlNavigator();
        this.contractModelFiller = new ContractModelFiller();
        this.subjectsParser = new ContractSubjectsParser();
        this.modelFillermodelFiller = new ContractSubjectsModelFiller();
    }

    @Override
    public Contract parseContract(String originalUrl,WebDriver driver) {
        try {
            String contractUrl = urlNavigator.findContractUrl(originalUrl, driver);
            if (contractUrl == null) return null;

            driver.get(contractUrl);

            ContractDetailsParser detailsParser = new ContractDetailsParser(driver);
            Map<String, String> contractDetails = detailsParser.parseContractDetails();
            Map<String, String> mainInfo = detailsParser.parseGeneralInfo();

            if (contractDetails.isEmpty() && mainInfo.isEmpty()) return null;

            return contractModelFiller.fillContractModel(contractDetails, mainInfo);
        } catch (Exception e) {
            System.err.println("Error parsing contract: " + e.getMessage());
            return null;
        }
    }

    @Override
    public List<ProcurementObject> parseContractSubjects(String originalUrl, WebDriver driver) {
        try {
            String contractUrl = urlNavigator.findContractUrl(originalUrl, driver);
            if (contractUrl == null) return Collections.emptyList();

            String subjectUrl = urlNavigator.convertToSubjectUrl(contractUrl);
            if (subjectUrl == null) return Collections.emptyList();

            driver.get(subjectUrl);

            // 1. Собираем сырые данные из таблицы
            List<Map<String, String>> rawData = subjectsParser.parseSubjects(driver);

            // 2. Преобразуем сырые данные в объекты ProcurementObject
            return modelFillermodelFiller.fillSubjectModel(rawData);

        } catch (Exception e) {
            System.err.println("Error parsing contract subjects: " + e.getMessage());
            return Collections.emptyList();
        }
    }
}