package Parser.implementations.Parser223.Contracts.Interfaces;

import Parser.Database.models.ProcurementObject;

import java.util.List;
import java.util.Map;

public interface IContractSubjectsModelFiller {
    List<ProcurementObject> fillSubjectModel(List<Map<String, String>> subjectData);
}