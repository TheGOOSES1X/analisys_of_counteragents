package Parser.implementations.Parser223.Contracts.Interfaces;

import Parser.Database.models.Contract;
import Parser.Database.models.ProcurementObject;
import org.openqa.selenium.WebDriver;

import java.util.List;

public interface IContractParser {
    Contract parseContract(String originalUrl, WebDriver driver);
    List<ProcurementObject> parseContractSubjects(String originalUrl,WebDriver driver);
}