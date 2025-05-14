package Parser.implementations.Parser44;

import Parser.Database.models.*;
import Parser.implementations.Parser44.Contracts.ComplaintsURLGetter;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.math.BigDecimal;
import java.util.*;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

public class PurchasePageParser {

    public Purchase parsePurchasePage(String url, WebDriver driver, WebDriverWait wait) {
        try {
            ComplaintsURLGetter complaintsCounter  = new ComplaintsURLGetter();
            Purchase purchase = new Purchase();

            parseCardMainInfo(driver, wait, purchase);
            // 2. Собираем все данные со страницы
            Map<String, String> allData = collectAllSectionData(driver);

            // 3. Собираем данные о датах из специального блока
            Map<String, String> dateData = collectDateInfo(driver);

            // 3. Выводим все собранные данные для анализа
//            System.out.println("\n=== ВСЕ ДАННЫЕ СО СТРАНИЦЫ ЗАКУПКИ ===");
//            allData.forEach((key, value) -> System.out.printf("%-50s: %s%n", key, value));
//            System.out.println("=======================================\n");
//
//            System.out.println("\n=== ДАННЫЕ О ДАТАХ ===");
//            dateData.forEach((key, value) -> System.out.printf("%-50s: %s%n", key, value));
//            System.out.println("=======================================\n");

            // Парсим объекты закупки
//            List<ProcurementObject> procurementObjects = parseProcurementObjectsTable(driver);
//            procurementObjects.forEach(purchase::addProcurementObject);
            List<ProcurementObject> procurementObjects = parseProcurementObjectsTable(driver);
            if (!procurementObjects.isEmpty()) {
                purchase.addProcurementObject(procurementObjects.get(0)); // Добавляем только первый элемент
            }

            // Выводим информацию о количестве найденных объектов
            System.out.println("Найдено объектов закупки: " + procurementObjects.size());

            // 4. Заполняем поля закупки из собранных данных

            if (allData.containsKey("Дата подведения итогов определения поставщика (подрядчика, исполнителя)")) {

                purchase.setAuctionDate(parseDate(allData.get("Дата подведения итогов определения поставщика (подрядчика, исполнителя)")));
            }
            if (allData.containsKey("Начальная (максимальная) цена контракта")) {
                purchase.setInitialMaxPrice(parsePrice(allData.get("Начальная (максимальная) цена контракта")));
            }
            if (allData.containsKey("Способ определения поставщика (подрядчика, исполнителя)")) {
                purchase.setProcurementMethod(allData.get("Способ определения поставщика (подрядчика, исполнителя)"));
            }

            if (allData.containsKey("Валюта")) {
                purchase.setCurrency(allData.get("Валюта"));
            }
            if (allData.containsKey("Идентификационный код закупки (ИКЗ)")) {
                purchase.setIkz(allData.get("Идентификационный код закупки (ИКЗ)"));
            }
            if (dateData.containsKey("Размещено")) {
                purchase.setPublicationDate(parseDate(dateData.get("Размещено")));
            }
            if (dateData.containsKey("Окончание подачи заявок")) {
                purchase.setApplicationEndDate(parseDate(dateData.get("Окончание подачи заявок")));
            }

            if (dateData.containsKey("Обновлено")) {
                purchase.setUpdateDate(parseDate(dateData.get("Обновлено")));
            }

            int complaintsCount = complaintsCounter.countComplaintsByRegNumber(url, driver, wait);
            purchase.setComplaints(complaintsCount);


            return purchase;

        } catch (Exception e) {
            throw new RuntimeException("Ошибка парсинга страницы закупки: " + e.getMessage(), e);
        }
    }
    private void parseCardMainInfo(WebDriver driver, WebDriverWait wait, Purchase purchase) {
        try {
            // 1. Ожидаем загрузки всей секции
            WebElement sectionMainInfo = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.cssSelector("div.sectionMainInfo")));

            // 2. Парсим закон и тип процедуры
            WebElement titleElement = sectionMainInfo.findElement(
                    By.cssSelector("div.cardMainInfo__title.d-flex.text-truncate"));
            purchase.setLaw(titleElement.getText().trim().split("\\s+")[0]); // Берем только "44-ФЗ"

            // 3. Парсим номер закупки
            WebElement numberElement = sectionMainInfo.findElement(
                    By.cssSelector("span.cardMainInfo__purchaseLink a"));
            purchase.setPurchaseNumber(numberElement.getText().trim());

            // 4. Парсим статус/этап закупки
            WebElement stageElement = sectionMainInfo.findElement(
                    By.cssSelector("span.cardMainInfo__state.distancedText"));
            purchase.setProcurementStage(stageElement.getText().trim());

