package srsoftwares; /**
 * User: Sumit Roy
 * Date: Apr 13, 2011
 * Time: 11:25:14 AM
 * To change this template use File | Settings | File Templates.
 */

import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.util.*;

public class DataBaseUtility {

    //  static Connection con = null;
    static String dbName = "";
    protected static int columnCountForCustomizedQry = 0;
    public static boolean autoCommit=true;
    protected static Vector<String> columnNamesForCustomizedQry = null;
    protected static Vector<Vector<Object>> dataVectorForCustomizedQry = null;

    private static String dbError = "";
    protected static Map<String, String> loginDetails = new HashMap<String, String>();


    private static DatabaseMetaData createDataSet(Connection connection) {
        DatabaseMetaData ds = null;
        try {
            if (!connection.isClosed()) {
                ds = connection.getMetaData();

            } else {
                System.out.println("Connection Closed");
            }
        } catch (SQLException e) {

        }
        return ds;
    }

    //.............................................................................................................................
    //                                                                                                                            .
    /*                                  NOTES : Naming conventions for databases. Please do not modify                            .
                                                                                                                                  .
      Database Name :         Saved in loginDetails Map as                                                                        .
      Oracle                - oracle                                                                                              .
      Microsoft SQL Server  - mssql                                                                                               .
      DB2                   - db2                                                                                                 .
      Derby                 - derby                                                                                               .
      MySQL                 - mysql                                                                                               .
    //                                                                                                                            .
  */// ............................................................................................................................


    // *********************************************** CONNECTION CREATION ************************************************** //


    public static Object createOracleConnection(String user, String pwd, String host, String port, String instanceName) {
        loginDetails.put("user", user);
        loginDetails.put("password", pwd);
        loginDetails.put("host", host);
        loginDetails.put("port", port);
        loginDetails.put("instance", instanceName);
        loginDetails.put("database", "Oracle");
        Connection connection = getDatabaseConnectionFor("Oracle");
        boolean result;
        if (connection != null) {
            try {
                connection.close();
                return true;
            } catch (Exception e) {
            }
        }
        return dbError;  // when no connection was established
    }

    public static Object createMSSqlConnection(String user, String pwd, String host, String port, String instanceName) {
        loginDetails.put("user", user);
        loginDetails.put("password", pwd);
        loginDetails.put("host", host);
        loginDetails.put("port", port);
        loginDetails.put("instance", instanceName);
        loginDetails.put("database", "Mssql");
        Connection connection = getDatabaseConnectionFor("Mssql");
        boolean result;
        if (connection != null) {
            try {
                connection.close();
                return true;
            } catch (Exception e) {
            }
        }
        return dbError;  // when no connection was established
    }

    public static Object createDerbyConnection(String user, String pwd, String host, String port, String instanceName) {
        loginDetails.put("user", user);
        loginDetails.put("password", pwd);
        loginDetails.put("host", host);
        loginDetails.put("port", port);
        loginDetails.put("instance", instanceName);
        loginDetails.put("database", "Derby");
        Connection connection = getDatabaseConnectionFor("Derby");
        boolean result;
        if (connection != null) {
            try {
                connection.close();
                return true;
            } catch (Exception e) {
            }
        }
        return dbError;  // when no connection was established
    }

    public static Object createMySQLConnection(String user, String pwd, String host, String port, String instanceName) {
        loginDetails.put("user", user);
        loginDetails.put("password", pwd);
        loginDetails.put("host", host);
        loginDetails.put("port", port);
        loginDetails.put("instance", instanceName);
        loginDetails.put("database", "Mysql");
        Connection connection = getDatabaseConnectionFor("Mysql");
        boolean result;
        if (connection != null) {
            try {
                connection.close();
                return true;
            } catch (Exception e) {
            }
        }
        return dbError;  // when no connection was established
    }

