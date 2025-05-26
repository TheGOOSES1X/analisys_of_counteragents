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

import MainAnalyzer.*;
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
    private JButton button_getContras;
    private JLabel label_numContras;
    private JTextField textFieldFilterContras;
    private JTextField textFieldFilterGood;
    private JTextField textFieldFilterDate;
    private JTextField textFieldFilterMinVolume;
    private JTable tableContrasGoodsOrders;
    private JTextField textFieldFilterOrder;
    private JButton button_statDateSupply;
    private JButton button_statMinVolume;
    private JButton button_statGoodQuality;
    private JButton button_statReputation;
    private JButton buttonSync;
    private JTabbedPane tabbedPaneMain;
    private JPanel tabPaneMainCrit;
    private JPanel tabPaneMainContras;
    private JTable tableCrit;
    private JLabel labelCritMinValue;
    private JLabel labelCritMaxValue;
    private JPanel tabPaneMainCritEdit;
    private JTable tableCritEdit;
    private JTextField textFieldCritEditWeight;
    private JComboBox comboBoxCritEditFunction;
    private JTextField textFieldCritEditMin;
    private JTextField textFieldCritEditMax;
    private JButton buttonCritEditShow;
    private JButton buttonCritEdit;
    private JLabel labelCritEditName;
    private JButton buttonCritEditSave;
    private JComboBox comboBoxUserCrit;
    private JButton buttonUserCritAdd;
    private JButton buttonUserCritShow;
    private JButton buttonCritEditAddPoint;
    private JTextField textFieldCritEditPointVal;
    private JTextField textFieldCritEditPointWgh;
    private JPanel panelCritEditPoints;
    private JButton button_getRating;
    private JPanel tabPaneMainRating;
    private JTable tableRating;
    private JLabel labelWinnerName;
    private JLabel lavelWinnerRating;
    private JButton button_getBest;
    private JButton buttonCritVolumeGoods;
    private JButton buttonCritVolumeFilter;
    private JComboBox comboBoxCritVolumeOrderList;
    private JTextField textFieldCritVolumeOrderFilter;
    private JPanel tabPaneCritInfo;
    private JTable tableCritOrderGoods;
    private JComboBox comboBoxCritVolumeOrderListIds;
    private JButton buttonCritVolumeGoodsExtra;
    private JPanel tabPaneCritDates;
    private JLabel labelCritDatesStart;
    private JLabel labelCritDatesEnd;
    private JLabel labelCritDatesStart2;
    private JLabel labelCritDatesEnd2;
    private JTable tableCritDateIntervals;
    private JButton buttonCritDatesView;
    private JTabbedPane tabbedPaneCritVolume;
    private JPanel tabPaneCritDatesEdit;
    private JTextField textFieldCritOrderStartDate;
    private JPanel panelCritOrderEndDate;
    private JButton buttonCritDatesEdit;
    private JPanel panelCritOrderStartDate;
    private JPanel panelCritIntervalStartDate;
    private JPanel panelCritIntervalEndDate;
    private JTextField textFieldCritIntervalQty;
    private JLabel labelCritIntervalQtyInfo;
    private JComboBox comboBoxCritVolumeOrderGoodList;
    private JComboBox comboBoxCritVolumeOrderGoodListIds;
    private JTable tableCritDateIntervalsEdit;
    private JButton buttonCritIntervalAddData;
    private JButton buttonCritVolumeShowAll;
    private JPanel tabPaneCritResult;
    private JTable tableCritDatesResult;
    private JButton buttonRecomContrasHistory;
    private JTextField textFieldRecomContrasFilter;
    private JTable tableContrasHistory;
    private JTabbedPane tabbedPaneContrasRecommend;
    private JButton buttonRecomContrasHistoryEdit;
    private JTable tableContrasHistoryEdit;
    private JButton buttonContrasHistorySave;
    private JButton buttonRecomContrasResult;
    private JTable tableContrasHistResult;
    private JTextField textFieldOptOrderFilter;
    private JButton buttonOptOrderGoodsWithCrits;
    private JTabbedPane tabbedPaneOptVolume;
    private JPanel panelOptInfoStartDate;
    private JPanel panelOptInfoEndDate;
    private JButton buttonOptInfoShow;
    private JTable tableOptVolumeInfoFiltered;
    private JTextField textFieldOptOrderGoodsFilterName;
    private JComboBox comboBoxOrderSelectionName;
    private JComboBox comboBoxOrderSelectionIds;
    private JButton buttonOptGoodCondShow;
    private JButton buttonOptOrderGoodsConditionsTab;
    private JTable tableOptGoodsConditionsEdit;
    private JButton buttonOptGoodsCondEdit;
    private JButton buttonOptOrderGoodsCells;
    private JTable tableOptCellsView;
    private JTextField textFieldOptOrderGoodsCellsFilterName;
    private JComboBox comboBoxOrderCellSelectionName;
    private JComboBox comboBoxOrderCellsSelectionIds;
    private JButton buttonOptOrderCellEdit;
    private JButton buttonOptOrderCellShow;
    private JLabel labelOptOrderSelectedName;
    private JComboBox comboBoxGoodCellSelectionName;
    private JComboBox comboBoxGoodCellSelectionIds;
    private JComboBox comboBoxCellCellSelectionIds;
    private JTextField textFieldOptOrderCellWeight;
    private JTextField textFieldOptOrderCellGoodQty;
    private JTable tableOptGoodCellData;
    private JButton buttonOptGoodCellUpdate;
    private JTable tableOptVolResult;
    private JButton buttonOptOrderResult;
    private JTable tableOptVolResultPurchase;
    private JButton buttonOptOrderResultPurchase;
    private JButton buttongetAllContras;
    private JButton buttonCritViewCG;
    private JButton buttonCritViewCGSave;
    private JButton buttonCritResultFilter;
    private JTextField textFieldCritResultGoodsFilter;
    private JComboBox comboBoxCellCellSelectionIdss;
    private JButton CritDataMassEditButton;
    private JTextField CritDataMassEdit;
    private JButton buttonCreateProfile;
    private JTextField SearchParamentInsert;
    private JButton QueryButton;
    private JTable HeadersTable;
    private JLabel StatusLabel;
    private JLabel CurrentRecords;
    private JLabel TotalRecords;
    private JPanel tablePanel;
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
    private DatabaseManager dbExtractor;
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

    private DatabaseManager dbManager;
    private JComboBox<String> comboBoxProfileCriterion;
    private JPanel ParserPanel;


    public enum Role {
        USER,
        EXPERT
    }

    public void setRole(Role role) {
        this.currentRole = role;
        applyRolePermissions();
    }

    private Role currentRole;
    private Thread EGRUL_Thread;
    private Thread PDF_to_Data_Thread;

    private void applyRolePermissions() {
        boolean isExpert = currentRole == Role.EXPERT;

        // Пример: отключаем/включаем кнопки
        buttonCritEdit.setEnabled(isExpert);
        buttonCritEditSave.setEnabled(isExpert);
        buttonUserCritAdd.setEnabled(isExpert);
        buttonUserCritShow.setEnabled(isExpert);
        buttonCritEditAddPoint.setEnabled(isExpert);
        buttonSync.setEnabled(isExpert);
        buttonCreateProfile.setEnabled(isExpert);
        ParserPanel.setVisible(isExpert);

        // И так далее для всех элементов
        textFieldCritEditWeight.setEditable(isExpert);
        textFieldCritEditMin.setEditable(isExpert);
        textFieldCritEditMax.setEditable(isExpert);
    }
    // List<rowGoodsOrders> rowTableCritIntervalEdit;

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


    public mainForm(Role role) {
        this.currentRole = role;
        // Не вызываем initComponents()

        setContentPane(MainPanel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        applyRolePermissions();

        // Добавляем скрытую комбинацию клавиш
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyEventDispatcher() {
            @Override
            public boolean dispatchKeyEvent(KeyEvent e) {
                if (e.getID() == KeyEvent.KEY_PRESSED) {
                    // Включение режима эксперта — Ctrl + Shift + E
                    if ((e.getModifiersEx() & KeyEvent.CTRL_DOWN_MASK) != 0 &&
                            (e.getModifiersEx() & KeyEvent.SHIFT_DOWN_MASK) != 0 &&
                            e.getKeyCode() == KeyEvent.VK_E) {

                        String password = JOptionPane.showInputDialog("Введите пароль для эксперта:");
                        if ("1234".equals(password)) {
                            JOptionPane.showMessageDialog(null, "Режим эксперта активирован");
                            setRole(Role.EXPERT);
                            setTitle("Система (Роль: ЭКСПЕРТ)");
                        } else {
                            JOptionPane.showMessageDialog(null, "Неверный пароль");
                        }
                    }

                    // Выход из режима эксперта — Ctrl + Shift + U
                    if ((e.getModifiersEx() & KeyEvent.CTRL_DOWN_MASK) != 0 &&
                            (e.getModifiersEx() & KeyEvent.SHIFT_DOWN_MASK) != 0 &&
                            e.getKeyCode() == KeyEvent.VK_U) {

                        setRole(Role.USER);
                        JOptionPane.showMessageDialog(null, "Вы переключились в режим пользователя");
                        setTitle("Система (Роль: ПОЛЬЗОВАТЕЛЬ)");
                    }
                }
                return false;
            }
        });
        String userAgent = RandomUserAgent.getRandomUserAgent();
        this.driverSetup = new ChromeDriverSetup(userAgent);
        String __URL = "";
        String __USER = "";
        String __PASSWORD = "";
        String __DB_Main = "";
        String __DB_Module = "";



        // read from json start
        try {
            InputStream isJSON = this.getClass().getResourceAsStream("/config.json");
            String textJSON = IOUtils.toString(isJSON, "UTF-8");
            JSONObject configJSON = new JSONObject(textJSON);

            __URL = configJSON.getString("URL");
            __USER = configJSON.getString("USER");
            __DB_Main = configJSON.getString("DB_Global_Marine");
            __DB_Module = configJSON.getString("DB_Global_Module");
            __PASSWORD = configJSON.getString("PASSWORD");

            //    System.out.println(configJSON);
        } catch (IOException | JSONException | NullPointerException e) {
            System.out.println("JSON failed");
        }
        //read from json end

        final String ___URL = __URL;
        final String ___USER = __USER;
        final String ___DB_Main = __DB_Main;
        final String ___DB_Module = __DB_Module;
        final String ___PASSWORD = __PASSWORD;

        dbExtractor = new DatabaseManager(___URL, ___USER, ___DB_Main, ___DB_Module, ___PASSWORD);

        // синхронизация основной и вспомогательной баз данных
        buttonSync.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dbExtractor.setTables(false);
                dbExtractor.updateTables(true, false);

                // установить соединение с БД модуля и создать таблицу в случае её отсутствия)
                //    dbExtractor.setCells(false);
            }
        });

        DefaultTableModel modelC = new DefaultTableModel(
                new Object[][]{},
                new String[]{"Код поставщика", "Наименование поставщика", "ИИН", "Деловая репутация, флаг"}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Все ячейки нередактируемы
            }
        };

        DefaultTableModel modelCGO = new DefaultTableModel(
                new Object[][]{},
                new String[]{"Наименование заказа", "Наименование поставщика", "Наименование ТМЦ", "Срок поставки, день", "Минимальная партия поставки, ед.","ЕИ"}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Все ячейки нередактируемы
            }
        };

        tableContrasGoodsOrders.setModel(modelCGO);

        // Массив всех JTabbedPane
        JTabbedPane[] tabbedPanes = {
                tabbedPaneCritVolume,
                tabbedPaneOptVolume,
                tabbedPaneContrasRecommend,
                tabbedPaneMain
        };

        // Для каждого JTabbedPane скрываем вкладки и отключаем их
        for (JTabbedPane tabbedPane : tabbedPanes) {
            // Скрываем все вкладки при старте, кроме активной
            for (int i = 0; i < tabbedPane.getTabCount(); i++) {
                if (i == tabbedPane.getSelectedIndex()) {
                    // Для активной вкладки оставляем её заголовок видимым
                    tabbedPane.setTabComponentAt(i, new JLabel(tabbedPane.getTitleAt(i)));
                } else {
                    // Скрываем остальные вкладки
                    tabbedPane.setTabComponentAt(i, new JPanel());
                }
            }

            // Отключаем все вкладки для кликов
            for (int i = 0; i < tabbedPane.getTabCount(); i++) {
                tabbedPane.setEnabledAt(i, false);  // Отключаем вкладки
            }

            // Добавляем слушатель на изменение вкладки для обновления видимости заголовков
            tabbedPane.addChangeListener(e -> {
                // При переключении вкладки обновляем только видимость заголовков
                int selectedIndex = tabbedPane.getSelectedIndex();
                for (int i = 0; i < tabbedPane.getTabCount(); i++) {
                    if (i == selectedIndex) {
                        // Для активной вкладки показываем её заголовок
                        tabbedPane.setTabComponentAt(i, new JLabel(tabbedPane.getTitleAt(i)));
                    } else {
                        // Для неактивной вкладки скрываем заголовок
                        tabbedPane.setTabComponentAt(i, new JPanel());
                    }
                }
            });
        }

        class CreateProfileDialog extends JDialog {
            private JTextField profileNameField;
            private JButton saveButton;
            private DatabaseManager dbManager;
            private JComboBox<String> comboBoxProfileCriterion;

            public CreateProfileDialog(DatabaseManager dbManager, JComboBox<String> comboBoxProfileCriterion) {
                this.dbManager = dbManager;
                this.comboBoxProfileCriterion = comboBoxProfileCriterion;

                setTitle("Создание профиля");
                setModal(true);
                setSize(300, 150);
                setLocationRelativeTo(null);
                setLayout(new GridLayout(2, 2));

                add(new JLabel("Название профиля:"));
                profileNameField = new JTextField();
                add(profileNameField);

                saveButton = new JButton("Сохранить");
                add(saveButton);
                add(new JLabel()); // пустая ячейка

                saveButton.addActionListener(e -> saveProfile());

                setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            }

            private void saveProfile() {
                String profileName = profileNameField.getText().trim();

                if (profileName.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Введите название профиля.");
                    return;
                }

                // Проверяем, существует ли уже профиль с таким именем
                for (int i = 0; i < comboBoxProfileCriterion.getItemCount(); i++) {
                    if (profileName.equals(comboBoxProfileCriterion.getItemAt(i))) {
                        JOptionPane.showMessageDialog(this, "Профиль с таким именем уже существует.");
                        return;
                    }
                }

                List<DatabaseManager.CriterionData> criteria = dbManager.getAllCriteria();

                for (DatabaseManager.CriterionData crit : criteria) {
                    JSONObject obj = new JSONObject();
                    obj.put("nfunctiontype", crit.nfunctiontype);
                    obj.put("nminval", crit.nminval);
                    obj.put("nmaxval", crit.nmaxval);
                    obj.put("nweight", crit.nweight);

                    boolean success = dbManager.insertProfile(profileName, crit.id, obj.toString());

                    if (!success) {
                        JOptionPane.showMessageDialog(this, "Ошибка при сохранении профиля для ID " + crit.id);
                        return;
                    }
                }

                // Добавляем новый профиль в комбобокс
                comboBoxProfileCriterion.addItem(profileName);
                comboBoxProfileCriterion.setSelectedItem(profileName);

                JOptionPane.showMessageDialog(this, "Профиль успешно сохранён для всех критериев.");
                dispose();
            }
        }

        // Использование:
        buttonCreateProfile.addActionListener(e -> {
            CreateProfileDialog dialog = new CreateProfileDialog(dbExtractor, comboBoxProfileCriterion);
            dialog.setVisible(true);
        });



        DefaultTableModel modelCrit = new DefaultTableModel(
                new Object[][]{},
                new String[]{"Значение критерия", "Весовой коэффициент допустимости критерия, доля"}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Все ячейки нередактируемы
            }
        };
        fillComboBoxProfile();

        comboBoxProfileCriterion.addActionListener(e -> onProfileSelection());


        comboBoxProfileCriterion.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedProfile = (String) comboBoxProfileCriterion.getSelectedItem();

                if (selectedProfile != null && !selectedProfile.isEmpty()) {
                    boolean success = dbExtractor.applyProfileByName(selectedProfile);

                    if (success) {
                        JOptionPane.showMessageDialog(null, "Профиль \"" + selectedProfile + "\" успешно применён!");
                        updateCritValues(); // если реализовано
                    } else {
                        JOptionPane.showMessageDialog(null, "Ошибка при применении профиля.");
                    }
                }
            }
        });

        buttongetAllContras.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                // установить соединение с БД модуля и заполнить данные о поставщиках
                int contrasNum = dbExtractor.getContrasNum(false);
                label_numContras.setText(String.format("%d", contrasNum));

                // установить соединение с БД модуля и отобразить поставщиков с учетом фильтров

                List<rowContras> rowsC = dbExtractor.getCs(false, textFieldFilterContras.getText());

                tableContrasGoodsOrders.setModel(modelC);
                updateTableContras(rowsC);

                tabbedPaneMain.setSelectedIndex(0);
                System.out.println("Button pressed");
            }
        });

        // обновление данных о поставщиках на основе вспомогательной БД
        button_getContras.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // установить соединение с БД модуля и отобразить поставщиков с учетом фильтров

                List<rowContrasGoodsOrders> rowsCGO = dbExtractor.getCGOs(false, textFieldFilterContras.getText(), textFieldFilterGood.getText(), textFieldFilterOrder.getText(), textFieldFilterDate.getText(), textFieldFilterMinVolume.getText(), textFieldFilterOkpd2.getText(), textFieldFilterGroup.getText());

                tableContrasGoodsOrders.setModel(modelCGO);

                updateTableContrasGoodsOrders(rowsCGO);
                tabbedPaneMain.setSelectedIndex(0);
                System.out.println("Button pressed");
            }
        });



        tableCrit.setModel(modelCrit);

        button_statDateSupply.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                buttonCritViewCG.setText("Данные");
                buttonCritViewCGSave.setEnabled(false);
                CritDataMassEditButton.setEnabled(false);
                CritDataMassEdit.setEnabled(false);
                tableCrit.setModel(modelCrit);
                // установить соединение с БД модуля и заполнить данные о критерии с учетом фильтров
                CritString = "ndeliverytime";   // CritString = "prs_lot.ndeliverytime";
                CritShort = "date_supply";
                CritId = 0;
                dbExtractor.updateCGsUserCritValues(false);
                dbExtractor.alterUserCritData(false, CritString);
                CritName = button_statDateSupply.getText();
                updateCritValues();
            }
        });


        button_statMinVolume.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                buttonCritViewCG.setText("Данные");
                buttonCritViewCGSave.setEnabled(false);
                CritDataMassEditButton.setEnabled(false);
                CritDataMassEdit.setEnabled(false);
                tableCrit.setModel(modelCrit);
                // установить соединение с БД модуля и заполнить данные о критерии с учетом фильтров
                CritString = "nqty";    // CritString = "prs_lot.nqty";
                CritShort = "min_vol";
                CritId = 1;
                dbExtractor.updateCGsUserCritValues(false);
                dbExtractor.alterUserCritData(false, CritString);
                CritName = button_statMinVolume.getText();
                updateCritValues();
            }
        });
        button_statGoodQuality.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                buttonCritViewCG.setText("Данные");
                buttonCritViewCGSave.setEnabled(false);
                CritDataMassEditButton.setEnabled(false);
                CritDataMassEdit.setEnabled(false);
                tableCrit.setModel(modelCrit);
                // установить соединение с БД модуля и заполнить данные о критерии с учетом фильтров
                CritString = "ngoodquality";    //  CritString = "prs_lot.ngoodquality";
                CritShort = "g_qual";
                CritId = 2;
                dbExtractor.updateCGsUserCritValues(false);
                dbExtractor.alterUserCritData(false, CritString);
                CritName = button_statGoodQuality.getText();
                updateCritValues();
            }
        });

        button_statReputation.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                buttonCritViewCG.setText("Данные");
                buttonCritViewCGSave.setEnabled(false);
                CritDataMassEditButton.setEnabled(false);
                CritDataMassEdit.setEnabled(false);
                tableCrit.setModel(modelCrit);
                // установить соединение с БД модуля и заполнить данные о критерии с учетом фильтров
                CritString = "bs_contras.ncontrasreliability";
                CritShort = "c_rep";
                CritId = 3;
                dbExtractor.updateCGsUserCritValues(false);
                CritName = button_statReputation.getText();
                updateCritValues();
            }
        });

        DefaultTableModel modelCritEdit = new DefaultTableModel(
                new Object[][]{},
                new String[]{"Значение критерия", "Весовой коэффициент допустимости критерия, доля"}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                if (column < 2)
                    return false; // Все ячейки до 2-го столбца включительно не редактируемы
                return true; // После редактируемы
            }
        };

        tableCritEdit.setModel(modelCritEdit);

        buttonCritEdit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // установить соединение с БД модуля и заполнить данные о критерии без учета фильтров

                List<rowCritValues> rowsCrVa;
                if (CritId == 3) {
                    rowsCrVa = dbExtractor.getCrVas(false, CritString, CritShort, textFieldFilterContras.getText(), textFieldFilterGood.getText(), textFieldFilterOrder.getText(), textFieldFilterDate.getText(), textFieldFilterMinVolume.getText(), textFieldFilterOkpd2.getText());
                } else {
                    rowsCrVa = dbExtractor.getUserCrVas(false, CritString, CritShort, textFieldFilterContras.getText(), textFieldFilterGood.getText(), textFieldFilterOrder.getText(), textFieldFilterDate.getText(), textFieldFilterMinVolume.getText(), textFieldFilterOkpd2.getText());
                }

                // установить соединение с БД модуля и создать таблицу в случае её отсутствия)
                dbExtractor.setCritData(false);
                // установить соединение с БД модуля и получить данные о текущем критерии
                List<rowCritData> rowsCrData = dbExtractor.getCritData(false, CritId);

                if (!rowsCrData.isEmpty()) {
                    textFieldCritEditMin.setText(Double.toString(rowsCrData.get(0).getMinVal()));
                    textFieldCritEditMax.setText(Double.toString(rowsCrData.get(0).getMaxVal()));
                    textFieldCritEditWeight.setText(Double.toString(rowsCrData.get(0).getCritWeight()));
                    labelCritEditName.setText(rowsCrData.get(0).getCritName());
                    comboBoxCritEditFunction.setSelectedIndex(rowsCrData.get(0).getCritFunction());

                    if (rowsCrData.get(0).getCritFunction() < 4) {
                        // для обычной функции всё уже получено
                    } else {
                        // для произвольной функции

                        List<rowCritValues> dataPointsSaved = new ArrayList<>();
                        JSONObject pointsJSON = new JSONObject(rowsCrData.get(0).getJsonDataPoints());
                        for (int i = 0; i < pointsJSON.length(); i++) {
                            String iKey = (JSONObject.getNames(pointsJSON))[i];
                            dataPointsSaved.add(new rowCritValues(Double.parseDouble(iKey), pointsJSON.getDouble(iKey)));
                            //
                        }
                        dataPointsSaved.sort(Comparator.comparingDouble(rowCritValues::getCritVal));

                        rowsCrVa = dataPointsSaved;
                    }

                    updateTableCritEditValues(rowsCrVa);

                } else {
                    if (!rowsCrVa.isEmpty()) {
                        textFieldCritEditMin.setText(Double.toString(rowsCrVa.get(0).getCritVal()));
                        textFieldCritEditMax.setText(Double.toString(rowsCrVa.get(rowsCrVa.size() - 1).getCritVal()));
                    } else {
                        textFieldCritEditMin.setText("");
                        textFieldCritEditMin.setText("");
                    }
                    textFieldCritEditWeight.setText("1");
                    labelCritEditName.setText(CritName);
                    comboBoxCritEditFunction.setSelectedIndex(0);
                }


                tabbedPaneMain.setSelectedIndex(2);
                System.out.println("Button pressed");
            }
        });


        buttonCritEditShow.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // заполнение таблицы значений весов критерия
                if (comboBoxCritEditFunction.getSelectedIndex() < 4) {
                    // Одна из фиксированных функций
                    double minV = Double.parseDouble(textFieldCritEditMin.getText());
                    double maxV = Double.parseDouble(textFieldCritEditMax.getText());
                    int funType = comboBoxCritEditFunction.getSelectedIndex();
                    List<rowCritValues> rowsCrVas = updateTableCritEditValues(minV, maxV, funType);
                } else {
                    // Произвольная функция

                    // обновить таблицу новым столбцом для ввода данных

                }

            }
        });


        buttonCritEditSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // сформировать json-объект из данных таблицы критерия
                JSONObject critData = new JSONObject();
                for (int i = 0; i < tableCritEdit.getRowCount(); i++) {
                    critData.put(tableCritEdit.getValueAt(i, 0).toString(), tableCritEdit.getValueAt(i, 1).toString());
                }
                // установить соединение с БД модуля и получить данные о текущем критерии
                List<rowCritData> rowsCrData = dbExtractor.getCritData(false, CritId);

                if (!rowsCrData.isEmpty()) {
                    // обновить критерий
                    dbExtractor.changeCritData(false, CritId, labelCritEditName.getText(), Integer.toString(comboBoxCritEditFunction.getSelectedIndex()), textFieldCritEditMin.getText(), textFieldCritEditMax.getText(), textFieldCritEditWeight.getText(), critData.toString());
                } else {
                    // добавить критерий
                    dbExtractor.addCritData(false, CritId, labelCritEditName.getText(), Integer.toString(comboBoxCritEditFunction.getSelectedIndex()), textFieldCritEditMin.getText(), textFieldCritEditMax.getText(), textFieldCritEditWeight.getText(), critData.toString());

                }

                tabbedPaneMain.setSelectedIndex(2);
                System.out.println("Button pressed");
            }
        });

        // установить соединение с БД модуля и создать таблицу связей критериев в случае её отсутствия
        dbExtractor.setUserCritData(false);

        // установить соединение с БД модуля и создать таблицу критериев в случае её отсутствия
        dbExtractor.setCritData(false);

        // установить соединение с БД модуля и получить данные о пользовательских критериях
        List<rowCritData> rowsUserCrData = dbExtractor.getUserCritData(false);
        if (!rowsUserCrData.isEmpty()) {
            for (rowCritData rowsUserCrD : rowsUserCrData) {
                comboBoxUserCrit.addItem(rowsUserCrD.getCritName());
            }
        }


        DefaultTableModel modelCritCGValuesNoEdit = new DefaultTableModel(
                new Object[][]{},
                new String[]{"Наименование заказа","Наименование поставщика", "Наименование ТМЦ","Значение критерия"}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Все ячейки нередактируемы
            }
        };


        DefaultTableModel modelCritCGValuesEdit = new DefaultTableModel(
                new Object[][]{},
                new String[]{"Идентификатор поставщика", "Код поставщика","Наименование поставщика","Идентификатор ТМЦ","Номенклатурный номер", "Наименование ТМЦ","Значение критерия"}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                if (column < 6)
                    return false; //  ячейки нередактируемы
                else return true;
            }
        };


        buttonCritViewCG.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // переключение
                if (Objects.equals(buttonCritViewCG.getText(), "Данные"))
                {
                    buttonCritViewCG.setText("Критерий");
                    // просмотр значений текущего критерия для поставщиков и ТМЦ

                    List<rowContrasGoodsOrders> rowsCGO;

                    if (CritId == 3) {                  // деловая репутация вычисляется по таблицам базы глобала
                        buttonCritViewCGSave.setEnabled(false);
                        tableCrit.setModel(modelCritCGValuesNoEdit);


                        rowsCGO = dbExtractor.getCGOs(false, textFieldFilterContras.getText(), textFieldFilterGood.getText(), textFieldFilterOrder.getText(), textFieldFilterDate.getText(), textFieldFilterMinVolume.getText(), textFieldFilterOkpd2.getText(), textFieldFilterGroup.getText());


                        updateTableCritGCViewValues(rowsCGO,CritId);
                    } else {
                        buttonCritViewCGSave.setEnabled(true);
                        CritDataMassEditButton.setEnabled(true);
                        CritDataMassEdit.setEnabled(true);
                        tableCrit.setModel(modelCritCGValuesEdit);

                        rowsCGO = dbExtractor.getCGOsUserCrVas(false, CritString, CritShort, textFieldFilterContras.getText(), textFieldFilterGood.getText(), textFieldFilterOrder.getText(), textFieldFilterDate.getText(), textFieldFilterMinVolume.getText(), textFieldFilterOkpd2.getText());

                        updateTableCritGCViewUserValues(rowsCGO,CritId);
                        //   rowsLotGCVa = dbExtractor.getUserCrVas(false, CritString, CritShort, "", "", "", "", "");
                    }
                }
                else {
                    buttonCritViewCG.setText("Данные");
                    buttonCritViewCGSave.setEnabled(false);
                    tableCrit.setModel(modelCrit);
                    updateCritValues();
                }

                System.out.println("Button pressed");
            }
        });

        buttonCritViewCGSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // сохранение
                List<rowContrasGoodsOrders> rowsCGOs = new ArrayList<>();

                for (int i = 0; i < tableCrit.getRowCount(); i++) {
                    Long c_id = Long.parseLong(tableCrit.getValueAt(i, 0).toString());
                    String c_code = tableCrit.getValueAt(i, 1).toString();
                    String c_name = tableCrit.getValueAt(i, 2).toString();
                    Long g_id = Long.parseLong(tableCrit.getValueAt(i, 3).toString());
                    String g_code_raw = tableCrit.getValueAt(i, 4) == null ? "" : tableCrit.getValueAt(i, 4).toString();
                    String g_code = g_code_raw.trim(); // можно еще trim(), чтобы убрать случайные пробелы
                    String g_name = tableCrit.getValueAt(i, 5).toString();

                    // Обрабатываем ввод пользователя (заменяем запятую на точку)
                    String rawValue = tableCrit.getValueAt(i, 6).toString().replace(",", ".");
                    double cr_us_val;
                    try {
                        cr_us_val = Double.parseDouble(rawValue);
                    } catch (NumberFormatException ex) {
                        cr_us_val = 0.0; // Можно выбрать другое дефолтное значение
                        System.err.println("Ошибка парсинга значения: " + rawValue);
                    }

                    rowsCGOs.add(new rowContrasGoodsOrders(
                            c_id, c_name, c_code, g_id, g_name, g_code,
                            0, "", 0, cr_us_val, 0, 0, ""
                    ));
                }

                dbExtractor.updateContrasGoodsUsCrForEdit(false, rowsCGOs, CritString);
                dbExtractor.updateNdeliverytimeAndNqtyFromModuleLotcriterion(false);
            }
        });



        buttonUserCritAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // добавление в список введённого значения
                comboBoxUserCrit.addItem(comboBoxUserCrit.getEditor().getItem());
            }
        });

        // Создание слушателя для кнопки
        CritDataMassEditButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    // Получаем значение из текстового поля и преобразуем в число
                    String inputText = CritDataMassEdit.getText();
                    double newValue = Double.parseDouble(inputText);

                    // Получаем модель таблицы
                    DefaultTableModel modelContrasGoodsOrders = (DefaultTableModel) tableCrit.getModel();

                    // Обновляем 7-й столбец (по индексу 6) для всех строк таблицы
                    for (int i = 0; i < modelContrasGoodsOrders.getRowCount(); i++) {
                        modelContrasGoodsOrders.setValueAt(newValue, i, 6); // Индекс 6 — это 7-й столбец
                    }

                    // Сообщение о успешном обновлении
                    JOptionPane.showMessageDialog(null, "Все значения в столбце данных обновлены на: " + newValue);
                } catch (NumberFormatException ex) {
                    // Обработка ошибки, если введенное значение не является числом
                    JOptionPane.showMessageDialog(null, "Ошибка: введено некорректное число.");
                }
            }
        });


        buttonUserCritShow.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                buttonCritViewCG.setText("Данные");
                buttonCritViewCGSave.setEnabled(false);
                tableCrit.setModel(modelCrit);

                Object selectedObj = comboBoxUserCrit.getSelectedItem();
                rowCritData selected = null;

                if (selectedObj instanceof rowCritData) {
                    selected = (rowCritData) selectedObj;
                } else if (selectedObj instanceof String) {
                    String inputName = ((String) selectedObj).trim();

                    if (!inputName.isEmpty()) {
                        List<rowCritData> rowsCrData = dbExtractor.getAllCritData(false);

                        // Проверяем существование критерия
                        for (rowCritData row : rowsCrData) {
                            if (row.getCritName().equalsIgnoreCase(inputName)) {
                                selected = row;
                                break;
                            }
                        }

                        if (selected == null) {
                            // Добавляем новый критерий
                            dbExtractor.addUserCritData(false, inputName, "0", "0", "100", "1", "{}");
                            long newCritId = dbExtractor.getMaxCritId(false);
                            String newColumn = "user_crit_" + newCritId;
                            dbExtractor.alterUserCritData(false, newColumn);

                            // Обновляем модель и выбираем новый элемент
                            updateComboBoxModel();
                            selectCritInComboBox(newCritId);

                            selected = new rowCritData(newCritId, inputName, 0, 0, 100, 1, "{}");
                        }
                    }
                }

                if (selected != null) {
                    processSelectedCrit(selected);
                }
            }
        });

        comboBoxCritEditFunction.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (comboBoxCritEditFunction.getSelectedIndex() < 4) {
                    // одна из стандартных функций
                    panelCritEditPoints.setVisible(false);
                    buttonCritEditAddPoint.setEnabled(false);
                } else {
                    // произвольная функция
                    panelCritEditPoints.setVisible(true);
                    buttonCritEditAddPoint.setEnabled(true);
                }
            }
        });


        buttonCritEditAddPoint.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DefaultTableModel modelCritEdit = (DefaultTableModel) tableCritEdit.getModel();
                modelCritEdit.addRow(new Object[]{
                        textFieldCritEditPointVal.getText(),
                        textFieldCritEditPointWgh.getText()
                });
            }
        });


        DefaultTableModel modelCGOws = new DefaultTableModel(
                new Object[][]{},
                new String[]{"Наименование заказа", "Наименование поставщика", "Наименование ТМЦ", "Срок поставки, день", "Минимальная партия поставки, ед.", "Уровень качества ТМЦ по жалобам", "Деловая репутация", "Рейтинг"}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Все ячейки нередактируемы
            }
        };

        DefaultTableModel modelCGOwsBest = new DefaultTableModel(
                new Object[][]{},
                new String[]{"Наименование заказа", "Наименование поставщика", "Наименование ТМЦ", "Рейтинг"}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Все ячейки нередактируемы
            }
        };




        tableRating.setModel(modelCGOws);

        button_getRating.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Создаем экземпляр калькулятора рейтингов
                RatingCalculator ratingCalculator = new RatingCalculator(dbExtractor);

                // Вычисляем рейтинги
                List<rowContrasGoodsOrdersWithWeights> rowsCGOws = ratingCalculator.calculateRatings(
                        textFieldFilterContras.getText(),
                        textFieldFilterGood.getText(),
                        textFieldFilterOrder.getText(),
                        textFieldFilterDate.getText(),
                        textFieldFilterMinVolume.getText(),
                        textFieldFilterOkpd2.getText()
                );

                // Обновляем таблицу
                tableRating.setModel(modelCGOws);
                updateTableContrasGoodsOrdersWes(rowsCGOws);

                // Сохраняем рейтинги поставщиков
                dbExtractor.setRatingTable(false);
                dbExtractor.updateRatingTable(false, rowsCGOws);

                button_getBest.setEnabled(true);
                tabbedPaneMain.setSelectedIndex(3);
                System.out.println("Button pressed");
            }
        });




        button_getBest.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                // получение рейтинговых данных
                List<rowContrasGoodsOrdersWithWeights> rowsCGOwsBest = dbExtractor.getCGOwesBest(false, textFieldFilterGood.getText());

                if (!rowsCGOwsBest.isEmpty()) {
                    labelWinnerName.setText(rowsCGOwsBest.get(0).getContrasName());
                    lavelWinnerRating.setText(String.valueOf(formatValue(rowsCGOwsBest.get(0).getRatingComplete())));
                    tableRating.setModel(modelCGOwsBest);
                    updateTableContrasGoodsOrdersWesBest(rowsCGOwsBest);
                }


                tabbedPaneMain.setSelectedIndex(3);
                System.out.println("Button pressed");
            }
        });

        //
        //
        // критический запас
        //
        //


        DefaultTableModel modelCritOGs = new DefaultTableModel(
                new Object[][]{},
                new String[]{"Наименование заказа", "Наименование ТМЦ", "Общее количество ТМЦ, ед.", "ЕИ"}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Все ячейки нередактируемы
            }
        };

        DefaultTableModel modelCritCGOs = new DefaultTableModel(
                new Object[][]{},
                new String[]{"Наименование заказа", "Наименование ТМЦ", "Наименование поставщика", "Срок поставки, день", "Минимальная партия поставки, ед.", "ЕИ"}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Все ячейки нередактируемы
            }
        };

        tableCritOrderGoods.setModel(modelCritOGs);

        buttonCritVolumeFilter.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // фильтр обновления списка заказов в поле выбора

                // очистить
                comboBoxCritVolumeOrderList.removeAllItems();
                comboBoxCritVolumeOrderListIds.removeAllItems();
                // установить соединение с БД модуля и отобразить заказы с учетом фильтра

                List<rowContrasGoodsOrders> rowsCGOCritVol = dbExtractor.getCGOCritVols(false, textFieldCritVolumeOrderFilter.getText());
                if (!rowsCGOCritVol.isEmpty()) {

                    for (rowContrasGoodsOrders rowsCGO : rowsCGOCritVol) {
                        comboBoxCritVolumeOrderList.addItem(rowsCGO.getOrderName().substring(0,Math.min(25,rowsCGO.getOrderName().length())));
                        comboBoxCritVolumeOrderListIds.addItem(Long.toString(rowsCGO.getIdOrder()));
                    }
                }
                //    tabbedPaneCritVolume.setSelectedIndex(0);
            }
        });


        comboBoxCritVolumeOrderList.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Проверяем, что comboBoxCritVolumeOrderList и comboBoxCritVolumeOrderListIds не null
                if (comboBoxCritVolumeOrderList == null || comboBoxCritVolumeOrderListIds == null) {
                    return;
                }

                int selectedIndex = comboBoxCritVolumeOrderList.getSelectedIndex();
                if (selectedIndex < 0) { // Проверяем, что есть выбранный элемент
                    return;
                }

                Object selectedItem = comboBoxCritVolumeOrderListIds.getItemAt(selectedIndex);
                if (selectedItem == null) {
                    return;
                }

                String selectedId = selectedItem.toString();

                // Обновляем комбобоксы
                comboBoxCritVolumeOrderGoodList.removeAllItems();
                comboBoxCritVolumeOrderGoodListIds.removeAllItems();

                List<rowGoodsOrders> rowsCGOCritVolDisGood = dbExtractor.getCGOCritVolDisGoods(false, selectedId);

                if (rowsCGOCritVolDisGood == null || rowsCGOCritVolDisGood.isEmpty()) {
                    return;
                }

                // Используем список для сортировки
                List<String> sortedGoods = new ArrayList<>();
                Map<String, String> goodsIdMap = new HashMap<>();

                for (rowGoodsOrders rowsGO : rowsCGOCritVolDisGood) {
                    if (rowsGO == null) continue; // Предотвращаем возможный null-объект

                    String goodName = rowsGO.getGoodName();
                    if (goodName == null) {
                        goodName = "UNKNOWN"; // Подстраховка от null
                    }

                    goodName = goodName.substring(0, Math.min(25, goodName.length()));
                    sortedGoods.add(goodName);
                    goodsIdMap.put(goodName, Long.toString(rowsGO.getIdGood())); // Связываем названия с ID
                }

                Collections.sort(sortedGoods); // Сортировка списка по алфавиту

                for (String good : sortedGoods) {
                    comboBoxCritVolumeOrderGoodList.addItem(good);
                    comboBoxCritVolumeOrderGoodListIds.addItem(goodsIdMap.get(good)); // Добавляем соответствующий ID
                }

                comboBoxCritVolumeOrderGoodList.setEnabled(true);
            }
        });


        buttonCritVolumeGoods.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                // установить соединение с БД модуля и отобразить ТМЦ по выбранному заказу
                if (comboBoxCritVolumeOrderList.getSelectedIndex() > -1) {

                    List<rowGoodsOrders> rowsCGOCritVolGood = dbExtractor.getCGOCritVolGoods(
                            false,
                            comboBoxCritVolumeOrderListIds.getItemAt(comboBoxCritVolumeOrderList.getSelectedIndex()).toString()
                    );

                    tableCritOrderGoods.setModel(modelCritOGs);
                    updateCritOrderGoodValues(rowsCGOCritVolGood);
                    tabbedPaneCritVolume.setSelectedIndex(0);


                }
            }
        });



        buttonCritVolumeGoodsExtra.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                // установить соединение с БД модуля и отобразить информацию по ТМЦ по выбранному заказу
                if (comboBoxCritVolumeOrderList.getSelectedIndex() > -1) {

                    List<rowContrasGoodsOrders> rowsCGOCritVolParam = dbExtractor.getCGOCritVolParams(false, comboBoxCritVolumeOrderListIds.getItemAt(comboBoxCritVolumeOrderList.getSelectedIndex()).toString());

                    tableCritOrderGoods.setModel(modelCritCGOs);
                    updateCritOrderGoodParams(rowsCGOCritVolParam);
                    tabbedPaneCritVolume.setSelectedIndex(0);
                }
            }
        });

        DefaultTableModel modelCritDatesViews = new DefaultTableModel(
                new Object[][]{},
                new String[]{"Наименование заказа", "Наименование ТМЦ", "Количество ТМЦ, ед.","ЕИ", "Дата начала интервала", "Дата конца интервала"}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Все ячейки нередактируемы
            }
        };

        tableCritDateIntervals.setModel(modelCritDatesViews);
        tableCritDateIntervalsEdit.setModel(modelCritDatesViews);
        tableCritDatesResult.setModel(modelCritDatesViews);

        buttonCritDatesView.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // установить соединение с БД модуля и отобразить информацию по интервалам реализации по выбранному заказу
                if (comboBoxCritVolumeOrderList.getSelectedIndex() > -1 && comboBoxCritVolumeOrderGoodList.getSelectedIndex() > -1) {

                    List<rowGoodsOrders> rowsOCritVolDates = dbExtractor.getOCritVolDates(false, comboBoxCritVolumeOrderListIds.getItemAt(comboBoxCritVolumeOrderList.getSelectedIndex()).toString(), comboBoxCritVolumeOrderGoodListIds.getItemAt(comboBoxCritVolumeOrderGoodList.getSelectedIndex()).toString());

                    if (!rowsOCritVolDates.isEmpty()) {
                        labelCritDatesStart.setText(rowsOCritVolDates.get(0).getStartDatePlan().toString());
                        labelCritDatesEnd.setText(rowsOCritVolDates.get(0).getEndDatePlan().toString());
                    } else {
                        labelCritDatesStart.setText("null");
                        labelCritDatesEnd.setText("null");
                    }

                    List<rowGoodsOrders> rowsGOCritVolIntDates = dbExtractor.getGOCritVolIntDates(false, comboBoxCritVolumeOrderListIds.getItemAt(comboBoxCritVolumeOrderList.getSelectedIndex()).toString(), comboBoxCritVolumeOrderGoodListIds.getItemAt(comboBoxCritVolumeOrderGoodList.getSelectedIndex()).toString());


                    //    tableCritOrderGoods.setModel(modelCritCGOs);
                    updateCritOrderGoodIntDates(rowsGOCritVolIntDates);

                    tabbedPaneCritVolume.setSelectedIndex(1);
                }
            }
        });


        // начальные и конечные даты для интервала заказа
        JDateChooser dateChooserIntStart = new JDateChooser();
        JDateChooser dateChooserIntEnd = new JDateChooser();


        dateChooserIntStart.setDateFormatString("dd.MM.yyyy");
        dateChooserIntEnd.setDateFormatString("dd.MM.yyyy");
        panelCritIntervalStartDate.setLayout(new BorderLayout());
        panelCritIntervalStartDate.add(dateChooserIntStart, BorderLayout.CENTER);
        panelCritIntervalEndDate.setLayout(new BorderLayout());
        panelCritIntervalEndDate.add(dateChooserIntEnd, BorderLayout.CENTER);


        buttonCritDatesEdit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if (comboBoxCritVolumeOrderList.getSelectedIndex() > -1 && comboBoxCritVolumeOrderGoodList.getSelectedIndex() > -1) {

                    List<rowGoodsOrders> rowsOCritVolDates = dbExtractor.getOCritVolDates(false, comboBoxCritVolumeOrderListIds.getItemAt(comboBoxCritVolumeOrderList.getSelectedIndex()).toString(), comboBoxCritVolumeOrderGoodListIds.getItemAt(comboBoxCritVolumeOrderGoodList.getSelectedIndex()).toString());

                    if (!rowsOCritVolDates.isEmpty()) {
                        labelCritDatesStart2.setText(rowsOCritVolDates.get(0).getStartDatePlan().toString());
                        labelCritDatesEnd2.setText(rowsOCritVolDates.get(0).getEndDatePlan().toString());
                    } else {
                        labelCritDatesStart2.setText("null");
                        labelCritDatesEnd2.setText("null");
                    }

                    // очистить данные интервалов
                    //rowTableCritIntervalEdit = new ArrayList<>();

                    List<rowGoodsOrders> rowsGOCritVolIntDates = dbExtractor.getGOCritVolIntDates(false, comboBoxCritVolumeOrderListIds.getItemAt(comboBoxCritVolumeOrderList.getSelectedIndex()).toString(), comboBoxCritVolumeOrderGoodListIds.getItemAt(comboBoxCritVolumeOrderGoodList.getSelectedIndex()).toString());

                    updateCritOrderGoodIntDatesEdit(rowsGOCritVolIntDates);

                    tabbedPaneCritVolume.setSelectedIndex(2);
                }

            }
        });

        buttonCritIntervalAddData.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // добавление информации об интервале в таблицу
                double q_ty_int = Double.parseDouble(textFieldCritIntervalQty.getText());
                Date s_date_int = dateChooserIntStart.getDate();
                Date e_date_int = dateChooserIntEnd.getDate();
                s_date_int.setHours(0);
                s_date_int.setMinutes(0);
                s_date_int.setSeconds(0);
                e_date_int.setHours(0);
                e_date_int.setMinutes(0);
                e_date_int.setSeconds(0);

                if (comboBoxCritVolumeOrderList.getSelectedIndex() > -1 && comboBoxCritVolumeOrderGoodList.getSelectedIndex() > -1) {

                    // установить соединение с БД модуля и получить данные
                    List<rowGoodsOrders> rowsGOIntData = dbExtractor.getIntervalData(false, comboBoxCritVolumeOrderListIds.getItemAt(comboBoxCritVolumeOrderList.getSelectedIndex()).toString(), comboBoxCritVolumeOrderGoodListIds.getItemAt(comboBoxCritVolumeOrderGoodList.getSelectedIndex()).toString(), s_date_int, e_date_int);

                    if (!rowsGOIntData.isEmpty()) {
                        // обновить данные
                        dbExtractor.changeIntervalData(false, comboBoxCritVolumeOrderListIds.getItemAt(comboBoxCritVolumeOrderList.getSelectedIndex()).toString(), comboBoxCritVolumeOrderGoodListIds.getItemAt(comboBoxCritVolumeOrderGoodList.getSelectedIndex()).toString(), q_ty_int, s_date_int, e_date_int);
                    } else {
                        // добавить данные
                        dbExtractor.addIntervalData(false, comboBoxCritVolumeOrderListIds.getItemAt(comboBoxCritVolumeOrderList.getSelectedIndex()).toString(), comboBoxCritVolumeOrderGoodListIds.getItemAt(comboBoxCritVolumeOrderGoodList.getSelectedIndex()).toString(), q_ty_int, s_date_int, e_date_int);

                    }

                    //rowTableCritIntervalEdit.add(new rowGoodsOrders(Long.parseLong(comboBoxCritVolumeOrderGoodListIds.getItemAt(comboBoxCritVolumeOrderGoodList.getSelectedIndex()).toString()),comboBoxCritVolumeOrderGoodList.getSelectedItem().toString(),Long.parseLong(comboBoxCritVolumeOrderListIds.getItemAt(comboBoxCritVolumeOrderList.getSelectedIndex()).toString()),comboBoxCritVolumeOrderList.getSelectedItem().toString(),q_ty_int,s_date_int,e_date_int));
                    //    updateTableCritDateIntEditValues(q_ty_int,s_date_int,e_date_int);


                    List<rowGoodsOrders> rowsGOCritVolIntDates = dbExtractor.getGOCritVolIntDates(false, comboBoxCritVolumeOrderListIds.getItemAt(comboBoxCritVolumeOrderList.getSelectedIndex()).toString(), comboBoxCritVolumeOrderGoodListIds.getItemAt(comboBoxCritVolumeOrderGoodList.getSelectedIndex()).toString());

                    updateCritOrderGoodIntDatesEdit(rowsGOCritVolIntDates);

                    tabbedPaneCritVolume.setSelectedIndex(2);
                }

            }
        });


        buttonCritVolumeShowAll.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // отобразить интервалы и величины по всем ТМЦ выбранного заказа

                textFieldCritResultGoodsFilter.setText("");

                if (comboBoxCritVolumeOrderList.getSelectedIndex() > -1) {

                    List<rowGoodsOrders> rowsGOCritVolIntDatesAll = dbExtractor.getGOCritVolIntDatesAll(false, comboBoxCritVolumeOrderListIds.getItemAt(comboBoxCritVolumeOrderList.getSelectedIndex()).toString());

                    updateCritOrderGoodIntDatesEditAll(rowsGOCritVolIntDatesAll);
                    tabbedPaneCritVolume.setSelectedIndex(3);
                }

            }
        });



        buttonCritResultFilter.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                // отобразить интервалы и величины по фильтру ТМЦ выбранного заказа

                if (comboBoxCritVolumeOrderList.getSelectedIndex() > -1) {

                    List<rowGoodsOrders> rowsGOCritVolIntDatesFilter = dbExtractor.getGOCritVolIntDatesFilter(false, comboBoxCritVolumeOrderListIds.getItemAt(comboBoxCritVolumeOrderList.getSelectedIndex()).toString(),textFieldCritResultGoodsFilter.getText());

                    updateCritOrderGoodIntDatesEditAll(rowsGOCritVolIntDatesFilter);
                    tabbedPaneCritVolume.setSelectedIndex(3);
                }
            }
        });


        //
        //
        //  рекомендации по взаимодействию с поставщиками
        //
        //
        DefaultTableModel modelCContrasHistFull = new DefaultTableModel(
                new Object[][]{},
                new String[]{"Наименование поставщика", "Количество выполненных в срок поставок", "Количество не выполненных в срок поставок", "Среднее время задержки, день", "Процент не выполненных в срок поставок, %"}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Все ячейки нередактируемы
            }
        };
        tableContrasHistory.setModel(modelCContrasHistFull);

        DefaultTableModel modelCContrasHistEdit = new DefaultTableModel(
                new Object[][]{},
                new String[]{"Идентификатор поставщика", "Код поставщика", "Наименование поставщика", "Среднее время задержки, день", "Количество не выполненных в срок поставок", "Общее количество поставок"}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                if (column < 3)
                    return false; //  ячейки нередактируемы
                else return true;
            }
        };
        tableContrasHistoryEdit.setModel(modelCContrasHistEdit);

        buttonRecomContrasHistory.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // создать таблицу с дополнительными сведениями, если отсутствует
                dbExtractor.setContrasHistory(false);

                // установить соединение с БД модуля и получить данные

                tableContrasHistory.setModel(modelCContrasHistFull);

                List<rowContrasWithHistory> rowsContrasHist = dbExtractor.getContrasHist(false, textFieldRecomContrasFilter.getText());

                updateContrasHistoryFull(rowsContrasHist);
                tabbedPaneContrasRecommend.setSelectedIndex(0);
            }
        });

        buttonRecomContrasHistoryEdit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                // установить соединение с БД модуля и получить данные

                tableContrasHistoryEdit.setModel(modelCContrasHistEdit);

                List<rowContrasWithHistory> rowsContrasHist = dbExtractor.getContrasHistForEdit(false, textFieldRecomContrasFilter.getText());

                updateContrasHistoryEdit(rowsContrasHist);
                tabbedPaneContrasRecommend.setSelectedIndex(1);
            }
        });

        buttonContrasHistorySave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // получить данные из обновлённой таблицы на форме

                List<rowContrasWithHistory> rowsContrasHist = new ArrayList<>();

                for (int i = 0; i < tableContrasHistoryEdit.getRowCount(); i++) {
                    long c_id = Long.parseLong(tableContrasHistoryEdit.getValueAt(i, 0).toString());
                    String c_code = tableContrasHistoryEdit.getValueAt(i, 1).toString();
                    String c_name = tableContrasHistoryEdit.getValueAt(i, 2) != null && !tableContrasHistoryEdit.getValueAt(i, 2).toString().isEmpty()
                            ? tableContrasHistoryEdit.getValueAt(i, 2).toString()
                            : null; // Если значение пустое или null, присваиваем null, чтобы не менять данные
                    double c_avrdelay = Double.parseDouble(tableContrasHistoryEdit.getValueAt(i, 3).toString());
                    int c_fail_his = Integer.parseInt(tableContrasHistoryEdit.getValueAt(i, 4).toString());
                    int c_com_his = Integer.parseInt(tableContrasHistoryEdit.getValueAt(i, 5).toString()) - c_fail_his;
                    double n_percFailed = (c_com_his + c_fail_his == 0 ? 0 : c_fail_his / (0.0 + c_com_his + c_fail_his));
                    rowsContrasHist.add(new rowContrasWithHistory(c_id, c_name, 0,c_code, c_com_his, c_fail_his, c_avrdelay, n_percFailed));

                }

                dbExtractor.updateContrasHistForEdit(false, rowsContrasHist);
            }
        });

        DefaultTableModel modelRecContrasHistRes = new DefaultTableModel(
                new Object[][]{},
                new String[]{"Наименование поставщика", "Наименование ТМЦ", "Заявленный срок поставки", "Ожидаемый срок поставки"}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Все ячейки нередактируемы
            }
        };
        tableContrasHistResult.setModel(modelRecContrasHistRes);

        buttonRecomContrasResult.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // обновить данные о сроке доставки
                dbExtractor.updateLotFromHist(false);
                // отобразить обновленный срок доставки

                List<rowLotGoodsContras> rowsLotContrasHist = dbExtractor.getLotWithContrasHist(false, textFieldRecomContrasFilter.getText());

                updateContrasHistoryResult(rowsLotContrasHist);
                tabbedPaneContrasRecommend.setSelectedIndex(2);
            }
        });



        //
        //
        // оптимальный запас
        //
        //


        // начальные и конечные даты для интервала заказа
        JDateChooser dateChooserOptIntStart = new JDateChooser();
        JDateChooser dateChooserOptIntEnd = new JDateChooser();

        dateChooserOptIntStart.setDateFormatString("dd.MM.yyyy");
        dateChooserOptIntEnd.setDateFormatString("dd.MM.yyyy");
        panelOptInfoStartDate.setLayout(new BorderLayout());
        panelOptInfoStartDate.add(dateChooserOptIntStart, BorderLayout.CENTER);
        panelOptInfoEndDate.setLayout(new BorderLayout());
        panelOptInfoEndDate.add(dateChooserOptIntEnd, BorderLayout.CENTER);




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


        buttonOptOrderGoodsWithCrits.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // перейти на вкладку с запросом интервала

                tabbedPaneOptVolume.setSelectedIndex(0);
            }
        });

        buttonOptInfoShow.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // считать данные интервала и вывести критический запас

                Date s_date_int = dateChooserOptIntStart.getDate();
                Date e_date_int = dateChooserOptIntEnd.getDate();
                if (s_date_int == null) {
                    s_date_int = new Date();
                }
                s_date_int.setHours(0);
                s_date_int.setMinutes(0);
                s_date_int.setSeconds(0);

                if (e_date_int == null) {
                    e_date_int = new Date();
                }
                e_date_int.setHours(0);
                e_date_int.setMinutes(0);
                e_date_int.setSeconds(0);

                System.out.println(s_date_int);
                System.out.println(e_date_int);


                List<rowGoodsOrders> rowGOdates = dbExtractor.getGOBetweenDates(false, textFieldOptOrderFilter.getText(), s_date_int, e_date_int);
                tableOptVolumeInfoFiltered.setModel(modelCritDatesViews);

                updateOptOrderGoodIntDatesInfo(rowGOdates);
            }
        });


        buttonOptOrderGoodsConditionsTab.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // отобразить окно фильтрации для условий хранения
                textFieldOptOrderGoodsFilterName.setText("");
                comboBoxOrderSelectionName.removeAllItems();
                comboBoxOrderSelectionIds.removeAllItems();

                List<rowContrasGoodsOrders> rowsCGOCritVol = dbExtractor.getCGOCritVols(false, textFieldOptOrderFilter.getText());
                if (!rowsCGOCritVol.isEmpty()) {

                    for (rowContrasGoodsOrders rowsCGO : rowsCGOCritVol) {
                        comboBoxOrderSelectionName.addItem(rowsCGO.getOrderName());
                        comboBoxOrderSelectionIds.addItem(Long.toString(rowsCGO.getIdOrder()));
                    }
                }

                tabbedPaneOptVolume.setSelectedIndex(1);
            }
        });

        DefaultTableModel modelOptGoodsCondEdit = new DefaultTableModel(
                new Object[][]{},
                new String[]{"Идентификатор ТМЦ", "Номенклатурный номер ТМЦ", "Наименование ТМЦ", "Время хранения перед использованием, день"}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                if (column < 3)
                    return false; //  ячейки нередактируемы
                else return true;
            }
        };
        tableOptGoodsConditionsEdit.setModel(modelOptGoodsCondEdit);

        buttonOptGoodCondShow.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // получить данные о ТМЦ с учетом условий хранения
                if (comboBoxOrderSelectionName.getSelectedIndex() > -1 && comboBoxOrderSelectionIds.getSelectedIndex() > -1) {
                    List<rowGoods> rowsGoodCond = dbExtractor.getOptGoodConds(false, comboBoxOrderSelectionIds.getItemAt(comboBoxOrderSelectionName.getSelectedIndex()).toString(), textFieldOptOrderGoodsFilterName.getText());
                    updateGoodCondEdit(rowsGoodCond);
                }
            }
        });


        buttonOptGoodsCondEdit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // собрать данные с таблице о ТМЦ и обновить

                List<rowGoods> rowsGoodCond = new ArrayList<>();

                for (int i = 0; i < tableOptGoodsConditionsEdit.getRowCount(); i++) {
                    Long g_id = Long.parseLong(tableOptGoodsConditionsEdit.getValueAt(i, 0).toString());
                    String g_code = tableOptGoodsConditionsEdit.getValueAt(i, 1).toString();
                    String g_name = tableOptGoodsConditionsEdit.getValueAt(i, 2).toString();
                    int g_cond = Integer.parseInt(tableOptGoodsConditionsEdit.getValueAt(i, 3).toString());
                    rowsGoodCond.add(new rowGoods(g_id, g_name, g_cond,g_code,0,0,0,0,0,"",""));

                }

                dbExtractor.updateGoodsForEdit(false, rowsGoodCond);
            }
        });


        buttonOptOrderGoodsCells.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                // отобразить окно фильтрации для складских помещений
                textFieldOptOrderGoodsCellsFilterName.setText("");
                comboBoxOrderCellSelectionName.removeAllItems();
                comboBoxOrderCellsSelectionIds.removeAllItems();

                List<rowContrasGoodsOrders> rowsCGOCritVol = dbExtractor.getCGOCritVols(false, textFieldOptOrderFilter.getText());
                if (!rowsCGOCritVol.isEmpty()) {

                    for (rowContrasGoodsOrders rowsCGO : rowsCGOCritVol) {
                        comboBoxOrderCellSelectionName.addItem(rowsCGO.getOrderName());
                        comboBoxOrderCellsSelectionIds.addItem(Long.toString(rowsCGO.getIdOrder()));
                    }
                }

                // создать таблицы для ячеек, если их ещё нет

                // установить соединение с БД модуля и создать таблицу в случае её отсутствия)
                dbExtractor.setOrderCell(false);
                // установить соединение с БД модуля и создать таблицу в случае её отсутствия)
                dbExtractor.setGoodCell(false);

                tabbedPaneOptVolume.setSelectedIndex(2);
            }
        });

        DefaultTableModel modelOptCellView = new DefaultTableModel(
                new Object[][]{},
                new String[]{"Идентификатор ТМЦ", "Номенклатурный номер", "Наименование ТМЦ", "Суммарный объем складских помещений, ед.","ЕИ"}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Все ячейки нередактируемы
            }
        };
        tableOptCellsView.setModel(modelOptCellView);

        buttonOptOrderCellShow.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // отображение общего объема по ТМЦ

                if (comboBoxOrderCellSelectionName.getSelectedIndex() > -1 && comboBoxOrderCellsSelectionIds.getSelectedIndex() > -1) {
                    List<rowGoodsSumcells> rowsGoodSumCells = dbExtractor.getOptGoodSumCells(false, comboBoxOrderCellsSelectionIds.getItemAt(comboBoxOrderCellSelectionName.getSelectedIndex()).toString(), textFieldOptOrderGoodsCellsFilterName.getText());

                    List<rowGoodsSumcells> rowsGoodSumCellsFromStock = dbExtractor.getOptGoodSumCellsFromStock(false, comboBoxOrderCellsSelectionIds.getItemAt(comboBoxOrderCellSelectionName.getSelectedIndex()).toString(), textFieldOptOrderGoodsCellsFilterName.getText());
                    updateGoodCellSumView(rowsGoodSumCells,rowsGoodSumCellsFromStock);
                }
            }
        });

        DefaultTableModel modelOptCellUpdate = new DefaultTableModel(
                new Object[][]{},
                new String[]{"Ид. ТМЦ", "Номенкл. номер", "Наименование ТМЦ", "Код ячейки склад. помещения","Наименование склада", "Пригодные габариты склад. помещений","Габариты ТМЦ", "Отношение объемов"}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Все ячейки нередактируемы
            }
        };
        tableOptGoodCellData.setModel(modelOptCellUpdate);

        buttonOptOrderCellEdit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                // формирование ТМЦ на новой вкладке
                if (comboBoxOrderCellSelectionName.getSelectedIndex() > -1 && comboBoxOrderCellsSelectionIds.getSelectedIndex() > -1) {

                    labelOptOrderSelectedName.setText(comboBoxOrderCellSelectionName.getSelectedItem().toString());
                    comboBoxGoodCellSelectionName.removeAllItems();
                    comboBoxGoodCellSelectionIds.removeAllItems();
                    comboBoxCellCellSelectionIds.removeAllItems();

                    List<rowGoodsOrders> rowsCGOCritVolDisGood = dbExtractor.getCGOCritVolDisGoods(false, comboBoxOrderCellsSelectionIds.getItemAt(comboBoxOrderCellSelectionName.getSelectedIndex()).toString());
                    if (!rowsCGOCritVolDisGood.isEmpty()) {

                        for (rowGoodsOrders rowsGO : rowsCGOCritVolDisGood) {
                            comboBoxGoodCellSelectionName.addItem(rowsGO.getGoodName());
                            comboBoxGoodCellSelectionIds.addItem(Long.toString(rowsGO.getIdGood()));
                        }
                    }

                    // Отобразить все возможные ячейки склада
                    List<rowStockCells> rowsAllCellCodes = dbExtractor.getAllCells(false);
                    if (!rowsAllCellCodes.isEmpty()) {

                        for (rowStockCells rowsGO : rowsAllCellCodes) {
                            comboBoxCellCellSelectionIdss.addItem(Long.toString(rowsGO.getIdCell()));
                            comboBoxCellCellSelectionIds.addItem(rowsGO.getCellCode());    //  addItem(Long.toString(rowsGO.getIdGood()))
                        }
                    }

                    List<rowGoodsCells> rowsGoodCells = dbExtractor.getOptGoodCells(false, comboBoxOrderCellsSelectionIds.getItemAt(comboBoxOrderCellSelectionName.getSelectedIndex()).toString());
                    List<rowGoodsCells> rowsGoodCellsFromInput = dbExtractor.getOptGoodCellsFromInput(false, comboBoxOrderCellsSelectionIds.getItemAt(comboBoxOrderCellSelectionName.getSelectedIndex()).toString());

                    updateGoodCellData(rowsGoodCells,rowsGoodCellsFromInput);


                    tabbedPaneOptVolume.setSelectedIndex(3);
                }
            }
        });


        buttonOptGoodCellUpdate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // все комбобоксы должны быть выбраны
                if (comboBoxOrderCellSelectionName.getSelectedIndex() > -1 && comboBoxOrderCellsSelectionIds.getSelectedIndex() > -1) {
                    if (comboBoxGoodCellSelectionName.getSelectedIndex() > -1 && comboBoxGoodCellSelectionIds.getSelectedIndex() > -1) {
                        if (comboBoxCellCellSelectionIds.getSelectedIndex() > -1) {
                            // установить переменные
                            double cellOrderWeight;
                            if (Objects.equals(textFieldOptOrderCellWeight.getText(), ""))
                                cellOrderWeight = 1;
                            else
                                cellOrderWeight = Double.parseDouble(textFieldOptOrderCellWeight.getText());
                            double goodInCellQty = Double.parseDouble(textFieldOptOrderCellGoodQty.getText());
                            // обновить таблицу заказ - объем
                            dbExtractor.updateOrderCell(false, comboBoxOrderCellsSelectionIds.getItemAt(comboBoxOrderCellSelectionName.getSelectedIndex()).toString(), comboBoxCellCellSelectionIdss.getItemAt(comboBoxCellCellSelectionIds.getSelectedIndex()).toString(), cellOrderWeight);
                            // обновить таблицу тмц - объем
                            dbExtractor.updateGoodCell(false, comboBoxGoodCellSelectionIds.getItemAt(comboBoxGoodCellSelectionName.getSelectedIndex()).toString(), comboBoxCellCellSelectionIdss.getItemAt(comboBoxCellCellSelectionIds.getSelectedIndex()).toString(), goodInCellQty);
                            // обновить отображение

                            List<rowGoodsCells> rowsGoodCells = dbExtractor.getOptGoodCells(false, comboBoxOrderCellsSelectionIds.getItemAt(comboBoxOrderCellSelectionName.getSelectedIndex()).toString());
                            List<rowGoodsCells> rowsGoodCellsFromInput = dbExtractor.getOptGoodCellsFromInput(false, comboBoxOrderCellsSelectionIds.getItemAt(comboBoxOrderCellSelectionName.getSelectedIndex()).toString());
                            updateGoodCellData(rowsGoodCells,rowsGoodCellsFromInput);

                        }
                    }
                }
            }
        });

        DefaultTableModel modelOptDatesViews = new DefaultTableModel(
                new Object[][]{},
                new String[]{"Наименование заказа", "Наименование ТМЦ", "Количество ТМЦ, ед.","На складе, ед.", "ЕИ", "Дата начала интервала", "Дата конца интервала"}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Все ячейки нередактируемы
            }
        };
        tableOptVolResult.setModel(modelOptDatesViews);

        buttonOptOrderResult.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // получить оптимальные запасы отфильтрованных закозов с условиями хранения

                List<rowGoodsOrdersOpt> rowsGoodOrdersOpt = dbExtractor.getGOAllDates(false, textFieldOptOrderFilter.getText());
                updateGoodOrderData(rowsGoodOrdersOpt);

                // обработать интервалы

                tabbedPaneOptVolume.setSelectedIndex(4);
            }
        });

        DefaultTableModel modelOptDatesViewsPurchase = new DefaultTableModel(
                new Object[][]{},
                new String[]{"Наименование заказа", "Наименование ТМЦ", "Количество закупаемого ТМЦ, ед.", "ЕИ", "Наименование поставщика", "Дата закупки", "Цена", "Стоимость"}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Все ячейки нередактируемы
            }
        };
        tableOptVolResultPurchase.setModel(modelOptDatesViewsPurchase);

        buttonOptOrderResultPurchase.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // получить лучших поставщиков для оптимальных запасов

                List<rowGoodsOrdersContrasOpt> rowsGoodOrdersOptPurchase = dbExtractor.getGOAllDatesWithContras(false, textFieldOptOrderFilter.getText());
                updateGoodOrderDataPurch(rowsGoodOrdersOptPurchase);

                tabbedPaneOptVolume.setSelectedIndex(5);
            }
        });

        setContentPane(MainPanel);
        setTitle("Модуль анализа поставщиков");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1250, 700);
        setLocationRelativeTo(null);
        setVisible(true);


        //Раздел парсинга

        ThreadCount.addItem(2);
        ThreadCount.addItem(4);
        ThreadCount.addItem(6);
        ThreadCount.addItem(8);

        statusForm = new StatusForm();
        statusForm.setStatusLabel(StatusLabel);
        statusForm.setCurrentRecords(CurrentRecords);
        statusForm.setTotalRecords(TotalRecords);
        statusForm.setHeadersTable(HeadersTable);
        QueryButton.addActionListener(e -> onQueryButtonClicked());
        StartParsing.addActionListener(e -> initStartParsingButton());
        ChooseAllElements.addActionListener(e -> initChooseAllElementsButton());
        ChooseRange.addActionListener(e -> initChooseRangeElementsButton());
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
        initHeadersTable();
        customizeTableRenderers();
        try {
            Okpd2Converter.loadFromJson("resources/okpd2_full.json");
        } catch (IOException e) {
            System.err.println("Ошибка загрузки файла ОКПД2: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Не удалось загрузить справочник ОКПД2",
                    "Ошибка",
                    JOptionPane.ERROR_MESSAGE);
        }