            // 5. Парсим объект закупки (может отсутствовать)
            try {
                purchase.setPurchaseObject(sectionMainInfo.findElement(
                                By.xpath(".//div[contains(@class,'cardMainInfo__section')]" +
                                        "[.//span[contains(@class,'cardMainInfo__title') and " +
                                        "contains(text(),'Объект закупки')]]"))
                        .findElement(By.cssSelector("span.cardMainInfo__content"))
                        .getText().trim());
            } catch (NoSuchElementException e) {
                System.out.println("Объект закупки не указан");
                purchase.setPurchaseObject(null);
            }

        } catch (Exception e) {
            System.out.println("Критическая ошибка при парсинге основной информации: " + e.getMessage());
            throw new RuntimeException("Не удалось распарсить основную информацию", e);
        }
    }

    private Map<String, String> collectAllSectionData(WebDriver driver) {
        Map<String, String> sectionData = new LinkedHashMap<>();
        Map<String, Integer> titleCounts = new HashMap<>();

        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
            List<WebElement> containers = wait.until(ExpectedConditions
                    .presenceOfAllElementsLocatedBy(By.cssSelector("div.container")));

//            System.out.println("Найдено контейнеров: " + containers.size());


            String[][] selectors = {
                    {"section.blockInfo__section.section", null},  // Прямые секции
                    {"section.blockInfo__section", null},         // Для блока ИКУ
                    {"section", "div.blockInfo__section"}         // Вложенные секции
            };

            for (WebElement container : containers) {
                for (String[] selectorPair : selectors) {
                    String sectionSelector = selectorPair[0];
                    String parentSelector = selectorPair[1];

                    try {
                        List<WebElement> parentElements = parentSelector != null ?
                                container.findElements(By.cssSelector(parentSelector)) :
                                Collections.singletonList(container);

                        for (WebElement parent : parentElements) {
                            try {
                                List<WebElement> sections = parent.findElements(By.cssSelector(sectionSelector));
//                                System.out.println("Найдено секций (" + sectionSelector + " в " +
//                                        (parentSelector != null ? parentSelector : "container") + "): " + sections.size());

                                for (WebElement section : sections) {
                                    try {
                                        List<WebElement> titles = section.findElements(By.cssSelector("span.section__title"));
                                        List<WebElement> infos = section.findElements(By.cssSelector("span.section__info"));

                                        if (!titles.isEmpty() && !infos.isEmpty()) {
                                            String title = titles.get(0).getText().trim();
                                            String info = infos.get(0).getText().trim();

                                            if (!title.isEmpty()) {

                                                String originalTitle = title;
                                                int count = titleCounts.getOrDefault(originalTitle, 0);

                                                if (count > 0) {
                                                    title = originalTitle + " (" + count + ")";
                                                }

                                                titleCounts.put(originalTitle, count + 1);

                                                sectionData.put(title, info);
//                                                System.out.println("Добавлено: '" + title + "' = '" + info + "'");
                                            }
                                        }
                                    } catch (Exception e) {
                                        System.out.println("Ошибка в секции: " + e.getMessage());
                                    }
                                }
                            } catch (Exception e) {
                                System.out.println("Ошибка при поиске секций: " + e.getMessage());
                            }
                        }
                    } catch (Exception e) {
                        System.out.println("Ошибка с родительским элементом: " + e.getMessage());
                    }
                }
            }

            // Дополнительная диагностика
//            System.out.println("\nНайдено заголовков блоков:");
//            driver.findElements(By.cssSelector("h2.blockInfo__title"))
//                    .forEach(h -> System.out.println("- " + h.getText().trim()));

        } catch (Exception e) {
            System.out.println("Ошибка при сборе данных: " + e.getMessage());
        }

