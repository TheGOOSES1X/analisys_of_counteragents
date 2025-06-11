package Parser.implementations.Parser223.Contracts.Interfaces;

import Parser.Database.models.ProcurementObject;
import org.openqa.selenium.WebDriver;

import java.util.List;
import java.util.Map;

public interface IContractSubjectsParser {
    List<Map<String, String>> parseSubjects(WebDriver driver);
}