    public static Connection getDatabaseConnectionFor(String databaseToConnect) {
        String user = loginDetails.get("user");
        String pwd = loginDetails.get("password");
        String host = loginDetails.get("host");
        String port = loginDetails.get("port");
        String instanceName = loginDetails.get("instance");
        dbName = instanceName;
        String driverName = "";
        String url = "";
        Connection connection = null;

        try {
            if (databaseToConnect.equals("Oracle")) {
                // ORACLE CONNECTION
                driverName = "oracle.jdbc.driver.OracleDriver";
                url = "jdbc:oracle:thin:" + user + "/" + pwd + "@" + host + ":" + port + ":" + instanceName;
                Class.forName(driverName);
                connection = DriverManager.getConnection(url);


            } else if (databaseToConnect.equals("Derby")) {
                // DERBY CONNECTION

                driverName = "com.ibm.db2.jcc.DB2Driver";
                url = "jdbc:derby:net:" + "//" + host + ":" + port + "/" + instanceName;
                Class.forName(driverName);
                connection = DriverManager.getConnection(url, user, pwd);
            } else if (databaseToConnect.equals("Mysql")) {
                // MySQL Connection
                driverName = "com.mysql.jdbc.Driver";
                url = "jdbc:mysql://user:pwd@host:port/instanceName";
                Class.forName(driverName);
                connection = DriverManager.getConnection(url);

            } else if (databaseToConnect.equals("Mssql")) {
                // Microsoft SQL Server
                Class.forName(driverName);
                connection = DriverManager.getConnection(url);
                Class.forName(driverName);
                connection = DriverManager.getConnection(url);
            } else if (databaseToConnect.equals("Db2")) {
                // DB2 Connection
                Class.forName(driverName);
                connection = DriverManager.getConnection(url);
                Class.forName(driverName);
                connection = DriverManager.getConnection(url);

            }
        } catch (Exception e) {
            dbError = e.getLocalizedMessage();  // saving the error
        }


        return connection;
    }

