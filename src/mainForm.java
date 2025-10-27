import javax.swing.*;
import java.awt.event.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import javax.swing.table.*;
import java.text.SimpleDateFormat;
import javax.swing.JTable;
import java.util.Comparator;
import java.awt.*;
import java.sql.PreparedStatement;

import Parser.interfaces.*;
import org.json.JSONArray;
import org.json.JSONObject;

import Parser.implementations.*;
import Parser.implementations.Parser44.PurchaseParser44;
import Parser.utils.Okpd2Converter;
import Parser.utils.RandomUserAgent;
import Parser.utils.StatusForm;
import com.toedter.calendar.JDateChooser;

import java.awt.*;
// для json
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;




public class mainForm extends JFrame {

    private JPanel MainPanel;
    private JButton buttonCreateProfile;
    private JTextField SearchParamentInsert;
    private JButton QueryButton;
    private JTable HeadersTable;
    private JLabel StatusLabel;
    private JLabel CurrentRecords;
    private JLabel TotalRecords;
    private JCheckBox PurchaseCancelled;
    private JCheckBox PurchaseCompleted;
    private JCheckBox SubmissionOfApplications;
    private JCheckBox CommissionWork;
    private JButton PauseParser;
    private JCheckBox fz44;
    private JCheckBox fz223;
    private JLabel PrieceLabel;
    private JTextField MaxPriceTextField;
    private JLabel MaxLebelPriece;
    private JTextField MinPriceTextField;
    private JLabel MinPrieceLabel;
    private JLabel CurrencyLable;
    private JComboBox comboBoxCurrency;
    private JLabel DateLabel;
    private JLabel PlacementLabel;
    private JLabel PlacementEndLabel;
    private JButton StartParsing;
    private JButton ChooseAllElements;
    private JProgressBar ParserProgressBar;
    private JTextField OKPD2Field;
    private JPanel PanelFieldDataStart;
    private JPanel PanelFieldDataEnd;
    private JButton StopParser;
    private JButton PauseParsingButton;
    private JButton StopParseringButton;

    private JButton EGRUL_PDF_Parser_Start;
    private JButton EGRUL_PDF_Parser_Stop;
    private AtomicBoolean EGRUL_Parser_isStopped = new AtomicBoolean(false);

    private JButton EGRUL_PDF_To_Data;
    private JProgressBar EGRUL_Progress_Bar;
    private JLabel EGRUL_PDF_Bar_status;
    private JLabel EGRUL_Parser_left;
    private JLabel EGRUL_PDF_left;

    private StatusForm statusForm;
    private JTextField textFieldFilterOkpd2;
    private JTextField textFieldFilterGroup;
    private JTextField From;
    private JTextField To;
    private JButton ChooseRange;
    private JComboBox ThreadCount;
    private volatile PurchaseListParser listParser;
    private volatile PurchaseDetailsParser detailsParser;

    private JComboBox comboBoxRoleCriterier;
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy");
    private String CritString;
    private String CritShort;
    private long CritId;
    private String CritName;
    private final DriverSetup driverSetup;
    private final JDateChooser dateChooseFilterStart = new JDateChooser();
    private final JDateChooser dateChooserFilterEnd = new JDateChooser();


    private enum ParserState {
        IDLE, RUNNING, PAUSED, STOPPED
    }

    private ParserState parserState = ParserState.IDLE;

    private Thread parserThread;
    private Thread EGRUL_Parser_Thread;


    private JComboBox<String> comboBoxProfileCriterion;
    private JPanel ParserPanel;

    private Thread EGRUL_Thread;
    private Thread PDF_to_Data_Thread;



