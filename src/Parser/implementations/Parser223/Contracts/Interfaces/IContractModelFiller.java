package Parser.implementations.Parser223.Contracts.Interfaces;

import Parser.Database.models.Contract;

import java.util.Map;

public interface IContractModelFiller {
    Contract fillContractModel(Map<String, String> contractDetails, Map<String, String> mainInfo);
}