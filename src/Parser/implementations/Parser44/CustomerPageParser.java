package Parser.implementations.Parser44;

import Parser.Database.models.Customer;
import org.openqa.selenium.*;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class CustomerPageParser {

    public Customer parseCustomerInfo(WebDriver driver) {
        String customerUrl = extractCustomerUrl(driver);
        if (customerUrl == null) return null;

        try {
            // Сохраняем текущий URL и куки
            String currentUrl = driver.getCurrentUrl();
            Set<Cookie> cookies = driver.manage().getCookies();

            driver.get(customerUrl);
            Customer customer = parseCustomerPage(customerUrl, driver);


            // Возвращаемся обратно
            driver.get(currentUrl);
            cookies.forEach(cookie -> driver.manage().addCookie(cookie));

            return customer;
        } catch (Exception e) {
            System.out.println("Ошибка при парсинге заказчика: " + e.getMessage());
            return null;
        }
    }
    private String extractCustomerUrl(WebDriver driver) {
        try {

            List<WebElement> sections = driver.findElements(By.cssSelector("section.blockInfo__section.section"));


            for (WebElement section : sections) {
                try {
                    WebElement title = section.findElement(By.cssSelector("span.section__title"));
                    if ("Размещение осуществляет".equals(title.getText().trim())) {
                        // В найденной секции ищем ссылку
                        WebElement link = section.findElement(By.cssSelector("span.section__info a"));
//                        System.out.println("Нашел ссылку: " + link);
                        return link.getAttribute("href");
                    }
                } catch (Exception e) {


                    continue;
                }
            }
        } catch (Exception e) {
            System.out.println("Не удалось найти ссылку на заказчика: " + e.getMessage());
        }
        return null;
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


    public Customer parseCustomerPage(String customerUrl, WebDriver driver) {
        if (customerUrl == null) {
            System.out.println("URL заказчика не предоставлен");
            return null;
        }

        try {
            driver.get(customerUrl);

            // Сначала собираем все данные в словарь
            Map<String, String> allData = collectAllSectionData(driver);

            String otherTabUrl = customerUrl + "&tab=other";
            driver.get(otherTabUrl);
//            System.out.println("Новая ссылка :" + otherTabUrl);
            Map<String, String> otherData = collectAllSectionData(driver);

            allData.putAll(otherData);
//            System.out.println("Объединенные данные с обеих вкладок:");
//            allData.forEach((key, value) -> System.out.println(key + ": " + value));

            // Затем создаем объект Customer и заполняем его
            Customer customer = new Customer();
            customer.setLastUpdated(LocalDateTime.now());

            // Заполняем поля из словаря
//            customer.setFullName(allData.getOrDefault("Полное наименование", null));
            customer.setShortName(allData.getOrDefault("Сокращенное наименование", null));
            customer.setConsolidatedRegisterCode(allData.getOrDefault("Код по Сводному реестру", null));

            // Обработка дат
            try {
                String regDateStr = allData.get("Дата регистрации");
                if (regDateStr != null) {
                    regDateStr = regDateStr.replaceAll("[^\\d.]", ""); // Очистка от лишних символов
                    customer.setRegistrationDate(LocalDate.parse(regDateStr, DateTimeFormatter.ofPattern("dd.MM.yyyy")));
                }
            } catch (Exception e) {
                System.out.println("Ошибка парсинга даты регистрации: " + e.getMessage());
            }
            WebElement searchResultElement = driver.findElement(By.cssSelector(".search-registry-entry-block"));
            try {
                // Парсинг полного наименования
                WebElement nameElement = null;
                try {
                    nameElement = searchResultElement.findElement(By.cssSelector(".registry-entry__header-mid__number a"));
                    customer.setFullName(nameElement.getText().trim());
                } catch (NoSuchElementException e) {
                    customer.setFullName(null);
                    System.out.println("Элемент полного наименования не найден");
                }

                // Парсинг местонахождения
                try {
                    WebElement locationElement = searchResultElement.findElement(
                            By.xpath(".//div[contains(@class, 'registry-entry__body-title') and contains(text(), 'Местонахождение')]/following-sibling::div"));
                    customer.setLocation(locationElement.getText().trim());
                } catch (NoSuchElementException e) {
                    customer.setLocation(null);
                    System.out.println("Элемент местонахождения не найден");
                }

                // Парсинг ОГРН, ИНН, КПП
                List<WebElement> infoBlocks = Collections.emptyList();
                try {
                    infoBlocks = searchResultElement.findElements(By.cssSelector(".registry-entry__body-block .row .col-md-auto"));
                } catch (NoSuchElementException e) {
                    System.out.println("Информационные блоки не найдены");
                }

                for (WebElement block : infoBlocks) {
                    try {
                        String title = block.findElement(By.cssSelector(".registry-entry__body-title")).getText().trim();
                        String value = block.findElement(By.cssSelector(".registry-entry__body-value")).getText().trim();

                        switch (title) {
                            case "ОГРН":
                                customer.setOgrn(value);
                                break;
                            case "ИНН":
                                customer.setInn(value);
                                break;
                            case "КПП":
                                customer.setKpp(value);
                                break;
                        }
                    } catch (NoSuchElementException e) {
                        System.out.println("Не удалось извлечь данные из информационного блока");
                    }
                }

                customer.setLastUpdated(LocalDateTime.now());

            } catch (Exception e) {
                System.out.println("Ошибка при парсинге элемента поиска: " + e.getMessage());
                // Продолжаем выполнение, не возвращаем null
            }

//            // Остальные поля...
//            customer.setInn(allData.getOrDefault("ИНН", null));
//            customer.setKpp(allData.getOrDefault("КПП", null));
//            customer.setOgrn(allData.getOrDefault("ОГРН", null));
            customer.setOktmo(allData.getOrDefault("ОКТМО", null));
//            customer.setLocation(allData.getOrDefault("Место нахождения", null));


            // ИКУ и дата назначения ИКУ
            customer.setIku(allData.getOrDefault("ИКУ", null));
            try {
                String ikuDateStr = allData.get("Дата присвоения ИКУ");
                if (ikuDateStr != null) {
                    ikuDateStr = ikuDateStr.replaceAll("[^\\d.]", "");
                    customer.setIkuAssignmentDate(LocalDate.parse(ikuDateStr, DateTimeFormatter.ofPattern("dd.MM.yyyy")));
                }
            } catch (Exception e) {
                System.out.println("Ошибка парсинга даты присвоения ИКУ: " + e.getMessage());
            }

            // Коды и формы собственности

            customer.setOkfsCode(allData.getOrDefault("Код по ОКФС", null));
            customer.setOwnershipFormName(allData.getOrDefault("Форма собственности", null));
            customer.setOkopfCode(allData.getOrDefault("Код по ОКОПФ", null));
            customer.setLegalFormName(allData.getOrDefault("Организационно-правовая форма", null));

            // Обработка даты регистрации (если еще не добавлено)
            try {
                String regDateStr = allData.get("Дата регистрации");
                if (regDateStr != null) {
                    regDateStr = regDateStr.replaceAll("[^\\d.]", "");
                    customer.setRegistrationDate(LocalDate.parse(regDateStr, DateTimeFormatter.ofPattern("dd.MM.yyyy")));
                }
            } catch (Exception e) {
                System.out.println("Ошибка парсинга даты регистрации: " + e.getMessage());
            }
            // Новые поля из предоставленного HTML
            customer.setOrganizationAuthorities(allData.getOrDefault("Полномочия организации", null));
            customer.setUniqueRegistrationNumber(allData.getOrDefault("Уникальный учетный номер организации", null));

            customer.setOrganizationType(allData.getOrDefault("Тип организации", null));
            customer.setOrganizationLevel(allData.getOrDefault("Уровень организации", null));

            // Обработка ОКВЭД (может быть несколько значений)
            String okveds = allData.entrySet().stream()
                    .filter(e -> e.getKey().contains("ОКВЭД"))
                    .map(Map.Entry::getValue)
                    .collect(Collectors.joining("; "));
            customer.setOkved(okveds.isEmpty() ? null : okveds);

            // Уполномоченная организация
            customer.setConsolidatedRegisterCodeAlt(allData.getOrDefault("Код по Сводному реестру", null));
            customer.setAuthorizedOrganizationName(allData.getOrDefault("Наименование", null));

            // Контактная информация
            customer.setPhone(allData.getOrDefault("Телефон", null));
            customer.setFax(allData.getOrDefault("Факс", null));
            customer.setPostalAddress(allData.getOrDefault("Почтовый адрес", null));
            customer.setEmail(allData.getOrDefault("Контактный адрес электронной почты", null));
            customer.setWebsite(allData.getOrDefault("Адрес организации в сети Интернет", null));
            customer.setContactPerson(allData.getOrDefault("Контактное лицо", null));
            customer.setTimeZone(allData.getOrDefault("Часовая зона", null));

            // Обработка дат
            try {
                String taxRegDate = allData.get("Дата постановки организации на учет в налоговом органе");
                if (taxRegDate != null) {
                    customer.setTaxRegistrationDate(LocalDate.parse(
                            taxRegDate.replaceAll("[^\\d.]", ""),
                            DateTimeFormatter.ofPattern("dd.MM.yyyy")
                    ));
                }
            } catch (Exception e) {
                System.out.println("Ошибка парсинга даты налоговой регистрации: " + e.getMessage());
            }


            return customer;

        } catch (Exception e) {
            System.out.println("Ошибка при парсинге страницы заказчика: " + e.getMessage());
            return null;
        }
    }
}