//        Эти методы для стилей таблицы, не трогать без необходимости
//        configureTableColumns();
//      initTableWithScroll();



    }

    private void updateTableContras(List<rowContras> rowsC) {
        if (tableContrasGoodsOrders != null) {
            clearTable(tableContrasGoodsOrders);

            DefaultTableModel modelContras = (DefaultTableModel) tableContrasGoodsOrders.getModel();

            for (rowContras rowC : rowsC) {
                modelContras.addRow(new Object[]{
                        rowC.getContrasCode(),
                        rowC.getContrasName(),
                        rowC.getInn(),
                        formatValue(rowC.getContrasReputation())
                });
            }

            tableContrasGoodsOrders.setCellSelectionEnabled(true);
            tableContrasGoodsOrders.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

            // Включаем сортировку только после добавления данных
            enableSortingForTable(tableContrasGoodsOrders, 2);
        } else {
            System.out.println("Ошибка: таблица не инициализирована.");
        }
    }




    private void updateTableContrasGoodsOrders(List<rowContrasGoodsOrders> rowsCGO) {
        // Проверка на null перед очисткой таблицы
        if (tableContrasGoodsOrders != null) {
            clearTable(tableContrasGoodsOrders);

            DefaultTableModel modelContrasGoodsOrders = (DefaultTableModel) tableContrasGoodsOrders.getModel();

            // Заполняем таблицу новыми данными
            for (rowContrasGoodsOrders rowCGO : rowsCGO) {
                modelContrasGoodsOrders.addRow(new Object[]{
                        rowCGO.getOrderName(),
                        rowCGO.getContrasName(),
                        rowCGO.getGoodName(),
                        formatValue(rowCGO.getDeliveryTime()),
                        formatValue(rowCGO.getMinVolume()),
                        rowCGO.getGoodMeasure()
                });
            }

            // Включаем сортировку для таблицы
            try {
                enableSortingForTable(tableContrasGoodsOrders, 3, 4);  // Указываем числовые колонки для сортировки
            } catch (Exception e) {
                System.err.println("Ошибка при применении сортировки: " + e.getMessage());
            }
        } else {
            System.out.println("Ошибка: таблица не инициализирована.");
        }
    }




    private void updateTableCritGCViewValues(List<rowContrasGoodsOrders> rowsCGO, long CritId) {
        if (tableCrit != null) {
            // Очищаем таблицу перед обновлением данных
            clearTable(tableCrit);

            DefaultTableModel modelContrasGoodsOrders = (DefaultTableModel) tableCrit.getModel();

            for (rowContrasGoodsOrders rowCGO : rowsCGO) {
                String formattedValue;

                // Определяем, какое значение нужно отформатировать в зависимости от CritId
                if (CritId == 0) {
                    formattedValue = formatValue(rowCGO.getDeliveryTime());
                } else if (CritId == 1) {
                    formattedValue = formatValue(rowCGO.getMinVolume());
                } else if (CritId == 2) {
                    formattedValue = formatValue(rowCGO.getGoodQuality());
                } else if (CritId == 3) {
                    formattedValue = formatValue(rowCGO.getContrasReputation());
                } else {
                    continue; // Пропускаем итерацию, если CritId не соответствует ни одному условию
                }

                // Добавляем строку с отформатированным значением
                modelContrasGoodsOrders.addRow(new Object[]{
                        rowCGO.getOrderName(),
                        rowCGO.getContrasName(),
                        rowCGO.getGoodName(),
                        formattedValue
                });
            }

            // Включаем сортировку только после обновления данных
            try {
                enableSortingForTable(tableCrit, 3); // Сортируем по четвертому столбцу (индекс 3)
            } catch (Exception e) {
                System.err.println("Ошибка при применении сортировки: " + e.getMessage());
            }
        } else {
            System.out.println("Ошибка: tableCrit не инициализирована.");
        }
    }






    private void updateTableCritGCViewUserValues(List<rowContrasGoodsOrders> rowsCGO, long CritId) {
        if (tableCrit != null) {
            // Очищаем таблицу перед обновлением данных
            clearTable(tableCrit);

            DefaultTableModel modelContrasGoodsOrders = (DefaultTableModel) tableCrit.getModel();

            for (rowContrasGoodsOrders rowCGO : rowsCGO) {
                modelContrasGoodsOrders.addRow(new Object[]{
                        rowCGO.getIdContras(),
                        rowCGO.getContrasCode(),
                        rowCGO.getContrasName(),
                        rowCGO.getIdGood(),
                        rowCGO.getGoodCode(),
                        rowCGO.getGoodName(),
                        formatValue(rowCGO.getMinVolume())
                });
            }

            // Делаем возможным выбор отдельных ячеек
            tableCrit.setCellSelectionEnabled(true);
            tableCrit.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);//SINGLE_SELECTION

            // Включаем сортировку после обновления данных
            try {
                enableSortingForTable(tableCrit, 3, 4, 6); // Указываем числовые колонки для сортировки
            } catch (Exception e) {
                System.err.println("Ошибка при применении сортировки: " + e.getMessage());
            }
        } else {
            System.out.println("Ошибка: tableCrit не инициализирована.");
        }
    }



    private void updateCritValues() {
        List<rowCritValues> rowsCrVa;
        if (CritId == 3) {
            rowsCrVa = dbExtractor.getCrVas(false, CritString, CritShort, textFieldFilterContras.getText(), textFieldFilterGood.getText(), "", textFieldFilterDate.getText(), textFieldFilterMinVolume.getText(), textFieldFilterOkpd2.getText());
        } else {
            rowsCrVa = dbExtractor.getUserCrVas(false, CritString, CritShort, textFieldFilterContras.getText(), textFieldFilterGood.getText(), "", textFieldFilterDate.getText(), textFieldFilterMinVolume.getText(), textFieldFilterOkpd2.getText());
        }
        // установить соединение с БД модуля и создать таблицу критериев в случае её отсутствия)
        dbExtractor.setCritData(false);
        // проверить наличие критерия в таблице
        // установить соединение с БД модуля и получить данные о текущем критерии
        List<rowCritData> rowsCrData = dbExtractor.getCritData(false, CritId);
        // обновить веса в списке значений, если критерий найден
        if (!rowsCrData.isEmpty()) {
            rowCritData rowsCrD = rowsCrData.get(0);

            if (rowsCrD.getCritFunction() < 4) {
                // Одна из фиксированных функций
                double minV = rowsCrD.getMinVal();
                double maxV = rowsCrD.getMaxVal();
                int funType = rowsCrD.getCritFunction();

                for (rowCritValues rowsCrV : rowsCrVa) {
                    double crW = calcWeight(rowsCrV.getCritVal(), minV, maxV, funType);
                    rowsCrV.setCritWeight(crW);
                }
            } else {
                // Произвольная функция
                List<rowCritValues> dataPointsSaved = new ArrayList<>();
                JSONObject pointsJSON = new JSONObject(rowsCrD.getJsonDataPoints());
                for (int i = 0; i < pointsJSON.length(); i++) {
                    String iKey = (JSONObject.getNames(pointsJSON))[i];
                    dataPointsSaved.add(new rowCritValues(Double.parseDouble(iKey), pointsJSON.getDouble(iKey)));
                    //
                }
                dataPointsSaved.sort(Comparator.comparingDouble(rowCritValues::getCritVal));

                // calcUserFunctionWeight(rowsCrD.getJsonDataPoints());
                rowsCrVa = dataPointsSaved;
            }

        }

        updateTableCritValues(rowsCrVa);
        if (!rowsCrVa.isEmpty()) {
            rowsCrVa.sort(Comparator.comparingDouble(rowCritValues::getCritVal)); // Сортируем список
            labelCritMinValue.setText(Double.toString(rowsCrVa.get(0).getCritVal())); // Минимум
            labelCritMaxValue.setText(Double.toString(rowsCrVa.get(rowsCrVa.size() - 1).getCritVal())); // Максимум
        } else {
            labelCritMaxValue.setText("");
            labelCritMinValue.setText("");
        }

        tabbedPaneMain.setSelectedIndex(1);
        System.out.println("Button pressed");
    }

    private void updateTableCritValues(List<rowCritValues> rowsCrVas) {
        if (tableCrit != null) {
            // Очищаем таблицу перед обновлением данных
            clearTable(tableCrit);

            DefaultTableModel modelCrit = (DefaultTableModel) tableCrit.getModel();

            for (rowCritValues rowsCrVa : rowsCrVas) {
                modelCrit.addRow(new Object[]{
                        formatValue(rowsCrVa.getCritVal()),   // Форматируем critVal с помощью formatValue
                        formatValue(rowsCrVa.getCritWeight()) // Форматируем critWeight с помощью formatValue
                });
            }

            // Включаем сортировку только после обновления данных
            try {
                enableSortingForTable(tableCrit, 0, 1); // Разрешаем сортировку по обоим столбцам
            } catch (Exception e) {
                System.err.println("Ошибка при применении сортировки: " + e.getMessage());
            }
        } else {
            System.out.println("Ошибка: tableCrit не инициализирована.");
        }
    }


    private void updateTableCritEditValues(List<rowCritValues> rowsCrVas) {
        if (tableCritEdit != null) {
            // Очищаем таблицу перед обновлением данных
            clearTable(tableCritEdit);

            DefaultTableModel modelCrit = (DefaultTableModel) tableCritEdit.getModel();

            for (rowCritValues rowsCrVa : rowsCrVas) {
                // Получаем отформатированные значения как строки
                String critVal = formatValue(rowsCrVa.getCritVal());
                String critWeight = formatValue(rowsCrVa.getCritWeight());

                modelCrit.addRow(new Object[]{
                        critVal,    // Теперь это String
                        critWeight  // Теперь это String
                });
            }

            // Включаем сортировку только после обновления данных
            try {
                enableSortingForTable(tableCritEdit, 0, 1); // Сортировка по обеим колонкам
            } catch (Exception e) {
                System.err.println("Ошибка при применении сортировки: " + e.getMessage());
            }
        } else {
            System.out.println("Ошибка: tableCritEdit не инициализирована.");
        }
    }

    private List<rowCritValues> updateTableCritEditValues(double minVal, double maxVal, int funType) {
        List<rowCritValues> rowsCrVas = new ArrayList<>();

        if (tableCritEdit != null) {
            DefaultTableModel modelCrit = (DefaultTableModel) tableCritEdit.getModel();

            for (int i = 0; i < modelCrit.getRowCount(); i++) {
                try {
                    double crVal = Double.parseDouble(modelCrit.getValueAt(i, 0).toString());
                    // Вычисляем вес
                    double crW = calcWeight(crVal, minVal, maxVal, funType);
                    String formattedCrW = formatValue(crW);

                    // Добавляем отформатированные значения в список
                    rowsCrVas.add(new rowCritValues(crVal, Double.parseDouble(formattedCrW)));
                    modelCrit.setValueAt(formattedCrW, i, 1); // Обновляем значение во втором столбце как строку
                } catch (NumberFormatException e) {
                    System.err.println("Ошибка преобразования значения в строке " + i + ": " + e.getMessage());
                }
            }

            // Включаем сортировку после обновления данных
            try {
                enableSortingForTable(tableCritEdit, 0, 1);
            } catch (Exception e) {
                System.err.println("Ошибка при применении сортировки: " + e.getMessage());
            }
        } else {
            System.out.println("Ошибка: tableCritEdit не инициализирована.");
        }

        return rowsCrVas;
    }



    private double calcWeight(double curV, double minV, double maxV, int funT) {
        if (funT == 0) {
            // линейная функция
            if (curV < minV)
                return 0;
            else if (curV > maxV)
                return 1;
            else
                return ((curV - minV) / (maxV - minV));
        } else if (funT == 1) {
            // обратная линейная функция
            if (curV < minV)
                return 1;
            else if (curV > maxV)
                return 0;
            else
                return (1 - (curV - minV) / (maxV - minV));
        } else if (funT == 2) {
            // s-функция
            if (curV < minV) {
                return 0.0;
            } else if (minV <= curV && curV < (minV + maxV) / 2) {
                return 2 * Math.pow((curV - minV) / (maxV - minV), 2);
            } else if ((minV + maxV) / 2 <= curV && curV < maxV) {
                return (1 - 2 * Math.pow((curV - maxV) / (maxV - minV), 2));
            } else if (curV >= maxV) {
                return 1.0;
            }
        } else if (funT == 3) {
            // z-функция
            if (curV < minV) {
                return 1.0;
            } else if (minV <= curV && curV < (minV + maxV) / 2) {
                return (1 - 2 * Math.pow((curV - minV) / (maxV - minV), 2));
            } else if ((minV + maxV) / 2.0 <= curV && curV < maxV) {
                return 2 * Math.pow((curV - maxV) / (maxV - minV), 2);
            } else if (curV >= maxV) {
                return 0.0;
            }
        }
        return 0;
    }


    private void initHeadersTable() {
        // Создаем модель с правильными названиями столбцов
        String[] columnNames = {"№", "Номер закупки", "Информация по закупке", "Выбор"};

        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 3 ? Boolean.class : String.class;
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3;
            }
        };

        HeadersTable.setModel(model);
        // Добавляем слушатель изменений
        HeadersTable.getModel().addTableModelListener(e -> {
            if (e.getColumn() == 3) {
                boolean isSelected = (boolean) HeadersTable.getModel().getValueAt(e.getFirstRow(), 3);
                PurchaseItem item = statusForm.allItems.get(e.getFirstRow());

                if (isSelected) {
                    statusForm.selectedUrls.add(item.getUrl());
//                    System.out.println("Добавлена ссылка: " + item.getUrl());
//                    System.out.println("Полная информация: " + item.toString());
                } else {
                    statusForm.selectedUrls.remove(item.getUrl());
//                    System.out.println("Удалена ссылка: " + item.getUrl());
                }

//                System.out.println("Текущий список выбранных ссылок: " + statusForm.selectedUrls);
            }
        });
        HeadersTable.setRowHeight(60); // Начальная высота строки
    }
    private void initTableWithScroll() {
        tablePanel.setLayout(new BorderLayout());
        JScrollPane scrollPane = new JScrollPane(HeadersTable);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setViewportBorder(BorderFactory.createEmptyBorder());
        JViewport viewport = scrollPane.getViewport();
        viewport.setOpaque(false);
        viewport.setBorder(null);
        HeadersTable.setFillsViewportHeight(true);
        tablePanel.removeAll();
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        tablePanel.revalidate();
        tablePanel.repaint();
    }
    private void configureTableColumns() {
        // Устанавливаем режим автоматического изменения размера
        HeadersTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        TableColumnModel columnModel = HeadersTable.getColumnModel();

        // Настройка каждого столбца
        columnModel.getColumn(0).setPreferredWidth(50);  // №
        columnModel.getColumn(0).setMaxWidth(80);

        columnModel.getColumn(1).setPreferredWidth(150); // Номер закупки
        columnModel.getColumn(1).setMaxWidth(200);

        // Основной столбец с информацией
        columnModel.getColumn(2).setPreferredWidth(400);
        columnModel.getColumn(2).setCellRenderer(new MultiLineCellRenderer());

        // Чекбоксы
        columnModel.getColumn(3).setPreferredWidth(60);
        columnModel.getColumn(3).setMaxWidth(80);

        // Включаем заполнение всего доступного пространства
        HeadersTable.setFillsViewportHeight(true);
    }
    private void customizeTableRenderers() {
        // Рендерер для столбца с разделенным текстом
        HeadersTable.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {

                String text = value != null ? value.toString() : "";
                String[] parts = splitTextInHalf(text);

                // HTML для отображения в две строки
                String htmlText = "<html><div style='text-align: center;'>" +
                        parts[0] + "<br>" + parts[1] + "</div></html>";

                return super.getTableCellRendererComponent(table, htmlText,
                        isSelected, hasFocus, row, column);
            }
        });
    }

    // Метод для разделения текста пополам
    private String[] splitTextInHalf(String text) {
        if (text == null || text.isEmpty()) return new String[]{"", ""};

        int mid = text.length() / 2;
        while (mid < text.length() && !Character.isWhitespace(text.charAt(mid))) {
            mid++;
        }
        return new String[]{
                text.substring(0, mid).trim(),
                text.substring(mid).trim()
        };
    }
    private double calcWeightDataPoints(double curV, List<rowCritValues> dataPoints) {

        if (!dataPoints.isEmpty()) {
            if (curV < dataPoints.get(0).getCritVal())
                return dataPoints.get(0).getCritWeight();
            if (curV > dataPoints.get(dataPoints.size() - 1).getCritVal())
                return dataPoints.get(dataPoints.size() - 1).getCritWeight();
            else {
                for (int i = 1; i < dataPoints.size(); i++) {
                    double minV = dataPoints.get(i - 1).getCritVal();
                    double maxV = dataPoints.get(i).getCritVal();
                    double minW = dataPoints.get(i - 1).getCritWeight();
                    double maxW = dataPoints.get(i).getCritWeight();
                    if ((curV >= minV) && (curV <= maxV)) {
                        return (minW + (curV - minV) * (maxW - minW) / (maxV - minV));
                    }
                }
            }
        }
        return 0;
    }

    private void updateTableContrasGoodsOrdersWes(List<rowContrasGoodsOrdersWithWeights> rowsCGOws) {
        if (tableRating != null) {
            // Очищаем таблицу перед обновлением данных
            clearTable(tableRating);

            DefaultTableModel modelContrasGoodsOrdersWeights = (DefaultTableModel) tableRating.getModel();

            for (rowContrasGoodsOrdersWithWeights rowCGOw : rowsCGOws) {
                modelContrasGoodsOrdersWeights.addRow(new Object[]{
                        rowCGOw.getOrderName(),
                        rowCGOw.getContrasName(),
                        rowCGOw.getGoodName(),
                        formatValue(rowCGOw.getDeliveryTime()),
                        formatValue(rowCGOw.getMinVolume()),
                        formatValue(rowCGOw.getGoodQuality()),
                        formatValue(rowCGOw.getContrasReputation()),
                        formatValue(rowCGOw.getRatingComplete())
                });
            }

            // Включаем сортировку после обновления данных
            try {
                enableSortingForTable(tableRating, 3, 4, 5, 6, 7); // Указываем числовые столбцы для корректной сортировки
            } catch (Exception e) {
                System.err.println("Ошибка при применении сортировки: " + e.getMessage());
            }
        } else {
            System.out.println("Ошибка: tableRating не инициализирована.");
        }
    }




    private String formatValue(double value) {
        // Форматируем значение: если целое, то без десятичных знаков, иначе с тремя знаками после запятой
        String formattedValue = (value == Math.floor(value))
                ? String.format(Locale.US, "%.0f", value)  // Если целое, выводим без десятичных знаков
                : String.format(Locale.US, "%.3f", value); // Если дробное, выводим с тремя знаками после запятой

        // Принудительно заменяем запятую на точку
        return formattedValue.replace(",", ".");
    }




    private void updateTableContrasGoodsOrdersWesBest(List<rowContrasGoodsOrdersWithWeights> rowsCGOws) {
        // Проверка, что таблица существует
        if (tableRating != null) {
            clearTable(tableRating); // Очищаем таблицу перед обновлением данных

            DefaultTableModel modelContrasGoodsOrdersWeights = (DefaultTableModel) tableRating.getModel();

            // Проверяем, что список не пустой, чтобы избежать IndexOutOfBoundsException
            if (!rowsCGOws.isEmpty()) {
                double max_rat = rowsCGOws.get(0).getRatingComplete();

                for (rowContrasGoodsOrdersWithWeights rowCGOw : rowsCGOws) {
                    if (rowCGOw.getRatingComplete() < max_rat) {
                        break;
                    }
                    modelContrasGoodsOrdersWeights.addRow(new Object[]{
                            rowCGOw.getOrderName(),
                            rowCGOw.getContrasName(),
                            rowCGOw.getGoodName(),
                            formatValue(rowCGOw.getRatingComplete())
                    });
                }
            }

            // Включаем сортировку
            try {
                enableSortingForTable(tableRating, 3); // Указываем индекс числового столбца для корректной сортировки
            } catch (Exception e) {
                System.err.println("Ошибка при применении сортировки: " + e.getMessage());
            }
        } else {
            System.out.println("Ошибка: tableRating не инициализирована.");
        }
    }




    private void updateCritOrderGoodValues(List<rowGoodsOrders> rowsGOs) {
        if (tableCritOrderGoods != null) {
            clearTable(tableCritOrderGoods);
            DefaultTableModel modelGoodsOrders = (DefaultTableModel) tableCritOrderGoods.getModel();

            for (rowGoodsOrders rowGO : rowsGOs) {
                modelGoodsOrders.addRow(new Object[]{
                        rowGO.getOrderName(),
                        rowGO.getGoodName(),
                        formatValue(rowGO.getGoodQuantity()),
                        rowGO.getGoodMeasure()
                });
            }

            enableSortingForTable(tableCritOrderGoods, 2); // Сортировка по количеству товара
        } else {
            System.out.println("Ошибка: tableCritOrderGoods не инициализирована.");
        }
    }

    private void updateCritOrderGoodParams(List<rowContrasGoodsOrders> rowsCGOs) {
        if (tableCritOrderGoods != null) {
            clearTable(tableCritOrderGoods);
            DefaultTableModel modelCGoodsOrders = (DefaultTableModel) tableCritOrderGoods.getModel();

            for (rowContrasGoodsOrders rowCGO : rowsCGOs) {
                modelCGoodsOrders.addRow(new Object[]{
                        rowCGO.getOrderName(),
                        rowCGO.getGoodName(),
                        rowCGO.getContrasName(),
                        formatValue(rowCGO.getDeliveryTime()),
                        formatValue(rowCGO.getMinVolume()),
                        rowCGO.getGoodMeasure()
                });
            }

            enableSortingForTable(tableCritOrderGoods, 3, 4); // Сортировка по DeliveryTime и MinVolume
        } else {
            System.out.println("Ошибка: tableCritOrderGoods не инициализирована.");
        }
    }

    private void updateCritOrderGoodIntDates(List<rowGoodsOrders> rowsGOs) {
        if (tableCritDateIntervals != null) {
            clearTable(tableCritDateIntervals);
            DefaultTableModel modelGoodsOrders = (DefaultTableModel) tableCritDateIntervals.getModel();

            for (rowGoodsOrders rowGO : rowsGOs) {
                modelGoodsOrders.addRow(new Object[]{
                        rowGO.getOrderName(),
                        rowGO.getGoodName(),
                        formatValue(rowGO.getGoodQuantity()),
                        rowGO.getGoodMeasure(),
                        rowGO.getStartDatePlan(),
                        rowGO.getEndDatePlan()
                });
            }

            enableSortingForTable(tableCritDateIntervals, 2); // Сортировка по количеству товара
        } else {
            System.out.println("Ошибка: tableCritDateIntervals не инициализирована.");
        }
    }

    private void updateCritOrderGoodIntDatesEdit(List<rowGoodsOrders> rowsGOs) {
        if (tableCritDateIntervalsEdit != null) {
            clearTable(tableCritDateIntervalsEdit);
            DefaultTableModel modelGoodsOrders = (DefaultTableModel) tableCritDateIntervalsEdit.getModel();

            for (rowGoodsOrders rowGO : rowsGOs) {
                modelGoodsOrders.addRow(new Object[]{
                        rowGO.getOrderName(),
                        rowGO.getGoodName(),
                        formatValue(rowGO.getGoodQuantity()),
                        rowGO.getGoodMeasure(),
                        rowGO.getStartDatePlan(),
                        rowGO.getEndDatePlan()
                });
            }

            enableSortingForTable(tableCritDateIntervalsEdit, 2); // Сортировка по количеству товара
        } else {
            System.out.println("Ошибка: tableCritDateIntervalsEdit не инициализирована.");
        }
    }

    private void updateCritOrderGoodIntDatesEditAll(List<rowGoodsOrders> rowsGOs) {
        if (tableCritDatesResult != null) {
            clearTable(tableCritDatesResult);
            DefaultTableModel modelGoodsOrders = (DefaultTableModel) tableCritDatesResult.getModel();

            for (rowGoodsOrders rowGO : rowsGOs) {
                modelGoodsOrders.addRow(new Object[]{
                        rowGO.getOrderName(),
                        rowGO.getGoodName(),
                        formatValue(rowGO.getGoodQuantity()),
                        rowGO.getGoodMeasure(),
                        rowGO.getStartDatePlan(),
                        rowGO.getEndDatePlan()
                });
            }

            enableSortingForTable(tableCritDatesResult, 2); // Сортировка по количеству товара
        } else {
            System.out.println("Ошибка: tableCritDatesResult не инициализирована.");
        }
    }



    private void updateOptOrderGoodIntDatesInfo(List<rowGoodsOrders> rowsGOs) {
        if (tableOptVolumeInfoFiltered != null) {
            DefaultTableModel modelGoodsOrders = (DefaultTableModel) tableOptVolumeInfoFiltered.getModel();
            modelGoodsOrders.setRowCount(0); // Очищаем таблицу перед добавлением новых данных

            for (rowGoodsOrders rowGO : rowsGOs) {
                modelGoodsOrders.addRow(new Object[]{
                        rowGO.getOrderName(),
                        rowGO.getGoodName(),
                        formatValue(rowGO.getGoodQuantity()),
                        rowGO.getGoodMeasure(),
                        rowGO.getStartDatePlan(),
                        rowGO.getEndDatePlan()
                });
            }

            enableSortingForTable(tableOptVolumeInfoFiltered, 2); // Сортировка по количеству товара
        } else {
            System.out.println("Ошибка: tableOptVolumeInfoFiltered не инициализирована.");
        }
    }

    private void updateContrasHistoryFull(List<rowContrasWithHistory> rowsCHs) {
        if (tableContrasHistory != null) {
            DefaultTableModel modelGoodsOrders = (DefaultTableModel) tableContrasHistory.getModel();
            modelGoodsOrders.setRowCount(0); // Очищаем таблицу перед добавлением новых данных

            for (rowContrasWithHistory rowCH : rowsCHs) {
                modelGoodsOrders.addRow(new Object[]{
                        rowCH.getContrasName(),
                        formatValue(rowCH.getNumCompleted()),
                        formatValue(rowCH.getNumFailed()),
                        formatValue(rowCH.getAvrDelay()),
                        formatValue(rowCH.getPercentFailed())
                });
            }

            enableSortingForTable(tableContrasHistory, 1, 2, 3, 4); // Сортировка по NumCompleted и NumFailed
        } else {
            System.out.println("Ошибка: tableContrasHistory не инициализирована.");
        }
    }


    private void updateContrasHistoryEdit(List<rowContrasWithHistory> rowsCHs) {
        if (tableContrasHistoryEdit != null) {
            DefaultTableModel modelGoodsOrders = (DefaultTableModel) tableContrasHistoryEdit.getModel();
            modelGoodsOrders.setRowCount(0); // Очищаем таблицу перед добавлением новых данных

            for (rowContrasWithHistory rowCH : rowsCHs) {
                modelGoodsOrders.addRow(new Object[]{
                        rowCH.getIdContras(),
                        rowCH.getContrasCode(),
                        rowCH.getContrasName(),
                        formatValue(rowCH.getAvrDelay()),
                        formatValue(rowCH.getNumFailed()),
                        formatValue(rowCH.getNumCompleted() + rowCH.getNumFailed())
                });
            }

            // Делаем возможным выбор отдельных ячеек
            tableContrasHistoryEdit.setCellSelectionEnabled(true);
            tableContrasHistoryEdit.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

            enableSortingForTable(tableContrasHistoryEdit, 0,1,3, 4,5); // Сортировка по AvrDelay и NumFailed
        } else {
            System.out.println("Ошибка: tableContrasHistoryEdit не инициализирована.");
        }
    }


    private void updateContrasHistoryResult(List<rowLotGoodsContras> rowsCHs) {
        DefaultTableModel modelGoodsOrders = (DefaultTableModel) tableContrasHistResult.getModel();
        modelGoodsOrders.setRowCount(0); // Очищаем таблицу перед добавлением новых данных

        for (rowLotGoodsContras rowCH : rowsCHs) {
            modelGoodsOrders.addRow(new Object[]{
                    rowCH.getContrasName(),
                    rowCH.getGoodName(),
                    formatValue(rowCH.getDeliveryTime()),
                    formatValue(rowCH.getDeliveryTime() + rowCH.getContrasDelay())
            });
        }

        enableSortingForTable(tableContrasHistResult, 2, 3); // Сортировка по DeliveryTime и суммарному времени
    }

    private void updateGoodCondEdit(List<rowGoods> rowsCHs) {
        DefaultTableModel modelGoodsOrders = (DefaultTableModel) tableOptGoodsConditionsEdit.getModel();
        modelGoodsOrders.setRowCount(0); // Очищаем таблицу перед добавлением новых данных

        for (rowGoods rowCH : rowsCHs) {
            modelGoodsOrders.addRow(new Object[]{
                    rowCH.getIdGood(),
                    rowCH.getGoodCode(),
                    rowCH.getGoodName(),
                    formatValue(rowCH.getPrepareDays())
            });
        }

        enableSortingForTable(tableOptGoodsConditionsEdit, 0,1,3); // Сортировка по PrepareDays
    }

    private void updateGoodCellSumView(List<rowGoodsSumcells> rowsCHs, List<rowGoodsSumcells> rowsCHsFromStock) {
        DefaultTableModel modelGoodsOrders = (DefaultTableModel) tableOptCellsView.getModel();
        modelGoodsOrders.setRowCount(0); // Очищаем таблицу перед добавлением новых данных

        for (rowGoodsSumcells rowCH : rowsCHsFromStock) {
            modelGoodsOrders.addRow(new Object[]{
                    rowCH.getIdGood(),
                    rowCH.getGoodCode(),
                    rowCH.getGoodName(),
                    formatValue(rowCH.getSun_q_ty()),
                    rowCH.getGoodMeasure()
            });
        }
        for (rowGoodsSumcells rowCH : rowsCHs) {
            modelGoodsOrders.addRow(new Object[]{
                    rowCH.getIdGood(),
                    rowCH.getGoodCode(),
                    rowCH.getGoodName(),
                    formatValue(rowCH.getSun_q_ty()),
                    rowCH.getGoodMeasure()
            });
        }

        enableSortingForTable(tableOptCellsView, 0,1,3); // Сортировка по количеству товара (Sun_q_ty)
    }


    private void updateGoodCellData(List<rowGoodsCells> rowsCHs, List<rowGoodsCells> rowsCHsFromInput) {
        DefaultTableModel modelGoodsOrders = (DefaultTableModel) tableOptGoodCellData.getModel();
        modelGoodsOrders.setRowCount(0); // Очищаем таблицу перед добавлением новых данных

        for (rowGoodsCells rowCH : rowsCHs) {
            String cellSize = "";
            if ((rowCH.getCellWidth() > 0) && (rowCH.getCellHeight() > 0) && (rowCH.getCellDepth() > 0)) {
                cellSize = " " + formatValue(rowCH.getCellWidth()) + "x" + formatValue(rowCH.getCellHeight()) + "x" + formatValue(rowCH.getCellDepth()) + " ";
            }
            String goodSize = "";
            if ((rowCH.getLength() > 0) && (rowCH.getHeight() > 0) && (rowCH.getWidth() > 0)) {
                goodSize = " " + formatValue(rowCH.getWidth()) + "x" + formatValue(rowCH.getHeight()) + "x" + formatValue(rowCH.getLength()) + " ";
            } else if ((rowCH.getLength() > 0) && (rowCH.getThickness() > 0) && (rowCH.getWidth() > 0)) {
                goodSize = " " + formatValue(rowCH.getWidth()) + "x" + formatValue(rowCH.getThickness()) + "x" + formatValue(rowCH.getLength()) + " ";
            } else if ((rowCH.getLength() > 0) && (rowCH.getDiameter() > 0)) {
                goodSize = " d:" + formatValue(rowCH.getDiameter()) + "x" + formatValue(rowCH.getLength()) + " ";
            }
            double vol = 0, cellVol = 0;
            if (rowCH.getGoodName().contains("Труб")) {
                if (!goodSize.equals("")) {
                    vol = rowCH.getDiameter() * rowCH.getDiameter() * rowCH.getLength();
                    cellVol = rowCH.getCellWidth() * rowCH.getCellHeight() * rowCH.getCellDepth();
                }
            } else if (rowCH.getGoodName().contains("Лист")) {
                if (!goodSize.equals("")) {
                    vol = rowCH.getWidth() * rowCH.getThickness() * rowCH.getLength();
                    cellVol = rowCH.getCellWidth() * rowCH.getCellHeight() * rowCH.getCellDepth();
                }
            }
            modelGoodsOrders.addRow(new Object[]{
                    rowCH.getIdGood(),
                    rowCH.getGoodCode(),
                    rowCH.getGoodName(),
                    rowCH.getCellCode(),
                    rowCH.getStockName(),
                    cellSize,
                    goodSize,
                    " " + formatValue(vol) + " / " + formatValue(cellVol) + " "
            });
        }

        for (rowGoodsCells rowCH : rowsCHsFromInput) {
            String cellSize = "";
            if ((rowCH.getCellWidth() > 0) && (rowCH.getCellHeight() > 0) && (rowCH.getCellDepth() > 0)) {
                cellSize = " " + formatValue(rowCH.getCellWidth()) + "x" + formatValue(rowCH.getCellHeight()) + "x" + formatValue(rowCH.getCellDepth()) + " ";
            }
            String goodSize = "";
            if ((rowCH.getLength() > 0) && (rowCH.getHeight() > 0) && (rowCH.getWidth() > 0)) {
                goodSize = " " + formatValue(rowCH.getWidth()) + "x" + formatValue(rowCH.getHeight()) + "x" + formatValue(rowCH.getLength()) + " ";
            } else if ((rowCH.getLength() > 0) && (rowCH.getThickness() > 0) && (rowCH.getWidth() > 0)) {
                goodSize = " " + formatValue(rowCH.getWidth()) + "x" + formatValue(rowCH.getThickness()) + "x" + formatValue(rowCH.getLength()) + " ";
            } else if ((rowCH.getLength() > 0) && (rowCH.getDiameter() > 0)) {
                goodSize = " d:" + formatValue(rowCH.getDiameter()) + "x" + formatValue(rowCH.getLength()) + " ";
            }
            double vol = 0, cellVol = 0;
            if (rowCH.getGoodName().contains("Труб")) {
                if (!goodSize.equals("")) {
                    vol = rowCH.getDiameter() * rowCH.getDiameter() * rowCH.getLength();
                    cellVol = rowCH.getCellWidth() * rowCH.getCellHeight() * rowCH.getCellDepth();
                }
            } else if (rowCH.getGoodName().contains("Лист")) {
                if (!goodSize.equals("")) {
                    vol = rowCH.getWidth() * rowCH.getThickness() * rowCH.getLength();
                    cellVol = rowCH.getCellWidth() * rowCH.getCellHeight() * rowCH.getCellDepth();
                }
            }
            modelGoodsOrders.addRow(new Object[]{
                    rowCH.getIdGood(),
                    rowCH.getGoodCode(),
                    rowCH.getGoodName(),
                    rowCH.getCellCode(),
                    rowCH.getStockName(),
                    cellSize,
                    goodSize,
                    " " + formatValue(vol) + " / " + formatValue(cellVol) + " "
            });
        }

        // Добавляем сортировку
        enableSortingForTable(tableOptGoodCellData, 0, 1, 3, 5,6); // Сортировка по GoodName, CellCode, StockName
    }

    private void updateGoodOrderData(List<rowGoodsOrdersOpt> rowsCHs) {
        DefaultTableModel modelGoodsOrders = (DefaultTableModel) tableOptVolResult.getModel();
        modelGoodsOrders.setRowCount(0); // Очищаем таблицу перед добавлением новых данных

        for (rowGoodsOrdersOpt rowCH : rowsCHs) {
            modelGoodsOrders.addRow(new Object[]{
                    rowCH.getOrderName(),
                    rowCH.getGoodName(),
                    formatValue(rowCH.getGoodQuantity()),
                    formatValue(rowCH.getStockQuantity()),
                    rowCH.getGoodMeasure(),
                    rowCH.getStartDateOpt(),
                    rowCH.getEndDatePlan()
            });
        }

        // Добавляем сортировку
        enableSortingForTable(tableOptVolResult,  2, 3); // Сортировка по OrderName, GoodName, GoodQuantity, StockQuantity
    }

    private void updateGoodOrderDataPurch(List<rowGoodsOrdersContrasOpt> rowsCHs) {
        DefaultTableModel modelGoodsOrders = (DefaultTableModel) tableOptVolResultPurchase.getModel();
        modelGoodsOrders.setRowCount(0); // Очищаем таблицу перед добавлением новых данных

        for (rowGoodsOrdersContrasOpt rowCH : rowsCHs) {
            modelGoodsOrders.addRow(new Object[]{
                    rowCH.getOrderName(),
                    rowCH.getGoodName(),
                    formatValue(rowCH.getPurchaseQuantity()),
                    rowCH.getGoodMeasure(),
                    rowCH.getContrasName(),
                    rowCH.getPurchaseDateOpt(),
                    String.format(Locale.US, "%.2f", rowCH.getPrice()),
                    String.format(Locale.US, "%.2f", rowCH.getOptPPrice())
            });
        }

        // Добавляем сортировку
        enableSortingForTable(tableOptVolResultPurchase,  2, 3,6,7); // Сортировка по OrderName, GoodName, PurchaseQuantity, GoodMeasure
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
        ParserProgressBar.setMaximum(statusForm.selectedUrls.size() + 1); // +1 для этапа парсинга судебных дел
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

                detailsParser.parseSupplierLitigations();
                detailsParser.parseSupplierStatuses();
//                detailsParser.cleanupDownloadDirectory();

            } finally {
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

                // Можно показать уведомление в UI
                JOptionPane.showMessageDialog(this,
                        "Ошибка при парсинге: " + result.error.getMessage(),
                        "Ошибка",
                        JOptionPane.ERROR_MESSAGE);
            } else if (result.purchaseData != null) {
                // Обработка успешного результата
                System.out.println("Успешно распарсено: " + result.purchaseData);

                // Здесь можно обновить UI или сохранить данные в базу
                // Например, добавить в таблицу результатов
            }
        });
    }

    private void transferTableDataToMatrix() {
        // Регулярные выражения для проверки форматов
        String numberPattern = "^-?\\d+(.\\d+)?$";
        String diapasonPattern = "^\\(?\\d+(.\\d+)?;\\d+(.\\d+)?\\)?$";

        // Проверка значения из текстового поля коэффициента
        String selectedW = "";
        if (!selectedW.matches(numberPattern)) {
            JOptionPane.showMessageDialog(null, "Неправильный формат в поле коэффициента: " + selectedW, "Ошибка", JOptionPane.ERROR_MESSAGE);
            return; // Завершаем выполнение метода
        }
    }
    private void fillComboBoxProfile() {
        List<String> profiles = dbExtractor.getAllProfileNames();
        for (String profile : profiles) {
            comboBoxProfileCriterion.addItem(profile);
        }
    }

    private void onProfileSelection() {
        String selectedProfile = (String) comboBoxProfileCriterion.getSelectedItem();
        String jValues = dbExtractor.getJValuesForProfile(selectedProfile);

        if (jValues == null || jValues.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Профиль не содержит данных.", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // Пытаемся распарсить как массив
            if (jValues.trim().startsWith("[")) {
                JSONArray jArray = new JSONArray(jValues);
                if (jArray.length() > 0) {
                    JSONObject firstObject = jArray.getJSONObject(0);
                    fillCritFields(firstObject);
                }
            }
            // Если это объект
            else if (jValues.trim().startsWith("{")) {
                JSONObject obj = new JSONObject(jValues);
                fillCritFields(obj);
            } else {
                JOptionPane.showMessageDialog(this, "Неверный формат JSON.", "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Ошибка при разборе JSON-данных.", "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Вынесем заполнение полей в отдельный метод
    private void fillCritFields(JSONObject obj) {
        textFieldCritEditMin.setText(obj.optString("nminval", ""));
        textFieldCritEditMax.setText(obj.optString("nmaxval", ""));
        textFieldCritEditWeight.setText(obj.optString("nweight", ""));
    }


    private void showCreateProfileDialog() {
        JDialog dialog = new JDialog(this, "Создание профиля", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new GridLayout(5, 2, 5, 5));

        JLabel labelName = new JLabel("Название профиля:");
        JTextField textFieldName = new JTextField();

        JLabel labelCriterion = new JLabel("ID критерия:");
        JTextField textFieldCriterionId = new JTextField(); // или JComboBox, если хочешь выбор из базы

        JLabel labelJValues = new JLabel("J значения:");
        JTextField textFieldJValues = new JTextField();

        JButton buttonSave = new JButton("Сохранить");
        JButton buttonCancel = new JButton("Отмена");

        dialog.add(labelName);
        dialog.add(textFieldName);
        dialog.add(labelCriterion);
        dialog.add(textFieldCriterionId);
        dialog.add(labelJValues);
        dialog.add(textFieldJValues);
        dialog.add(buttonSave);
        dialog.add(buttonCancel);

        // Сохранение в базу данных
        buttonSave.addActionListener(e -> {
            String name = textFieldName.getText().trim();
            String critIdStr = textFieldCriterionId.getText().trim();
            String jValues = textFieldJValues.getText().trim();

            if (name.isEmpty() || critIdStr.isEmpty() || jValues.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Заполните все поля");
                return;
            }

            try {
                long critId = Long.parseLong(critIdStr);

                String sql = "INSERT INTO module_profiles (prifile_name, module_criterion_id, j_values) VALUES (?, ?, ?)";
                PreparedStatement stmt = dbManager.getConnection().prepareStatement(sql);
                stmt.setString(1, name);
                stmt.setLong(2, critId);
                stmt.setString(3, jValues);
                stmt.executeUpdate();

                JOptionPane.showMessageDialog(dialog, "Профиль сохранён");
                dialog.dispose();

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "ID критерия должен быть числом");
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(dialog, "Ошибка при сохранении: " + ex.getMessage());
            }
        });

        buttonCancel.addActionListener(e -> dialog.dispose());

        dialog.setVisible(true);
    }



    private Map<String, String> createQueryParams(String searchQuery, boolean hasAnyFilter) {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("searchString", searchQuery);

        // Добавляем стандартные параметры только если есть хотя бы один фильтр
        if (hasAnyFilter) {
            params.put("morphology", "on");
            params.put("pageNumber", "1");
            params.put("sortDirection", "false");
            params.put("showLotsInfoHidden", "false");
            params.put("sortBy", "UPDATE_DATE");
        }

        return params;
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
//        params.put("currencyIdGeneral",
//                comboBoxCurrency.getSelectedItem() != null ? "1" : "-1");

        System.out.println("Final URL params: " + params);
        return params;
    }

    private void onQueryButtonClicked() {
        // Сбрасываем UI перед запуском нового парсера
        resetParserUI();
        statusForm.clearAllItems(); //

        Map<String, String> params = buildFinalParams();
        DefaultTableModel model = (DefaultTableModel) HeadersTable.getModel();

        // Очищаем все строки
        model.setRowCount(0);
        // Логируем все параметры
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

        listParser = new PurchasesParserHead(driverSetup, null, params, statusForm);

        new Thread(() -> {
            listParser.parse();
            SwingUtilities.invokeLater(() -> {
                // После завершения парсинга сбрасываем кнопки
                PauseParser.setEnabled(true);  // "Пауза" активна
                StopParser.setEnabled(true);
                StatusLabel.setText("Статус: запуск парсера...");
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
            StatusLabel.setText("Статус: готов к работе");

            // Если у вас есть другие элементы (например, ProgressBar), их тоже можно сбросить
            // ParserProgressBar.setValue(0);
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

    private void initChooseRangeElementsButton() {
        DefaultTableModel model = (DefaultTableModel) HeadersTable.getModel();
        int rowCount = model.getRowCount();

        try {
            // Получаем значения из текстовых полей
            int from = From.getText().isEmpty() ? 1 : Integer.parseInt(From.getText());
            int to = To.getText().isEmpty() ? rowCount : Integer.parseInt(To.getText());

            // Корректируем значения, если они выходят за границы
            from = Math.max(1, Math.min(from, rowCount));
            to = Math.max(1, Math.min(to, rowCount));

            // Меняем местами, если from > to
            if (from > to) {
                int temp = from;
                from = to;
                to = temp;
            }

            // Проверяем, есть ли хотя бы один выбранный элемент в диапазоне
            boolean hasSelectedItemsInRange = false;
            for (int i = from - 1; i < to; i++) {
                if (Boolean.TRUE.equals(model.getValueAt(i, 3))) {
                    hasSelectedItemsInRange = true;
                    break;
                }
            }

            // Если есть выбранные элементы в диапазоне - снимаем все галочки в этом диапазоне
            if (hasSelectedItemsInRange) {
                for (int i = from - 1; i < to; i++) {
                    model.setValueAt(false, i, 3); // Снимаем галочку
                    PurchaseItem item = statusForm.allItems.get(i);
                    statusForm.selectedUrls.remove(item.getUrl()); // Удаляем URL
                }
                System.out.println("Элементы с " + from + " по " + to + " сняты.");
            }
            // Если нет выбранных элементов в диапазоне - выбираем все в диапазоне
            else {
                for (int i = from - 1; i < to; i++) {
                    model.setValueAt(true, i, 3); // Ставим галочку
                    PurchaseItem item = statusForm.allItems.get(i);
                    if (!statusForm.selectedUrls.contains(item.getUrl())) {
                        statusForm.selectedUrls.add(item.getUrl()); // Добавляем URL
                    }
                }
                System.out.println("Выбраны элементы с " + from + " по " + to +
                        ". Текущий список: " + statusForm.selectedUrls);
            }
        } catch (NumberFormatException e) {
            System.out.println("Ошибка: введите корректные числовые значения");
        }
    }

    private void initChooseAllElementsButton() {
        DefaultTableModel model = (DefaultTableModel) HeadersTable.getModel();
        int rowCount = model.getRowCount();

        // Проверяем, есть ли хотя бы один выбранный элемент
        boolean hasSelectedItems = false;
        for (int i = 0; i < rowCount; i++) {
            if (Boolean.TRUE.equals(model.getValueAt(i, 3))) {
                hasSelectedItems = true;
                break;
            }
        }

        // Если есть выбранные элементы — снимаем все галочки и очищаем список
        if (hasSelectedItems) {
            for (int i = 0; i < rowCount; i++) {
                model.setValueAt(false, i, 3); // Снимаем галочку
            }
            statusForm.selectedUrls.clear(); // Очищаем список URL
            System.out.println("Все элементы сняты. Список пуст.");
        }
        // Если нет выбранных элементов — выбираем все
        else {
            for (int i = 0; i < rowCount; i++) {
                model.setValueAt(true, i, 3); // Ставим галочку
                PurchaseItem item = statusForm.allItems.get(i);
                if (!statusForm.selectedUrls.contains(item.getUrl())) {
                    statusForm.selectedUrls.add(item.getUrl()); // Добавляем URL
                }
            }
            System.out.println("Выбраны все элементы. Текущий список: " + statusForm.selectedUrls);
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
            statusForm.selectedUrls.clear();
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

            });
        }
    }

    private void updateComboBoxModel() {
        // Сохраняем текущий выбранный элемент
        Object selectedItem = comboBoxUserCrit.getSelectedItem();

        List<rowCritData> crits = dbExtractor.getAllCritData(false);
        DefaultComboBoxModel<rowCritData> model = new DefaultComboBoxModel<>();
        for (rowCritData row : crits) {
            model.addElement(row);
        }
        comboBoxUserCrit.setModel(model);

        // Восстанавливаем выбор, если элемент все еще существует в новой модели
        if (selectedItem != null) {
            for (int i = 0; i < model.getSize(); i++) {
                if (model.getElementAt(i).equals(selectedItem)) {
                    comboBoxUserCrit.setSelectedIndex(i);
                    break;
                }
            }
        } else if (model.getSize() > 0) {
            comboBoxUserCrit.setSelectedIndex(0);
        }
    }

    private void selectCritInComboBox(long critId) {
        DefaultComboBoxModel<rowCritData> model = (DefaultComboBoxModel<rowCritData>) comboBoxUserCrit.getModel();
        for (int i = 0; i < model.getSize(); i++) {
            if (model.getElementAt(i).getIdCrit() == critId) {
                comboBoxUserCrit.setSelectedIndex(i);
                break;
            }
        }
    }

    private void processSelectedCrit(rowCritData selected) {
        long critId = selected.getIdCrit();
        String critName = selected.getCritName();
        String columnName = "user_crit_" + critId;

        // Проверка существования записи
        List<rowCritData> rowsCrData = dbExtractor.getCritData(false, critId);
        if (rowsCrData.isEmpty()) {
            dbExtractor.addUserCritData(false, critName, "0", "0", "100", "1", "{}");
            dbExtractor.alterUserCritData(false, columnName);
        }

        dbExtractor.updateCGsUserCritValues(false);

        CritId = critId;
        CritString = columnName;
        CritShort = "uc_" + critId;
        CritName = critName;

        updateCritValues();
    }




    public static void main(String[] args) {
        // Параметры по умолчанию
        String contras = "";
        String good = "";
        String date = "";
        String minVolume = "";
        String order = "";

        // Если есть аргументы командной строки, используем их
        if (args.length >= 5) {
            contras = args[0];  // первый аргумент (после имени программы)
            good = args[1];     // второй аргумент
            date = args[2];     // третий аргумент
            minVolume = args[3]; // четвертый аргумент
            order = args[4];    // пятый аргумент
        }


        final String finalContras = contras;
        final String finalGood = good;
        final String finalDate = date;
        final String finalMinVolume = minVolume;
        final String finalOrder = order;


        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            try {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        // Запускаем интерфейс
        SwingUtilities.invokeLater(() -> {
            mainForm form = new mainForm(Role.EXPERT); // ← по умолчанию роль USER
            form.setContentPane(form.MainPanel);
            form.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            form.pack();
            form.setVisible(true);

            form.textFieldFilterContras.setText(finalContras);
            System.out.println(finalContras);
            form.textFieldFilterDate.setText(finalDate);
            form.textFieldFilterGood.setText(finalGood);
            form.textFieldFilterMinVolume.setText(finalMinVolume);
            form.textFieldFilterOrder.setText(finalOrder);
        });
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }

}