    public static List<String> getSchemaNames() {
        //  reBuildConnection();
        List<String> schemaList = new ArrayList<String>();
        Connection connection = getDatabaseConnectionFor(loginDetails.get("database"));

        if (connection != null) {
            try {

                DatabaseMetaData ds = createDataSet(connection);
                ResultSet rs = null;
                rs = ds.getSchemas();
                while (rs.next()) {
                    String schemaName = rs.getString(1);
                    schemaList.add(schemaName);
                }
                rs.close();
                connection.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return schemaList;
    }

    public static Map<String,String> getSystemInformation(){
     Connection connection=getDatabaseConnectionFor(loginDetails.get("database"));
        Map<String,String> databaseProperty=new HashMap<String,String>();

        if(connection!=null){
        
         DatabaseMetaData ds=createDataSet(connection);
             try {
                databaseProperty.put("Database Product Name", String.valueOf(ds.getDatabaseProductName()));
                databaseProperty.put("Database Major Version", String.valueOf(ds.getDatabaseMajorVersion()));
                databaseProperty.put("Driver Name", String.valueOf(ds.getDriverName()));
                databaseProperty.put("SQL Keywords", String.valueOf(ds.getSQLKeywords()));
                databaseProperty.put("Number Functions", String.valueOf(ds.getNumericFunctions()));
                databaseProperty.put("String Functions", String.valueOf(ds.getStringFunctions()));
                databaseProperty.put("System Functions", String.valueOf(ds.getSystemFunctions()));
                databaseProperty.put("Max Row Size", String.valueOf(ds.getMaxRowSize()));
                databaseProperty.put("Scheme Name Length", String.valueOf(ds.getMaxSchemaNameLength()));
                databaseProperty.put("Table Name Length", String.valueOf(ds.getMaxTableNameLength()));
                databaseProperty.put("User Name Length", String.valueOf(ds.getMaxUserNameLength()));
            } catch (SQLException e) {

            }
        }
        return databaseProperty;
    }

    public static List<String> getTableNames(String schemaName) {

        List<String> tableList = new ArrayList<String>();
        Connection connection = getDatabaseConnectionFor(loginDetails.get("database"));


        if (connection != null) {
            try {
                ResultSet rs = null;
                DatabaseMetaData ds = createDataSet(connection);
                String type[] = {"TABLE", "VIEW"};
                rs = ds.getTables(null, schemaName, null, type);
                while (rs.next()) {
                    String tableName = rs.getString(3);

                    tableList.add(tableName);
                }
                rs.close();
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }


        return tableList;

    }

    public static int getColumnCount(String schemaName, String tableName) {
        int i = 0;


        Connection connection = getDatabaseConnectionFor(loginDetails.get("database"));

        if (connection != null) {
            try {
                ResultSet rs = null;
                DatabaseMetaData ds = createDataSet(connection);
                rs = ds.getColumns(null, schemaName, tableName, null);
                while (rs.next()) {
                    i++;
                }
                rs.close();
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }

        return i;

    }

    public static Vector<Vector<Object>> getDataVectorForCustomizedQry() {
        return dataVectorForCustomizedQry;
    }

    public static int getColumnNoForCustomizedQry() {
        return columnCountForCustomizedQry;
    }

    public static Vector<String> getColumnNamesForCustomizedQry() {
        return columnNamesForCustomizedQry;
    }

    public static List<String> executeUserUpdateQuery(String sql){
        int qryReturn=0;
        List<String> qryResult=new ArrayList<String>(3);
        Connection connection = getDatabaseConnectionFor(loginDetails.get("database"));
        String error="";
        if(connection!=null){
            Statement statement=null;
            try {
                statement=connection.createStatement();
                qryReturn=statement.executeUpdate(sql);
                System.out.println("QUERY RETURN VALUE = ");
                System.out.println(qryReturn);
                if(autoCommit){
                    statement.executeUpdate("commit");
                }
                statement.close();
                connection.close();
                qryResult.add(0,"true");
                qryResult.add(1,String.valueOf(qryReturn));
                qryResult.add(2,error);
            } catch (SQLException e) {
               error= e.getMessage();  //To change body of catch statement use File | Settings | File Templates.
            }finally {
               if(error.length()>0){
                   qryResult.add(0,"false");
                   qryResult.add(1,String.valueOf(-1));
                   qryResult.add(2,error);
               }
            }
        }
        return qryResult;

    }
    


    public static DefaultTableModel executeUserReadQuery(String sql) throws SQLException {

        DefaultTableModel model = new DefaultTableModel() {
            public boolean isCellEditable(int row, int col) {
                return false;
            }

        };
        Connection connection = getDatabaseConnectionFor(loginDetails.get("database"));
        if (connection != null) {
            Vector<String> columnNames = new Vector<String>();
            Statement statement = null;
            try {

                statement = connection.createStatement();
            } catch (SQLException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            ResultSet rs = null;
            ResultSetMetaData rsmd = null;




                rs = statement.executeQuery(sql);

                rsmd = rs.getMetaData();
                int columnCount = rsmd.getColumnCount();

                for (int i = 1; i <= columnCount; i++) {
                    String columnName = rsmd.getColumnName(i);
                    columnNames.add(columnName);
                }

                Vector<Vector<Object>> dataVector = new Vector<Vector<Object>>();
                while (rs.next()) {
                    Vector<Object> rowVector = new Vector<Object>();
                    for (int i = 1; i <= columnCount; i++) {
                        rowVector.add(rs.getObject(i));
                    }
                    dataVector.add(rowVector);
                }

                dataVectorForCustomizedQry = dataVector;
                columnCountForCustomizedQry = columnCount;
                columnNamesForCustomizedQry = columnNames;


                model.setDataVector(dataVector, columnNames);
                rs.close();
                connection.close();

        }
        //   killConnection();
        return model;

    }

    public static Vector<Vector<Object>> fillTable(String schemaName, String tableName) {

        Vector<Vector<Object>> data = new Vector<Vector<Object>>();
        Connection connection = getDatabaseConnectionFor(loginDetails.get("database"));

        if (connection != null) {
            try {
                ResultSet rs = null;

                //  Statement statement = con.createStatement();
                Statement statement = connection.createStatement();
                String sql = "select * from \"" + schemaName+"\"" + "." +"\""+ tableName+"\"";
                System.out.println("SQL FIRED = "+sql);
                rs = statement.executeQuery(sql);

                int columnNo = getColumnCount(schemaName, tableName);

                while (rs.next()) {
                    Vector<Object> row = new Vector<Object>();
                    for (int i = 1; i <= columnNo; i++) {
                        String str = rs.getString(i);
                        row.add(str);
                    }
                    data.add(row);
                }

                rs.close();
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }


        return data;

    }

    public static Vector<String> getColumnNames(String schemaName, String tableName) throws SQLException {
//        schemaName="\""+schemaName+"\"";
//        tableName="\""+tableName+"\"";

        Vector<String> columnNames = new Vector<String>();
        Connection connection = getDatabaseConnectionFor(loginDetails.get("database"));

        if (connection != null) {
            ResultSet rs = null;

            try {
                DatabaseMetaData ds = createDataSet(connection);
                rs = ds.getColumns(null, schemaName, tableName, null);
                System.out.println("PRINTING COLUMN NAMES ,SIZE OF RESULT IS = "+rs.getFetchSize());
                while (rs.next()) {
                    String names = rs.getString(4);
                    System.out.println("NAME -> "+names);
                    columnNames.add(names);
                }
                System.out.println("Ended");
                rs.close();
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }


        return columnNames;
    }

    public static String getColumnType(String schemaName, String tableName, String columnName) throws SQLException {

        String str = "";
        Connection connection = getDatabaseConnectionFor(loginDetails.get("database"));

        if (connection != null) {
            try {
                ResultSet rs = null;
                DatabaseMetaData ds = createDataSet(connection);
                rs = ds.getColumns(null, schemaName, tableName, columnName);
                while (rs.next()) {
                    str = rs.getString(6);
                }
                rs.close();
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }


        return str;

    }

    public static int getColumnSize(String schemaName, String tableName, String columnName) throws SQLException {

        String str = "0";
        Connection connection = getDatabaseConnectionFor(loginDetails.get("database"));
        int i = 0;
        if (connection != null) {
            ResultSet rs = null;
            i = 0;
            try {
                DatabaseMetaData ds = createDataSet(connection);
                rs = ds.getColumns(null, schemaName, tableName, columnName);

                while (rs.next()) {
                    str = rs.getString(7);
                }
                i = Integer.parseInt(str);
                rs.close();
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }

        return i;

    }

    public static boolean checkPrimary(String schemaName, String tableName, String columnName) throws SQLException {

        boolean primary = false;
        Connection connection = getDatabaseConnectionFor(loginDetails.get("database"));
        if (connection != null) {
            ResultSet rs = null;


            try {
                DatabaseMetaData ds = createDataSet(connection);
                rs = ds.getPrimaryKeys(null, schemaName, tableName);
                while (rs.next()) {
                    String str = rs.getString(4);
                    if (str.equalsIgnoreCase(columnName)) {
                        return true;
                    }
                }
                rs.close();
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }


        return primary;

    }

    public static boolean checkNullable(String schemaName, String tableName, String columnName) throws SQLException {

        boolean nullable = false;
        Connection connection = getDatabaseConnectionFor(loginDetails.get("database"));
        if (connection != null) {
            int i = 0;
            ResultSet rs = null;


            try {
                DatabaseMetaData ds = createDataSet(connection);
                rs = ds.getColumns(null, schemaName, tableName, columnName);
                while (rs.next()) {
                    i = rs.getInt(11);

                }

                if (i == 1) {
                    nullable = true;
                } else {
                    nullable = false;
                }

                rs.close();
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }

        return nullable;

    }

    public static int getRowCount(String schemaName, String tableName) {

        int row = 0;
        Connection connection = getDatabaseConnectionFor(loginDetails.get("database"));
        if (connection != null) {
            ResultSet rs = null;
            Statement statement = null;


            try {

                //  statement = con.createStatement();
                statement = connection.createStatement();
                String sql = "select count(*) from \"" + schemaName + "\".\"" + tableName+"\"";
                System.out.println("SQL FIRED FOR COLUMN COUNT = "+sql);
                rs = statement.executeQuery(sql);
                while (rs.next()) {
                    row = Integer.parseInt(rs.getString(1));
                }
                System.out.println("NO. OF ROWS : "+row);
                rs.close();
                connection.close();
            } catch (Exception e) {

            }
        }

        return row;

    }

    public static List<String> parseSQL(String sql) {
        int countSingleQt = 0, start = 0, end = 0, j = 0;
        if (!sql.endsWith(";")) {
            sql = sql.concat(";");
        }
        List arrs = new ArrayList(0);

        for (int i = 0; i < sql.length(); i++) {
            char ct = sql.charAt(i);
            if (ct == '\'') {
                countSingleQt++;
                if (countSingleQt == 2) {
                    countSingleQt = 0;
                }
            }
            if (ct == ';') {
                if (countSingleQt == 1) {

                } else {
                    end = i;
                    String s = sql.substring(start, end);
                    arrs.add(s);
                    start = end + 1;
                }
            }
        }
        return arrs;
    }

    public static boolean isValidSQLQuery(String sql){
        sql=sql.trim();
        if(sql.length()==0){
            return false;
        }
        int firstKeywordIndex=sql.indexOf(" ");
        if(firstKeywordIndex<0){
            return false;
        }
        if(MiscUtility.getOccurrenceTimes(sql, '\'')%2 !=0 || MiscUtility.getOccurrenceTimes(sql, '\"')%2!=0){
            return false;
        }

        String firstKeyword=sql.substring(0,firstKeywordIndex).toUpperCase();
        List<String> validKeywords=new ArrayList<String>();
        validKeywords.add("CREATE");
        validKeywords.add("DROP");
        validKeywords.add("ALTER");
        validKeywords.add("TRUNCATE");
        validKeywords.add("INSERT");
        validKeywords.add("UPDATE");
        validKeywords.add("DELETE");
        validKeywords.add("GRANT");
        validKeywords.add("REVOKE");
        validKeywords.add("ROLLBACK");
        validKeywords.add("COMMIT");
        validKeywords.add("SELECT");
        validKeywords.add("DESC");
        validKeywords.add("EXEC");
        validKeywords.add("SHOW");
        if(!validKeywords.contains(firstKeyword)){
            return false;
        }else{
            return true;
        }


    }

    public static boolean isReadQuery(String sql){
        String firstKeyword=sql.substring(0,sql.indexOf(" ")).toUpperCase();
        List<String> readQuery=new ArrayList<String>();
        readQuery.add("SELECT");
        readQuery.add("DESC");
        readQuery.add("EXEC");
        readQuery.add("SHOW");
        if(readQuery.contains(firstKeyword)){
          return true;
        }else {
            return false;
        }
    }

    // *#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*# Called From Application *#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*# //


    /* public static void main(String[] args) {
        DataBaseUtility obj = new DataBaseUtility();
     // boolean result = createDerbyConnection("DEMO", "DEMO1", "192.168.33.64", "1530", "DEMO1");
     // boolean result = createMySQLConnection("root", "root", "127.0.0.1", "3306", "");
    //  boolean result = createMSSqlConnection("globalid", "globalid", "192.168.33.9", "1476", "");
        boolean result = createDB2Connection("db2admin", "admin", "192.168.33.5", "50000", "sample");


         if (result) {
           JOptionPane.showMessageDialog(null, "CONNECTION SUCESSFUL", "Database Connection", JOptionPane.INFORMATION_MESSAGE);

        } else {
             JOptionPane.showMessageDialog(null, "CONNECTION FAILED", "Darabase Connection", JOptionPane.ERROR_MESSAGE);
         }


    }*/
}
