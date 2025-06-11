package Parser.implementations.Parser223.Contracts.Interfaces;

import org.openqa.selenium.WebDriver;

public interface IUrlNavigator {
    String findContractUrl(String url, WebDriver driver);
    String convertToSubjectUrl(String contractInfoUrl);
}