package Parser.implementations.Parser44.Contracts;

import Parser.Database.models.*;

import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static Parser.implementations.Parser44.Contracts.ParserUtils.*;

public class ContractModelFiller {
    public void fillContractModel(Contract contract, Map<String, Object> contractData) {
        try {
            // 3. Предмет контракта
            Map<String, Object> subjectData = (Map<String, Object>) contractData.get("3. Предмет контракта");
            if (subjectData != null) {
                Map<String, String> mainSubject = (Map<String, String>) subjectData.get("3. Предмет контракта");
                if (mainSubject != null) {
                    String subjectKey = findPartialKey(mainSubject, "Предмет контракта");
                    String specKey = findPartialKey(mainSubject, "Специализация");
                    String defenseKey = findPartialKey(mainSubject, "Контракт заключен для выполнения");
                    String lifecycleKey = findPartialKey(mainSubject, "Контракт жизненного цикла");
                    String quantityKey = findPartialKey(mainSubject, "Невозможно определить количество");

                    contract.setSubject(subjectKey != null ? mainSubject.get(subjectKey) : null);
                    contract.setSpecialization(specKey != null ? mainSubject.get(specKey) : null);
                    contract.setDefenseOrder(defenseKey != null ? mainSubject.get(defenseKey) : null);
                    contract.setLifecycleContract(lifecycleKey != null ? mainSubject.get(lifecycleKey) : null);
                    contract.setQuantityUndefined(quantityKey != null ? mainSubject.get(quantityKey) : null);
                }
            }
            contract.setStateContractId((String) contractData.get("state_contract_id"));

            // 1. Номер контракта
            String contractNumberKey = findPartialKey(contractData, "Номер контракта");
            if (contractNumberKey != null) {
                Object contractNumberValue = contractData.get(contractNumberKey);

                if (contractNumberValue instanceof Map) {
                    // Если значение - Map (старая логика)
                    Map<String, Object> contractNumberSection = (Map<String, Object>) contractNumberValue;
                    if (!contractNumberSection.isEmpty()) {
                        Object firstValue = contractNumberSection.values().iterator().next();
                        if (firstValue instanceof Map) {
                            Map<String, String> contractNumberData = (Map<String, String>) firstValue;
                            contract.setContractNumber(contractNumberData.get("Номер контракта"));
                        }
                    }
                } else if (contractNumberValue instanceof String) {
                    // Если значение - просто строка (новая логика)
                    contract.setContractNumber((String) contractNumberValue);
                } else if (contractNumberValue != null) {
                    // Другие случаи - преобразуем в строку
                    contract.setContractNumber(contractNumberValue.toString());
                }
            }

            // 4. Условия контракта
            Map<String, Object> conditionsData = (Map<String, Object>) contractData.get("4. Условия контракта");
            if (conditionsData != null) {
                // 4.1. Сроки исполнения контракта

                Map<String, String> generalData = (Map<String, String>) contractData.get("Общие данные");
                if (generalData != null) {
                    // Преобразуем в Map<String, String> для удобства поиска
                    String startDateKey = findPartialKey(generalData, "Дата заключения");
                   String endDateKey = findPartialKey(generalData, "Дата окончания");

                    contract.setStartDate(startDateKey != null ?
                            parseDate(generalData.get(startDateKey)) : null);
                    contract.setEndDate(endDateKey != null ?
                            parseDate(generalData.get(endDateKey)) : null);


                }
//                Map<String, String> executionTerms = (Map<String, String>) conditionsData.get("4.1. Сроки исполнения контракта");
//                if (executionTerms != null) {
//                    String startDateKey = findPartialKey(executionTerms, "Дата начала");
//                    String endDateKey = findPartialKey(executionTerms, "Дата окончания");
//
//                    contract.setStartDate(startDateKey != null ?
//                            parseDate(executionTerms.get(startDateKey)) : null);
//                    contract.setEndDate(endDateKey != null ?
//                            parseDate(executionTerms.get(endDateKey)) : null);
//                }
                // 4.2. Этапы исполнения контракта
                String executionStagesKey = findPartialKey(conditionsData, "Этапы исполнения");
                if (executionStagesKey != null) {
                    Map<String, String> executionStages = (Map<String, String>) conditionsData.get(executionStagesKey);
                    if (executionStages != null && !executionStages.isEmpty()) {
                        // Если мапа не пустая, значит есть этапы, иначе - "Контракт не разделен на этапы"
                        contract.setExecutionStages(executionStages.isEmpty() ?
                                "Контракт не разделен на этапы исполнения контракта" :
                                String.join(", ", executionStages.values()));
                    }
                }

                // 4.3. Место поставки
                Map<String, String> deliveryPlace = (Map<String, String>) conditionsData.get("4.3. Место поставки товара, выполнения работы или оказания услуги");
                if (deliveryPlace != null) {
                    String countryKey = findPartialKey(deliveryPlace, "Страна");
                    String addressKey = findPartialKey(deliveryPlace, "Место");
                    String addInfoKey = findPartialKey(deliveryPlace, "Адрес");

                    contract.setCountry(countryKey != null ? deliveryPlace.get(countryKey) : null);
                    contract.setAddress(addressKey != null ? deliveryPlace.get(addressKey) : null);
                    contract.setAdditionalAddressInfo(addInfoKey != null ?
                            deliveryPlace.get(addInfoKey) : null);
                }

                // 4.4. Требования к гарантии качества
                Map<String, String> qualityGuarantee = (Map<String, String>) conditionsData.get("4.4. Требования к гарантии качества товара, работы, услуги");
                if (qualityGuarantee != null) {
                    String qualReqKey = findPartialKey(qualityGuarantee, "Требования гарантия качества");
                    String warrantyReqKey = findPartialKey(qualityGuarantee, "Информация о требованиях ");
                    String manufReqKey = findPartialKey(qualityGuarantee, "Требования к гарантии ");
                    String warrantyPeriodKey = findPartialKey(qualityGuarantee, "Срок, на который предоставляется гарантия");
                    String warrantyGuarKey = findPartialKey(qualityGuarantee, "Требуется обеспечение исполнения");

                    contract.setQualityGuaranteeRequired(qualReqKey != null ? qualityGuarantee.get(qualReqKey) : null);
                    contract.setWarrantyRequirements(warrantyReqKey != null ? qualityGuarantee.get(warrantyReqKey) : null);
                    contract.setManufacturerWarrantyRequirements(manufReqKey != null ? qualityGuarantee.get(manufReqKey) : null);
                    contract.setWarrantyPeriod(warrantyPeriodKey != null ? qualityGuarantee.get(warrantyPeriodKey) : null);
                    contract.setWarrantyGuaranteeRequired(warrantyGuarKey != null ? qualityGuarantee.get(warrantyGuarKey) : null);
                }

                // 4.6. Условия привлечения субподрядчиков
                Map<String, String> subcontractors = (Map<String, String>) conditionsData.get("4.6.  Условия привлечения субподрядчиков, соисполнителей из числа СМП, СОНО");
                if (subcontractors != null) {
                    String smpReqKey = findPartialKey(subcontractors, "Предъявляется требование о привлечении к исполнению контракта субподрядчиков");
                    String smpExemptKey = findPartialKey(subcontractors, "Объем привлечения к исполнению контракта");
                    String smpPrecentKey = findPartialKey(subcontractors, "За неисполнение условий по привлечению");

                    contract.setSmpSubcontractorsRequired(smpReqKey != null ? subcontractors.get(smpReqKey) : null);
                    contract.setSmpSubcontractorsExempt(smpExemptKey != null ? subcontractors.get(smpExemptKey) : null);
                    contract.setSmpSubcontractorsLiability(smpPrecentKey != null ? subcontractors.get(smpExemptKey) : null);

                }

                // 4.7. Прочие условия
                Map<String, String> otherConditions = (Map<String, String>) conditionsData.get("4.7. Прочие условия контракта");
                if (otherConditions != null) {
                    String terminationKey = findPartialKey(otherConditions, "Предусмотрена возможность");
                    contract.setUnilateralTerminationAllowed(terminationKey != null ? otherConditions.get(terminationKey) : null);
                }
            }

            // 5. Финансирование контракта
            Map<String, Object> financingData = (Map<String, Object>) contractData.get("5. Финансирование контракта");
            if (financingData != null) {
                // 5.1. Источники финансирования
                Map<String, String> fundingSources = (Map<String, String>) financingData.get("5.1. Источники финансирования");
                if (fundingSources != null) {
                    String budgetNameKey = findPartialKey(fundingSources, "Наименование бюджета");
                    String budgetTypeKey = findPartialKey(fundingSources, "Вид бюджета");
                    String municipKey = findPartialKey(fundingSources, "Код территории");
                    String selfFundKey = findPartialKey(fundingSources, "Закупка за счет");
                    String bankSupportKey = findPartialKey(fundingSources, "Информация о банковском");

                    contract.setBudgetName(budgetNameKey != null ? fundingSources.get(budgetNameKey) : null);
                    contract.setBudgetType(budgetTypeKey != null ? fundingSources.get(budgetTypeKey) : null);
                    contract.setMunicipalityCode(municipKey != null ? fundingSources.get(municipKey) : null);
                    contract.setSelfFunded(selfFundKey != null ? fundingSources.get(selfFundKey) : null);
                    contract.setBankingSupportInfo(bankSupportKey != null ? fundingSources.get(bankSupportKey) : null);
                }
                // 5.2. Цена контракта
                String contractPriceKey = findPartialKey(financingData, "Цена контракта");
                if (contractPriceKey != null) {
                    Map<String, String> contractPrice = (Map<String, String>) financingData.get(contractPriceKey);
                    if (contractPrice != null) {
                        contract.setContractRightPrice(
                                parseBigDecimal(findAndGet(contractPrice, "Цена за право заключения")));
                    }
                }

                // 5.2. Цена контракта
                Map<String, String> contractPrice = (Map<String, String>) financingData.get("5.2. Цена контракта");
                if (contractPrice != null) {
                    String priceMethodKey = findPartialKey(contractPrice, "Способ указания цены");
                    String priceKey = findPartialKey(contractPrice, "Цена контракта");
                    String vatKey = findPartialKey(contractPrice, "В том числе НДС");
                    String treasuryKey = findPartialKey(contractPrice, "казначейского обеспечения");
                    String formulaKey = findPartialKey(contractPrice, "Формула цены");
                    String currencyKey = findPartialKey(contractPrice, "Валюта контракта");

                    contract.setPriceIndicationMethod(priceMethodKey != null ? contractPrice.get(priceMethodKey) : null);
                    contract.setContractPrice(priceKey != null ? parseBigDecimal(contractPrice.get(priceKey)) : null);
                    contract.setIncludingVat(vatKey != null ? parseBigDecimal(contractPrice.get(vatKey)) : null);
                    contract.setTreasuryGuaranteeAmount(treasuryKey != null ? parseBigDecimal(contractPrice.get(treasuryKey)) : null);
                    contract.setPriceFormula(formulaKey != null ? contractPrice.get(formulaKey) : null);
                    contract.setCurrency(currencyKey != null ? contractPrice.get(currencyKey) : null);
                }

                // 5.3. Порядок расчетов
                Map<String, String> paymentTerms = (Map<String, String>) financingData.get("5.3. Порядок расчетов");
                if (paymentTerms != null) {
                    String advanceKey = findPartialKey(paymentTerms, "Предусмотрена выплата аванса");
                    String advancePercKey = findPartialKey(paymentTerms, "Размер аванса (%)");
                    String advanceAmtKey = findPartialKey(paymentTerms, "Размер аванса в валюте");
                    String taxDeductKey = findPartialKey(paymentTerms, "Суммы, уплачиваемые заказчиком");
                    String penaltyKey = findPartialKey(paymentTerms, "Предусмотрено удержание");

                    contract.setAdvancePaymentAvailable(advanceKey != null ? paymentTerms.get(advanceKey) : null);
                    contract.setAdvancePercentage(advancePercKey != null ? parseBigDecimal(paymentTerms.get(advancePercKey)) : null);
                    contract.setAdvanceAmount(advanceAmtKey != null ? parseBigDecimal(paymentTerms.get(advanceAmtKey)) : null);
                    contract.setTaxDeductionApplied(taxDeductKey != null ? paymentTerms.get(taxDeductKey) : null);
                    contract.setPenaltyDeductionApplied(penaltyKey != null ? paymentTerms.get(penaltyKey) : null);
                }
            }
        } catch (Exception e) {
            System.out.println("Ошибка при заполнении модели контракта: " + e.getMessage());
            throw new RuntimeException("Не удалось заполнить данные контракта", e);
        }
    }