//        System.out.println("\n=== ИТОГОВЫЕ ДАННЫЕ ===");
//        sectionData.forEach((k, v) -> System.out.printf("%-40s: %s%n", k, v));
//        System.out.println("=======================");

        return sectionData;
    }

    private Map<String, String> collectDateInfo(WebDriver driver) {
        Map<String, String> dateData = new LinkedHashMap<>();
        try {
            WebElement dateBlock = driver.findElement(By.cssSelector("div.date"));
            List<WebElement> dateSections = dateBlock.findElements(By.cssSelector("div.cardMainInfo__section"));

            for (WebElement section : dateSections) {
                try {
                    String title = section.findElement(By.cssSelector("span.cardMainInfo__title")).getText().trim();
                    String value = section.findElement(By.cssSelector("span.cardMainInfo__content")).getText().trim();

                    if (!title.isEmpty() && !value.isEmpty()) {
                        dateData.put(title, value);
//                        System.out.println("Добавлена дата: '" + title + "' = '" + value + "'");
                    }
                } catch (Exception e) {
                    System.out.println("Ошибка при обработке секции с датой: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.out.println("Ошибка при поиске блока с датами: " + e.getMessage());
        }
        return dateData;
    }

    private List<ProcurementObject> parseProcurementObjectsTable(WebDriver driver) {
        List<ProcurementObject> procurementObjects = new ArrayList<>();

        try {
            // Находим контейнер с id="positionKTRU"
            WebElement container = driver.findElement(By.id("positionKTRU"));

            // Ищем таблицу внутри этого контейнера
            WebElement table = container.findElement(By.cssSelector("table.blockInfo__table.tableBlock"));

            // Остальной код парсинга остается таким же
            List<WebElement> rows = table.findElements(By.cssSelector("tbody.tableBlock__body tr.tableBlock__row"));

            for (WebElement row : rows) {
                try {
                    ProcurementObject obj = new ProcurementObject();
                    List<WebElement> cells = row.findElements(By.tagName("td"));

                    // Парсинг данных из ячеек
                    if (cells.size() > 1) obj.setKtruOkpd2Codes(cells.get(1).getText().trim());
                    if (cells.size() > 2) obj.setName(cells.get(2).getText().trim());
                    if (cells.size() > 3) obj.setUnit(cells.get(3).getText().trim());

                    if (cells.size() > 4) {
                        String quantityStr = cells.get(4).getText().trim().replace(",", ".");
                        try {
                            obj.setQuantity(new BigDecimal(quantityStr));
                        } catch (Exception e) {
                            System.out.println("Ошибка парсинга количества: " + quantityStr);
                        }
                    }

                    if (cells.size() > 5) {
                        String priceStr = cells.get(5).getText()
                                .replaceAll("[^\\d.]", "")
                                .trim();
                        try {
                            obj.setPricePerUnit(new BigDecimal(priceStr));
                        } catch (Exception e) {
                            System.out.println("Ошибка парсинга цены: " + priceStr);
                        }
                    }

                    if (cells.size() > 6) {
                        String amountStr = cells.get(6).getText()
                                .replaceAll("[^\\d.]", "")
                                .trim();
                        try {
                            obj.setTotalAmount(new BigDecimal(amountStr));
                        } catch (Exception e) {
                            System.out.println("Ошибка парсинга суммы: " + amountStr);
                        }
                    }

                    procurementObjects.add(obj);

                    // Логирование для отладки
//                    System.out.println("Добавлен объект закупки:");
//                    System.out.println("Код: " + obj.getKtruOkpd2Codes());
//                    System.out.println("Наименование: " + obj.getName());
//                    System.out.println("Ед.изм: " + obj.getUnit());
//                    System.out.println("Количество: " + obj.getQuantity());
//                    System.out.println("Цена за ед.: " + obj.getPricePerUnit());
//                    System.out.println("Стоимость: " + obj.getTotalAmount());
//                    System.out.println("----------------------");

                } catch (Exception e) {
                    System.out.println("Ошибка при парсинге строки таблицы: " + e.getMessage());
                }
            }

            // Парсим итоговую сумму
            try {
                WebElement footer = table.findElement(By.cssSelector("tfoot.tableBlock__foot"));
                String totalAmountStr = footer.findElement(By.cssSelector("span.cost"))
                        .getText()
                        .replaceAll("[^\\d.]", "")
                        .trim();
//                System.out.println("Итоговая сумма: " + totalAmountStr);
            } catch (Exception e) {
                System.out.println("Не удалось распарсить итоговую сумму: " + e.getMessage());
            }

        } catch (NoSuchElementException e) {
            System.out.println("Не найден контейнер с объектами закупки (positionKTRU)");
        } catch (Exception e) {
            System.out.println("Ошибка при парсинге таблицы объектов закупки: " + e.getMessage());
        }

        return procurementObjects;
    }
    private LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) return null;
        try {
            return LocalDate.parse(dateStr.trim().replaceAll("[^\\d.]", ""),
                    DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        } catch (Exception e) {
            System.out.println("Ошибка парсинга даты: " + dateStr);
            return null;
        }
    }
    private BigDecimal parsePrice(String priceStr) {
        if (priceStr == null || priceStr.trim().isEmpty()) return null;
        try {
            return new BigDecimal(priceStr.replaceAll("[^\\d.]", ""));
        } catch (Exception e) {
            System.out.println("Ошибка парсинга цены: " + priceStr);
            return null;
        }
    }

    // Метод для создания скринш
}