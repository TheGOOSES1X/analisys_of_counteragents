package Parser.Database.hooks;
import Parser.Database.models.*;
import Parser.implementations.Parser44.PurchaseParser44;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import java.util.*;
import java.util.stream.Collectors;
public class DatabaseService {
    public void initializeDatabase() {
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        try (Session session = sessionFactory.openSession()) {
            session.createQuery("from Purchase where 1=0").list();
        }
    }

    public synchronized void saveToDatabase(PurchaseParser44.ParseResult result) {
        if (result.purchaseData == null) return;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                Purchase purchase = result.purchaseData;

                // 1. Обработка Customer
                handleCustomer(session, purchase);

                // 2. Обработка Supplier и Contract
                handleSupplierAndContract(session, purchase);

                handlePurchase(session, purchase);
//
//                handleProcurementObjects(session, purchase);



                transaction.commit();
            } catch (Exception e) {
                if (transaction != null) {
                    transaction.rollback();
                }
                throw new RuntimeException("Failed to save to database", e);
            }
        }
    }

    private void handleCustomer(Session session, Purchase purchase) {
        if (purchase.getCustomer() == null) return;

        Customer customer = purchase.getCustomer();
        Customer existingCustomer = session.createQuery(
                        "FROM Customer WHERE fullName = :fullName", Customer.class)
                .setParameter("fullName", customer.getFullName())
                .uniqueResult();

        if (existingCustomer != null) {
            updateCustomer(existingCustomer, customer);
            purchase.setCustomer(existingCustomer);
        } else {
            session.persist(customer);
            session.flush();
        }
    }

    private void handleSupplierAndContract(Session session, Purchase purchase) {
        if (purchase.getContract() == null) return;

        // Обработка Supplier
        Supplier supplier = purchase.getContract().getSupplier();
        if (supplier != null) {
            Supplier existingSupplier = session.createQuery(
                            "FROM Supplier WHERE name = :name", Supplier.class)
                    .setParameter("name", supplier.getName())
                    .uniqueResult();

            if (existingSupplier != null) {
                updateSupplier(existingSupplier, supplier);
                supplier = existingSupplier;
            } else {
                session.persist(supplier);
                session.flush();
            }
            purchase.getContract().setSupplier(supplier);
        }

        // Обработка Contract
        Contract contract = purchase.getContract();
        contract.setPurchase(purchase);

        Contract existingContract = session.createQuery(
                        "FROM Contract WHERE contractNumber = :contractNumber", Contract.class)
                .setParameter("contractNumber", contract.getContractNumber())
                .uniqueResult();

        if (existingContract != null) {
            updateContract(existingContract, contract);
            purchase.setContract(existingContract);
        } else {
            session.persist(contract);
        }
        session.flush();
    }

    private void handlePurchase(Session session, Purchase purchase) {
        Purchase existingPurchase = session.createQuery(
                        "FROM Purchase WHERE purchaseNumber = :purchaseNumber", Purchase.class)
                .setParameter("purchaseNumber", purchase.getPurchaseNumber())
                .uniqueResult();

        if (existingPurchase != null) {
            updatePurchase(existingPurchase, purchase);
            purchase = existingPurchase;
        } else {
            session.persist(purchase);
            session.flush(); // Необходимо для получения ID перед обработкой ProcurementObjects
        }

        // Обрабатываем связанные объекты
        handleSupplierAndContract(session, purchase);
        updateProcurementObjects(session, purchase); // Заменяем handleProcurementObjects на updateProcurementObjects
    }


    private void handleProcurementObjects(Session session, Purchase purchase) {
        if (purchase.getProcurementObjects() == null || purchase.getProcurementObjects().isEmpty()) return;

        for (ProcurementObject po : purchase.getProcurementObjects()) {
            po.setPurchase(purchase);
            session.persist(po);
        }
    }

    private synchronized void updatePurchase(Purchase existing, Purchase newData) {
        existing.setLaw(newData.getLaw());
        existing.setInitialMaxPrice(newData.getInitialMaxPrice());
        existing.setCurrency(newData.getCurrency());
        existing.setPurchaseObject(newData.getPurchaseObject());
        existing.setProcurementMethod(newData.getProcurementMethod());
        existing.setIkz(newData.getIkz());
        existing.setExecutor(newData.getExecutor());
        existing.setPublicationDate(newData.getPublicationDate());
        existing.setUpdateDate(newData.getUpdateDate());
        existing.setApplicationEndDate(newData.getApplicationEndDate());
        existing.setAuctionDate(newData.getAuctionDate());
        existing.setProcurementStage(newData.getProcurementStage());
        existing.setComplaints(newData.getComplaints());


    }

    private synchronized void updateCustomer(Customer existing, Customer newData) {
        existing.setShortName(newData.getShortName());
        existing.setConsolidatedRegisterCode(newData.getConsolidatedRegisterCode());
        existing.setRegistrationDate(newData.getRegistrationDate());
        existing.setLastUpdated(newData.getLastUpdated());
        existing.setInn(newData.getInn());
        existing.setKpp(newData.getKpp());
        existing.setOgrn(newData.getOgrn());
        existing.setOktmo(newData.getOktmo());
        existing.setLocation(newData.getLocation());
        existing.setIku(newData.getIku());
        existing.setIkuAssignmentDate(newData.getIkuAssignmentDate());
        existing.setOkfsCode(newData.getOkfsCode());
        existing.setOwnershipFormName(newData.getOwnershipFormName());
        existing.setOkopfCode(newData.getOkopfCode());
        existing.setLegalFormName(newData.getLegalFormName());
        existing.setOrganizationAuthorities(newData.getOrganizationAuthorities());
        existing.setUniqueRegistrationNumber(newData.getUniqueRegistrationNumber());
        existing.setTaxRegistrationDate(newData.getTaxRegistrationDate());
        existing.setOrganizationType(newData.getOrganizationType());
        existing.setOrganizationLevel(newData.getOrganizationLevel());
        existing.setOkved(newData.getOkved());
        existing.setConsolidatedRegisterCodeAlt(newData.getConsolidatedRegisterCodeAlt());
        existing.setAuthorizedOrganizationName(newData.getAuthorizedOrganizationName());
        existing.setPhone(newData.getPhone());
        existing.setFax(newData.getFax());
        existing.setPostalAddress(newData.getPostalAddress());
        existing.setEmail(newData.getEmail());
        existing.setWebsite(newData.getWebsite());
        existing.setContactPerson(newData.getContactPerson());
        existing.setTimeZone(newData.getTimeZone());

    }

    private synchronized void updateSupplier(Supplier existing, Supplier newData) {
        existing.setType(newData.getType());
        existing.setCountryName(newData.getCountryName());
        existing.setCountryCode(newData.getCountryCode());
        existing.setAddress(newData.getAddress());
        existing.setPostalAddress(newData.getPostalAddress());
        existing.setOgrn(newData.getOgrn());
        existing.setInn(newData.getInn());
        existing.setKpp(newData.getKpp());
        existing.setStatus(newData.getStatus());
        existing.setEmail(newData.getEmail());
        existing.setPhone(newData.getPhone());


    }

    private synchronized void updateContract(Contract existing, Contract newData) {
        if (newData.getRegistryNumber() != null) {
            existing.setRegistryNumber(newData.getRegistryNumber());
        }
        if (newData.getStatus() != null) {
            existing.setStatus(newData.getStatus());
        }
        if (newData.getProcurementNoticeNumber() != null) {
            existing.setProcurementNoticeNumber(newData.getProcurementNoticeNumber());
        }
        if (newData.getProcurementIdentificationCode() != null) {
            existing.setProcurementIdentificationCode(newData.getProcurementIdentificationCode());
        }
        if (newData.getElectronicContractId() != null) {
            existing.setElectronicContractId(newData.getElectronicContractId());
        }
        if (newData.getSoleSupplierBasis() != null) {
            existing.setSoleSupplierBasis(newData.getSoleSupplierBasis());
        }
        if (newData.getSoleSupplierDocumentDetails() != null) {
            existing.setSoleSupplierDocumentDetails(newData.getSoleSupplierDocumentDetails());
        }
        if (newData.getBankingTreasurySupportInfo() != null) {
            existing.setBankingTreasurySupportInfo(newData.getBankingTreasurySupportInfo());
        }
        if (newData.getConclusionDate() != null) {
            existing.setConclusionDate(newData.getConclusionDate());
        }
        if (newData.getContractNumber() != null) {
            existing.setContractNumber(newData.getContractNumber());
        }
        if (newData.getSubject() != null) {
            existing.setSubject(newData.getSubject());
        }
        if (newData.getContractPrice() != null) {
            existing.setContractPrice(newData.getContractPrice());
        }
        if (newData.getIncludingVat() != null) {
            existing.setIncludingVat(newData.getIncludingVat());
        }
        if (newData.getCurrency() != null) {
            existing.setCurrency(newData.getCurrency());
        }
        if (newData.getStartDate() != null) {
            existing.setStartDate(newData.getStartDate());
        }
        if (newData.getEndDate() != null) {
            existing.setEndDate(newData.getEndDate());
        }
        if (newData.getContractStageId() != null) {
            existing.setContractStageId(newData.getContractStageId());
        }
        if (newData.getAdvanceAmount() != null) {
            existing.setAdvanceAmount(newData.getAdvanceAmount());
        }
        if (newData.getPenaltyDeductionApplied() != null) {
            existing.setPenaltyDeductionApplied(newData.getPenaltyDeductionApplied());
        }
        if (newData.getAdditionalInfo() != null) {
            existing.setAdditionalInfo(newData.getAdditionalInfo());
        }
        if (newData.getTreasuryGuaranteeAmount() != null) {
            existing.setTreasuryGuaranteeAmount(newData.getTreasuryGuaranteeAmount());
        }
        if (newData.getNationalRegimeInfo() != null) {
            existing.setNationalRegimeInfo(newData.getNationalRegimeInfo());
        }
        if (newData.getContractGuaranteeInfo() != null) {
            existing.setContractGuaranteeInfo(newData.getContractGuaranteeInfo());
        }
        if (newData.getQualityGuaranteeInfo() != null) {
            existing.setQualityGuaranteeInfo(newData.getQualityGuaranteeInfo());
        }
        if (newData.getDeliveryPlaceInfo() != null) {
            existing.setDeliveryPlaceInfo(newData.getDeliveryPlaceInfo());
        }
        if (newData.getSupplier() != null) {
            existing.setSupplier(newData.getSupplier());
        }
        if (newData.getPurchase() != null) {
            existing.setPurchase(newData.getPurchase());
        }
    }

    private synchronized void updateProcurementObjects(Session session, Purchase purchase) {
        // Получаем существующие объекты закупки из БД
        List<ProcurementObject> existingObjects = session.createQuery(
                        "FROM ProcurementObject WHERE purchase.id = :purchaseId", ProcurementObject.class)
                .setParameter("purchaseId", purchase.getId())
                .list();



        // Создаем мапу для быстрого поиска по ID (если объект уже сохранен) или по составному ключу
        Map<String, ProcurementObject> existingObjectsMap = new HashMap<>();
        for (ProcurementObject obj : existingObjects) {
            // Используем составной ключ: name + ktruOkpd2Codes + unit
            String key = obj.getName() + "|" + obj.getKtruOkpd2Codes() + "|" + obj.getUnit();
            existingObjectsMap.put(key, obj);
        }

        // Обрабатываем новые объекты
        for (ProcurementObject newObj : purchase.getProcurementObjects()) {
            // Генерируем тот же составной ключ
            String key = newObj.getName() + "|" + newObj.getKtruOkpd2Codes() + "|" + newObj.getUnit();

            ProcurementObject existingObj = existingObjectsMap.get(key);

            if (existingObj != null) {
                // Обновляем существующий объект
                existingObj.setType(newObj.getType());
                existingObj.setQuantity(newObj.getQuantity());
                existingObj.setPricePerUnit(newObj.getPricePerUnit());
                existingObj.setVatRate(newObj.getVatRate());
                existingObj.setCountryOfOrigin(newObj.getCountryOfOrigin());
                existingObj.setTotalAmount(newObj.getTotalAmount());
                session.merge(existingObj);
            } else {
                // Добавляем новый объект
                newObj.setPurchase(purchase);
                session.persist(newObj);
            }
        }

        // Удаляем объекты, которых нет в новых данных (опционально)
        Set<String> newKeys = purchase.getProcurementObjects().stream()
                .map(obj -> obj.getName() + "|" + obj.getKtruOkpd2Codes() + "|" + obj.getUnit())
                .collect(Collectors.toSet());

        existingObjectsMap.keySet().removeAll(newKeys);
        for (ProcurementObject obsoleteObj : existingObjectsMap.values()) {
            session.remove(obsoleteObj);
        }
    }

    public List<String> getAllSupplierInns() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                            "SELECT DISTINCT s.inn FROM Supplier s WHERE s.inn IS NOT NULL", String.class)
                    .getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch supplier INNs", e);
        }
    }
    public synchronized void saveSuppliers(List<Supplier> suppliers) {
        if (suppliers == null || suppliers.isEmpty()) return;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                int batchSize = 50;
                for (int i = 0; i < suppliers.size(); i++) {
                    Supplier supplier = suppliers.get(i);
                    saveOrUpdateSupplier(session, supplier);

                    if (i % batchSize == 0 && i > 0) {
                        session.flush();
                        session.clear();
                    }
                }
                transaction.commit();
            } catch (Exception e) {
                if (transaction != null) {
                    transaction.rollback();
                }
                throw new RuntimeException("Failed to save suppliers", e);
            }
        }
    }

    public synchronized void saveSingleSupplier(Supplier supplier) {
        if (supplier == null) {
            System.out.println("Поставщик для сохранения равен null");
            return;
        }

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                // Проверяем существование поставщика по NaturalId (имени)
                Supplier existingSupplier = session.bySimpleNaturalId(Supplier.class)
                        .load(supplier.getName());

                if (existingSupplier == null) {
                    // Сохраняем нового поставщика
                    session.persist(supplier);
                    System.out.println("Сохранен новый поставщик: " + supplier.getName());
                } else {
                    // Обновляем существующего поставщика


                    System.out.println("Обновлен существующий поставщик: " + supplier.getName());
                }

                transaction.commit();
            } catch (Exception e) {
                if (transaction != null && transaction.isActive()) {
                    transaction.rollback();
                }
                throw new RuntimeException("Ошибка при сохранении поставщика: " + supplier.getName(), e);
            }
        }
    }
    private synchronized Supplier saveOrUpdateSupplier(Session session, Supplier supplier) {
        Supplier existingSupplier = null;

        // Поиск по ИНН, если он есть
        if (supplier.getInn() != null && !supplier.getInn().isEmpty()) {
            existingSupplier = session.byNaturalId(Supplier.class)
                    .using("inn", supplier.getInn())
                    .load();
        }

        // Если не найден по ИНН, ищем по имени
        if (existingSupplier == null && supplier.getName() != null) {
            existingSupplier = session.createQuery(
                            "FROM Supplier WHERE name = :name", Supplier.class)
                    .setParameter("name", supplier.getName())
                    .uniqueResult();
        }

        if (existingSupplier != null) {
            updateSupplier(existingSupplier, supplier);
            session.merge(existingSupplier);
            return existingSupplier;
        } else {
            session.persist(supplier);
            return supplier;
        }
    }
    public synchronized  void saveSupplierReliability(List<SupplierReliability> reliabilities) {
        if (reliabilities == null || reliabilities.isEmpty()) return;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                for (SupplierReliability reliability : reliabilities) {
                    // Находим поставщика по ИНН
                    Supplier supplier = session.createQuery(
                                    "FROM Supplier WHERE inn = :inn", Supplier.class)
                            .setParameter("inn", reliability.getInn())
                            .uniqueResult();

                    if (supplier != null) {
                        reliability.setSupplier(supplier);
                    }

                    // Проверяем, существует ли уже запись для этого поставщика
                    SupplierReliability existing = session.createQuery(
                                    "FROM SupplierReliability WHERE inn = :inn", SupplierReliability.class)
                            .setParameter("inn", reliability.getInn())
                            .uniqueResult();

                    if (existing != null) {
                        // Обновляем существующую запись
                        updateSupplierReliability(existing, reliability);
                        session.merge(existing);
                    } else {
                        // Создаем новую запись
                        session.persist(reliability);
                    }
                }
                transaction.commit();
            } catch (Exception e) {
                if (transaction != null) {
                    transaction.rollback();
                }
                throw new RuntimeException("Failed to save supplier reliability data", e);
            }
        }
    }

    private synchronized void updateSupplierReliability(SupplierReliability existing, SupplierReliability newData) {
        existing.setName(newData.getName());
        existing.setInn(newData.getInn());
        existing.setAuthority(newData.getAuthority());
        existing.setReason(newData.getReason());
        existing.setRegistryNumber(newData.getRegistryNumber());
        existing.setInclusionDate(newData.getInclusionDate());
        existing.setStatus(newData.getStatus());
        existing.setExclusionDate(newData.getExclusionDate());
        existing.setEntityType(newData.getEntityType());
        existing.setEruzNumber(newData.getEruzNumber());
        existing.setPersonInn(newData.getPersonInn());
        existing.setLaw(newData.getLaw());
        existing.setRecordNumber(newData.getRecordNumber());
        existing.setUpdateDate(newData.getUpdateDate());
        existing.setPlannedExclusionDate(newData.getPlannedExclusionDate());
    }
    public synchronized void saveJudicialProceedings(List<JudicialProceeding> proceedings, String supplierInn) {
        if (proceedings == null || proceedings.isEmpty()) return;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                // Находим поставщика по ИНН
                Supplier supplier = session.createQuery(
                                "FROM Supplier WHERE inn = :inn", Supplier.class)
                        .setParameter("inn", supplierInn)
                        .uniqueResult();

                if (supplier == null) {
                    System.out.println("Поставщик с ИНН " + supplierInn + " не найден");
                    return;
                }

                // Сохраняем каждое судебное дело
                for (JudicialProceeding proceeding : proceedings) {
                    // Проверяем, существует ли уже такое дело
                    JudicialProceeding existing = session.createQuery(
                                    "FROM JudicialProceeding WHERE caseNumber = :caseNumber AND supplier.id = :supplierId",
                                    JudicialProceeding.class)
                            .setParameter("caseNumber", proceeding.getCaseNumber())
                            .setParameter("supplierId", supplier.getId())
                            .uniqueResult();

                    if (existing != null) {
                        // Обновляем существующее дело
                        updateJudicialProceeding(existing, proceeding);
                        session.merge(existing);
                    } else {
                        // Создаем новое дело и связываем с поставщиком
                        proceeding.setSupplier(supplier);
                        session.persist(proceeding);
                    }
                }

                transaction.commit();
            } catch (Exception e) {
                if (transaction != null) {
                    transaction.rollback();
                }
                throw new RuntimeException("Ошибка при сохранении судебных дел", e);
            }
        }
    }

    private synchronized void updateJudicialProceeding(JudicialProceeding existing, JudicialProceeding newData) {
        existing.setCaseNumber(newData.getCaseNumber());
        existing.setJudge(newData.getJudge());
        existing.setCurrentInstance(newData.getCurrentInstance());
        existing.setPlaintiff(newData.getPlaintiff());
        existing.setDefendant(newData.getDefendant());
        existing.setStartDate(newData.getStartDate());
        existing.setStatus(newData.getStatus());
        existing.setDescription(newData.getDescription());
        existing.setReason(newData.getReason());
        existing.setEndDate(newData.getEndDate());
        existing.setDuration(newData.getDuration());
        existing.setStatus(newData.getStatus());
        existing.setOutcome(newData.getOutcome());
    }

}