    public void fillSupplierModel(Supplier supplier, Map<String, Object> contractData) {
        try {
            // 2.2. Информация о поставщике
            Map<String, Object> contractParties = (Map<String, Object>) contractData.get("2. Стороны контракта");
            if (contractParties != null) {
                Map<String, String> supplierInfo = (Map<String, String>) contractParties.get("2.2. Информация о поставщике");
                if (supplierInfo != null) {
                    supplier.setType(supplierInfo.get("Вид"));
                    supplier.setName(supplierInfo.get("Наименование организации (ФИО физического лица)"));

                    // Парсим страну (формат: "Российская Федерация (643)")
                    String countryInfo = supplierInfo.get("Наименование страны, код по ОКСМ");
                    if (countryInfo != null && countryInfo.contains("(")) {
                        supplier.setCountryName(countryInfo.substring(0, countryInfo.indexOf("(")).trim());
                        supplier.setCountryCode(countryInfo.substring(countryInfo.indexOf("(") + 1, countryInfo.indexOf(")")).trim());
                    }

                    supplier.setAddress(supplierInfo.get("Адрес места нахождения (адрес места жительства)"));
                    supplier.setPostalAddress(supplierInfo.get("Почтовый адрес"));
                    supplier.setOgrn(supplierInfo.get("ОГРН (для юридических лиц)"));
                    supplier.setInn(supplierInfo.get("ИНН"));
                    supplier.setKpp(supplierInfo.get("КПП (для юридических лиц)"));
                    supplier.setStatus(supplierInfo.get("Статус"));
                    supplier.setEmail(supplierInfo.get("Электронная почта"));
                    supplier.setPhone(supplierInfo.get("Телефон"));

                    // Парсим информацию о руководителе (если есть)
//                    String directorInfo = supplierInfo.get("Руководитель (лицо, имеющее право без доверенности действовать от имени юридического лица)");
//                    if (directorInfo != null) {
//                        // Можно сохранить дополнительную информацию о руководителе
//                    }
                }
            }



        } catch (Exception e) {
            System.out.println("Ошибка при заполнении данных поставщика: " + e.getMessage());
        }// Логика заполнения модели поставщика
    }
    public void fillContractModelFromCommonInfo(Contract contract, Map<String, Object> contractData) {
        try {
            // Получаем блок с основной информацией
            Map<String, String> commonInfo = (Map<String, String>) contractData.get("Информация о контракте");
            if (commonInfo == null) {
                System.out.println("Блок 'Информация о контракте' не найден");
                return;
            }

            // Вывод содержимого для отладки
            System.out.println("=== Данные для заполнения контракта ===");
            commonInfo.forEach((k, v) -> System.out.println(k + ": " + v));

            // Заполняем поля контракта
            contract.setStateContractId((String) contractData.get("state_contract_id"));

            // Предмет контракта
            contract.setSubject(commonInfo.get("Предмет контракта"));

            // Цена контракта
            String priceStr = commonInfo.get("Цена контракта");
            if (priceStr != null) {
                priceStr = priceStr.replace("Российский рубль", "").trim();
                contract.setContractPrice(ParserUtils.parseBigDecimal(priceStr));
            }

            // Номер контракта
            contract.setContractNumber(commonInfo.get("Номер контракта"));

            // Дата заключения контракта (используем как дату окончания)
            String contractDateStr = commonInfo.get("Дата заключения контракта");
            contract.setEndDate(ParserUtils.parseDate(contractDateStr));



            Map<String, String> generalData = (Map<String, String>) contractData.get("Общие данные");
            if (generalData != null) {
                // Преобразуем в Map<String, String> для удобства поиска
                String startDateKey = findPartialKey(generalData, "Дата заключения");
                String endDateKey = findPartialKey(generalData, "Дата окончания");

                contract.setStartDate(startDateKey != null ?
                        parseDate(generalData.get(startDateKey)) : null);
                contract.setEndDate(endDateKey != null ?
                        parseDate(generalData.get(endDateKey)) : null);


            }

//            System.out.println("=== Результаты заполнения ===");
//            System.out.println("Предмет: " + contract.getSubject());
//            System.out.println("Цена: " + contract.getContractPrice());
//            System.out.println("Номер: " + contract.getContractNumber());
//            System.out.println("Дата: " + contract.getEndDate());

        } catch (Exception e) {
            System.out.println("Ошибка при заполнении модели из common-info.html: " + e.getMessage());
            throw new RuntimeException("Не удалось заполнить данные контракта из common-info.html", e);
        }
    }

