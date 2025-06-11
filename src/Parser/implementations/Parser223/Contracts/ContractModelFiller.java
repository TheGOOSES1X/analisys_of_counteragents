package Parser.implementations.Parser223.Contracts;


import Parser.Database.models.Contract;
import Parser.implementations.Parser223.Contracts.Interfaces.IContractModelFiller;

import java.util.Map;

import static Parser.utils.ParserUtils.parseBigDecimal;
import static Parser.utils.ParserUtils.parseDate;
public class ContractModelFiller implements IContractModelFiller {
    @Override
    public Contract fillContractModel(Map<String, String> contractDetails, Map<String, String> mainInfo) {
        Contract contract = new Contract();

        contract.setRegistryNumber(contractDetails.get("Номер договора (заголовок)"));
        contract.setProcurementIdentificationCode(contractDetails.get("Номер договора"));
        contract.setStatus(contractDetails.get("Статус контракта"));
        contract.setSoleSupplierDocumentDetails(contractDetails.get("Заказчик"));
        contract.setContractPrice(parseBigDecimal(contractDetails.get("Цена договора")));
        contract.setStartDate(parseDate(contractDetails.get("Дата заключения")));
        contract.setEndDate(parseDate(contractDetails.get("Срок исполнения (окончание)")));
        contract.setStartDate(parseDate(contractDetails.get("Дата обновления")));
        contract.setSubject(mainInfo.get("Предмет договора"));
        contract.setBankingTreasurySupportInfo(mainInfo.get("Способ закупки"));
        contract.setCurrency("₽");

        return contract;
    }
}