    private void enableSortingForTable(JTable table, int... numericColumns) {
        if (table == null || table.getModel() == null) {
            System.out.println("Ошибка: таблица не инициализирована.");
            return;
        }

        TableRowSorter<TableModel> sorter = new TableRowSorter<>(table.getModel());

        // Добавляем числовые компараторы для указанных колонок
        for (final int col : numericColumns) {
            sorter.setComparator(col, new Comparator<Object>() {
                @Override
                public int compare(Object o1, Object o2) {
                    double val1 = getNumericValue(o1);
                    double val2 = getNumericValue(o2);
                    return Double.compare(val1, val2);
                }
            });
        }

        table.setRowSorter(sorter);
    }
    private void clearTable(JTable table) {
        if (table != null) {
            DefaultTableModel model = (DefaultTableModel) table.getModel();

            // Выключаем сортировку перед очисткой
            table.setRowSorter(null);

            model.setRowCount(0);  // Очищаем таблицу

            System.out.println("Таблица очищена.");
        } else {
            System.out.println("Ошибка: таблица не инициализирована.");
        }
    }

    // Метод для получения числового значения из объекта
    private double getNumericValue(Object obj) {
        if (obj instanceof Number) {
            return ((Number) obj).doubleValue();
        }
        try {
            return Double.parseDouble(obj.toString().replace(",", ".")); // замена для "12,34"
        } catch (NumberFormatException e) {
            return Double.NaN; // Возвращаем NaN, если не удается преобразовать в число
        }
    }


