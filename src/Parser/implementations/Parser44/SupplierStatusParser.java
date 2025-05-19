package Parser.implementations.Parser44;


import Parser.Database.models.SupplierReliability;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.NoSuchElementException;

public class SupplierStatusParser {
    private final WebDriver driver;
    private final WebDriverWait wait;
    private static final String BASE_URL = "https://zakupki.gov.ru/epz/dishonestsupplier/search/results.html?searchString=%s&morphology=on&search-filter=Дате+размещения&sortBy=UPDATE_DATE&pageNumber=1&sortDirection=false&recordsPerPage=_10&showLotsInfoHidden=false&fz94=on&fz223=on&ppRf615=on";

    public SupplierStatusParser(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(5));
    }

    public List<String> parseSupplierStatuses(List<String> inns) {
        List<String> dishonestyLinks = new ArrayList<>();
        if (inns == null || inns.isEmpty()) return dishonestyLinks;

        String originalUrl = driver.getCurrentUrl();
        Set<Cookie> originalCookies = driver.manage().getCookies();

        try {
            for (String inn : inns) {
                try {
                    List<String> linksForInn = parseSupplierStatus(inn);
                    if (linksForInn != null) {
                        dishonestyLinks.addAll(linksForInn);
                        for (String url : linksForInn) {
                            try {
                                parseAndPrintDetails(url);
                            } catch (Exception e) {
                                System.err.println("Ошибка при обработке ссылки " + url);
                            }
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Ошибка для INN: " + inn);
                } finally {
                    // Восстановление состояния после каждого INN
                    restoreOriginalState(originalUrl, originalCookies);
                }
            }
        } finally {
            restoreOriginalState(originalUrl, originalCookies);
        }
        return dishonestyLinks;
    }
    private void restoreOriginalState(String originalUrl, Set<Cookie> originalCookies) {
        try {
            driver.get(originalUrl);
            driver.manage().deleteAllCookies();
            originalCookies.forEach(c -> {
                try {
                    driver.manage().addCookie(c);
                } catch (Exception e) {
                    System.err.println("Не удалось восстановить куки: " + c.getName());
                }
            });
        } catch (Exception e) {
            System.err.println("Ошибка восстановления состояния: " + e.getMessage());
        }
    }

    private List<String> parseSupplierStatus(String inn) {
        List<String> detailLinks = new ArrayList<>();
        String url = String.format(BASE_URL, inn);
        driver.get(url);

        try {
            // Проверяем наличие сообщения "Поиск не дал результатов"
            List<WebElement> noResultsElements = driver.findElements(By.cssSelector("p.noRecords"));
            if (!noResultsElements.isEmpty() && noResultsElements.get(0).isDisplayed()) {
                System.out.println("No dishonesty records found for INN: " + inn);
                return detailLinks;
            }

            // Ожидаем загрузки результатов
            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".search-registry-entrys-block")));

            // Получаем все блоки с записями
            List<WebElement> entries = driver.findElements(By.cssSelector(".search-registry-entry-block"));

            if (entries.isEmpty()) {
                System.out.println("No entries found for INN: " + inn);
                return detailLinks;
            }

            System.out.println("Found " + entries.size() + " records for INN: " + inn);

            // Собираем ссылки на детальную информацию
            for (WebElement entry : entries) {
                try {
                    WebElement linkElement = entry.findElement(By.cssSelector(".registry-entry__header-mid__number a"));
                    String detailUrl = linkElement.getAttribute("href");
                    detailLinks.add(detailUrl);
                    System.out.println("Found detail URL: " + detailUrl);
                } catch (NoSuchElementException e) {
                    System.out.println("Detail link not found in one of the entries for INN: " + inn);
                }
            }

        } catch (TimeoutException e) {
            System.out.println("Timeout while waiting for results for INN: " + inn);
        } catch (Exception e) {
            System.out.println("Error parsing page for INN: " + inn + ": " + e.getMessage());
        }

        return detailLinks;
    }


    public SupplierReliability parseAndPrintDetails(String url) {
        try {
            // Загрузка страницы с таймаутом
            driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
            driver.get(url);

            // Ожидание основного элемента с дополнительными проверками
            try {
                wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".cardMainInfo")));
            } catch (TimeoutException e) {
                System.err.println("Основной элемент .cardMainInfo не найден на странице: " + url);

            }

            // 1. Получаем информацию о законе
            String law = getSafeElementText(
                    By.cssSelector(".cardMainInfo__title span.cardMainInfo__title"),
                    "не удалось определить"
            );

            // 2. Получаем номер записи
            String recordNumber = getSafeElementText(
                    By.cssSelector(".cardMainInfo__purchaseLink a"),
                    "не указан"
            ).replace("№", "").trim();

            // 3. Получаем номер ЕРУЗ (с дополнительными проверками)
            String eruzNumber = "не указан";
            try {
                List<WebElement> eruzElements = driver.findElements(By.xpath(
                        "//span[contains(@class, 'section__title') and contains(., 'Номер реестровой записи в ЕРУЗ')]/following-sibling::span"
                ));
                if (!eruzElements.isEmpty()) {
                    eruzNumber = eruzElements.get(0).getText().trim();
                }
            } catch (Exception e) {
                System.err.println("Ошибка при поиске номера ЕРУЗ: " + e.getMessage());
            }

            // 4. Получаем даты
            String inclusionDate = getSafeDate("Включено");
            String updateDate = getSafeDate("Обновлено");
            String plannedExclusionDate = getSafeDate("Планируемая дата исключения");

            // Вывод результатов

            // 5. Парсим все блоки с дополнительной информацией
            Map<String, String> supplierInfo = parseSupplierInfoBlocks();



            // 6. Парсим информацию об иных лицах
            List<Map<String, String>> otherPersons = parseOtherPersonsInfo();

           return printResults(url, law, recordNumber, eruzNumber, inclusionDate, updateDate,
                    plannedExclusionDate,supplierInfo,otherPersons);

        } catch (Exception e) {
            System.err.println("Критическая ошибка при обработке страницы " + url + ": " + e.getClass().getSimpleName());
            if (!(e instanceof WebDriverException)) {
                e.printStackTrace();
            }
            return null;
        } finally {
            // Сброс таймаута к значению по умолчанию
            driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(300));
        }
    }

    private String getSafeElementText(By locator, String defaultValue) {
        try {
            return driver.findElement(locator).getText().trim();
        } catch (NoSuchElementException | StaleElementReferenceException e) {
            return defaultValue;
        }
    }

    private String getSafeDate(String title) {
        try {
            // Ищем по точному совпадению текста в заголовке
            List<WebElement> dateElements = driver.findElements(By.xpath(String.format(
                    "//span[contains(@class, 'cardMainInfo__title') and normalize-space()='%s']/following-sibling::span[contains(@class, 'cardMainInfo__content')]",
                    title
            )));

            if (!dateElements.isEmpty()) {
                return dateElements.get(0).getText().trim();
            }

            // Альтернативный поиск для случая, если структура немного отличается
            dateElements = driver.findElements(By.xpath(String.format(
                    "//span[contains(@class, 'cardMainInfo__title') and contains(., '%s')]/following::span[contains(@class, 'cardMainInfo__content')][1]",
                    title
            )));

            return dateElements.isEmpty() ? "не указана" : dateElements.get(0).getText().trim();
        } catch (Exception e) {
            return "не указана";
        }
    }
    private Map<String, String> parseSupplierInfoBlocks() {
        Map<String, String> infoMap = new LinkedHashMap<>();

        // Добавляем явное ожидание появления хотя бы одного блока
        try {
            wait.ignoring(NoSuchElementException.class)
                    .until(d -> d.findElements(By.cssSelector(".blockInfo__section.section")).size() > 0);
        } catch (TimeoutException e) {
            System.err.println("Блоки информации не найдены на странице");
            return infoMap;
        }

        // Используем безопасный поиск элементов
        List<WebElement> infoBlocks = Collections.emptyList();
        try {
            infoBlocks = driver.findElements(By.cssSelector(".blockInfo__section.section"));
        } catch (Exception e) {
            System.err.println("Ошибка при поиске блоков информации: " + e.getMessage());
            return infoMap;
        }

        for (WebElement block : infoBlocks) {
            try {
                // Безопасный поиск внутри блока
                List<WebElement> titles = block.findElements(By.cssSelector(".section__title"));
                List<WebElement> infos = block.findElements(By.cssSelector(".section__info"));

                if (!titles.isEmpty() && !infos.isEmpty()) {
                    String title = titles.get(0).getText().trim();
                    String value = infos.get(0).getText().trim();
                    infoMap.put(title, value);
                }
            } catch (StaleElementReferenceException e) {
                System.err.println("Элемент устарел, пропускаем блок");
                continue;
            } catch (Exception e) {
                System.err.println("Ошибка при обработке блока: " + e.getMessage());
                continue;
            }
        }

        return infoMap;
    }
    private List<Map<String, String>> parseOtherPersonsInfo() {
        List<Map<String, String>> personsList = new ArrayList<>();

        try {
            // Проверяем наличие заголовка блока
            List<WebElement> blockTitles = driver.findElements(
                    By.xpath("//h2[@class='blockInfo__title' and contains(text(), 'Информация об иных лицах')]")
            );

            if (blockTitles.isEmpty()) {
                return personsList; // Блок не найден - возвращаем пустой список
            }

            // Ожидаем появления таблицы
            wait.ignoring(NoSuchElementException.class)
                    .until(d -> d.findElements(By.cssSelector(".blockInfo__table.tableBlock")).size() > 0);

            // Получаем заголовки таблицы
            List<String> headers = new ArrayList<>();
            List<WebElement> headerElements = driver.findElements(
                    By.cssSelector(".blockInfo__table.tableBlock thead .tableBlock__col_header")
            );

            for (WebElement header : headerElements) {
                headers.add(header.getText().trim());
            }

            // Получаем первую строку таблицы
            List<WebElement> firstRowCells = driver.findElements(
                    By.cssSelector(".blockInfo__table.tableBlock tbody .tableBlock__row:first-child .tableBlock__col")
            );

            if (!firstRowCells.isEmpty() && firstRowCells.size() == headers.size()) {
                Map<String, String> personInfo = new LinkedHashMap<>();
                for (int i = 0; i < headers.size(); i++) {
                    personInfo.put(headers.get(i), firstRowCells.get(i).getText().trim());
                }
                personsList.add(personInfo);
            }

        } catch (Exception e) {
            System.err.println("Ошибка при парсинге информации об иных лицах: " + e.getMessage());
        }

        return personsList;
    }
    private SupplierReliability createSupplierReliability(
            String url,
            String law,
            String recordNumber,
            String eruzNumber,
            String inclusionDate,
            String updateDate,
            String plannedExclusionDate,
            Map<String, String> supplierInfo,
            List<Map<String, String>> otherPersons) {

        SupplierReliability reliability = new SupplierReliability();

        // Заполняем основные поля из printResults
        reliability.setLaw(law);
        reliability.setRecordNumber(recordNumber);
        reliability.setEruzNumber(eruzNumber.equals("не указан") ? null : eruzNumber);

        // Парсим даты
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        try {
            if (!inclusionDate.equals("не указана")) {
                reliability.setInclusionDate(LocalDate.parse(inclusionDate, formatter));
            }
            if (!updateDate.equals("не указана")) {
                reliability.setUpdateDate(LocalDate.parse(updateDate, formatter));
            }
            if (!plannedExclusionDate.equals("не указана")) {
                reliability.setPlannedExclusionDate(LocalDate.parse(plannedExclusionDate, formatter));
            }
        } catch (Exception e) {
            System.err.println("Ошибка при парсинге даты: " + e.getMessage());
        }

        // Заполняем данные из supplierInfo
        if (supplierInfo != null) {
            reliability.setName(supplierInfo.getOrDefault("Наименование/ФИО", null));
            reliability.setInn(supplierInfo.getOrDefault("ИНН (или аналог ИНН для иностранного лица)", null));
            reliability.setAuthority(supplierInfo.getOrDefault("Уполномоченный орган, осуществивший включение сведений в РНП 44-ФЗ", null));
            reliability.setReason(supplierInfo.getOrDefault("Причина для внесения в РНП 44-ФЗ", null));
            reliability.setRegistryNumber(supplierInfo.getOrDefault("Реестровый номер", null));
            reliability.setStatus(supplierInfo.getOrDefault("Статус записи", null));

            // Парсим дату исключения из supplierInfo
            String exclusionDateStr = supplierInfo.get("Дата исключения");
            if (exclusionDateStr != null && !exclusionDateStr.equals("не указана")) {
                try {
                    reliability.setExclusionDate(LocalDate.parse(exclusionDateStr, formatter));
                } catch (Exception e) {
                    System.err.println("Ошибка при парсинге даты исключения: " + e.getMessage());
                }
            }
        }

        // Заполняем данные об иных лицах (берем только первую запись)
        if (otherPersons != null && !otherPersons.isEmpty()) {
            Map<String, String> firstPerson = otherPersons.get(0);
            reliability.setEntityType(firstPerson.getOrDefault("ТИП ЛИЦА", null));
            // Если ИНН совпадает с основным ИНН, возможно, это одно и то же лицо
            String personInn = firstPerson.get("ИНН (ИЛИ АНАЛОГ ИНН ДЛЯ ИНОСТРАННОГО ЛИЦА)");
            if (personInn != null && !personInn.equals(reliability.getInn())) {
                reliability.setPersonInn(personInn);
            }
        }

        return reliability;
    }

    private SupplierReliability printResults(String url, String law, String recordNumber, String eruzNumber,
                              String inclusionDate, String updateDate,
                              String plannedExclusionDate,Map<String, String> supplierInfo,
                              List<Map<String, String>> otherPersons) {
        // Выводим все найденные данные о поставщике
        System.out.println("\nИнформация о поставщике:");
        for (Map.Entry<String, String> entry : supplierInfo.entrySet()) {
            System.out.println(" - " + entry.getKey() + ": " + entry.getValue());
        }
        // Выводим информацию об иных лицах
        if (!otherPersons.isEmpty()) {
            System.out.println("\nИнформация об иных лицах (первая запись):");
            otherPersons.get(0).forEach((k, v) -> System.out.println(" - " + k + ": " + v));
        }

        System.out.println("\n=== Результаты парсинга ===");
        System.out.println("URL: " + url);
        System.out.println("1. Закон: " + law);
        System.out.println("2. Номер записи: " + recordNumber);
        System.out.println("3. Номер ЕРУЗ: " + eruzNumber);
        System.out.println("4. Дата включения: " + inclusionDate);
        System.out.println("5. Дата обновления: " + updateDate);
        System.out.println("6. Планируемая дата исключения: " + plannedExclusionDate);
        System.out.println("===========================\n");
        return createSupplierReliability(url, law, recordNumber, eruzNumber,
                inclusionDate, updateDate, plannedExclusionDate,
                supplierInfo, otherPersons);
    }
}