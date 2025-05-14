//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

import org.json.JSONObject;
import org.postgresql.util.PGobject;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;


public class DatabaseManager {
    private String __URL;
    private String __USER;
    private String __PASSWORD;
    private String __DB_Main;
    private String __DB_Module;

    DatabaseManager(String _URL, String _USER, String _DB_Main, String _DB_Module, String _PASSWORD) {
        this.__URL = _URL;
        this.__USER = _USER;
        this.__DB_Main = _DB_Main;
        this.__DB_Module = _DB_Module;
        this.__PASSWORD = _PASSWORD;
    }

    public Connection getConnection() throws SQLException {
        String _fullURL = "jdbc:postgresql:" + this.__URL + "/" + this.__DB_Module;
        return DriverManager.getConnection(_fullURL, this.__USER, this.__PASSWORD);
    }

    private Connection getConnection(boolean db_main) throws SQLException {
        String _fullURL = "jdbc:postgresql:" + this.__URL + "/";
        if (db_main) {
            _fullURL = _fullURL + this.__DB_Main;
        } else {
            _fullURL = _fullURL + this.__DB_Module;
        }

        return DriverManager.getConnection(_fullURL, this.__USER, this.__PASSWORD);
    }

    private void closeResources(Connection connection, Statement statement, ResultSet resultSet) {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException var7) {
                var7.printStackTrace();
            }
        }

        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException var6) {
                var6.printStackTrace();
            }
        }

        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException var5) {
                var5.printStackTrace();
            }
        }

    }

    private ResultSet executeQueryWithParams(boolean db_main, String query, Object... params) throws SQLException {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = this.getConnection(db_main);
            statement = connection.prepareStatement(query);

            for(int i = 0; i < params.length; ++i) {
                statement.setObject(i + 1, params[i]);
            }

            resultSet = statement.executeQuery();
            return resultSet;
        } catch (SQLException var8) {
            this.closeResources(connection, statement, resultSet);
            throw var8;
        }
    }

    private void executeQueryNoResult(boolean db_main, String query) throws SQLException {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = this.getConnection(db_main);
            statement = connection.prepareStatement(query);
            statement.execute();
        } catch (SQLException var7) {
            this.closeResources(connection, statement, (ResultSet)resultSet);
            throw var7;
        }
    }

    private void executeQueryBatchContras(boolean db_main, String query, List<rowContras> params) throws SQLException {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = this.getConnection(db_main);
            connection.setAutoCommit(false);
            statement = connection.prepareStatement(query);
            Iterator var7 = params.iterator();

            while(var7.hasNext()) {
                rowContras rowC = (rowContras)var7.next();
                statement.setLong(1, rowC.getIdContras());
                statement.setString(2, rowC.getContrasName());
                statement.setDouble(3, rowC.getContrasReputation());
                statement.setString(4, rowC.getContrasCode());
                // Обработка 5-го параметра (sinn) с проверкой на NULL
                String inn = rowC.getInn();
                if (inn != null && !inn.isEmpty()) {
                    statement.setString(5, inn);
                } else {
                    statement.setNull(5, Types.VARCHAR); // Явное указание NULL
                }

                // 6-й параметр (nfromglobalstatus)
                statement.setInt(6, rowC.getGlobalStatus());
                statement.addBatch();
            }

            statement.executeBatch();
            connection.commit();
        } catch (SQLException var9) {
            this.closeResources(connection, statement, (ResultSet)resultSet);
            throw var9;
        }
    }

    private void executeQueryBatchGoods(boolean db_main, String query, List<rowGoods> params) throws SQLException {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = this.getConnection(db_main);
            connection.setAutoCommit(false);
            statement = connection.prepareStatement(query);
            Iterator var7 = params.iterator();

            while(var7.hasNext()) {
                rowGoods rowG = (rowGoods)var7.next();
                statement.setLong(1, rowG.getIdGood());
                statement.setString(2, rowG.getGoodName());
                statement.setInt(3, rowG.getPrepareDays());
                statement.setString(4, rowG.getGoodCode());
                statement.setDouble(5, rowG.getWidth());
                statement.setDouble(6, rowG.getHeight());
                statement.setDouble(7, rowG.getLength());
                statement.setDouble(8, rowG.getDiameter());
                statement.setDouble(9, rowG.getThickness());
                statement.setString(10, rowG.getGoodMeasure());
                statement.addBatch();
            }

            statement.executeBatch();
            connection.commit();
        } catch (SQLException var9) {
            this.closeResources(connection, statement, (ResultSet)resultSet);
            throw var9;
        }
    }

    private void executeQueryBatchGoodsUpdateNoName(boolean db_main, String query, List<rowGoods> params) throws SQLException {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = this.getConnection(db_main);
            connection.setAutoCommit(false);
            statement = connection.prepareStatement(query);
            Iterator var7 = params.iterator();

            while(var7.hasNext()) {
                rowGoods rowG = (rowGoods)var7.next();
                statement.setInt(1, rowG.getPrepareDays());
                statement.setLong(2, rowG.getIdGood());
                statement.addBatch();
            }

            statement.executeBatch();
            connection.commit();
        } catch (SQLException var9) {
            this.closeResources(connection, statement, (ResultSet)resultSet);
            throw var9;
        }
    }

    private void executeQueryBatchCGsUsCrUpdateNoName(boolean db_main, String query, List<rowContrasGoodsOrders> params) throws SQLException {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = this.getConnection(db_main);
            connection.setAutoCommit(false);
            statement = connection.prepareStatement(query);
            Iterator var7 = params.iterator();

            while(var7.hasNext()) {
                rowContrasGoodsOrders rowCG = (rowContrasGoodsOrders)var7.next();
                statement.setDouble(1, rowCG.getMinVolume());
                statement.setLong(2, rowCG.getIdContras());
                statement.setLong(3, rowCG.getIdGood());
                statement.addBatch();
            }

            statement.executeBatch();
            connection.commit();
        } catch (SQLException var9) {
            this.closeResources(connection, statement, (ResultSet)resultSet);
            throw var9;
        }
    }

    private void executeQueryBatchCGs(boolean db_main, String query, List<rowContrasGoodsOrders> params) throws SQLException {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = this.getConnection(db_main);
            connection.setAutoCommit(false);
            statement = connection.prepareStatement(query);
            Iterator var7 = params.iterator();

            while(var7.hasNext()) {
                rowContrasGoodsOrders rowCG = (rowContrasGoodsOrders)var7.next();
                statement.setLong(1, rowCG.getIdContras());
                statement.setLong(2, rowCG.getIdGood());
                statement.addBatch();
            }

            statement.executeBatch();
            connection.commit();
        } catch (SQLException var9) {
            this.closeResources(connection, statement, (ResultSet)resultSet);
            throw var9;
        }
    }

    private void executeQueryBatchOrders(boolean db_main, String query, List<rowOrders> params) throws SQLException {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = this.getConnection(db_main);
            connection.setAutoCommit(false);
            statement = connection.prepareStatement(query);
            Iterator var7 = params.iterator();

            while(var7.hasNext()) {
                rowOrders rowO = (rowOrders)var7.next();
                statement.setLong(1, rowO.getIdOrder());
                statement.setString(2, rowO.getOrderName());
                statement.setTimestamp(3, rowO.getStartDate() != null ? new Timestamp(rowO.getStartDate().getTime()) : null);
                statement.setTimestamp(4, rowO.getEndDatePlan() != null ? new Timestamp(rowO.getEndDatePlan().getTime()) : null);
                statement.addBatch();
            }

            statement.executeBatch();
            connection.commit();
        } catch (SQLException var9) {
            this.closeResources(connection, statement, (ResultSet)resultSet);
            throw var9;
        }
    }

    private void executeQueryBatchWorkOrders(boolean db_main, String query, List<rowWorkOrder> params) throws SQLException {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = this.getConnection(db_main);
            connection.setAutoCommit(false);
            statement = connection.prepareStatement(query);
            Iterator var7 = params.iterator();

            while(var7.hasNext()) {
                rowWorkOrder rowGO = (rowWorkOrder)var7.next();
                statement.setLong(1, rowGO.getIdGood());
                statement.setLong(2, rowGO.getIdOrder());
                statement.setDouble(3, rowGO.getQuantity());
                statement.setTimestamp(4, rowGO.getStartDatePlan() != null ? new Timestamp(rowGO.getStartDatePlan().getTime()) : null);
                statement.setTimestamp(5, rowGO.getEndDatePlan() != null ? new Timestamp(rowGO.getEndDatePlan().getTime()) : null);
                statement.addBatch();
            }

            statement.executeBatch();
            connection.commit();
        } catch (SQLException var9) {
            this.closeResources(connection, statement, (ResultSet)resultSet);
            throw var9;
        }
    }

    private void executeQueryBatchGoodsOrdersStock(boolean db_main, String query, List<rowGoodsOrdersStock> params) throws SQLException {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = this.getConnection(db_main);
            connection.setAutoCommit(false);
            statement = connection.prepareStatement(query);
            Iterator var7 = params.iterator();

            while(var7.hasNext()) {
                rowGoodsOrdersStock rowGOS = (rowGoodsOrdersStock)var7.next();
                statement.setLong(1, rowGOS.getIdGOS());
                statement.setLong(2, rowGOS.getIdGood());
                statement.setLong(3, rowGOS.getIdStock());
                statement.setLong(4, rowGOS.getIdOrder());
                statement.setDouble(5, rowGOS.getGoodQuantity());
                statement.setString(6, rowGOS.getTypeGOS());
                statement.addBatch();
            }

            statement.executeBatch();
            connection.commit();
        } catch (SQLException var9) {
            this.closeResources(connection, statement, (ResultSet)resultSet);
            throw var9;
        }
    }

    private void executeQueryBatchStocks(boolean db_main, String query, List<rowStockCells> params) throws SQLException {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = this.getConnection(db_main);
            connection.setAutoCommit(false);
            statement = connection.prepareStatement(query);
            Iterator var7 = params.iterator();

            while(var7.hasNext()) {
                rowStockCells rowSTs = (rowStockCells)var7.next();
                statement.setLong(1, rowSTs.getIdStock());
                statement.setString(2, rowSTs.getStockCode());
                statement.setString(3, rowSTs.getStockName());
                statement.addBatch();
            }

            statement.executeBatch();
            connection.commit();
        } catch (SQLException var9) {
            this.closeResources(connection, statement, (ResultSet)resultSet);
            throw var9;
        }
    }

    private void executeQueryBatchStCells(boolean db_main, String query, List<rowStockCells> params) throws SQLException {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = this.getConnection(db_main);
            connection.setAutoCommit(false);
            statement = connection.prepareStatement(query);
            Iterator var7 = params.iterator();

            while(var7.hasNext()) {
                rowStockCells rowSTCs = (rowStockCells)var7.next();
                statement.setLong(1, rowSTCs.getIdCell());
                statement.setString(2, rowSTCs.getCellCode());
                statement.setLong(3, rowSTCs.getIdStock());
                statement.setDouble(4, rowSTCs.getCellWidth());
                statement.setDouble(5, rowSTCs.getCellDepth());
                statement.setDouble(6, rowSTCs.getCellHeight());
                statement.addBatch();
            }

            statement.executeBatch();
            connection.commit();
        } catch (SQLException var9) {
            this.closeResources(connection, statement, (ResultSet)resultSet);
            throw var9;
        }
    }

    private void executeQueryBatchLot(boolean db_main, String query, List<rowLot> params) throws SQLException {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = this.getConnection(db_main);
            connection.setAutoCommit(false);
            statement = connection.prepareStatement(query);
            Iterator var7 = params.iterator();

            while(var7.hasNext()) {
                rowLot rowCG = (rowLot)var7.next();
                statement.setLong(1, rowCG.getIdGood());
                statement.setLong(2, rowCG.getIdContras());
                statement.setInt(3, rowCG.getDeliveryTime());
                statement.setDouble(4, rowCG.getMinVolume());
                statement.setDouble(5, rowCG.getPrice());
                statement.setDouble(6, rowCG.getSaleVolume());
                statement.setDouble(7, rowCG.getSalePrice());
                statement.setDouble(8, rowCG.getGoodQuality());
                statement.setDouble(9, rowCG.getContrasDelay());
                statement.addBatch();
            }

            statement.executeBatch();
            connection.commit();
        } catch (SQLException var9) {
            this.closeResources(connection, statement, (ResultSet)resultSet);
            throw var9;
        }
    }

    private void executeQueryBatchRating(boolean db_module, String query, List<rowContrasGoodsOrdersWithWeights> params) throws SQLException {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = this.getConnection(db_module);
            connection.setAutoCommit(false);
            statement = connection.prepareStatement(query);
            Iterator var7 = params.iterator();

            while(var7.hasNext()) {
                rowContrasGoodsOrdersWithWeights rowRat = (rowContrasGoodsOrdersWithWeights)var7.next();
                statement.setLong(1, rowRat.getIdOrder());
                statement.setLong(2, rowRat.getIdContras());
                statement.setLong(3, rowRat.getIdGood());
                statement.setDouble(4, rowRat.getRatingComplete());
                statement.addBatch();
            }

            statement.executeBatch();
            connection.commit();
        } catch (SQLException var9) {
            this.closeResources(connection, statement, (ResultSet)resultSet);
            throw var9;
        }
    }

    private void executeQueryBatchCH(boolean db_module, String query, List<rowContrasWithHistory> params) throws SQLException {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = this.getConnection(db_module);
            connection.setAutoCommit(false);
            statement = connection.prepareStatement(query);
            Iterator var7 = params.iterator();

            while(var7.hasNext()) {
                rowContrasWithHistory rowCH = (rowContrasWithHistory)var7.next();
                statement.setLong(1, rowCH.getIdContras());
                statement.setLong(2, (long)rowCH.getNumCompleted());
                statement.setLong(3, (long)rowCH.getNumFailed());
                statement.setDouble(4, rowCH.getAvrDelay());
                statement.setDouble(5, rowCH.getPercentFailed());
                statement.addBatch();
            }

            statement.executeBatch();
            connection.commit();
        } catch (SQLException var9) {
            this.closeResources(connection, statement, (ResultSet)resultSet);
            throw var9;
        }
    }

    private void executeQueryBatchCells(boolean db_module, String query, Object[] params) throws SQLException {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = this.getConnection(db_module);
            connection.setAutoCommit(false);
            statement = connection.prepareStatement(query);
            Object[] var7 = params;
            int var8 = params.length;

            for(int var9 = 0; var9 < var8; ++var9) {
                Object rowCH = var7[var9];
                statement.setLong(1, Long.parseLong(rowCH.toString()));
                statement.addBatch();
            }

            statement.executeBatch();
            connection.commit();
        } catch (SQLException var11) {
            this.closeResources(connection, statement, (ResultSet)resultSet);
            throw var11;
        }
    }

    private ResultSet executeQuery(boolean db_main, String query) throws SQLException {
        return this.executeQueryWithParams(db_main, query);
    }

    private void rollbackTransaction(Connection connection) {
        try {
            if (connection != null) {
                connection.rollback();
            }
        } catch (SQLException var3) {
            var3.printStackTrace();
        }

    }

    public void setTables(boolean db_module) {
        String query = "CREATE TABLE IF NOT EXISTS public.bs_contras (id bigint NOT NULL, scaption character varying(254), ncontrasreliability numeric(38,18), scode character varying(254), sinn character varying(254), nfromglobalstatus numeric(38,18))";

        try {
            this.executeQueryNoResult(db_module, query);
        } catch (SQLException var12) {
            var12.printStackTrace();
        }

        query = "CREATE TABLE IF NOT EXISTS public.bs_goods (id bigint NOT NULL, sname character varying(512), npreparedays int, sarticle character varying(512), nwidth numeric(38,18), nheight numeric(38,18), nlength numeric(38,18), ndiameter numeric(38,18), nthickness numeric(38,18), goodmsritem character varying(254), okpd2 character varying(254))";

        try {
            this.executeQueryNoResult(db_module, query);
        } catch (SQLException var11) {
            var11.printStackTrace();
        }

        query = "CREATE TABLE IF NOT EXISTS public.bs_order (id bigint NOT NULL, scaption character varying(254), ddate timestamp without time zone, ddatecloseplan timestamp without time zone)";

        try {
            this.executeQueryNoResult(db_module, query);
        } catch (SQLException var10) {
            var10.printStackTrace();
        }

        query = "CREATE TABLE IF NOT EXISTS public.mes_workorder (id_goods bigint NOT NULL, id_order bigint NOT NULL, nqtybase numeric(38,18), dplanbegin timestamp without time zone, dplanend timestamp without time zone)";

        try {
            this.executeQueryNoResult(db_module, query);
        } catch (SQLException var9) {
            var9.printStackTrace();
        }

        query = "CREATE TABLE IF NOT EXISTS public.prs_lot (id_goods bigint NOT NULL, id_contras bigint NOT NULL, ndeliverytime int, nqty numeric(38,18), nprc numeric(38,18), nqtysale numeric(38,18), nprcsale numeric(38,18), ngoodquality numeric(38,18), ncontrasdelay numeric(38,18))";

        try {
            this.executeQueryNoResult(db_module, query);
        } catch (SQLException var8) {
            var8.printStackTrace();
        }

        query = "CREATE TABLE IF NOT EXISTS public.module_workorder (id_goods bigint NOT NULL, id_order bigint NOT NULL, nqtybase numeric(38,18), dplanbegin timestamp without time zone, dplanend timestamp without time zone, bneedsfrommain numeric(1,0))";

        try {
            this.executeQueryNoResult(db_module, query);
        } catch (SQLException var7) {
            var7.printStackTrace();
        }

        query = "CREATE TABLE IF NOT EXISTS public.stk_regmovmat (id bigint NOT NULL, idgds bigint, idstock bigint, idorder bigint, nqtybasemsr numeric(38,18), stype character varying(254))";

        try {
            this.executeQueryNoResult(db_module, query);
        } catch (SQLException var6) {
            var6.printStackTrace();
        }

        query = "CREATE TABLE IF NOT EXISTS public.stk_stock (id bigint NOT NULL, scode character varying(254), scaption character varying(254))";

        try {
            this.executeQueryNoResult(db_module, query);
        } catch (SQLException var5) {
            var5.printStackTrace();
        }

        query = "CREATE TABLE IF NOT EXISTS public.wms_cell (id bigint NOT NULL, scode character varying(254), idstock bigint, nwidth numeric(38,18), ndepth numeric(38,18), nheight numeric(38,18))";

        try {
            this.executeQueryNoResult(db_module, query);
        } catch (SQLException var4) {
            var4.printStackTrace();
        }

    }

    public void updateTables(boolean db_main, boolean db_module) {
        List<rowContras> rowCs = new ArrayList();
        String query = "SELECT DISTINCT bc.id as id_c, bc.scaption as name_c, bc.scode as code_c, bc.sinn as inn_c, bsr.bforbiddencontraspurchase as fb_pch, bsr.bforbiddencooperation as fb_coop FROM public.bs_contras bc ";
        query = query + "LEFT JOIN Bs_ContrasReliability bsr ON bc.idContrasReliability = bsr.id ";

        String g_Name;
        try {
            ResultSet resultSet = this.executeQuery(db_main, query);

            try {
                while(resultSet.next()) {
                    long c_id = resultSet.getLong("id_c");
                    String c_Name = resultSet.getString("name_c");
                    g_Name = resultSet.getString("code_c");
                    boolean fb_pch = resultSet.getBoolean("fb_pch");
                    boolean fb_coop = resultSet.getBoolean("fb_coop");
                    double c_rep = !fb_pch && !fb_coop ? 1.0 : 0.0;
                    String c_inn = resultSet.getString("inn_c");
                    int globalStatus = 1;
                    rowCs.add(new rowContras(c_id, c_Name, c_rep, g_Name, c_inn, globalStatus));
                }
            } catch (Throwable var70) {
                if (resultSet != null) {
                    try {
                        resultSet.close();
                    } catch (Throwable var53) {
                        var70.addSuppressed(var53);
                    }
                }

                throw var70;
            }

            if (resultSet != null) {
                resultSet.close();
            }
        } catch (SQLException var71) {
            var71.printStackTrace();
        }

        query = "DELETE FROM public.bs_contras";

        try {
            this.executeQueryNoResult(db_module, query);
        } catch (SQLException var52) {
            var52.printStackTrace();
        }

        query = "INSERT INTO public.bs_contras (id, scaption, ncontrasreliability, scode, sinn, nfromglobalstatus) VALUES (?,?,?,?,?,?)";

        try {
            this.executeQueryBatchContras(db_module, query, rowCs);
        } catch (SQLException var51) {
            var51.printStackTrace();
        }

        List<rowGoods> rowGs = new ArrayList();
        query = "SELECT DISTINCT bs.id as id_g, bs.sname as name_g, bs.sarticle as code_g, bs.jtypesizeattrs ->> 'width' as w_g, bs.jtypesizeattrs ->> 'length' as l_g, bs.jtypesizeattrs ->> 'height' as h_g, bs.jtypesizeattrs ->> 'diameter' as d_g, bs.jtypesizeattrs ->> 'thickness' as t_g, bg_mi.sheadline as measure_g FROM public.bs_goods bs ";
        query = query + "LEFT JOIN bs_goodmsritem bg_mi ON bg_mi.idgoods = bs.id and bg_mi.bbasemsritem = 1 ";


        double n_qty;
        double nprc;
        double n_qty_b;
        String o_Name;
        try {
            ResultSet resultSet = this.executeQuery(db_main, query);

            try {
                while(resultSet.next()) {
                    long g_id = resultSet.getLong("id_g");
                    g_Name = resultSet.getString("name_g");
                    o_Name = resultSet.getString("code_g");
                    double g_W = resultSet.getDouble("w_g");
                    n_qty = resultSet.getDouble("l_g");
                    n_qty = resultSet.getDouble("h_g");
                    nprc = resultSet.getDouble("d_g");
                    n_qty_b = resultSet.getDouble("t_g");
                    String g_MU = resultSet.getString("measure_g");
                    rowGs.add(new rowGoods(g_id, g_Name, 0, o_Name, g_W, n_qty, n_qty, nprc, n_qty_b, g_MU));
                }
            } catch (Throwable var68) {
                if (resultSet != null) {
                    try {
                        resultSet.close();
                    } catch (Throwable var50) {
                        var68.addSuppressed(var50);
                    }
                }

                throw var68;
            }

            if (resultSet != null) {
                resultSet.close();
            }
        } catch (SQLException var69) {
            var69.printStackTrace();
        }

        query = "DELETE FROM public.bs_goods";

        try {
            this.executeQueryNoResult(db_module, query);
        } catch (SQLException var49) {
            var49.printStackTrace();
        }

        query = "INSERT INTO public.bs_goods (id, sname, npreparedays, sarticle, nwidth, nheight, nlength, ndiameter, nthickness, goodmsritem) VALUES (?,?,?,?,?,?,?,?,?,?)";

        try {
            this.executeQueryBatchGoods(db_module, query, rowGs);
        } catch (SQLException var48) {
            var48.printStackTrace();
        }

        List<rowOrders> rowOs = new ArrayList();
        query = "SELECT DISTINCT id as id_o, scaption as name_o, ddate as sDate_o, ddateclosefact as eDate_o FROM public.bs_order";

        Timestamp need_Date;
        try {
            ResultSet resultSet = this.executeQuery(db_main, query);

            try {
                while(resultSet.next()) {
                    long o_id = resultSet.getLong("id_o");
                    o_Name = resultSet.getString("name_o");
                    need_Date = resultSet.getTimestamp("sDate_o");
                    Date o_eDate = resultSet.getTimestamp("eDate_o");
                    rowOs.add(new rowOrders(o_id, o_Name, need_Date, o_eDate));
                }
            } catch (Throwable var66) {
                if (resultSet != null) {
                    try {
                        resultSet.close();
                    } catch (Throwable var47) {
                        var66.addSuppressed(var47);
                    }
                }

                throw var66;
            }

            if (resultSet != null) {
                resultSet.close();
            }
        } catch (SQLException var67) {
            var67.printStackTrace();
        }

        query = "DELETE FROM public.bs_order";

        try {
            this.executeQueryNoResult(db_module, query);
        } catch (SQLException var46) {
            var46.printStackTrace();
        }

        query = "INSERT INTO public.bs_order (id, scaption, ddate, ddatecloseplan) VALUES (?,?,?,?)";

        try {
            this.executeQueryBatchOrders(db_module, query, rowOs);
        } catch (SQLException var45) {
            var45.printStackTrace();
        }

        query = "DELETE FROM public.mes_workorder";

        try {
            this.executeQueryNoResult(db_module, query);
        } catch (SQLException var44) {
            var44.printStackTrace();
        }

        List<rowWorkOrder> rowmesGOs = new ArrayList();
        query = "SELECT DISTINCT wog.idgds as id_g, wog.idorder as id_o, wog.nqtybase as n_qty, wo.dplanbegin as s_Date, wo.dplanend as e_Date FROM public.mes_workordergds wog ";
        query = query + "JOIN mes_workorder wo ON wog.idwrkorder = wo.id ";

        try {
            ResultSet resultSet = this.executeQuery(db_main, query);

            try {
                while(resultSet.next()) {
                    long g_id = resultSet.getLong("id_g");
                    long o_id = resultSet.getLong("id_o");
                    n_qty = resultSet.getDouble("n_qty");
                    Date s_Date = resultSet.getTimestamp("s_Date");
                    Date e_Date = resultSet.getTimestamp("e_Date");
                    rowmesGOs.add(new rowWorkOrder(g_id, o_id, n_qty, s_Date, e_Date));
                }
            } catch (Throwable var64) {
                if (resultSet != null) {
                    try {
                        resultSet.close();
                    } catch (Throwable var43) {
                        var64.addSuppressed(var43);
                    }
                }

                throw var64;
            }

            if (resultSet != null) {
                resultSet.close();
            }
        } catch (SQLException var65) {
            var65.printStackTrace();
        }

        query = "INSERT INTO public.mes_workorder (id_goods, id_order, nqtybase, dplanbegin, dplanend) VALUES (?,?,?,?,?)";

        try {
            this.executeQueryBatchWorkOrders(db_module, query, rowmesGOs);
        } catch (SQLException var42) {
            var42.printStackTrace();
        }

        query = "DELETE FROM public.prs_lot";

        try {
            this.executeQueryNoResult(db_module, query);
        } catch (SQLException var41) {
            var41.printStackTrace();
        }
        long id_g;
        List<rowLot> rowprsCGs = new ArrayList();
        query = "SELECT DISTINCT bg.id as id_g, bc.id as id_c, pe.ndeldays as n_time, pe.nqty as n_qty, pe.nprc as nprc, pe.nqtybase as n_qty_b, pe.nprcbase as nprc_b FROM public.prs_etrep pe ";
        query = query + "JOIN bs_goods bg on pe.gidgdssrv = bg.gid ";
        query = query + "JOIN bs_contras bc on pe.gidsettler = bc.gid ";


        try {
            ResultSet resultSet = this.executeQuery(db_main, query);

            try {
                while(resultSet.next()) {
                    long g_id = resultSet.getLong("id_g");
                    id_g = resultSet.getLong("id_c");
                    int n_time = resultSet.getInt("n_time");
                    n_qty = resultSet.getDouble("n_qty");
                    nprc = resultSet.getDouble("nprc");
                    n_qty_b = resultSet.getDouble("n_qty_b");
                    double nprc_b = resultSet.getDouble("nprc_b");
                    double g_qual = 1.0;
                    double c_del = 0.0;
                    rowprsCGs.add(new rowLot(g_id, id_g, 0, 0.0, nprc, 0.0, nprc_b, 1.0, 0.0));
                }
            } catch (Throwable var62) {
                if (resultSet != null) {
                    try {
                        resultSet.close();
                    } catch (Throwable var40) {
                        var62.addSuppressed(var40);
                    }
                }

                throw var62;
            }

            if (resultSet != null) {
                resultSet.close();
            }
        } catch (SQLException var63) {
            var63.printStackTrace();
        }


        query = "INSERT INTO public.prs_lot (id_goods, id_contras, ndeliverytime, nqty, nprc, nqtysale, nprcsale, ngoodquality, ncontrasdelay) VALUES (?,?,?,?,?,?,?,?,?)";

        try {
            this.executeQueryBatchLot(db_module, query, rowprsCGs);
        } catch (SQLException var39) {
            var39.printStackTrace();
        }

        query = "UPDATE public.prs_lot pl " +
                "SET ndeliverytime = mlc.ndeliverytime, " +
                "    nqty = mlc.nqty " +
                "FROM public.module_lotcriterion mlc " +
                "WHERE pl.id_contras = mlc.id_contras " +
                "AND pl.id_goods = mlc.id_goods";

        try {
            this.executeQueryNoResult(db_module, query);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        query = "DELETE FROM public.module_workorder where bneedsfrommain = 1 ";

        try {
            this.executeQueryNoResult(db_module, query);
        } catch (SQLException var38) {
            var38.printStackTrace();
        }

        List<rowWorkOrder> rowGOs = new ArrayList();
        query = "SELECT DISTINCT pe.dneed as pe_dateneed, bg.id as id_g, bo.id as id_o, pe.nqtybase as pe_qty FROM prs_etrep pe ";
        query = query + "left join bs_goods bg on pe.gidgdssrv = bg.gid ";
        query = query + "left join bs_order bo on pe.idorder = bo.id ";
        query = query + "left join mes_workorder mw on pe.giddocneed = mw.gid ";
        query = query + "where pe.ntype = 200 ";

        long id_s;
        try {
            ResultSet resultSet = this.executeQuery(db_main, query);

            try {
                while(resultSet.next()) {
                    need_Date = resultSet.getTimestamp("pe_dateneed");
                    id_g = resultSet.getLong("id_o");
                    id_s = resultSet.getLong("id_g");
                    double need_qty = resultSet.getDouble("pe_qty");
                    rowGOs.add(new rowWorkOrder(id_s, id_g, need_qty, need_Date, need_Date));
                }
            } catch (Throwable var60) {
                if (resultSet != null) {
                    try {
                        resultSet.close();
                    } catch (Throwable var37) {
                        var60.addSuppressed(var37);
                    }
                }

                throw var60;
            }

            if (resultSet != null) {
                resultSet.close();
            }
        } catch (SQLException var61) {
            var61.printStackTrace();
        }

        query = "INSERT INTO public.module_workorder (id_goods, id_order, nqtybase, dplanbegin, dplanend, bneedsfrommain) VALUES (?,?,?,?,?,'1')";

        try {
            this.executeQueryBatchWorkOrders(db_module, query, rowGOs);
        } catch (SQLException var36) {
            var36.printStackTrace();
        }

        query = "DELETE FROM public.stk_regmovmat";

        try {
            this.executeQueryNoResult(db_module, query);
        } catch (SQLException var35) {
            var35.printStackTrace();
        }

        List<rowGoodsOrdersStock> rowGOSs = new ArrayList();
        query = "SELECT DISTINCT sr.idgds as id_g, sr.idstock as id_s, bo.id as id_o, sum(sr.nqtybasemsr) as qty, sr.stype as tpy FROM public.stk_regmovmat sr ";
        query = query + "left join bs_order bo on sr.gidmaster = bo.gid ";
        query = query + "where sr.stype = 'mov' ";
        query = query + "GROUP BY (sr.idgds, sr.idstock, bo.id, sr.stype) ";
        query = query + "HAVING (sum(sr.nqtybasemsr)>0) ";

        double n_w;
        long id_st;
        try {
            ResultSet resultSet = this.executeQuery(db_main, query);

            try {
                while(resultSet.next()) {
                    id_g = resultSet.getLong("id_g");
                    id_s = resultSet.getLong("id_s");
                    id_st = resultSet.getLong("id_o");
                    n_w = resultSet.getDouble("qty");
                    String tpy = resultSet.getString("tpy");
                    rowGOSs.add(new rowGoodsOrdersStock((long)resultSet.getRow(), id_g, id_s, id_st, n_w, tpy));
                }
            } catch (Throwable var58) {
                if (resultSet != null) {
                    try {
                        resultSet.close();
                    } catch (Throwable var34) {
                        var58.addSuppressed(var34);
                    }
                }

                throw var58;
            }

            if (resultSet != null) {
                resultSet.close();
            }
        } catch (SQLException var59) {
            var59.printStackTrace();
        }

        query = "INSERT INTO public.stk_regmovmat (id, idgds, idstock, idorder, nqtybasemsr, stype) VALUES (?,?,?,?,?,?)";

        try {
            this.executeQueryBatchGoodsOrdersStock(db_module, query, rowGOSs);
        } catch (SQLException var33) {
            var33.printStackTrace();
        }

        List<rowStockCells> rowSCs = new ArrayList();
        query = "SELECT DISTINCT id as id_st, scode as st_code, scaption as st_name FROM public.stk_stock ";

        ResultSet resultSet;
        long id_cl;
        String cl_code;
        try {
            resultSet = this.executeQuery(db_main, query);

            try {
                while(resultSet.next()) {
                    id_cl = resultSet.getLong("id_st");
                    cl_code = resultSet.getString("st_code");
                    String st_name = resultSet.getString("st_name");
                    rowSCs.add(new rowStockCells(id_cl, st_name, cl_code, 0L, "", 0.0, 0.0, 0.0));
                }
            } catch (Throwable var56) {
                if (resultSet != null) {
                    try {
                        resultSet.close();
                    } catch (Throwable var32) {
                        var56.addSuppressed(var32);
                    }
                }

                throw var56;
            }

            if (resultSet != null) {
                resultSet.close();
            }
        } catch (SQLException var57) {
            var57.printStackTrace();
        }

        query = "DELETE FROM public.stk_stock";

        try {
            this.executeQueryNoResult(db_module, query);
        } catch (SQLException var31) {
            var31.printStackTrace();
        }

        query = "INSERT INTO public.stk_stock (id, scode, scaption) VALUES (?,?,?)";

        try {
            this.executeQueryBatchStocks(db_module, query, rowSCs);
        } catch (SQLException var30) {
            var30.printStackTrace();
        }

        rowSCs.clear();
        query = "SELECT DISTINCT id as id_cl, scode as cl_code, idstock as id_st, nwidth as n_w, ndepth as n_d, nheight as n_h FROM public.wms_cell ";

        try {
            resultSet = this.executeQuery(db_main, query);

            try {
                while(resultSet.next()) {
                    id_cl = resultSet.getLong("id_cl");
                    cl_code = resultSet.getString("cl_code");
                    id_st = resultSet.getLong("id_st");
                    n_w = resultSet.getDouble("n_w");
                    double n_d = resultSet.getDouble("n_d");
                    double n_h = resultSet.getDouble("n_h");
                    rowSCs.add(new rowStockCells(id_st, "", "", id_cl, cl_code, n_w, n_d, n_h));
                }
            } catch (Throwable var54) {
                if (resultSet != null) {
                    try {
                        resultSet.close();
                    } catch (Throwable var29) {
                        var54.addSuppressed(var29);
                    }
                }

                throw var54;
            }

            if (resultSet != null) {
                resultSet.close();
            }
        } catch (SQLException var55) {
            var55.printStackTrace();
        }

        query = "DELETE FROM public.wms_cell";

        try {
            this.executeQueryNoResult(db_module, query);
        } catch (SQLException var28) {
            var28.printStackTrace();
        }

        query = "INSERT INTO public.wms_cell (id, scode, idstock, nwidth, ndepth, nheight) VALUES (?,?,?,?,?,?)";

        try {
            this.executeQueryBatchStCells(db_module, query, rowSCs);
        } catch (SQLException var27) {
            var27.printStackTrace();
        }

    }

    public int getContrasNum(boolean db_main) {
        int numContras = 0;
        String query = "SELECT COUNT(id) as numc FROM public.bs_contras";

        try {
            ResultSet resultSet = this.executeQuery(db_main, query);

            try {
                System.out.println(resultSet);

                while(resultSet.next()) {
                    numContras = resultSet.getInt("numc");
                }
            } catch (Throwable var8) {
                if (resultSet != null) {
                    try {
                        resultSet.close();
                    } catch (Throwable var7) {
                        var8.addSuppressed(var7);
                    }
                }

                throw var8;
            }

            if (resultSet != null) {
                resultSet.close();
            }
        } catch (SQLException var9) {
            var9.printStackTrace();
        }

        return numContras;
    }

    public List<rowContras> getCs(boolean db_module, String filterContrasName) {
        List<rowContras> filteredCGO = new ArrayList<>();
        StringBuilder query = new StringBuilder(
                "SELECT bs_contras.id as c_id, " +
                        "bs_contras.scaption as c_name, " +
                        "bs_contras.ncontrasreliability as c_rep, " +
                        "bs_contras.scode as c_code, " +
                        "bs_contras.sinn as c_inn, " +
                        "bs_contras.nfromglobalstatus as c_global_status " +
                        "FROM bs_contras"
        );

        List<Object> params = new ArrayList<>();

        // Фильтр по названию контрагента (оставляем без изменений)
        if (filterContrasName != null && !filterContrasName.trim().isEmpty()) {
            query.append(" WHERE (")
                    .append(" REPLACE(LOWER(bs_contras.scaption), ' ', '') ILIKE REPLACE(LOWER(?), ' ', '') ")
                    .append(" OR REPLACE(bs_contras.scaption, '\"', '') ILIKE REPLACE(?, '\"', '') ")
                    .append(" OR bs_contras.scaption SIMILAR TO ? )");

            params.add("%" + filterContrasName + "%");
            params.add("%" + filterContrasName + "%");
            params.add("%" + filterContrasName.replace(" ", "%") + "%");
        }

        try (Connection conn = getConnection(db_module);
             PreparedStatement stmt = conn.prepareStatement(query.toString())) {

            // Устанавливаем параметры
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }

            try (ResultSet resultSet = stmt.executeQuery()) {
                while (resultSet.next()) {
                    long c_id = resultSet.getLong("c_id");
                    String c_name = resultSet.getString("c_name");
                    double c_rep = resultSet.getDouble("c_rep");
                    String c_code = resultSet.getString("c_code");
                    String c_inn = resultSet.getString("c_inn");
                    int c_global_status = resultSet.getInt("c_global_status");

                    // Передаем все параметры в конструктор
                    filteredCGO.add(new rowContras(
                            c_id, c_name, c_rep, c_code,
                            c_inn != null ? c_inn : "", // Обработка NULL для ИНН
                            c_global_status
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при получении контрагентов: " + e.getMessage());
            e.printStackTrace();
        }

        return filteredCGO;
    }


    public List<rowContrasGoodsOrders> getCGOs(boolean db_module, String filterContrasName, String filterGoodName,
                                               String filterOrderName, String filterCGDateSupply, String filterCGMinVolume,
                                               String okpd2, String group) {
        List<rowContrasGoodsOrders> filteredCGO = new ArrayList<>();
        String query = "SELECT DISTINCT ON (bs_order.id, bs_contras.id, bs_goods.id) " +
                "bs_order.id as o_id, bs_order.scaption as o_name, " +
                "bs_contras.id as c_id, bs_contras.scaption as c_name, bs_contras.scode as c_code, " +
                "bs_goods.id as g_id, bs_goods.sname as g_name, bs_goods.sarticle as g_code, " +
                "prs_lot.ndeliverytime as date_supply, prs_lot.nqty as min_vol, " +
                "prs_lot.ngoodquality as g_qual, bs_contras.ncontrasreliability as c_rep, " +
                "bs_goods.goodmsritem as g_msr " +
                "FROM bs_order " +
                "JOIN mes_workorder ON bs_order.id = mes_workorder.id_order " +
                "JOIN bs_goods ON mes_workorder.id_goods = bs_goods.id " +
                "JOIN prs_lot ON bs_goods.id = prs_lot.id_goods " +
                "JOIN bs_contras ON prs_lot.id_contras = bs_contras.id ";

        List<String> conditions = new ArrayList<>();
        List<Object> params = new ArrayList<>();

        if (!filterContrasName.isEmpty()) {
            conditions.add("(REPLACE(LOWER(bs_contras.scaption), ' ', '') ILIKE REPLACE(LOWER(?), ' ', '') " +
                    "OR REPLACE(bs_contras.scaption, '\"', '') ILIKE REPLACE(?, '\"', '') " +
                    "OR bs_contras.scaption SIMILAR TO ?)");
            params.add("%" + filterContrasName + "%");
            params.add("%" + filterContrasName + "%");
            params.add("%" + filterContrasName.replace(" ", "%") + "%");
        }
        if (!filterGoodName.isEmpty()) {
            conditions.add("(REPLACE(LOWER(bs_goods.sname), ' ', '') ILIKE REPLACE(LOWER(?), ' ', '') " +
                    "OR REPLACE(bs_goods.sname, '\"', '') ILIKE REPLACE(?, '\"', '') " +
                    "OR bs_goods.sname SIMILAR TO ?)");
            params.add("%" + filterGoodName + "%");
            params.add("%" + filterGoodName + "%");
            params.add("%" + filterGoodName.replace(" ", "%") + "%");
        }
        if (!filterOrderName.isEmpty()) {
            conditions.add("(REPLACE(LOWER(bs_order.scaption), ' ', '') ILIKE REPLACE(LOWER(?), ' ', '') " +
                    "OR REPLACE(bs_order.scaption, '\"', '') ILIKE REPLACE(?, '\"', '') " +
                    "OR bs_order.scaption SIMILAR TO ?)");
            params.add("%" + filterOrderName + "%");
            params.add("%" + filterOrderName + "%");
            params.add("%" + filterOrderName.replace(" ", "%") + "%");
        }
        if (!filterCGDateSupply.isEmpty()) {
            conditions.add("prs_lot.ndeliverytime = ?");
            params.add(Integer.parseInt(filterCGDateSupply));
        }
        if (!filterCGMinVolume.isEmpty()) {
            conditions.add("prs_lot.nqty = ?");
            params.add(Double.parseDouble(filterCGMinVolume));
        }
        if (!okpd2.isEmpty()) {
            conditions.add("bs_goods.okpd2 = ?");  // фильтр по полю okpd2 в таблице bs_goods
            params.add(okpd2);
        }

        if (!conditions.isEmpty()) {
            query += "WHERE " + String.join(" AND ", conditions);
        }

        try (Connection conn = getConnection(db_module);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            for (int i = 0; i < params.size(); i++) {
                if (params.get(i) instanceof Integer) {
                    stmt.setInt(i + 1, (Integer) params.get(i));
                } else if (params.get(i) instanceof Double) {
                    stmt.setDouble(i + 1, (Double) params.get(i));
                } else {
                    stmt.setString(i + 1, (String) params.get(i));
                }
            }

            try (ResultSet resultSet = stmt.executeQuery()) {
                while (resultSet.next()) {
                    long o_id = resultSet.getLong("o_id");
                    long c_id = resultSet.getLong("c_id");
                    long g_id = resultSet.getLong("g_id");
                    String o_name = resultSet.getString("o_name");
                    String c_name = resultSet.getString("c_name");
                    String g_name = resultSet.getString("g_name");
                    String c_code = resultSet.getString("c_code");
                    String g_code = resultSet.getString("g_code");
                    int deliveryTime = resultSet.getInt("date_supply");
                    double minQuantity = resultSet.getDouble("min_vol");
                    double g_quality = resultSet.getDouble("g_qual");
                    double c_rep = resultSet.getDouble("c_rep");
                    String g_msr = resultSet.getString("g_msr");

                    filteredCGO.add(new rowContrasGoodsOrders(c_id, c_name, c_code, g_id, g_name, g_code,
                            o_id, o_name, deliveryTime, minQuantity, g_quality, c_rep, g_msr));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return filteredCGO;
    }



    public List<rowCritValues> getCrVas(
            boolean db_module, String CritString, String CritShort,
            String filterContrasName, String filterGoodName,
            String filterOrderName, String filterCGDateSupply, String filterCGMinVolume, String okpd2) {

        List<rowCritValues> filteredCrVa = new ArrayList<>();

        // Базовый запрос - обратите внимание на пробелы в конце строк
        StringBuilder query = new StringBuilder()
                .append("SELECT DISTINCT ").append(CritString).append(" as ").append(CritShort)
                .append(" FROM bs_order ")
                .append("JOIN mes_workorder ON bs_order.id = mes_workorder.id_order ")
                .append("JOIN bs_goods ON mes_workorder.id_goods = bs_goods.id ")
                .append("JOIN prs_lot ON bs_goods.id = prs_lot.id_goods ")
                .append("JOIN bs_contras ON prs_lot.id_contras = bs_contras.id ");

        List<String> conditions = new ArrayList<>();
        List<Object> params = new ArrayList<>();

        // Добавляем условия фильтрации
        if (!filterContrasName.isEmpty()) {
            conditions.add("bs_contras.scaption ILIKE ?");
            params.add("%" + filterContrasName + "%");
        }
        if (!filterGoodName.isEmpty()) {
            conditions.add("bs_goods.sname ILIKE ?");
            params.add("%" + filterGoodName + "%");
        }
        if (!filterOrderName.isEmpty()) {
            conditions.add("bs_order.scaption ILIKE ?");
            params.add("%" + filterOrderName + "%");
        }
        if (!filterCGDateSupply.isEmpty()) {
            conditions.add("prs_lot.ndeliverytime = ?");
            params.add(Integer.parseInt(filterCGDateSupply));
        }
        if (!filterCGMinVolume.isEmpty()) {
            conditions.add("prs_lot.nqty = ?");
            params.add(Double.parseDouble(filterCGMinVolume));
        }
        if (!okpd2.isEmpty()) {
            conditions.add("bs_goods.okpd2 = ?");
            params.add(okpd2);
        }

        // Добавляем условия к запросу
        if (!conditions.isEmpty()) {
            query.append(" WHERE ").append(String.join(" AND ", conditions));
        }

        // Добавляем сортировку
        query.append(" ORDER BY ").append(CritString);

        try (Connection conn = getConnection(db_module);
             PreparedStatement stmt = conn.prepareStatement(query.toString())) {

            // Устанавливаем параметры
            for (int i = 0; i < params.size(); i++) {
                Object param = params.get(i);
                if (param instanceof Integer) {
                    stmt.setInt(i + 1, (Integer) param);
                } else if (param instanceof Double) {
                    stmt.setDouble(i + 1, (Double) param);
                } else {
                    stmt.setString(i + 1, (String) param);
                }
            }

            try (ResultSet resultSet = stmt.executeQuery()) {
                while (resultSet.next()) {
                    double critVal = resultSet.getDouble(CritShort);
                    filteredCrVa.add(new rowCritValues(critVal, 0.0));
                }
            }
        } catch (SQLException e) {
            System.err.println("SQL Error executing query: " + e.getMessage());
            e.printStackTrace();
        }

        return filteredCrVa;
    }


    public List<rowCritValues> getUserCrVas(
            boolean db_module, String CritString, String CritShort,
            String filterContrasName, String filterGoodName,
            String filterOrderName, String filterCGDateSupply, String filterCGMinVolume, String okpd2) {

        List<rowCritValues> filteredCrVa = new ArrayList<>();

        // Базовый запрос с использованием StringBuilder для безопасности
        StringBuilder query = new StringBuilder()
                .append("SELECT DISTINCT module_lotcriterion.").append(CritString).append(" as ").append(CritShort)
                .append(" FROM bs_order ")
                .append("JOIN mes_workorder ON bs_order.id = mes_workorder.id_order ")
                .append("JOIN bs_goods ON mes_workorder.id_goods = bs_goods.id ")
                .append("JOIN prs_lot ON bs_goods.id = prs_lot.id_goods ")
                .append("JOIN bs_contras ON prs_lot.id_contras = bs_contras.id ")
                .append("JOIN module_lotcriterion ON module_lotcriterion.id_contras = bs_contras.id ")
                .append("AND module_lotcriterion.id_goods = bs_goods.id ");

        List<String> conditions = new ArrayList<>();
        List<Object> params = new ArrayList<>();

        // Добавляем условия фильтрации с параметрами
        if (!filterContrasName.isEmpty()) {
            conditions.add("bs_contras.scaption ILIKE ?");
            params.add("%" + filterContrasName + "%");
        }
        if (!filterGoodName.isEmpty()) {
            conditions.add("bs_goods.sname ILIKE ?");
            params.add("%" + filterGoodName + "%");
        }
        if (!filterOrderName.isEmpty()) {
            conditions.add("bs_order.scaption ILIKE ?");
            params.add("%" + filterOrderName + "%");
        }
        if (!filterCGDateSupply.isEmpty()) {
            conditions.add("prs_lot.ndeliverytime = ?");
            params.add(filterCGDateSupply); // как строка, если поле текстовое
        }
        if (!filterCGMinVolume.isEmpty()) {
            conditions.add("prs_lot.nqty = ?");
            params.add(Double.parseDouble(filterCGMinVolume));
        }
        if (!okpd2.isEmpty()) {
            conditions.add("bs_goods.okpd2 = ?");
            params.add(okpd2);
        }

        // Добавляем условия к запросу
        if (!conditions.isEmpty()) {
            query.append(" WHERE ").append(String.join(" AND ", conditions));
        }

        // Добавляем сортировку
        query.append(" ORDER BY module_lotcriterion.").append(CritString);


        try (Connection conn = getConnection(db_module);
             PreparedStatement stmt = conn.prepareStatement(query.toString())) {

            // Устанавливаем параметры
            for (int i = 0; i < params.size(); i++) {
                Object param = params.get(i);
                if (param instanceof Integer) {
                    stmt.setInt(i + 1, (Integer) param);
                } else if (param instanceof Double) {
                    stmt.setDouble(i + 1, (Double) param);
                } else {
                    stmt.setString(i + 1, param.toString());
                }
            }

            try (ResultSet resultSet = stmt.executeQuery()) {
                while (resultSet.next()) {
                    double critVal = resultSet.getDouble(CritShort);
                    filteredCrVa.add(new rowCritValues(critVal, 0.0));
                }
            }
        } catch (SQLException e) {
            System.err.println("SQL Error executing query: " + e.getMessage());
            e.printStackTrace();
        }

        return filteredCrVa;
    }


    public void setCritData(boolean db_module) {
        String query = "CREATE TABLE IF NOT EXISTS public.module_criterion (id bigint NOT NULL, sname character varying(254), nfunctiontype int, nminval numeric(38,18), nmaxval numeric(38,18), nweight numeric(38,18), jdatapoints jsonb)";

        try {
            this.executeQueryNoResult(db_module, query);
        } catch (SQLException var4) {
            var4.printStackTrace();
        }

    }

    public long getMaxCritId(boolean db_module) {
        String query = "SELECT COALESCE(MAX(id), 3) FROM public.module_criterion";
        try (Connection conn = this.getConnection(db_module);
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getLong(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 3; // fallback, если таблица пустая
    }

    public List<rowCritData> getCritData(boolean db_module, long CritId) {
        List<rowCritData> critData = new ArrayList();
        String query = "SELECT DISTINCT id as cr_id, sname as cr_name, nfunctiontype as cr_fun, nminval as cr_min, nmaxval as cr_max, nweight as cr_weight, jdatapoints as cr_pts  FROM module_criterion WHERE id = " + CritId + " ";

        try {
            ResultSet resultSet = this.executeQuery(db_module, query);

            try {
                while(resultSet.next()) {
                    long crId = resultSet.getLong("cr_id");
                    String crName = resultSet.getString("cr_name");
                    int crFun = resultSet.getInt("cr_fun");
                    double crMin = resultSet.getDouble("cr_min");
                    double crMax = resultSet.getDouble("cr_max");
                    double crW = resultSet.getDouble("cr_weight");
                    String crPoints = resultSet.getString("cr_pts");
                    critData.add(new rowCritData(crId, crName, crFun, crMin, crMax, crW, crPoints));
                }
            } catch (Throwable var19) {
                if (resultSet != null) {
                    try {
                        resultSet.close();
                    } catch (Throwable var18) {
                        var19.addSuppressed(var18);
                    }
                }

                throw var19;
            }

            if (resultSet != null) {
                resultSet.close();
            }
        } catch (SQLException var20) {
            var20.printStackTrace();
        }

        return critData;
    }

    public void addCritData(boolean db_module, long critId, String critName, String funcInd, String minVal, String maxVal, String critW, String jPoints) {
        String query = "INSERT INTO public.module_criterion (id, sname, nfunctiontype, nminval, nmaxval, nweight, jdatapoints) VALUES (" + critId + ",'" + critName + "','" + funcInd + "','" + minVal + "','" + maxVal + "','" + critW + "','" + jPoints + "')";

        try {
            this.executeQueryNoResult(db_module, query);
        } catch (SQLException var12) {
            var12.printStackTrace();
        }

    }

    public long addUserCritData(boolean db_module, String critName, String funcInd, String minVal, String maxVal, String critW, String jPoints) {
        String query = "INSERT INTO public.module_criterion (sname, nfunctiontype, nminval, nmaxval, nweight, jdatapoints) " +
                "VALUES ('" + critName + "', '" + funcInd + "', '" + minVal + "', '" + maxVal + "', '" + critW + "', '" + jPoints + "') RETURNING id";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = this.getConnection(db_module);  // Открываем соединение
            stmt = conn.prepareStatement(query);  // Подготавливаем запрос
            rs = stmt.executeQuery();  // Выполняем запрос

            if (rs.next()) {
                return rs.getLong("id");  // Возвращаем сгенерированный id
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();  // Закрываем ResultSet
                }
                if (stmt != null) {
                    stmt.close();  // Закрываем PreparedStatement
                }
                if (conn != null) {
                    conn.close();  // Закрываем соединение
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return -1;  // Если ошибка, возвращаем -1
    }
    public List<rowCritData> getAllCritData(boolean db_module) {
        List<rowCritData> critData = new ArrayList<>();
        String query = "SELECT DISTINCT id as cr_id, sname as cr_name, nfunctiontype as cr_fun, nminval as cr_min, nmaxval as cr_max, nweight as cr_weight, jdatapoints as cr_pts FROM module_criterion WHERE id > 3";

        try {
            ResultSet resultSet = this.executeQuery(db_module, query);

            try {
                while (resultSet.next()) {
                    long crId = resultSet.getLong("cr_id");
                    String crName = resultSet.getString("cr_name");
                    int crFun = resultSet.getInt("cr_fun");
                    double crMin = resultSet.getDouble("cr_min");
                    double crMax = resultSet.getDouble("cr_max");
                    double crW = resultSet.getDouble("cr_weight");
                    String crPoints = resultSet.getString("cr_pts");
                    critData.add(new rowCritData(crId, crName, crFun, crMin, crMax, crW, crPoints));
                }
            } catch (Throwable var19) {
                if (resultSet != null) {
                    try {
                        resultSet.close();
                    } catch (Throwable var18) {
                        var19.addSuppressed(var18);
                    }
                }
                throw var19;
            }

            if (resultSet != null) {
                resultSet.close();
            }
        } catch (SQLException var20) {
            var20.printStackTrace();
        }

        return critData;
    }

    public void changeCritData(boolean db_module, long critId, String critName, String funcInd, String minVal, String maxVal, String critW, String jPoints) {
        String query = "UPDATE public.module_criterion SET sname = '" + critName + "', nfunctiontype = '" + funcInd + "', nminval = '" + minVal + "', nmaxval = '" + maxVal + "', nweight = '" + critW + "', jdatapoints = '" + jPoints + "' WHERE id = " + critId + " ";

        try {
            this.executeQueryNoResult(db_module, query);
        } catch (SQLException var12) {
            var12.printStackTrace();
        }

    }

    public void setUserCritData(boolean db_module) {
        String query = "CREATE TABLE IF NOT EXISTS public.module_lotcriterion (id_contras bigint NOT NULL, id_goods bigint NOT NULL, CONSTRAINT cgl UNIQUE(id_contras, id_goods))";

        try {
            this.executeQueryNoResult(db_module, query);
        } catch (SQLException var4) {
            var4.printStackTrace();
        }

    }

    public void alterUserCritData(boolean db_module, String critStr) {
        String query = "ALTER TABLE public.module_lotcriterion ADD COLUMN IF NOT EXISTS " + critStr + " numeric(38,18)";

        try {
            this.executeQueryNoResult(db_module, query);
        } catch (SQLException var5) {
            var5.printStackTrace();
        }

    }

    public void updateCGsUserCritValues(boolean db_module) {
        String query = "INSERT INTO public.module_lotcriterion ( SELECT DISTINCT bs_contras.id as c_id, bs_goods.id as g_id FROM bs_order JOIN mes_workorder ON bs_order.id = mes_workorder.id_order JOIN bs_goods ON mes_workorder.id_goods = bs_goods.id JOIN prs_lot ON bs_goods.id = prs_lot.id_goods JOIN bs_contras ON prs_lot.id_contras = bs_contras.id ) ON CONFLICT (id_contras, id_goods) DO NOTHING ";

        try {
            this.executeQueryNoResult(db_module, query);
        } catch (SQLException var4) {
            var4.printStackTrace();
        }

    }

    public List<rowCritData> getUserCritData(boolean db_module) {
        List<rowCritData> critData = new ArrayList();
        String query = "SELECT DISTINCT id as cr_id, sname as cr_name, nfunctiontype as cr_fun, nminval as cr_min, nmaxval as cr_max, nweight as cr_weight, jdatapoints as cr_pts  FROM module_criterion WHERE id > 3 ORDER BY id ";

        try {
            ResultSet resultSet = this.executeQuery(db_module, query);

            try {
                while(resultSet.next()) {
                    long crId = resultSet.getLong("cr_id");
                    String crName = resultSet.getString("cr_name");
                    int crFun = resultSet.getInt("cr_fun");
                    double crMin = resultSet.getDouble("cr_min");
                    double crMax = resultSet.getDouble("cr_max");
                    double crW = resultSet.getDouble("cr_weight");
                    String crPoints = resultSet.getString("cr_pts");
                    critData.add(new rowCritData(crId, crName, crFun, crMin, crMax, crW, crPoints));
                }
            } catch (Throwable var17) {
                if (resultSet != null) {
                    try {
                        resultSet.close();
                    } catch (Throwable var16) {
                        var17.addSuppressed(var16);
                    }
                }

                throw var17;
            }

            if (resultSet != null) {
                resultSet.close();
            }
        } catch (SQLException var18) {
            var18.printStackTrace();
        }

        return critData;
    }

    public List<rowContrasGoodsOrders> getCGOsUserCrVas(
            boolean db_module, String CritString, String CritShort,
            String filterContrasName, String filterGoodName,
            String filterOrderName, String filterCGDateSupply, String filterCGMinVolume, String okpd2) {

        List<rowContrasGoodsOrders> filteredCGOsUsCrVa = new ArrayList<>();

        // Базовый запрос с использованием StringBuilder
        StringBuilder query = new StringBuilder()
                .append("SELECT DISTINCT ")
                .append("bs_contras.id AS c_id, bs_contras.scaption AS c_name, bs_contras.scode AS c_code, ")
                .append("bs_goods.id AS g_id, bs_goods.sname AS g_name, bs_goods.sarticle AS g_code, ")
                .append("module_lotcriterion.").append(CritString).append(" AS ").append(CritShort).append(", ")
                .append("bs_goods.okpd2 AS g_okpd2 ") // Добавляем okpd2 в выборку
                .append("FROM bs_order ")
                .append("JOIN mes_workorder ON bs_order.id = mes_workorder.id_order ")
                .append("JOIN bs_goods ON mes_workorder.id_goods = bs_goods.id ")
                .append("JOIN prs_lot ON bs_goods.id = prs_lot.id_goods ")
                .append("JOIN bs_contras ON prs_lot.id_contras = bs_contras.id ")
                .append("JOIN module_lotcriterion ON module_lotcriterion.id_contras = bs_contras.id ")
                .append("AND module_lotcriterion.id_goods = bs_goods.id ");

        List<String> conditions = new ArrayList<>();
        List<Object> params = new ArrayList<>();

        // Добавляем условия фильтрации с параметрами
        if (!filterContrasName.isEmpty()) {
            conditions.add("bs_contras.scaption ILIKE ?");
            params.add("%" + filterContrasName + "%");
        }
        if (!filterGoodName.isEmpty()) {
            conditions.add("bs_goods.sname ILIKE ?");
            params.add("%" + filterGoodName + "%");
        }
        if (!filterOrderName.isEmpty()) {
            conditions.add("bs_order.scaption ILIKE ?");
            params.add("%" + filterOrderName + "%");
        }
        if (!filterCGDateSupply.isEmpty()) {
            conditions.add("prs_lot.ndeliverytime = ?");
            params.add(filterCGDateSupply); // как строка, если поле текстовое
        }
        if (!filterCGMinVolume.isEmpty()) {
            conditions.add("prs_lot.nqty = ?");
            params.add(Double.parseDouble(filterCGMinVolume));
        }
        if (!okpd2.isEmpty()) {
            conditions.add("bs_goods.okpd2 = ?");
            params.add(okpd2);
        }

        // Добавляем условия к запросу
        if (!conditions.isEmpty()) {
            query.append(" WHERE ").append(String.join(" AND ", conditions));
        }

        // Добавляем сортировку
        query.append(" ORDER BY bs_contras.scaption, bs_goods.sname");


        try (Connection conn = getConnection(db_module);
             PreparedStatement stmt = conn.prepareStatement(query.toString())) {

            // Устанавливаем параметры
            for (int i = 0; i < params.size(); i++) {
                Object param = params.get(i);
                if (param instanceof Integer) {
                    stmt.setInt(i + 1, (Integer) param);
                } else if (param instanceof Double) {
                    stmt.setDouble(i + 1, (Double) param);
                } else {
                    stmt.setString(i + 1, param.toString());
                }
            }

            try (ResultSet resultSet = stmt.executeQuery()) {
                while (resultSet.next()) {
                    long c_id = resultSet.getLong("c_id");
                    long g_id = resultSet.getLong("g_id");
                    String c_name = resultSet.getString("c_name");
                    String g_name = resultSet.getString("g_name");
                    String c_code = resultSet.getString("c_code");
                    String g_code = resultSet.getString("g_code");
                    double minQuantity = resultSet.getDouble(CritShort);
                    String g_okpd2 = resultSet.getString("g_okpd2"); // Получаем okpd2

                    filteredCGOsUsCrVa.add(new rowContrasGoodsOrders(
                            c_id, c_name, c_code, g_id, g_name, g_code,
                            0L, "", 0, minQuantity, 0.0, 0.0, g_okpd2 // Передаем okpd2
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("SQL Error executing query: " + e.getMessage());
            e.printStackTrace();
        }

        return filteredCGOsUsCrVa;
    }


    public void updateContrasGoodsUsCrForEdit(boolean db_module, List<rowContrasGoodsOrders> rowsCGOs, String CritString) {
        String query = "UPDATE public.module_lotcriterion set " + CritString + " = ? WHERE ( id_contras = ? ) AND ( id_goods = ? )";

        try {
            this.executeQueryBatchCGsUsCrUpdateNoName(db_module, query, rowsCGOs);
        } catch (SQLException var6) {
            var6.printStackTrace();
        }

    }

    public void updateNdeliverytimeAndNqtyFromModuleLotcriterion(boolean db_main) {
        Connection connection = null;
        PreparedStatement statement = null;

        // Запрос для обновления существующих данных в prs_lot
        String query = "UPDATE prs_lot " +
                "SET ndeliverytime = module_lotcriterion.ndeliverytime, " +
                "    nqty = module_lotcriterion.nqty " +
                "FROM module_lotcriterion " +
                "WHERE prs_lot.id_goods = module_lotcriterion.id_goods " +
                "  AND prs_lot.id_contras = module_lotcriterion.id_contras";

        try {
            // Получаем соединение с базой данных
            connection = this.getConnection(db_main);
            connection.setAutoCommit(false); // Отключаем автокоммит для транзакций

            // Подготовка запроса
            statement = connection.prepareStatement(query);

            // Выполнение запроса
            int rowsUpdated = statement.executeUpdate();

            // Если все прошло успешно, фиксируем изменения
            connection.commit();
            System.out.println("Обновлено записей: " + rowsUpdated);
        } catch (SQLException ex) {
            try {
                if (connection != null) {
                    connection.rollback(); // Откатываем изменения в случае ошибки
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            ex.printStackTrace();
        } finally {
            // Закрываем ресурсы
            closeResources(connection, statement, null);
        }
    }












    public List<rowContrasGoodsOrdersWithWeights> getCGOwes(boolean db_module, String filterContrasName, String filterGoodName, String filterOrderName, String filterCGDateSupply, String filterCGMinVolume) {
        List<rowContrasGoodsOrdersWithWeights> filteredCGOwes = new ArrayList();
        String query = "SELECT bs_order.id as o_id, bs_order.scaption as o_name, bs_contras.id as c_id, bs_contras.scaption as c_name, bs_goods.id as g_id, bs_goods.sname as g_name, prs_lot.ndeliverytime as date_supply, prs_lot.nqty as min_vol, prs_lot.ngoodquality as g_qual, bs_contras.ncontrasreliability as c_rep  FROM bs_order JOIN mes_workorder ON bs_order.id = mes_workorder.id_order JOIN bs_goods ON mes_workorder.id_goods = bs_goods.id JOIN prs_lot ON bs_goods.id = prs_lot.id_goods JOIN bs_contras ON prs_lot.id_contras = bs_contras.id ";
        if (!filterContrasName.isEmpty() || !filterGoodName.isEmpty() || !filterOrderName.isEmpty() || !filterCGDateSupply.isEmpty() || !filterCGMinVolume.isEmpty()) {
            query = query + "WHERE ";
            boolean is_not_first = false;
            if (!filterContrasName.isEmpty()) {
                query = query + "(bs_contras.scaption LIKE '%" + filterContrasName + "%') ";
                is_not_first = true;
            }

            if (!filterGoodName.isEmpty()) {
                if (is_not_first) {
                    query = query + "AND ";
                }

                query = query + "(bs_goods.sname LIKE '%" + filterGoodName + "%') ";
                is_not_first = true;
            }

            if (!filterOrderName.isEmpty()) {
                if (is_not_first) {
                    query = query + "AND ";
                }

                query = query + "(bs_order.scaption LIKE '%" + filterOrderName + "%') ";
                is_not_first = true;
            }

            if (!filterCGDateSupply.isEmpty()) {
                if (is_not_first) {
                    query = query + "AND ";
                }

                query = query + "(prs_lot.ndeliverytime = " + filterCGDateSupply + ") ";
                is_not_first = true;
            }

            if (!filterCGMinVolume.isEmpty()) {
                if (is_not_first) {
                    query = query + "AND ";
                }

                query = query + "(prs_lot.nqty = " + filterCGMinVolume + ") ";
            }
        }

        try {
            ResultSet resultSet = this.executeQuery(db_module, query);

            try {
                while(resultSet.next()) {
                    long o_id = resultSet.getLong("o_id");
                    long c_id = resultSet.getLong("c_id");
                    long g_id = resultSet.getLong("g_id");
                    String o_name = resultSet.getString("o_name");
                    String c_name = resultSet.getString("c_name");
                    String g_name = resultSet.getString("g_name");
                    int deliveryTime = resultSet.getInt("date_supply");
                    double minQuantity = resultSet.getDouble("min_vol");
                    double g_quality = resultSet.getDouble("g_qual");
                    double c_rep = resultSet.getDouble("c_rep");
                    filteredCGOwes.add(new rowContrasGoodsOrdersWithWeights(c_id, c_name, g_id, g_name, o_id, o_name, deliveryTime, 1.0, minQuantity, 1.0, g_quality, 1.0, c_rep, 1.0, (List)null, 1.0));
                }
            } catch (Throwable var27) {
                if (resultSet != null) {
                    try {
                        resultSet.close();
                    } catch (Throwable var26) {
                        var27.addSuppressed(var26);
                    }
                }

                throw var27;
            }

            if (resultSet != null) {
                resultSet.close();
            }
        } catch (SQLException var28) {
            var28.printStackTrace();
        }

        return filteredCGOwes;
    }

    public List<rowContrasGoodsOrdersWithWeights> getCGOwesAsUserCrit(boolean db_module, String filterContrasName, String filterGoodName, String filterOrderName, String filterCGDateSupply, String filterCGMinVolume, String okpd2) {
        List<rowContrasGoodsOrdersWithWeights> filteredCGOwes = new ArrayList<>();

        // Базовый запрос
        String query = "SELECT DISTINCT bs_order.id AS o_id, bs_order.scaption AS o_name, " +
                "bs_contras.id AS c_id, bs_contras.scaption AS c_name, " +
                "bs_goods.id AS g_id, bs_goods.sname AS g_name, " +
                "module_lotcriterion.ndeliverytime AS date_supply, " +
                "module_lotcriterion.nqty AS min_vol, " +
                "module_lotcriterion.ngoodquality AS g_qual, " +
                "bs_contras.ncontrasreliability AS c_rep, " +
                "bs_goods.okpd2 AS g_okpd2 " +
                "FROM bs_order " +
                "JOIN mes_workorder ON bs_order.id = mes_workorder.id_order " +
                "JOIN bs_goods ON mes_workorder.id_goods = bs_goods.id " +
                "JOIN module_lotcriterion ON bs_goods.id = module_lotcriterion.id_goods " +
                "JOIN bs_contras ON module_lotcriterion.id_contras = bs_contras.id " +
                "WHERE 1=1";

        List<String> conditions = new ArrayList<>();
        List<Object> params = new ArrayList<>();

        if (!filterContrasName.isEmpty()) {
            conditions.add("bs_contras.scaption ILIKE ?");
            params.add("%" + filterContrasName.trim() + "%");
        }

        if (!filterGoodName.isEmpty()) {
            conditions.add("bs_goods.sname ILIKE ?");
            params.add("%" + filterGoodName.trim() + "%");
        }

        if (!filterOrderName.isEmpty()) {
            conditions.add("bs_order.scaption ILIKE ?");
            params.add("%" + filterOrderName.trim() + "%");
        }

        if (!filterCGDateSupply.isEmpty()) {
            conditions.add("module_lotcriterion.ndeliverytime = ?");
            params.add(Integer.parseInt(filterCGDateSupply));
        }

        if (!filterCGMinVolume.isEmpty()) {
            conditions.add("module_lotcriterion.nqty = ?");
            params.add(Double.parseDouble(filterCGMinVolume));
        }

        if (!okpd2.isEmpty()) {
            conditions.add("bs_goods.okpd2 = ?");
            params.add(okpd2);
        }

        if (!conditions.isEmpty()) {
            query += " AND " + String.join(" AND ", conditions);
        }

        try (Connection conn = getConnection(db_module);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            for (int i = 0; i < params.size(); i++) {
                if (params.get(i) instanceof Integer) {
                    stmt.setInt(i + 1, (Integer) params.get(i));
                } else if (params.get(i) instanceof Double) {
                    stmt.setDouble(i + 1, (Double) params.get(i));
                } else {
                    stmt.setString(i + 1, (String) params.get(i));
                }
            }

            try (ResultSet resultSet = stmt.executeQuery()) {
                while (resultSet.next()) {
                    long o_id = resultSet.getLong("o_id");
                    long c_id = resultSet.getLong("c_id");
                    long g_id = resultSet.getLong("g_id");
                    String o_name = resultSet.getString("o_name");
                    String c_name = resultSet.getString("c_name");
                    String g_name = resultSet.getString("g_name");
                    int deliveryTime = resultSet.getInt("date_supply");
                    double minQuantity = resultSet.getDouble("min_vol");
                    double g_quality = resultSet.getDouble("g_qual");
                    double c_rep = resultSet.getDouble("c_rep");

                    filteredCGOwes.add(new rowContrasGoodsOrdersWithWeights(
                            c_id, c_name, g_id, g_name, o_id, o_name,
                            deliveryTime, 1.0, minQuantity, 1.0,
                            g_quality, 1.0, c_rep, 1.0, null, 1.0
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return filteredCGOwes;
    }



    public void setRatingTable(boolean db_module) {
        String query = "CREATE TABLE IF NOT EXISTS public.module_rating (id_orders bigint NOT NULL, id_contras bigint NOT NULL, id_goods bigint NOT NULL, nrating numeric(38,18))";

        try {
            this.executeQueryNoResult(db_module, query);
        } catch (SQLException var4) {
            var4.printStackTrace();
        }

    }

    public void updateRatingTable(boolean db_module, List<rowContrasGoodsOrdersWithWeights> rowsCGOws) {
        String query = "DELETE FROM public.module_rating";

        try {
            this.executeQueryNoResult(db_module, query);
        } catch (SQLException var6) {
            var6.printStackTrace();
        }

        query = "INSERT INTO public.module_rating (id_orders, id_contras, id_goods, nrating) VALUES (?,?,?,?)";

        try {
            this.executeQueryBatchRating(db_module, query, rowsCGOws);
        } catch (SQLException var5) {
            var5.printStackTrace();
        }

    }

    public List<rowContrasGoodsOrdersWithWeights> getCGOwesBest(boolean db_module, String filterGood) {
        List<rowContrasGoodsOrdersWithWeights> ratedCGOwes = new ArrayList<>();
        String query = "SELECT DISTINCT bs_order.id as o_id, bs_order.scaption as o_name, " +
                "bs_contras.id as c_id, bs_contras.scaption as c_name, " +
                "bs_goods.id as g_id, bs_goods.sname as g_name, " +
                "module_rating.nrating as r_rat " +
                "FROM bs_order " +
                "JOIN module_rating ON bs_order.id = module_rating.id_orders " +
                "JOIN bs_goods ON module_rating.id_goods = bs_goods.id " +
                "JOIN bs_contras ON module_rating.id_contras = bs_contras.id ";

        if (!filterGood.isEmpty()) {
            query += "WHERE TRIM(bs_goods.sname) ILIKE TRIM('%" + filterGood + "%') ";
        }

        query += "ORDER BY module_rating.nrating DESC, bs_order.scaption, bs_goods.id ";

        try {
            ResultSet resultSet = this.executeQuery(db_module, query);
            try {
                while (resultSet.next()) {
                    long o_id = resultSet.getLong("o_id");
                    long c_id = resultSet.getLong("c_id");
                    long g_id = resultSet.getLong("g_id");
                    String o_name = resultSet.getString("o_name").trim();
                    String c_name = resultSet.getString("c_name").trim();
                    String g_name = resultSet.getString("g_name").trim();
                    double r_rat = resultSet.getDouble("r_rat");

                    ratedCGOwes.add(new rowContrasGoodsOrdersWithWeights(
                            c_id, c_name, g_id, g_name, o_id, o_name,
                            0, 1.0D, 0.0D, 1.0D, 0.0D, 1.0D, 0.0D, 1.0D, null, r_rat));
                }
            } catch (Throwable var18) {
                if (resultSet != null) {
                    try {
                        resultSet.close();
                    } catch (Throwable var17) {
                        var18.addSuppressed(var17);
                    }
                }
                throw var18;
            }
            if (resultSet != null) {
                resultSet.close();
            }
        } catch (SQLException var19) {
            var19.printStackTrace();
        }

        return ratedCGOwes;
    }


    public List<rowContrasGoodsOrders> getCGOCritVols(boolean db_module, String filterOrderName) {
        List<rowContrasGoodsOrders> filteredCGO_CV = new ArrayList<>();
        StringBuilder query = new StringBuilder(
                "SELECT DISTINCT bs_order.id as o_id, bs_order.scaption as o_name " +
                        "FROM bs_order " +
                        "JOIN mes_workorder ON bs_order.id = mes_workorder.id_order " +
                        "JOIN bs_goods ON mes_workorder.id_goods = bs_goods.id " +
                        "JOIN prs_lot ON bs_goods.id = prs_lot.id_goods " +
                        "JOIN bs_contras ON prs_lot.id_contras = bs_contras.id"
        );

        if (!filterOrderName.isEmpty()) {
            query.append(" WHERE bs_order.scaption ILIKE '%").append(filterOrderName).append("%' ");
        }

        try {
            ResultSet resultSet = this.executeQuery(db_module, query.toString());

            try {
                while (resultSet.next()) {
                    long o_id = resultSet.getLong("o_id");
                    String o_name = resultSet.getString("o_name");
                    filteredCGO_CV.add(new rowContrasGoodsOrders(0L, "", "", 0L, "", "", o_id, o_name, 0, 0.0, 0.0, 0.0, ""));
                }
            } catch (Throwable var10) {
                if (resultSet != null) {
                    try {
                        resultSet.close();
                    } catch (Throwable var9) {
                        var10.addSuppressed(var9);
                    }
                }
                throw var10;
            }

            if (resultSet != null) {
                resultSet.close();
            }
        } catch (SQLException var11) {
            var11.printStackTrace();
        }

        return filteredCGO_CV;
    }


    public List<rowGoodsOrders> getCGOCritVolGoods(boolean db_module, String fullOrderId) {
        List<rowGoodsOrders> filteredCGO_CV = new ArrayList();
        String query = "SELECT DISTINCT bs_order.scaption as o_name, bs_goods.id as g_id, bs_goods.sname as g_name, mes_workorder.nqtybase as g_qty, bs_goods.goodmsritem as g_msr FROM bs_order JOIN mes_workorder ON bs_order.id = mes_workorder.id_order JOIN bs_goods ON mes_workorder.id_goods = bs_goods.id JOIN prs_lot ON bs_goods.id = prs_lot.id_goods JOIN bs_contras ON prs_lot.id_contras = bs_contras.id WHERE bs_order.id = '" + fullOrderId + "' ";

        try {
            ResultSet resultSet = this.executeQuery(db_module, query);

            try {
                while(resultSet.next()) {
                    long g_id = resultSet.getLong("g_id");
                    String o_name = resultSet.getString("o_name");
                    String g_name = resultSet.getString("g_name");
                    double g_quantity = resultSet.getDouble("g_qty");
                    String g_msr = resultSet.getString("g_msr");
                    filteredCGO_CV.add(new rowGoodsOrders(g_id, g_name, 0L, o_name, g_quantity, (Date)null, (Date)null, g_msr));
                }
            } catch (Throwable var14) {
                if (resultSet != null) {
                    try {
                        resultSet.close();
                    } catch (Throwable var13) {
                        var14.addSuppressed(var13);
                    }
                }

                throw var14;
            }

            if (resultSet != null) {
                resultSet.close();
            }
        } catch (SQLException var15) {
            var15.printStackTrace();
        }

        return filteredCGO_CV;
    }

    public List<rowContrasGoodsOrders> getCGOCritVolParams(boolean db_module, String fullOrderId) {
        List<rowContrasGoodsOrders> filteredCGO_CV = new ArrayList();
        String query = "SELECT DISTINCT bs_order.scaption as o_name, bs_goods.sname as g_name, bs_contras.scaption as c_name, prs_lot.ndeliverytime as date_supply, prs_lot.nqty as min_vol, bs_goods.goodmsritem as g_msr FROM bs_order JOIN mes_workorder ON bs_order.id = mes_workorder.id_order JOIN bs_goods ON mes_workorder.id_goods = bs_goods.id JOIN prs_lot ON bs_goods.id = prs_lot.id_goods JOIN bs_contras ON prs_lot.id_contras = bs_contras.id WHERE bs_order.id = '" + fullOrderId + "' ";

        try {
            ResultSet resultSet = this.executeQuery(db_module, query);

            try {
                while(resultSet.next()) {
                    String o_name = resultSet.getString("o_name");
                    String c_name = resultSet.getString("c_name");
                    String g_name = resultSet.getString("g_name");
                    int deliveryTime = resultSet.getInt("date_supply");
                    double minQuantity = resultSet.getDouble("min_vol");
                    String g_msr = resultSet.getString("g_msr");
                    filteredCGO_CV.add(new rowContrasGoodsOrders(0L, c_name, "", 0L, g_name, "", 0L, o_name, deliveryTime, minQuantity, 0.0, 0.0, g_msr));
                }
            } catch (Throwable var14) {
                if (resultSet != null) {
                    try {
                        resultSet.close();
                    } catch (Throwable var13) {
                        var14.addSuppressed(var13);
                    }
                }

                throw var14;
            }

            if (resultSet != null) {
                resultSet.close();
            }
        } catch (SQLException var15) {
            var15.printStackTrace();
        }

        return filteredCGO_CV;
    }

    public List<rowGoodsOrders> getOCritVolDates(boolean db_module, String fullOrderId, String fullGoodId) {
        List<rowGoodsOrders> filteredCGO_CV = new ArrayList();
        String query = "SELECT DISTINCT bs_order.scaption as o_name, mes_workorder.nqtybase as g_qty, bs_order.ddate as date_start, bs_order.ddatecloseplan as date_end FROM bs_order JOIN mes_workorder ON bs_order.id = mes_workorder.id_order WHERE bs_order.id = '" + fullOrderId + "' AND mes_workorder.id_goods = '" + fullGoodId + "' AND bs_order.ddate is NOT NULL AND bs_order.ddatecloseplan is NOT NULL";

        try {
            ResultSet resultSet = this.executeQuery(db_module, query);

            try {
                while(resultSet.next()) {
                    String o_name = resultSet.getString("o_name");
                    Date s_date = resultSet.getDate("date_start");
                    Date e_date = resultSet.getDate("date_end");
                    double g_quantity = resultSet.getDouble("g_qty");
                    filteredCGO_CV.add(new rowGoodsOrders(0L, "", 0L, o_name, g_quantity, s_date, e_date, ""));
                }
            } catch (Throwable var13) {
                if (resultSet != null) {
                    try {
                        resultSet.close();
                    } catch (Throwable var12) {
                        var13.addSuppressed(var12);
                    }
                }

                throw var13;
            }

            if (resultSet != null) {
                resultSet.close();
            }
        } catch (SQLException var14) {
            var14.printStackTrace();
        }

        return filteredCGO_CV;
    }

    public List<rowGoodsOrders> getGOCritVolIntDates(boolean db_module, String fullOrderId, String fullGoodId) {
        List<rowGoodsOrders> filteredCGO_CV = new ArrayList();
        String query = "SELECT DISTINCT bs_order.scaption as o_name, bs_goods.sname as g_name, module_workorder.nqtybase as nq_ty, module_workorder.dplanbegin as date_start, module_workorder.dplanend as date_end, bs_goods.goodmsritem as g_msr FROM bs_order JOIN module_workorder ON bs_order.id = module_workorder.id_order JOIN bs_goods ON module_workorder.id_goods = bs_goods.id WHERE bs_order.id = '" + fullOrderId + "' AND module_workorder.id_goods = '" + fullGoodId + "' ";

        try {
            ResultSet resultSet = this.executeQuery(db_module, query);

            try {
                while(resultSet.next()) {
                    String o_name = resultSet.getString("o_name");
                    String g_name = resultSet.getString("g_name");
                    double q_ty = resultSet.getDouble("nq_ty");
                    Date s_date = resultSet.getDate("date_start");
                    Date e_date = resultSet.getDate("date_end");
                    String g_msr = resultSet.getString("g_msr");
                    filteredCGO_CV.add(new rowGoodsOrders(0L, g_name, 0L, o_name, q_ty, s_date, e_date, g_msr));
                }
            } catch (Throwable var15) {
                if (resultSet != null) {
                    try {
                        resultSet.close();
                    } catch (Throwable var14) {
                        var15.addSuppressed(var14);
                    }
                }

                throw var15;
            }

            if (resultSet != null) {
                resultSet.close();
            }
        } catch (SQLException var16) {
            var16.printStackTrace();
        }

        return filteredCGO_CV;
    }

    public List<rowGoodsOrders> getCGOCritVolDisGoods(boolean db_module, String fullOrderId) {
        List<rowGoodsOrders> filteredCGO_CV = new ArrayList();
        String query = "SELECT DISTINCT bs_goods.id as g_id, bs_goods.sname as g_name FROM bs_order JOIN mes_workorder ON bs_order.id = mes_workorder.id_order JOIN bs_goods ON mes_workorder.id_goods = bs_goods.id JOIN prs_lot ON bs_goods.id = prs_lot.id_goods JOIN bs_contras ON prs_lot.id_contras = bs_contras.id WHERE bs_order.id = '" + fullOrderId + "' ";

        try {
            ResultSet resultSet = this.executeQuery(db_module, query);

            try {
                while(resultSet.next()) {
                    long g_id = resultSet.getLong("g_id");
                    String g_name = resultSet.getString("g_name");
                    filteredCGO_CV.add(new rowGoodsOrders(g_id, g_name, 0L, "", 0.0, (Date)null, (Date)null, ""));
                }
            } catch (Throwable var10) {
                if (resultSet != null) {
                    try {
                        resultSet.close();
                    } catch (Throwable var9) {
                        var10.addSuppressed(var9);
                    }
                }

                throw var10;
            }

            if (resultSet != null) {
                resultSet.close();
            }
        } catch (SQLException var11) {
            var11.printStackTrace();
        }

        return filteredCGO_CV;
    }

    public List<rowGoodsOrders> getIntervalData(boolean db_module, String fullOrderId, String fullGoodId, Date s_d, Date e_d) {
        List<rowGoodsOrders> intervalData = new ArrayList();
        String query = "SELECT DISTINCT module_workorder.id_order as o_id, module_workorder.id_goods as g_id, dplanbegin as s_date, dplanend as e_date  FROM module_workorder WHERE module_workorder.id_order = '" + fullOrderId + "' AND module_workorder.id_goods = '" + fullGoodId + "' AND module_workorder.dplanbegin = '" + s_d.toString() + "' AND module_workorder.dplanend = '" + e_d.toString() + "' ";

        try {
            ResultSet resultSet = this.executeQuery(db_module, query);

            try {
                while(resultSet.next()) {
                    long o_id = resultSet.getLong("o_id");
                    long g_id = resultSet.getLong("g_id");
                    Date s_date = resultSet.getDate("s_date");
                    Date e_date = resultSet.getDate("e_date");
                    intervalData.add(new rowGoodsOrders(g_id, "", o_id, "", 0.0, s_date, e_date, ""));
                }
            } catch (Throwable var16) {
                if (resultSet != null) {
                    try {
                        resultSet.close();
                    } catch (Throwable var15) {
                        var16.addSuppressed(var15);
                    }
                }

                throw var16;
            }

            if (resultSet != null) {
                resultSet.close();
            }
        } catch (SQLException var17) {
            var17.printStackTrace();
        }

        return intervalData;
    }

    public void addIntervalData(boolean db_module, String fullOrderId, String fullGoodId, Double q_ty, Date s_d, Date e_d) {
        String query = "INSERT INTO public.module_workorder (id_goods, id_order, nqtybase, dplanbegin, dplanend, bneedsfrommain) VALUES (" + fullGoodId + ",'" + fullOrderId + "','" + q_ty + "','" + s_d.toString() + "','" + e_d.toString() + "','0')";

        try {
            this.executeQueryNoResult(db_module, query);
        } catch (SQLException var9) {
            var9.printStackTrace();
        }

    }

    public void changeIntervalData(boolean db_module, String fullOrderId, String fullGoodId, Double q_ty, Date s_d, Date e_d) {
        String query = "UPDATE public.module_workorder SET nqtybase = '" + q_ty + "' WHERE module_workorder.id_order = '" + fullOrderId + "' AND module_workorder.id_goods = '" + fullGoodId + "' AND module_workorder.dplanbegin = '" + s_d.toString() + "' AND module_workorder.dplanend = '" + e_d.toString() + "' ";

        try {
            this.executeQueryNoResult(db_module, query);
        } catch (SQLException var9) {
            var9.printStackTrace();
        }

    }

    public List<rowGoodsOrders> getGOCritVolIntDatesAll(boolean db_module, String fullOrderId) {
        List<rowGoodsOrders> filteredCGO_CV = new ArrayList();
        String query = "SELECT DISTINCT bs_order.scaption as o_name, bs_goods.sname as g_name, module_workorder.nqtybase as nq_ty, module_workorder.dplanbegin as date_start, module_workorder.dplanend as date_end, bs_goods.goodmsritem as g_msr FROM bs_order JOIN module_workorder ON bs_order.id = module_workorder.id_order JOIN bs_goods ON module_workorder.id_goods = bs_goods.id WHERE bs_order.id = '" + fullOrderId + "' and module_workorder.nqtybase > 0 ";

        try {
            ResultSet resultSet = this.executeQuery(db_module, query);

            try {
                while(resultSet.next()) {
                    String o_name = resultSet.getString("o_name");
                    String g_name = resultSet.getString("g_name");
                    double q_ty = resultSet.getDouble("nq_ty");
                    Date s_date = resultSet.getDate("date_start");
                    Date e_date = resultSet.getDate("date_end");
                    String g_msr = resultSet.getString("g_msr");
                    filteredCGO_CV.add(new rowGoodsOrders(0L, g_name, 0L, o_name, q_ty, s_date, e_date, g_msr));
                }
            } catch (Throwable var14) {
                if (resultSet != null) {
                    try {
                        resultSet.close();
                    } catch (Throwable var13) {
                        var14.addSuppressed(var13);
                    }
                }

                throw var14;
            }

            if (resultSet != null) {
                resultSet.close();
            }
        } catch (SQLException var15) {
            var15.printStackTrace();
        }

        return filteredCGO_CV;
    }

    public List<rowGoodsOrders> getGOCritVolIntDatesFilter(boolean db_module, String fullOrderId, String filterGood) {
        List<rowGoodsOrders> filteredCGO_CV = new ArrayList<>();
        StringBuilder query = new StringBuilder(
                "SELECT DISTINCT bs_order.scaption as o_name, " +
                        "bs_goods.sname as g_name, module_workorder.nqtybase as nq_ty, " +
                        "module_workorder.dplanbegin as date_start, " +
                        "module_workorder.dplanend as date_end, " +
                        "bs_goods.goodmsritem as g_msr " +
                        "FROM bs_order " +
                        "JOIN module_workorder ON bs_order.id = module_workorder.id_order " +
                        "JOIN bs_goods ON module_workorder.id_goods = bs_goods.id " +
                        "WHERE bs_order.id = '" + fullOrderId + "'"
        );

        if (!filterGood.isEmpty()) {
            query.append(" AND bs_goods.sname ILIKE '%").append(filterGood).append("%' ");
        }

        try {
            ResultSet resultSet = this.executeQuery(db_module, query.toString());

            try {
                while (resultSet.next()) {
                    String o_name = resultSet.getString("o_name");
                    String g_name = resultSet.getString("g_name");
                    double q_ty = resultSet.getDouble("nq_ty");
                    Date s_date = resultSet.getDate("date_start");
                    Date e_date = resultSet.getDate("date_end");
                    String g_msr = resultSet.getString("g_msr");
                    filteredCGO_CV.add(new rowGoodsOrders(0L, g_name, 0L, o_name, q_ty, s_date, e_date, g_msr));
                }
            } catch (Throwable var15) {
                if (resultSet != null) {
                    try {
                        resultSet.close();
                    } catch (Throwable var14) {
                        var15.addSuppressed(var14);
                    }
                }
                throw var15;
            }

            if (resultSet != null) {
                resultSet.close();
            }
        } catch (SQLException var16) {
            var16.printStackTrace();
        }

        return filteredCGO_CV;
    }


    public void setContrasHistory(boolean db_module) {
        String query = "CREATE TABLE IF NOT EXISTS public.module_contrashistory (id_contras bigint NOT NULL UNIQUE, ncompleted_number bigint, nfailed_number bigint, ncontrasdelay numeric(38,18), nfailed_percent numeric(38,18))";

        try {
            this.executeQueryNoResult(db_module, query);
        } catch (SQLException var4) {
            var4.printStackTrace();
        }

    }

    public List<rowContrasWithHistory> getContrasHist(boolean db_module, String filterContrasName) {
        List<rowContrasWithHistory> filteredCGO_CV = new ArrayList<>();
        String query = "SELECT DISTINCT bs_contras.scaption as c_name, " +
                "module_contrashistory.ncompleted_number as nc_h, " +
                "module_contrashistory.nfailed_number as nf_h, " +
                "module_contrashistory.ncontrasdelay as c_delay " +
                "FROM bs_contras " +
                "LEFT JOIN module_contrashistory ON bs_contras.id = module_contrashistory.id_contras";

        if (!filterContrasName.isEmpty()) {
            query += " WHERE bs_contras.scaption ILIKE '%" + filterContrasName + "%'";
        }

        try {
            ResultSet resultSet = this.executeQuery(db_module, query);

            try {
                while (resultSet.next()) {
                    String c_name = resultSet.getString("c_name");
                    int nc_h = resultSet.getInt("nc_h");
                    int nf_h = resultSet.getInt("nf_h");
                    double c_delay = resultSet.getDouble("c_delay");

                    double n_percFailed = (nc_h + nf_h == 0) ? 0.0 : (100.0 * nf_h / (nc_h + nf_h));

                    filteredCGO_CV.add(new rowContrasWithHistory(0L, c_name, 0.0, "", nc_h, nf_h, c_delay, n_percFailed));
                }
            } catch (Throwable var14) {
                if (resultSet != null) {
                    try {
                        resultSet.close();
                    } catch (Throwable var13) {
                        var14.addSuppressed(var13);
                    }
                }
                throw var14;
            }

            if (resultSet != null) {
                resultSet.close();
            }
        } catch (SQLException var15) {
            var15.printStackTrace();
        }

        return filteredCGO_CV;
    }


    public List<rowContrasWithHistory> getContrasHistForEdit(boolean db_module, String filterContrasName) {
        List<rowContrasWithHistory> filteredCGO_CV = new ArrayList<>();
        String query = "SELECT DISTINCT bs_contras.id as c_id, " +
                "bs_contras.scode as c_code, " +
                "bs_contras.scaption as c_name, " +
                "module_contrashistory.ncompleted_number as nc_h, " +
                "module_contrashistory.nfailed_number as nf_h, " +
                "module_contrashistory.ncontrasdelay as c_delay " +
                "FROM bs_contras " +
                "LEFT JOIN module_contrashistory ON bs_contras.id = module_contrashistory.id_contras";

        if (!filterContrasName.isEmpty()) {
            query += " WHERE bs_contras.scaption ILIKE '%" + filterContrasName + "%'";
        }

        try {
            ResultSet resultSet = this.executeQuery(db_module, query);

            try {
                while (resultSet.next()) {
                    long c_id = resultSet.getLong("c_id");
                    String c_code = resultSet.getString("c_code");
                    String c_name = resultSet.getString("c_name");
                    int nc_h = resultSet.getInt("nc_h");
                    int nf_h = resultSet.getInt("nf_h");
                    double c_delay = resultSet.getDouble("c_delay");

                    double n_percFailed = (nc_h + nf_h == 0) ? 0.0 : (100.0 * nf_h / (nc_h + nf_h));

                    filteredCGO_CV.add(new rowContrasWithHistory(c_id, c_name, 0.0, c_code, nc_h, nf_h, c_delay, n_percFailed));
                }
            } catch (Throwable var17) {
                if (resultSet != null) {
                    try {
                        resultSet.close();
                    } catch (Throwable var16) {
                        var17.addSuppressed(var16);
                    }
                }
                throw var17;
            }

            if (resultSet != null) {
                resultSet.close();
            }
        } catch (SQLException var18) {
            var18.printStackTrace();
        }

        return filteredCGO_CV;
    }


    public void updateContrasHistForEdit(boolean db_module, List<rowContrasWithHistory> rowsCHs) {
        String query = "INSERT INTO public.module_contrashistory (id_contras, ncompleted_number, nfailed_number, ncontrasdelay, nfailed_percent) VALUES (?, ?, ?, ?, ?) ON CONFLICT (id_contras) DO UPDATE SET ncompleted_number = EXCLUDED.ncompleted_number, nfailed_number = EXCLUDED.nfailed_number, ncontrasdelay = EXCLUDED.ncontrasdelay, nfailed_percent = EXCLUDED.nfailed_percent ";

        try {
            this.executeQueryBatchCH(db_module, query, rowsCHs);
        } catch (SQLException var5) {
            var5.printStackTrace();
        }

    }

    public void updateLotFromHist(boolean db_module) {
        String query = "UPDATE prs_lot SET (ncontrasdelay) = ( SELECT module_contrashistory.ncontrasdelay FROM module_contrashistory WHERE prs_lot.id_contras = module_contrashistory.id_contras ) ";

        try {
            this.executeQueryNoResult(db_module, query);
        } catch (SQLException var4) {
            var4.printStackTrace();
        }

    }

    public List<rowLotGoodsContras> getLotWithContrasHist(boolean db_module, String filterContrasName) {
        List<rowLotGoodsContras> filteredCGO_CV = new ArrayList<>();
        String query = "SELECT DISTINCT bs_contras.scaption as c_name, " +
                "bs_goods.sname as g_name, " +
                "prs_lot.ndeliverytime as l_dt, " +
                "prs_lot.ncontrasdelay as l_delay " +
                "FROM bs_contras " +
                "JOIN prs_lot ON bs_contras.id = prs_lot.id_contras " +
                "JOIN bs_goods ON bs_goods.id = prs_lot.id_goods";

        if (!filterContrasName.isEmpty()) {
            query += " WHERE bs_contras.scaption ILIKE '%" + filterContrasName + "%'";
        }

        try {
            ResultSet resultSet = this.executeQuery(db_module, query);

            try {
                while (resultSet.next()) {
                    String c_name = resultSet.getString("c_name");
                    String g_name = resultSet.getString("g_name");
                    int l_dt = resultSet.getInt("l_dt");
                    double l_delay = resultSet.getDouble("l_delay");

                    filteredCGO_CV.add(new rowLotGoodsContras(0L, g_name, 0L, c_name, l_dt, 0.0, 0.0, 0.0, 0.0, 0.0, l_delay));
                }
            } catch (Throwable var12) {
                if (resultSet != null) {
                    try {
                        resultSet.close();
                    } catch (Throwable var11) {
                        var12.addSuppressed(var11);
                    }
                }
                throw var12;
            }

            if (resultSet != null) {
                resultSet.close();
            }
        } catch (SQLException var13) {
            var13.printStackTrace();
        }

        return filteredCGO_CV;
    }


    public List<rowGoodsOrders> getGOBetweenDates(boolean db_module, String filterOrderName, Date s_d, Date e_d) {
        List<rowGoodsOrders> filteredCGO_CV = new ArrayList<>();
        String query = "SELECT DISTINCT bs_order.scaption as o_name, bs_goods.sname as g_name, " +
                "module_workorder.nqtybase as nq_ty, module_workorder.dplanbegin as date_start, " +
                "module_workorder.dplanend as date_end, bs_goods.goodmsritem as g_msr " +
                "FROM bs_order " +
                "JOIN module_workorder ON bs_order.id = module_workorder.id_order " +
                "JOIN bs_goods ON module_workorder.id_goods = bs_goods.id " +
                "WHERE (NOT (module_workorder.dplanbegin > '" + e_d.toString() + "')) " +
                "AND (NOT (module_workorder.dplanend < '" + s_d.toString() + "')) " +
                "AND module_workorder.nqtybase > 0";

        if (!filterOrderName.isEmpty()) {
            query += " AND bs_order.scaption ILIKE '%" + filterOrderName + "%' ";
        }

        try {
            ResultSet resultSet = this.executeQuery(db_module, query);

            try {
                while (resultSet.next()) {
                    String o_name = resultSet.getString("o_name");
                    String g_name = resultSet.getString("g_name");
                    double q_ty = resultSet.getDouble("nq_ty");
                    Date s_date = resultSet.getDate("date_start");
                    Date e_date = resultSet.getDate("date_end");
                    String g_msr = resultSet.getString("g_msr");
                    filteredCGO_CV.add(new rowGoodsOrders(0L, g_name, 0L, o_name, q_ty, s_date, e_date, g_msr));
                }
            } catch (Throwable var16) {
                if (resultSet != null) {
                    try {
                        resultSet.close();
                    } catch (Throwable var15) {
                        var16.addSuppressed(var15);
                    }
                }
                throw var16;
            }

            if (resultSet != null) {
                resultSet.close();
            }
        } catch (SQLException var17) {
            var17.printStackTrace();
        }

        return filteredCGO_CV;
    }


    public List<rowGoods> getOptGoodConds(boolean db_module, String fullOrderId, String filterGoodName) {
        List<rowGoods> filteredCGO_CV = new ArrayList<>();
        String query = "SELECT DISTINCT bs_goods.id as g_id, bs_goods.sname as g_name, " +
                "bs_goods.npreparedays as g_cond, bs_goods.sarticle as g_code " +
                "FROM bs_goods " +
                "JOIN mes_workorder ON bs_goods.id = mes_workorder.id_goods " +
                "JOIN bs_order ON mes_workorder.id_order = bs_order.id " +
                "WHERE bs_order.id = '" + fullOrderId + "'";

        if (!filterGoodName.isEmpty()) {
            query += " AND bs_goods.sname ILIKE '%" + filterGoodName + "%' ";
        }

        try {
            ResultSet resultSet = this.executeQuery(db_module, query);

            try {
                while (resultSet.next()) {
                    long g_id = resultSet.getLong("g_id");
                    String g_name = resultSet.getString("g_name");
                    int g_cond = resultSet.getInt("g_cond");
                    String g_code = resultSet.getString("g_code");
                    filteredCGO_CV.add(new rowGoods(g_id, g_name, g_cond, g_code, 0.0, 0.0, 0.0, 0.0, 0.0, ""));
                }
            } catch (Throwable var13) {
                if (resultSet != null) {
                    try {
                        resultSet.close();
                    } catch (Throwable var12) {
                        var13.addSuppressed(var12);
                    }
                }
                throw var13;
            }

            if (resultSet != null) {
                resultSet.close();
            }
        } catch (SQLException var14) {
            var14.printStackTrace();
        }

        return filteredCGO_CV;
    }


    public void updateGoodsForEdit(boolean db_module, List<rowGoods> rowsGs) {
        String query = "UPDATE public.bs_goods set npreparedays = ? WHERE id = ?";

        try {
            this.executeQueryBatchGoodsUpdateNoName(db_module, query, rowsGs);
        } catch (SQLException var5) {
            var5.printStackTrace();
        }

    }

    public void setCells(boolean db_module) {
        String query = "CREATE TABLE IF NOT EXISTS public.wms_cell (id bigint NOT NULL)  ";

        try {
            this.executeQueryNoResult(db_module, query);
        } catch (SQLException var5) {
            var5.printStackTrace();
        }

        query = "INSERT INTO public.wms_cell (id) VALUES (?)";

        try {
            this.executeQueryBatchCells(db_module, query, new Object[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10});
        } catch (SQLException var4) {
            var4.printStackTrace();
        }

    }

    public void setOrderCell(boolean db_module) {
        String query = "CREATE TABLE IF NOT EXISTS public.module_cellorder (id_order bigint NOT NULL, id_cell bigint NOT NULL, nweight numeric(38,18), CONSTRAINT co UNIQUE(id_order, id_cell))  ";

        try {
            this.executeQueryNoResult(db_module, query);
        } catch (SQLException var4) {
            var4.printStackTrace();
        }

    }

    public void setGoodCell(boolean db_module) {
        String query = "CREATE TABLE IF NOT EXISTS public.module_cellgood (id_goods bigint NOT NULL, id_cell bigint NOT NULL, nq_ty_in_cell numeric(38,18), CONSTRAINT cg UNIQUE(id_goods, id_cell))  ";

        try {
            this.executeQueryNoResult(db_module, query);
        } catch (SQLException var4) {
            var4.printStackTrace();
        }

    }

    public List<rowGoodsSumcells> getOptGoodSumCells(boolean db_module, String fullOrderId, String filterGoodName) {
        List<rowGoodsSumcells> filteredCGO_CV = new ArrayList<>();
        String query = "SELECT DISTINCT bs_goods.id as g_id, bs_goods.sname as g_name, " +
                "bs_goods.sarticle as g_code, bs_goods.goodmsritem as g_msr, " +
                "SUM(module_cellgood.nq_ty_in_cell) as cg_sum " +
                "FROM bs_goods " +
                "JOIN module_cellgood ON bs_goods.id = module_cellgood.id_goods " +
                "JOIN module_cellorder ON module_cellorder.id_cell = module_cellgood.id_cell " +
                "JOIN bs_order ON module_cellorder.id_order = bs_order.id " +
                "WHERE bs_order.id = '" + fullOrderId + "'";

        if (!filterGoodName.isEmpty()) {
            query += " AND bs_goods.sname ILIKE '%" + filterGoodName + "%' ";
        }

        query += "GROUP BY bs_goods.id, bs_goods.sname, bs_goods.sarticle, bs_goods.goodmsritem";

        try {
            ResultSet resultSet = this.executeQuery(db_module, query);

            try {
                while (resultSet.next()) {
                    long g_id = resultSet.getLong("g_id");
                    String g_name = resultSet.getString("g_name");
                    String g_code = resultSet.getString("g_code");
                    double cg_sum = resultSet.getDouble("cg_sum");
                    String g_msr = resultSet.getString("g_msr");
                    filteredCGO_CV.add(new rowGoodsSumcells(g_id, g_name, cg_sum, g_code, g_msr));
                }
            } catch (Throwable var15) {
                if (resultSet != null) {
                    try {
                        resultSet.close();
                    } catch (Throwable var14) {
                        var15.addSuppressed(var14);
                    }
                }
                throw var15;
            }

            if (resultSet != null) {
                resultSet.close();
            }
        } catch (SQLException var16) {
            var16.printStackTrace();
        }

        return filteredCGO_CV;
    }

    public List<rowGoodsSumcells> getOptGoodSumCellsFromStock(boolean db_module, String fullOrderId, String filterGoodName) {
        List<rowGoodsSumcells> filteredCGO_CV = new ArrayList<>();
        String query = "SELECT DISTINCT bs_goods.id as g_id, bs_goods.sname as g_name, " +
                "bs_goods.sarticle as g_code, bs_goods.goodmsritem as g_msr, " +
                "SUM(sr.nqtybasemsr) as cg_sum " +
                "FROM bs_order " +
                "JOIN stk_regmovmat sr ON bs_order.id = sr.idorder " +
                "JOIN stk_stock ON sr.idstock = stk_stock.id " +
                "JOIN wms_cell ON wms_cell.idstock = stk_stock.id " +
                "JOIN bs_goods ON bs_goods.id = sr.idgds " +
                "WHERE bs_order.id = '" + fullOrderId + "'";

        if (!filterGoodName.isEmpty()) {
            query += " AND bs_goods.sname ILIKE '%" + filterGoodName + "%' ";
        }

        query += "GROUP BY bs_goods.id, bs_goods.sname, bs_goods.sarticle, bs_goods.goodmsritem";

        try {
            ResultSet resultSet = this.executeQuery(db_module, query);

            try {
                while (resultSet.next()) {
                    long g_id = resultSet.getLong("g_id");
                    String g_name = resultSet.getString("g_name");
                    String g_code = resultSet.getString("g_code");
                    double cg_sum = resultSet.getDouble("cg_sum");
                    String g_msr = resultSet.getString("g_msr");
                    filteredCGO_CV.add(new rowGoodsSumcells(g_id, g_name, cg_sum, g_code, g_msr));
                }
            } catch (Throwable var15) {
                if (resultSet != null) {
                    try {
                        resultSet.close();
                    } catch (Throwable var14) {
                        var15.addSuppressed(var14);
                    }
                }
                throw var15;
            }

            if (resultSet != null) {
                resultSet.close();
            }
        } catch (SQLException var16) {
            var16.printStackTrace();
        }

        return filteredCGO_CV;
    }


    public List<rowStockCells> getAllCells(boolean db_module) {
        List<rowStockCells> filteredStCl = new ArrayList();
        String query = "SELECT DISTINCT wms_cell.id as cl_id, wms_cell.scode as cl_code, stk_stock.scaption as st_name FROM wms_cell join stk_stock on stk_stock.id = wms_cell.idstock ";

        try {
            ResultSet resultSet = this.executeQuery(db_module, query);

            try {
                while(resultSet.next()) {
                    long cl_id = resultSet.getLong("cl_id");
                    String cl_code = resultSet.getString("cl_code");
                    String st_name = resultSet.getString("st_name");
                    filteredStCl.add(new rowStockCells(0L, st_name, "", cl_id, cl_code, 0.0, 0.0, 0.0));
                }
            } catch (Throwable var10) {
                if (resultSet != null) {
                    try {
                        resultSet.close();
                    } catch (Throwable var9) {
                        var10.addSuppressed(var9);
                    }
                }

                throw var10;
            }

            if (resultSet != null) {
                resultSet.close();
            }
        } catch (SQLException var11) {
            var11.printStackTrace();
        }

        return filteredStCl;
    }

    public List<rowGoodsCells> getOptGoodCells(boolean db_module, String fullOrderId) {
        List<rowGoodsCells> filteredCGO_CV = new ArrayList();
        String query = "SELECT DISTINCT bs_goods.id as g_id, bs_goods.sname as g_name, bs_goods.sarticle as g_code, stk_stock.scaption as st_name, wms_cell.scode as cl_code, wms_cell.nwidth as cl_w, wms_cell.ndepth as cl_d, wms_cell.nheight as cl_h, bs_goods.goodmsritem as g_msr, bs_goods.nwidth as g_w, bs_goods.nheight as g_h, bs_goods.nlength as g_l, bs_goods.ndiameter as g_d, bs_goods.nthickness as g_t FROM bs_order JOIN stk_regmovmat ON bs_order.id = stk_regmovmat.idorder JOIN stk_stock ON stk_regmovmat.idstock = stk_stock.id JOIN wms_cell ON wms_cell.idstock = stk_stock.id JOIN bs_goods ON bs_goods.id = stk_regmovmat.idgds WHERE bs_order.id = '" + fullOrderId + "' ";

        try {
            ResultSet resultSet = this.executeQuery(db_module, query);

            try {
                while(resultSet.next()) {
                    long g_id = resultSet.getLong("g_id");
                    String g_name = resultSet.getString("g_name");
                    String g_code = resultSet.getString("g_code");
                    String st_name = resultSet.getString("st_name");
                    String cl_code = resultSet.getString("cl_code");
                    double cl_w = (double)resultSet.getInt("cl_w");
                    double cl_d = (double)resultSet.getInt("cl_d");
                    double cl_h = (double)resultSet.getInt("cl_h");
                    String g_msr = resultSet.getString("g_msr");
                    double g_w = (double)resultSet.getInt("g_w");
                    double g_l = (double)resultSet.getInt("g_l");
                    double g_h = (double)resultSet.getInt("g_h");
                    double g_d = (double)resultSet.getInt("g_d");
                    double g_t = (double)resultSet.getInt("g_t");
                    filteredCGO_CV.add(new rowGoodsCells(g_id, g_name, g_code, 0L, st_name, cl_code, 0.0, g_msr, g_w, g_h, g_l, g_d, g_t, cl_w, cl_d, cl_h));
                }
            } catch (Throwable var30) {
                if (resultSet != null) {
                    try {
                        resultSet.close();
                    } catch (Throwable var29) {
                        var30.addSuppressed(var29);
                    }
                }

                throw var30;
            }

            if (resultSet != null) {
                resultSet.close();
            }
        } catch (SQLException var31) {
            var31.printStackTrace();
        }

        return filteredCGO_CV;
    }

    public List<rowGoodsCells> getOptGoodCellsFromInput(boolean db_module, String fullOrderId) {
        List<rowGoodsCells> filteredCGO_CV = new ArrayList();
        String query = "SELECT DISTINCT bs_goods.id as g_id, bs_goods.sname as g_name, bs_goods.sarticle as g_code, stk_stock.scaption as st_name, wms_cell.scode as cl_code, wms_cell.nwidth as cl_w, wms_cell.ndepth as cl_d, wms_cell.nheight as cl_h, bs_goods.goodmsritem as g_msr, bs_goods.nwidth as g_w, bs_goods.nheight as g_h, bs_goods.nlength as g_l, bs_goods.ndiameter as g_d, bs_goods.nthickness as g_t FROM bs_goods JOIN module_cellgood ON bs_goods.id = module_cellgood.id_goods JOIN module_cellorder ON module_cellorder.id_cell = module_cellgood.id_cell JOIN bs_order ON module_cellorder.id_order = bs_order.id JOIN wms_cell ON module_cellorder.id_cell = wms_cell.id JOIN stk_stock ON wms_cell.idstock = stk_stock.id WHERE bs_order.id = '" + fullOrderId + "' ";

        try {
            ResultSet resultSet = this.executeQuery(db_module, query);

            try {
                while(resultSet.next()) {
                    long g_id = resultSet.getLong("g_id");
                    String g_name = resultSet.getString("g_name");
                    String g_code = resultSet.getString("g_code");
                    String st_name = resultSet.getString("st_name");
                    String cl_code = resultSet.getString("cl_code");
                    double cl_w = (double)resultSet.getInt("cl_w");
                    double cl_d = (double)resultSet.getInt("cl_d");
                    double cl_h = (double)resultSet.getInt("cl_h");
                    String g_msr = resultSet.getString("g_msr");
                    double g_w = (double)resultSet.getInt("g_w");
                    double g_l = (double)resultSet.getInt("g_l");
                    double g_h = (double)resultSet.getInt("g_h");
                    double g_d = (double)resultSet.getInt("g_d");
                    double g_t = (double)resultSet.getInt("g_t");
                    filteredCGO_CV.add(new rowGoodsCells(g_id, g_name, g_code, 0L, st_name, cl_code, 0.0, g_msr, g_w, g_h, g_l, g_d, g_t, cl_w, cl_d, cl_h));
                }
            } catch (Throwable var30) {
                if (resultSet != null) {
                    try {
                        resultSet.close();
                    } catch (Throwable var29) {
                        var30.addSuppressed(var29);
                    }
                }

                throw var30;
            }

            if (resultSet != null) {
                resultSet.close();
            }
        } catch (SQLException var31) {
            var31.printStackTrace();
        }

        return filteredCGO_CV;
    }

    public void updateOrderCell(boolean db_module, String fullOrderId, String fullCellId, double co_weight) {
        String query = "INSERT INTO public.module_cellorder (id_order, id_cell, nweight) VALUES ('" + fullOrderId + "', '" + fullCellId + "', '" + co_weight + "') ON CONFLICT (id_order, id_cell) DO UPDATE SET nweight = EXCLUDED.nweight ";

        try {
            this.executeQueryNoResult(db_module, query);
        } catch (SQLException var8) {
            var8.printStackTrace();
        }

    }

    public void updateGoodCell(boolean db_module, String fullGoodId, String fullCellId, double cg_qty) {
        String query = "INSERT INTO public.module_cellgood (id_goods, id_cell, nq_ty_in_cell) VALUES ('" + fullGoodId + "', '" + fullCellId + "', '" + cg_qty + "') ON CONFLICT (id_goods, id_cell) DO UPDATE SET nq_ty_in_cell = EXCLUDED.nq_ty_in_cell ";

        try {
            this.executeQueryNoResult(db_module, query);
        } catch (SQLException var8) {
            var8.printStackTrace();
        }

    }

    public List<rowGoodsOrdersOpt> getGOAllDates(boolean db_module, String filterOrderName) {
        List<rowGoodsOrdersOpt> filteredCGO_CV = new ArrayList<>();
        StringBuilder query = new StringBuilder(
                "SELECT DISTINCT bs_order.id as o_id, bs_order.scaption as o_name, " +
                        "bs_goods.id as g_id, bs_goods.sname as g_name, bs_goods.npreparedays as g_cond, " +
                        "module_workorder.nqtybase as nq_ty, module_workorder.dplanbegin as date_start, " +
                        "module_workorder.dplanend as date_end, " +
                        "(date(module_workorder.dplanbegin) - bs_goods.npreparedays) as n_date, " +
                        "bs_goods.goodmsritem as g_msr, sum(sr.nqtybasemsr) as sr_sum " +
                        "FROM bs_order " +
                        "JOIN module_workorder ON bs_order.id = module_workorder.id_order " +
                        "JOIN bs_goods ON module_workorder.id_goods = bs_goods.id " +
                        "LEFT JOIN stk_regmovmat sr ON sr.idgds = bs_goods.id AND sr.idorder = bs_order.id " +
                        "WHERE module_workorder.nqtybase > 0"
        );

        if (!filterOrderName.isEmpty()) {
            query.append(" AND bs_order.scaption ILIKE '%").append(filterOrderName).append("%' ");
        }

        query.append(
                " GROUP BY bs_goods.id, bs_order.id, bs_order.scaption, bs_goods.sname, " +
                        "bs_goods.npreparedays, module_workorder.nqtybase, " +
                        "module_workorder.dplanbegin, module_workorder.dplanend, bs_goods.goodmsritem " +
                        "ORDER BY n_date"
        );

        try {
            ResultSet resultSet = this.executeQuery(db_module, query.toString());

            try {
                while (resultSet.next()) {
                    long o_id = resultSet.getLong("o_id");
                    long g_id = resultSet.getLong("g_id");
                    String o_name = resultSet.getString("o_name");
                    String g_name = resultSet.getString("g_name");
                    int g_cond = resultSet.getInt("g_cond");
                    double q_ty = resultSet.getDouble("nq_ty");
                    Date s_date = resultSet.getDate("date_start");
                    Date e_date = resultSet.getDate("date_end");
                    Date n_da = resultSet.getDate("n_date");
                    String g_msr = resultSet.getString("g_msr");
                    double sr_sum = resultSet.getDouble("sr_sum");
                    filteredCGO_CV.add(new rowGoodsOrdersOpt(g_id, g_name, o_id, o_name, q_ty, s_date, e_date, g_cond, n_da, g_msr, sr_sum));
                }
            } catch (Throwable var22) {
                if (resultSet != null) {
                    try {
                        resultSet.close();
                    } catch (Throwable var21) {
                        var22.addSuppressed(var21);
                    }
                }
                throw var22;
            }

            if (resultSet != null) {
                resultSet.close();
            }
        } catch (SQLException var23) {
            var23.printStackTrace();
        }

        return filteredCGO_CV;
    }


    public List<rowGoodsOrdersContrasOpt> getGOAllDatesWithContras(boolean db_module, String filterOrderName) {
        List<rowGoodsOrdersContrasOpt> filteredCGO_CV = new ArrayList<>();
        String query = "SELECT DISTINCT bs_order.id as o_id, bs_order.scaption as o_name, bs_goods.id as g_id, bs_goods.sname as g_name, \n" +
                "bs_goods.npreparedays as g_cond, bs_goods.goodmsritem as g_msr, module_workorder.nqtybase as nq_ty, module_workorder.dplanbegin as date_start, \n" +
                "module_workorder.dplanend as date_end, (date(module_workorder.dplanbegin) - bs_goods.npreparedays) as n_date, \n" +
                "bs_contras.id as c_id, bs_contras.scaption as c_name, prs_lot.ndeliverytime as lt_deltime, \n" +
                "(date(module_workorder.dplanbegin) - bs_goods.npreparedays - prs_lot.ndeliverytime) as n_purchdate, prs_lot.nqty as lt_minvol, GREATEST(module_workorder.nqtybase, prs_lot.nqty) as n_purchqty, \n" +
                "prs_lot.nprc as lt_prc, (prs_lot.nprc * GREATEST(module_workorder.nqtybase, prs_lot.nqty)) as n_purchprc\n" +
                "FROM (SELECT DISTINCT o_id, o_name, g_id, g_name, n_date, date_end, MIN(n_purchprc) as minn_purchase\n" +
                "FROM (SELECT DISTINCT bs_order.id as o_id, bs_order.scaption as o_name, bs_goods.id as g_id, bs_goods.sname as g_name, \n" +
                "bs_goods.npreparedays as g_cond, module_workorder.nqtybase as nq_ty, module_workorder.dplanbegin as date_start, \n" +
                "module_workorder.dplanend as date_end, (date(module_workorder.dplanbegin) - bs_goods.npreparedays) as n_date, \n" +
                "bs_contras.id as c_id, bs_contras.scaption as c_name, prs_lot.ndeliverytime as lt_deltime, \n" +
                "(date(module_workorder.dplanbegin) - bs_goods.npreparedays - prs_lot.ndeliverytime) as n_purchdate, prs_lot.nqty as lt_minvol, GREATEST(module_workorder.nqtybase, prs_lot.nqty) as n_purchqty, \n" +
                "prs_lot.nprc as lt_prc, (prs_lot.nprc * GREATEST(module_workorder.nqtybase, prs_lot.nqty)) as n_purchprc\n" +
                "FROM bs_order \n" +
                "JOIN module_workorder ON bs_order.id = module_workorder.id_order \n" +
                "JOIN bs_goods ON module_workorder.id_goods = bs_goods.id\n" +
                "JOIN prs_lot ON prs_lot.id_goods = bs_goods.id \n" +
                "JOIN bs_contras ON prs_lot.id_contras = bs_contras.id\n" +
                "ORDER BY n_purchprc) as innre\n" +
                "GROUP BY o_id, o_name, g_id, g_name, n_date, date_end ) as grp\n" +
                "JOIN bs_order ON bs_order.id = grp.o_id\n" +
                "JOIN module_workorder ON bs_order.id = module_workorder.id_order \n" +
                "JOIN bs_goods ON module_workorder.id_goods = bs_goods.id\n" +
                "JOIN prs_lot ON prs_lot.id_goods = bs_goods.id \n" +
                "JOIN bs_contras ON prs_lot.id_contras = bs_contras.id\n" +
                "WHERE (prs_lot.nprc * GREATEST(module_workorder.nqtybase, prs_lot.nqty)) = minn_purchase\n" +
                "AND bs_order.scaption ILIKE COALESCE(?, bs_order.scaption)";

        try {
            PreparedStatement preparedStatement = this.getConnection(db_module).prepareStatement(query);
            if (filterOrderName == null || filterOrderName.isEmpty()) {
                preparedStatement.setNull(1, java.sql.Types.VARCHAR);
            } else {
                preparedStatement.setString(1, "%" + filterOrderName + "%");
            }

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                long o_id = resultSet.getLong("o_id");
                String o_name = resultSet.getString("o_name");
                long g_id = resultSet.getLong("g_id");
                String g_name = resultSet.getString("g_name");
                int g_cond = resultSet.getInt("g_cond");
                double q_ty = resultSet.getDouble("nq_ty");
                Date s_date = resultSet.getDate("date_start");
                Date e_date = resultSet.getDate("date_end");
                Date n_da = resultSet.getDate("n_date");
                long c_id = resultSet.getLong("c_id");
                String c_name = resultSet.getString("c_name");
                int lt_deltime = resultSet.getInt("lt_deltime");
                Date n_purchdate = resultSet.getDate("n_purchdate");
                double lt_minvol = resultSet.getDouble("lt_minvol");
                double lt_prc = resultSet.getDouble("lt_prc");
                double n_purchqty = resultSet.getDouble("n_purchqty");
                double n_purchprc = resultSet.getDouble("n_purchprc");
                String g_msr = resultSet.getString("g_msr");

                filteredCGO_CV.add(new rowGoodsOrdersContrasOpt(
                        g_id, g_name, o_id, o_name, c_id, c_name, q_ty, s_date, e_date,
                        g_cond, n_da, lt_deltime, n_purchdate, lt_minvol, n_purchqty,
                        lt_prc, n_purchprc, g_msr));
            }

            resultSet.close();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return filteredCGO_CV;
    }


    public String getJValuesForProfile(String profileName) {
        String sql = "SELECT jvalues FROM module_profiles WHERE profile_name = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, profileName);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getString("jvalues");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean applyProfileByName(String profileName) {
        String sql = "SELECT module_criterion_id, jvalues FROM module_profiles WHERE profile_name = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, profileName);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int moduleCriterionId = rs.getInt("module_criterion_id");
                String jvalues = rs.getString("jvalues");

                // jvalues — это один JSON-объект, а не массив
                JSONObject obj = new JSONObject(jvalues);

                int functionType = obj.getInt("nfunctiontype");
                double minVal = obj.getDouble("nminval");
                double maxVal = obj.getDouble("nmaxval");
                double weight = obj.getDouble("nweight");

                // Обновляем module_criterion по id
                String updateSql = "UPDATE module_criterion SET nfunctiontype = ?, nminval = ?, nmaxval = ?, nweight = ? WHERE id = ?";

                try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                    updateStmt.setInt(1, functionType);
                    updateStmt.setDouble(2, minVal);
                    updateStmt.setDouble(3, maxVal);
                    updateStmt.setDouble(4, weight);
                    updateStmt.setInt(5, moduleCriterionId);
                    updateStmt.executeUpdate();
                }
            }

            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }


    }







    public boolean insertProfile(String profileName, int moduleCriterionId, String jValues) {
        String sql = "INSERT INTO module_profiles (profile_name, module_criterion_id, jvalues) VALUES (?, ?, ?)";
        System.out.println("Executing SQL: " + sql);

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Преобразуем строку в валидный JSON (если нужно проверить, что это корректный JSON)
            JSONObject jsonObject = new JSONObject(jValues);

            // Создаем объект PGobject и задаем тип jsonb
            PGobject jsonbObject = new PGobject();
            jsonbObject.setType("jsonb");
            jsonbObject.setValue(jsonObject.toString());

            stmt.setString(1, profileName);
            stmt.setInt(2, moduleCriterionId);
            stmt.setObject(3, jsonbObject); // <-- передаем jsonb

            stmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public List<String> getAllProfileNames() {
        List<String> profileNames = new ArrayList<>();
        String sql = "SELECT DISTINCT profile_name FROM module_profiles";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                profileNames.add(rs.getString("profile_name"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return profileNames;
    }





    public class ModuleProfile {
        private int id;
        private String name;

        public ModuleProfile(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            return name; // Отображаем имя в JComboBox
        }
    }

    public List<CriterionData> getAllCriteria() {
        List<CriterionData> list = new ArrayList<>();
        String sql = "SELECT id, nfunctiontype, nminval, nmaxval, nweight FROM module_criterion";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(new CriterionData(
                        rs.getInt("id"),
                        rs.getString("nfunctiontype"),
                        rs.getDouble("nminval"),
                        rs.getDouble("nmaxval"),
                        rs.getDouble("nweight")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public class CriterionData {
        public int id;
        public String nfunctiontype;
        public double nminval;
        public double nmaxval;
        public double nweight;

        public CriterionData(int id, String type, double min, double max, double weight) {
            this.id = id;
            this.nfunctiontype = type;
            this.nminval = min;
            this.nmaxval = max;
            this.nweight = weight;
        }
    }

}