    public mainForm() {



        setContentPane(MainPanel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();

        String userAgent = RandomUserAgent.getRandomUserAgent();
        this.driverSetup = new ChromeDriverSetup(userAgent);


        // read from json start
        try {
            InputStream isJSON = this.getClass().getResourceAsStream("/config.json");
            String textJSON = IOUtils.toString(isJSON, "UTF-8");
            JSONObject configJSON = new JSONObject(textJSON);

            //    System.out.println(configJSON);
        } catch (IOException | JSONException | NullPointerException e) {
            System.out.println("JSON failed");
        }


        // начальные и конечные даты для интервала заказа
        JDateChooser dateChooserOptIntStart = new JDateChooser();
        JDateChooser dateChooserOptIntEnd = new JDateChooser();

        dateChooserOptIntStart.setDateFormatString("dd.MM.yyyy");
        dateChooserOptIntEnd.setDateFormatString("dd.MM.yyyy");


        MinPriceTextField.setToolTipText("Минимальная цена");
        MaxPriceTextField.setToolTipText("Максимальная цена");
        dateChooseFilterStart.setDateFormatString("dd.MM.yyyy");
        dateChooserFilterEnd.setDateFormatString("dd.MM.yyyy");
        PanelFieldDataStart.setLayout(new BorderLayout());
        PanelFieldDataStart.add(dateChooseFilterStart, BorderLayout.CENTER);
        PanelFieldDataEnd.setLayout(new BorderLayout());
        PanelFieldDataEnd.add(dateChooserFilterEnd, BorderLayout.CENTER);

        Date s_date_int = dateChooseFilterStart.getDate();
        Date e_date_int = dateChooserFilterEnd.getDate();

        //Раздел парсинга

        ThreadCount.addItem(2);
        ThreadCount.addItem(4);
        ThreadCount.addItem(6);
        ThreadCount.addItem(8);

        statusForm = new StatusForm();
        statusForm.setStatusLabel(StatusLabel);
        statusForm.setCurrentRecords(CurrentRecords);
        statusForm.setTotalRecords(TotalRecords);
//        statusForm.setHeadersTable(HeadersTable);
        QueryButton.addActionListener(e -> onQueryButtonClicked());
        StartParsing.addActionListener(e -> initStartParsingButton());
        ChooseAllElements.addActionListener(e -> initChooseAllElementsButton());
        ChooseRange.addActionListener(e -> {
            try {
                int from = From.getText().isEmpty() ? 1 : Integer.parseInt(From.getText());
                int to = To.getText().isEmpty() ? statusForm.allItems.size() : Integer.parseInt(To.getText());

                initChooseRangeElementsButton(from, to);
            } catch (NumberFormatException ex) {
                System.out.println("Ошибка: введите корректные числовые значения");
            }
        });
        StopParser.addActionListener(e -> stopParser());
        PauseParser.addActionListener(e -> togglePauseParser());
        StopParseringButton.addActionListener(e -> {
            stopParsing();
        });
        EGRUL_PDF_Parser_Start.addActionListener(e -> EGRUL_Parser_Start());
        EGRUL_PDF_Parser_Stop.addActionListener(e -> {
            try {
                EGRUL_Parser_Stop();
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
        });
        EGRUL_PDF_To_Data.addActionListener(e -> EGRUL_PDF_Processing());

        Okpd2Converter.fillComboBoxWithCurrencies(comboBoxCurrency, "resources/currency.json");

        // Кнопка паузы/продолжения
        PauseParsingButton.addActionListener(e -> {
            switch (parserState) {
                case RUNNING:
                    pauseParsering();
                    break;
                case PAUSED:
                    resumeParsing();
                    break;
                default:
                    // Ничего не делаем для других состояний
                    break;
            }
        });
    }

    private String formatValue(double value) {
        // Форматируем значение: если целое, то без десятичных знаков, иначе с тремя знаками после запятой
        String formattedValue = (value == Math.floor(value))
                ? String.format(Locale.US, "%.0f", value)  // Если целое, выводим без десятичных знаков
                : String.format(Locale.US, "%.3f", value); // Если дробное, выводим с тремя знаками после запятой

        // Принудительно заменяем запятую на точку
        return formattedValue.replace(",", ".");
    }

    private void EGRUL_Parser_Start(){
        if (EGRUL_Thread != null && EGRUL_Thread.isAlive()) {
            return;
        }

        EGRUL_Parser_isStopped.set(false);

        EGRUL_Thread = new Thread(() -> {

            EGRUL_PDF_Parser_Start.setText("Парсинг...");
            EGRUL_PDF_Parser_Start.setEnabled(false);
            EGRUL_PDF_To_Data.setEnabled(false);
            EGRUL_PDF_Parser_Stop.setEnabled(true);
            EGRUL_PDF_Bar_status.setText("Парсинг PDF");
            EGRUL_Progress_Bar.setMinimum(0);

            try {
                Parser_EGRUL.Parser.StartParsingEGRUL(EGRUL_Parser_isStopped, new Parser_EGRUL.Parser.ProgressUpdater() {
                    @Override
                    public void incrementProgress() {
                        SwingUtilities.invokeLater(() -> {
                            EGRUL_Progress_Bar.setValue(EGRUL_Progress_Bar.getValue() + 1);
                        });
                    }

                    @Override
                    public void updateStatus(String text) {
                        SwingUtilities.invokeLater(() -> {
                            EGRUL_Parser_left.setText(text);
                        });
                    }

                    @Override
                    public void defineBarMaximum(int number) {
                        SwingUtilities.invokeLater(() -> {
                            EGRUL_Progress_Bar.setMaximum(number);
                        });
                    }
                });
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            // Восстанавливаем UI
            SwingUtilities.invokeLater(() -> {
                EGRUL_PDF_Bar_status.setText("Парсинг остановлен");
                EGRUL_PDF_Parser_Start.setText("Сбор PDF");
                EGRUL_PDF_Parser_Start.setEnabled(true);
                EGRUL_PDF_To_Data.setEnabled(true);
                EGRUL_PDF_Parser_Stop.setEnabled(false);
            });

        });
        EGRUL_Thread.start();
    }

    private void EGRUL_Parser_Stop() throws InterruptedException {
        EGRUL_Parser_isStopped.set(true);  // Устанавливаем флаг остановки

        if (EGRUL_Thread != null && EGRUL_Thread.isAlive()) {
            EGRUL_Thread.interrupt();
        }

        try {
            Runtime.getRuntime().exec("taskkill /F /IM chromedriver.exe /T");
            Runtime.getRuntime().exec("taskkill /F /IM chrome.exe /T");
        } catch (IOException e) {
            System.err.println("Ошибка при закрытии Chrome: " + e.getMessage());
        }

        System.out.println("Обработка остановлена");
    }

    private void EGRUL_PDF_Processing(){
        if (PDF_to_Data_Thread != null && PDF_to_Data_Thread.isAlive()) {
            return;
        }

        PDF_to_Data_Thread = new Thread(() -> {

            EGRUL_PDF_To_Data.setText("Обрабатываем...");
            EGRUL_PDF_Parser_Start.setEnabled(false);
            EGRUL_PDF_To_Data.setEnabled(false);
            EGRUL_PDF_Bar_status.setText("Обработка PDF");
            EGRUL_Progress_Bar.setMinimum(0);
            EGRUL_Progress_Bar.setValue(0);

            Parser_EGRUL.Data_Extractor.readAllInfoFromFiles(new Parser_EGRUL.Data_Extractor.ProgressUpdater() {
                @Override
                public void incrementProgress() {
                    SwingUtilities.invokeLater(() -> {
                        EGRUL_Progress_Bar.setValue(EGRUL_Progress_Bar.getValue() + 1);
                    });
                }

                @Override
                public void updateStatus(String text) {
                    SwingUtilities.invokeLater(() -> {
                        EGRUL_PDF_left.setText(text);
                    });
                }

                @Override
                public void defineBarMaximum(int number) {
                    SwingUtilities.invokeLater(() -> {
                        EGRUL_Progress_Bar.setMaximum(number);
                    });
                }
            });

            SwingUtilities.invokeLater(() -> {
                EGRUL_PDF_Bar_status.setText("Обработка завершена");
                EGRUL_PDF_Parser_Start.setEnabled(true);
                EGRUL_PDF_To_Data.setEnabled(true);
                EGRUL_PDF_To_Data.setText("Обработка PDF");
            });

        });
        PDF_to_Data_Thread.start();
    }

    private void initStartParsingButton() {
        if (statusForm.selectedUrls.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Не выбрано ни одной закупки для парсинга",
                    "Ошибка",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        detailsParser = new PurchaseParser44(driverSetup);

        // Если парсер на паузе - возобновляем
        if (parserState == ParserState.PAUSED) {
            resumeParsing();
            return;
        }

        // Если парсер уже работает, ничего не делаем
        if (parserState == ParserState.RUNNING) {
            return;
        }

        // Настройка UI
        ParserProgressBar.setMinimum(0);
        ParserProgressBar.setMaximum(statusForm.selectedUrls.size()); // +1 для этапа парсинга судебных дел
        ParserProgressBar.setValue(0);
        ParserProgressBar.setStringPainted(true);


        parserState = ParserState.RUNNING;

        StartParsing.setText("Парсинг...");
        StartParsing.setEnabled(false);
        PauseParsingButton.setText("Пауза");
        PauseParsingButton.setEnabled(true);
        StopParseringButton.setEnabled(true);
        StatusLabel.setText("Парсинг запущен");
        int selectedThreadCount = (Integer)ThreadCount.getSelectedItem();
        parserThread = new Thread(() -> {
            try {
                // Этап 1: Парсинг закупок
                detailsParser.parseUrlsParallel(
                        new ArrayList<>(statusForm.selectedUrls),
                        this::handleParseResult,
                        selectedThreadCount,
                        progress -> SwingUtilities.invokeLater(() -> {
                            ParserProgressBar.setValue(progress);
                            StatusLabel.setText(String.format("Обработано %d из %d (парсинг закупок)",
                                    progress, statusForm.selectedUrls.size()));
                        })
                );

                // Проверяем, не была ли остановка
                if (parserState == ParserState.STOPPED) {
                    return;
                }

                // Этап 2: Парсинг судебных дел
                SwingUtilities.invokeLater(() -> {
                    StatusLabel.setText("Парсинг судебных дел поставщиков...");
                    ParserProgressBar.setValue(statusForm.selectedUrls.size() + 1);
                });
                deselectAllCheckboxes();
//                detailsParser.parseSupplierLitigations();
                detailsParser.parseSupplierLitigationsParallel(selectedThreadCount);
//                detailsParser.parseSupplierStatuses();
                detailsParser.parseSupplierStatusesParallel(selectedThreadCount);
//                detailsParser.cleanupDownloadDirectory();

            }
            finally {
                SwingUtilities.invokeLater(() -> {
                    if (parserState != ParserState.STOPPED) {
                        StartParsing.setText("Начать парсинг");
                        StartParsing.setEnabled(true);
                        PauseParsingButton.setEnabled(false);
                        StopParseringButton.setEnabled(false);
                        parserState = ParserState.IDLE;
                        StatusLabel.setText("Парсинг завершен");

                        statusForm.selectedUrls.clear();
                    }
                });
            }
        });

        parserThread.start();
    }

    // Метод для обработки результатов парсинга
    private void handleParseResult(PurchaseParser44.ParseResult result) {
        SwingUtilities.invokeLater(() -> {
            if (result.error != null) {
                // Обработка ошибки
                System.err.println("Ошибка при парсинге URL: " + result.url);
                result.error.printStackTrace();


                JOptionPane.showMessageDialog(this,
                        "Парсинг завершен досрочно.",
                        "Внимание!",
                        JOptionPane.ERROR_MESSAGE);
            } else if (result.purchaseData != null) {
                // Обработка успешного результата
                System.out.println("Успешно распарсено: " + result.purchaseData);

                // Здесь можно обновить UI или сохранить данные в базу
                // Например, добавить в таблицу результатов
            }
        });
    }

    private Map<String, String> buildFinalParams() {
        String searchText = SearchParamentInsert.getText().trim();
        String searchQuery = searchText.isEmpty() ? "" : processSearchQuery(searchText);

        // Всегда добавляем базовые параметры
        Map<String, String> params = new LinkedHashMap<>();
        params.put("morphology", "on");
        params.put("pageNumber", "1");
        params.put("sortDirection", "false");
        params.put("showLotsInfoHidden", "false");
        params.put("sortBy", "UPDATE_DATE");
        params.put("recordsPerPage", "_10"); // Важно для пагинации

        // Параметры поиска
        params.put("searchString", searchQuery);

        // Фильтры статусов
        if (PurchaseCancelled.isSelected()) params.put("pa", "on");
        if (PurchaseCompleted.isSelected()) params.put("pc", "on");
        if (SubmissionOfApplications.isSelected()) params.put("af", "on");
        if (CommissionWork.isSelected()) params.put("ca", "on");

        // Фильтры ФЗ
        if (fz44.isSelected()) params.put("fz44", "on");
        if (fz223.isSelected()) params.put("fz223", "on");

        // Обработка ОКПД2
        String okpd2Code = OKPD2Field.getText().trim();
        if (!okpd2Code.isEmpty()) {
            try {
                String okpd2Id = Okpd2Converter.getOkpd2Id(okpd2Code);
                if (okpd2Id != null) {
                    params.put("okpd2Ids", okpd2Id);
                    params.put("okpd2IdsCodes", okpd2Code);
                    params.put("okpd2IdsWithNested", "on");
                }
            } catch (Exception e) {
                System.err.println("Ошибка обработки ОКПД2: " + e.getMessage());
            }
        }

        // Обработка дат
        SimpleDateFormat urlDateFormat = new SimpleDateFormat("dd.MM.yyyy");
        // Обработка даты публикации (основной фильтр даты)
        if (dateChooseFilterStart.getDate() != null) {
            params.put("publishDateFrom", urlDateFormat.format(dateChooseFilterStart.getDate()));
        }
        if (dateChooserFilterEnd.getDate() != null) {
            params.put("publishDateTo", urlDateFormat.format(dateChooserFilterEnd.getDate()));
        }

        // Фильтр по цене
        try {
            if (!MinPriceTextField.getText().trim().isEmpty()) {
                double minPrice = Double.parseDouble(MinPriceTextField.getText().trim());
                params.put("priceFromGeneral", String.format("%.0f", minPrice));
            }
            if (!MaxPriceTextField.getText().trim().isEmpty()) {
                double maxPrice = Double.parseDouble(MaxPriceTextField.getText().trim());
                params.put("priceToGeneral", String.format("%.0f", maxPrice));
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null,
                    "Некорректный формат цены. Используйте только цифры.",
                    "Ошибка", JOptionPane.ERROR_MESSAGE);
        }

        // Валюта
        if (comboBoxCurrency.getSelectedItem() != null) {
            String currencyName = comboBoxCurrency.getSelectedItem().toString();
            String currencyId = Okpd2Converter.getCurrencyIdByName(currencyName);
            if (currencyId != null) {
                params.put("currencyIdGeneral", currencyId);
            } else {
                params.put("currencyIdGeneral", "-1");
            }
        } else {
            params.put("currencyIdGeneral", "-1");
        }
        System.out.println("Final URL params: " + params);
        return params;
    }

    private void onQueryButtonClicked() {
        // Сбрасываем UI перед запуском нового парсера
        resetParserUI();
        statusForm.clearAllItems(); //

        Map<String, String> params = buildFinalParams();

        System.out.println("Формируемые параметры:");
        params.forEach((k, v) -> System.out.println(k + " = " + v));

        // Формируем тестовый URL для проверки в браузере
        String testUrl = "https://zakupki.gov.ru/epz/order/extendedsearch/results.html?" +
                params.entrySet().stream()
                        .map(e -> e.getKey() + "=" + e.getValue())
                        .collect(Collectors.joining("&"));
        System.out.println("ПОЛНЫЙ URL ДЛЯ ПРОВЕРКИ:\n" + testUrl);
        // Очищаем предыдущие данные
        TotalRecords.setText("Всего записей: 0");
        CurrentRecords.setText("Обработано: 0");
        StatusLabel.setText("Статус: запуск парсера...");
        ResultsSaver<PurchaseItem> saver = new TextFileResultsSaver();
        listParser = new PurchasesParserHead(driverSetup, saver, params, statusForm);

        new Thread(() -> {
            listParser.parse();
            SwingUtilities.invokeLater(() -> {
                // После завершения парсинга сбрасываем кнопки
                PauseParser.setEnabled(true);  // "Пауза" активна
                StopParser.setEnabled(true);
//                StatusLabel.setText("Статус: запуск парсера...");
            });
        }).start();
    }

    private void resetParserUI() {
        SwingUtilities.invokeLater(() -> {
            // Сбрасываем текст кнопок
            PauseParser.setText("Пауза");
            PauseParser.setEnabled(true);
            StopParser.setEnabled(true); // Аналогично

            // Сбрасываем статус
//            StatusLabel.setText("Статус: готов к работе");

            // Если у вас есть другие элементы (например, ProgressBar), их тоже можно сбросить
             ParserProgressBar.setValue(0);
        });
    }

    // Метод для обработки поискового запроса
    private String processSearchQuery(String rawQuery) {
        try {
            // Кодируем всю строку, включая пробелы (%20) и специальные символы
            String encoded = URLEncoder.encode(rawQuery, StandardCharsets.UTF_8.toString());

            // Дополнительные замены для соответствия требованиям сайта
            encoded = encoded.replace("+", "%20");  // Заменяем + на %20
            encoded = encoded.replace("%27", "%22"); // Заменяем одинарные кавычки на двойные
            return encoded;
        } catch (Exception e) {
            System.err.println("Ошибка кодирования запроса: " + e.getMessage());
            return rawQuery.replace(" ", "%20"); // Фолбэк замена пробелов
        }
    }

    public void initChooseAllElementsButton() {
        List<String> selectedUrls = statusForm.selectedUrls;
        List<PurchaseItem> allItems = statusForm.allItems;

        boolean hasSelectedItems = !selectedUrls.isEmpty();

        if (hasSelectedItems) {
            selectedUrls.clear();
            System.out.println("Все элементы сняты. Список пуст.");
        } else {
            for (PurchaseItem item : allItems) {
                if (!selectedUrls.contains(item.getUrl())) {
                    selectedUrls.add(item.getUrl());
                }
            }
            System.out.println("Выбраны все элементы. Уникальных элементов: " + selectedUrls.size());
            System.out.println("Список: " + selectedUrls);
        }
    }

    public void deselectAllCheckboxes() {
        statusForm.selectedUrls.clear();
        System.out.println("Все элементы сняты. selectedUrls очищен.");
    }

    public void initChooseRangeElementsButton(int from, int to) {
        List<String> selectedUrls = statusForm.selectedUrls;
        List<PurchaseItem> allItems = statusForm.allItems;
        int rowCount = allItems.size();

        from = Math.max(1, Math.min(from, rowCount));
        to = Math.max(1, Math.min(to, rowCount));
        if (from > to) {
            int temp = from;
            from = to;
            to = temp;
        }

        boolean hasSelectedInRange = false;
        for (int i = from - 1; i < to; i++) {
            if (selectedUrls.contains(allItems.get(i).getUrl())) {
                hasSelectedInRange = true;
                break;
            }
        }

        if (hasSelectedInRange) {
            Set<String> urlsInRange = new HashSet<>();
            for (int i = from - 1; i < to; i++) {
                urlsInRange.add(allItems.get(i).getUrl());
            }

            for (int i = 0; i < rowCount; i++) {
                if (i >= from - 1 && i < to) continue;
                urlsInRange.remove(allItems.get(i).getUrl());
            }

            for (int i = from - 1; i < to; i++) {
                String url = allItems.get(i).getUrl();
                if (urlsInRange.contains(url)) {
                    selectedUrls.remove(url);
                }
            }

            System.out.println("Элементы с " + from + " по " + to + " сняты.");
            System.out.println("Уникальных элементов осталось: " + selectedUrls.size());
        } else {
            int added = 0;
            for (int i = from - 1; i < to; i++) {
                String url = allItems.get(i).getUrl();
                if (!selectedUrls.contains(url)) {
                    selectedUrls.add(url);
                    added++;
                }
            }

            System.out.println("Выбраны элементы с " + from + " по " + to +
                    ". Добавлено уникальных элементов: " + added);
            System.out.println("Всего уникальных элементов: " + selectedUrls.size());
            System.out.println("Список: " + selectedUrls);
        }
    }

    private void togglePauseParser() {
        if (listParser != null) {
            PurchasesParserHead parser = (PurchasesParserHead) listParser;

            if (parser.isPaused) {
                // Если парсер на паузе — возобновляем
                parser.resumeParser();
                PauseParser.setText("Пауза");
                StatusLabel.setText("Статус: парсинг продолжен");
            } else {
                // Если парсер работает — ставим на паузу
                parser.pauseParser();
                PauseParser.setText("Продолжить");
                StatusLabel.setText("Статус: парсинг на паузе");
            }
        }
    }

    private void stopParser() {
        if (listParser != null) {
            PurchasesParserHead parser = (PurchasesParserHead) listParser;

            parser.stopParser();
            StopParser.setEnabled(false);
            PauseParser.setEnabled(false);
            StatusLabel.setText("Статус: парсинг остановлен");

        }
    }

    private void pauseParsering() {
        if (detailsParser != null && parserState == ParserState.RUNNING) {
            detailsParser.pauseParser();
            parserState = ParserState.PAUSED;
            PauseParsingButton.setText("Продолжить");
            StatusLabel.setText("Парсинг на паузе" +
                    (ParserProgressBar.getValue() >= statusForm.selectedUrls.size() ?
                            " (парсинг судебных дел)" : ""));
            StopParseringButton.setEnabled(true);

        }
    }

    private void resumeParsing() {
        if (detailsParser != null && parserState == ParserState.PAUSED) {
            detailsParser.resumeParser();
            parserState = ParserState.RUNNING;
            PauseParsingButton.setText("Пауза");
            StatusLabel.setText("Парсинг возобновлен" +
                    (ParserProgressBar.getValue() >= statusForm.selectedUrls.size() ?
                            " (парсинг судебных дел)" : ""));
        }
    }

    private void stopParsing() {
        if (detailsParser != null && (parserState == ParserState.RUNNING || parserState == ParserState.PAUSED)) {
            detailsParser.stopParser();
            parserState = ParserState.STOPPED;
            statusForm.selectedUrls.clear();
            if (parserThread != null) {
                parserThread.interrupt();
            }

            SwingUtilities.invokeLater(() -> {
                StartParsing.setText("Начать парсинг");
                StartParsing.setEnabled(true);
                PauseParsingButton.setText("Пауза");
                PauseParsingButton.setEnabled(false);
                StopParseringButton.setEnabled(false);
                StatusLabel.setText("Парсинг остановлен");
                ParserProgressBar.setValue(0);
                deselectAllCheckboxes();
            });

        }
    }


    public static void main(String[] args) {

        // Запускаем интерфейс
        SwingUtilities.invokeLater(() -> {
            mainForm form = new mainForm(); // ← по умолчанию роль USER
            form.setContentPane(form.MainPanel);
            form.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            form.pack();
            form.setVisible(true);

        });
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }

}