    public void fillSupplierModelFromCommonInfo(Supplier supplier, Map<String, Object> contractData) {
        try {
            // Получаем блок с основной информацией
            Map<String, String> commonInfo = (Map<String, String>) contractData.get("Информация о поставщике");
            if (commonInfo == null) {
                System.out.println("Не найден блок 'Информация о контракте'");
                return;
            }
            // Парсим данные поставщика
            String supplierName = commonInfo.get("Полное наименование поставщика");
            if (supplierName != null) {
                supplier.setName(supplierName);
            }
            // Парсим данные заказчика (если нужны)
            String customerInfo = commonInfo.get("Наименование заказчика");
            if (customerInfo != null) {
                // Можно сохранить в дополнительное поле, если нужно
            }
            // Парсим адрес поставщика
            String address = commonInfo.get("Адрес");
            if (address != null) {
                supplier.setAddress(address);
                // Дополнительно парсим страну и индекс из адреса
                if (address.contains("Российская Федерация")) {
                    supplier.setCountryName("Российская Федерация");
                    supplier.setCountryCode("643"); // Код ОКСМ для России

                }
            }
            // Парсим реквизиты
            supplier.setInn(commonInfo.get("ИНН"));
            supplier.setKpp(commonInfo.get("КПП"));

            // Устанавливаем тип поставщика (юридическое лицо по умолчанию)
            supplier.setType("Юридическое лицо");
        } catch (Exception e) {
            System.out.println("Ошибка при заполнении данных поставщика из common-info: " + e.getMessage());
        }
    }



}
