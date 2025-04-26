package Parser.implementations.Parser44;
import Parser.Database.hooks.HibernateUtil;
import Parser.Database.models.*;
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

    public void saveToDatabase(PurchaseParser44.ParseResult result) {
        if (result.purchaseData == null) return;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                Purchase purchase = result.purchaseData;

                // 1. Обработка Customer (должен быть сохранен первым)
                if (purchase.getCustomer() != null) {
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
                        session.flush(); // Гарантируем получение ID
                    }
                }

                // 2. Обработка Purchase
                Purchase existingPurchase = session.createQuery(
                                "FROM Purchase WHERE purchaseNumber = :purchaseNumber", Purchase.class)
                        .setParameter("purchaseNumber", purchase.getPurchaseNumber())
                        .uniqueResult();

                if (existingPurchase != null) {
                    updatePurchase(existingPurchase, purchase);
                    purchase = existingPurchase;
                } else {
                    session.persist(purchase);
                }

                // 3. Обработка Contract и Supplier
                if (purchase.getContract() != null) {
                    Contract contract = purchase.getContract();
                    contract.setPurchase(purchase);

                    if (contract.getSupplier() != null) {
                        Supplier supplier = contract.getSupplier();
                        Supplier existingSupplier = session.byNaturalId(Supplier.class)
                                .using("name", supplier.getName())
                                .load();

                        if (existingSupplier != null) {
                            updateSupplier(existingSupplier, supplier);
                            contract.setSupplier(existingSupplier);
                        } else {
                            session.persist(supplier);
                            session.flush();
                        }
                    }

                    if (contract.getId() != null) {
                        session.merge(contract);
                    } else {
                        session.persist(contract);
                    }
                }

                // 4. Обработка ProcurementObjects
                if (purchase.getProcurementObjects() != null && !purchase.getProcurementObjects().isEmpty()) {
                    updateProcurementObjects(session, purchase);
                }

                transaction.commit();
            } catch (Exception e) {
                if (transaction != null) {
                    transaction.rollback();
                }
                throw new RuntimeException("Failed to save to database", e);
            }
        }
    }

    private void updatePurchase(Purchase existing, Purchase newData) {
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


    }

    private void updateCustomer(Customer existing, Customer newData) {
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

    private void updateSupplier(Supplier existing, Supplier newData) {
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

    private void updateProcurementObjects(Session session, Purchase purchase) {
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
    public void saveJudicialProceedings(List<JudicialProceeding> proceedings, String supplierInn) {
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

    private void updateJudicialProceeding(JudicialProceeding existing, JudicialProceeding newData) {
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
