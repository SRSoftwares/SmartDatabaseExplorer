package srsoftwares;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * Author: Sumit Roy
 * Date: 12/27/11
 * Time: 5:52 PM
 * To change this template use File | Settings | File Templates.
 */
public class MiscUtility implements ClipboardOwner {
    public static Map<String, String> dbKeywordResultMap = new HashMap<String, String>();

    static {
        dbKeywordResultMap.put("CREATE", "Schema/Table Created Successfully");
        dbKeywordResultMap.put("DROP", "Schema/Table Dropped Successfully");
        dbKeywordResultMap.put("ALTER", "Schema/Table Altered Successfully");
        dbKeywordResultMap.put("TRUNCATE", "Schema/Table Truncated Successfully");
        dbKeywordResultMap.put("INSERT", "Record(s) inserted to table Successfully");
        dbKeywordResultMap.put("UPDATE", "Schema/Table Updated Successfully");
        dbKeywordResultMap.put("DELETE", "Record(s) Deleted from table Successfully");
        dbKeywordResultMap.put("GRANT", "Permission Granted to Schema/Table Successfully");
        dbKeywordResultMap.put("REVOKE", "Permission Revoked from table Successfully");
        dbKeywordResultMap.put("ROLLBACK", "Successfully Rollback to earlier Check Point");
        dbKeywordResultMap.put("COMMIT", "Changes committed to database Successfully");

    }

    public static String getUpdateQueryMessageForQuery(String query) {
        String message = "";
        String keyword = query.substring(0, query.indexOf(" ")).toUpperCase();
        message = dbKeywordResultMap.get(keyword);
        return message;
    }

    public static Map<String, Integer> fontStylesMap = new HashMap<String, Integer>();

    public static String exportRecordToExcel(String fileNameWithDirectory, String sheetName, Vector<String> tableHead, Vector<Vector<Object>> data) {
        try {
            File excelFile = new File(fileNameWithDirectory);
            HSSFWorkbook hwb = null;
            HSSFSheet sheet;
            if (excelFile.exists() && fileNameWithDirectory.endsWith(".xls")) {    // VALID EXCEL FILE that already exist...new sheet will be created instead of creation of new file
                int sheetCount = getSheetCountForAnExcel(excelFile);
                sheetName = sheetName + " " + sheetCount++;
                FileInputStream fin = new FileInputStream(fileNameWithDirectory);
                hwb = new HSSFWorkbook(fin);
            } else {
                hwb = new HSSFWorkbook();
            }

            sheet = hwb.createSheet(sheetName);
            HSSFRow rowHead = sheet.createRow(0);
            int i = 0;
            for (String eachColumn : tableHead) {
                rowHead.createCell(i).setCellValue(eachColumn);
                i++;
            }
            int rowIndex = 1;
            for (Vector<Object> eachRow : data) {
                HSSFRow dataRow = sheet.createRow(rowIndex);
                int j = 0;
                for (Object item : eachRow) {
                    String itemVal = item == null ? "null" : item.toString();
                    dataRow.createCell(j).setCellValue(itemVal);
                    j++;
                }
                rowIndex++;
            }
            FileOutputStream fileOut = null;

                fileOut = new FileOutputStream(fileNameWithDirectory);
                hwb.write(fileOut);
                fileOut.close();

        } catch (Exception e) {
            return e.getMessage();
        }


        return "Data Saved Successfully";
    }

    public static String saveQueryAsSQLText(String file,String textToWrite){
        String message="";
        System.out.println("FILE NAME TO SAVE = "+file);
        try {
            FileOutputStream fileOutputStream=new FileOutputStream(file);
            PrintStream printStream=new PrintStream(fileOutputStream);
            printStream.print(textToWrite);
            printStream.close();
            fileOutputStream.close();
            message ="Data Saved Successfully";
        } catch (Exception e) {
            return e.getMessage();
        }
        return message;
    }

    public static int getSheetCountForAnExcel(File file) {
        int sheetCount = 0;

        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            HSSFWorkbook workbook = new HSSFWorkbook(fileInputStream);
            sheetCount = workbook.getNumberOfSheets();
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return sheetCount;
    }

    /**
     * -- SR Softwares - Smart Database Explorer
     * <p/>  Method that return current system time hour minute seconds milliseconds
     *
     * @return current system time as a map (key ,value pair).
     *         <p/> Here are the Map value (CASE SENSITIVE)
     *         <p/> Key : Value
     *         <p/> hour  - current hour as String
     *         <p/> minute - current minute as String
     *         <p/> second - current second as String
     *         <p/> millisecond - current millisecond as string
     *         <p/> time - current system time in milli second as string
     *         <p/> timeFormatted - current system time im HHMMSSMS (hour minute sec millisecond) as string
     *         <p/> date - current system date in yyyymmdd format
     *         <p/>
     *         -- Copyrights SR Softwares , 2011
     */

    public static Map<String, String> getCurrentSysDateTime() {
        Map<String, String> sysTime = new HashMap<String, String>();
        Calendar calendar = new GregorianCalendar();
        long timeInMilli = calendar.getTimeInMillis();

        String DATE_FORMAT_NOW = "yyyyMMdd";
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HHmmssSSS");

        int h = calendar.get(Calendar.HOUR);
        int m = calendar.get(Calendar.MINUTE);
        int s = calendar.get(Calendar.SECOND);
        int ms = calendar.get(Calendar.MILLISECOND);

        String hour = String.valueOf(h);
        String minute = String.valueOf(m);
        String seconds = String.valueOf(s);
        String milliSecond = String.valueOf(ms);
        String time = String.valueOf(timeInMilli);
        sysTime.put("hour", hour);
        sysTime.put("minute", minute);
        sysTime.put("second", seconds);
        sysTime.put("millisecond", milliSecond);
        sysTime.put("time", time);
        sysTime.put("timeFormatted", simpleDateFormat.format(cal.getTime()));
        sysTime.put("date", sdf.format(cal.getTime()));
        return sysTime;
    }

    // Convert to Standard Time Format hh:mm:ss ms aa  e.g 06:05:10.304 pm
    public static String convertToStandardTimeFormat(String timeFormatted) {
        String formattedTime = "";
        String tt = "";
        if (timeFormatted.length() == 9) {
            int hr = Integer.parseInt(timeFormatted.substring(0, 2));
            if (hr >= 12) {
                hr -= 12;
                tt = "pm";
            } else {
                tt = "am";
            }
            String hour = String.valueOf(hr);
            String min = timeFormatted.substring(2, 4);
            String sec = timeFormatted.substring(4, 6);
            String milliSec = timeFormatted.substring(6);
            formattedTime = hour + ":" + min + ":" + sec + "." + milliSec + " " + tt;
        }

        return formattedTime;
    }

    public static Dimension getScreenSize() {
        Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        return screenSize;
    }

    public static String convertToStandardDateFormat(String unformattedDate) {
        String formattedDate = "";
        if (unformattedDate.length() == 8) {
            String year = unformattedDate.substring(0, 4);
            String month = unformattedDate.substring(4, 6);
            String date = unformattedDate.substring(6);
            formattedDate = date + "." + month + "." + year;
        }

        return formattedDate;
    }

    public static Node getNodeFromXML(Node root, String tagName, String nodeValueToFind, String attribute) {
        Node nodeToFind = null;
        NodeList childNodes = ((Element) root).getElementsByTagName(tagName);

        for (int i = 0; i < childNodes.getLength(); i++) {
            Node node = childNodes.item(i);
            String nodeValue = ((Element) node).getAttribute(attribute);

            if (nodeValue.equals(nodeValueToFind)) {
                nodeToFind = node;

                break;
            }
        }


        return nodeToFind;
    }

    public static boolean isValidXML(Element root, String mustHaveTagName) {
        NodeList childNodes = null;
        int queryCount = 0;
        String database = DataBaseUtility.loginDetails.get("database");
        if (mustHaveTagName.equals("Time")) {   // no. of query will be always equal to no of time tag.

            queryCount = root.getAttribute("Total_Query_Count").isEmpty() ? -1 : Integer.parseInt(root.getAttribute("Total_Query_Count"));
            if (queryCount < 0) {
                return false;
            }
            childNodes = root.getElementsByTagName(mustHaveTagName);
            if (childNodes.getLength() == queryCount) {
                return true;
            } else {
                return false;
            }
        } else {

            childNodes = root.getElementsByTagName(mustHaveTagName);
            if (childNodes.getLength() == 0) {
                return false;
            } else {
                return true;
            }
        }

    }

    public static double getDifferenceTime(long from, long to) {
        String differTime = "";

        long dif = to - from;
        double toSec = (double) dif / 1000;

        return toSec;
    }

    /**
     * -- SR Softwares - Smart Database Explorer
     * <p/>  Method that returns no. of times a given character occurred in a given text
     *
     * @param sourceText source text where a specific character is expected to be occur
     * @param charToFind key character to be occurred
     *                   <p/> example : sourceText = HELLO WORLD , charToFind=L , returns 3 as L Occurred 3 times.
     *                   <p/>
     *                   -- Copyrights SR Softwares , 2011
     * @return No. of times a character has occurred, 0 when no occurrence found
     */
    public static int getOccurrenceTimes(String sourceText, char charToFind) {
        int occurrenceTime = 0;
        for (int i = 0; i < sourceText.length(); i++) {
            if (sourceText.charAt(i) == charToFind) {
                occurrenceTime++;
            }
        }


        return occurrenceTime;
    }

    public static String[] splitIPAddress(String ipAddress) {
        int no_of_Separator = getOccurrenceTimes(ipAddress, '.');
        String[] ipAddresses = new String[4];
        if (no_of_Separator == 3) {
            int i = 0;
            for (i = 0; i < 3; i++) {
                int index = ipAddress.indexOf(".");
                ipAddresses[i] = ipAddress.substring(0, index);
                ipAddress = ipAddress.substring(index + 1);
                index = ipAddress.indexOf(".");
            }
            ipAddresses[i] = ipAddress;
        }
        return ipAddresses;
    }

    // validation of an IP Address of type a.b.c.d
    public static boolean isValidIPAddress(String ipAddress) {
        String[] addressParts = splitIPAddress(ipAddress);
        int a = Integer.parseInt(addressParts[0]);
        int b = Integer.parseInt(addressParts[1]);
        int c = Integer.parseInt(addressParts[2]);
        int d = Integer.parseInt(addressParts[3]);
        if ((a >= 0 && a <= 255) && (b >= 0 && b <= 255) && (c >= 0 && c <= 255) && (d >= 0 && d <= 255)) {
            return true;
        } else {
            return false;
        }

    }

    public static void addScrollAction(final JComboBox comboBox) {
        comboBox.addMouseWheelListener(new MouseWheelListener() {
            public void mouseWheelMoved(MouseWheelEvent e) {
                int scrollType = e.getWheelRotation();
                if (scrollType > 0 && (comboBox.getSelectedIndex() < (comboBox.getItemCount() - 1))) { // DOWN SCROLLING
                    comboBox.setSelectedIndex(comboBox.getSelectedIndex() + 1);


                } else if (scrollType < 0) {   // UP SCROLLING
                    if (comboBox.getSelectedIndex() > 0) {
                        comboBox.setSelectedIndex(comboBox.getSelectedIndex() - 1);

                    }

                }


            }
        });


    }

    public static String extractIPAddress(String hostWithIP, String separator) {
        String ip = "";

        int index = hostWithIP.indexOf(separator);
        // when no separator exist
        if (index < 0) {
            return hostWithIP;
        } else {
            ip = hostWithIP.substring(0, index);
            return ip;
        }
    }

    public static String extractHostName(String hostWithIP, String separator) {
        String hostName = "";
        int index = hostWithIP.indexOf(separator);
        hostName = hostWithIP.substring(index + 1, hostWithIP.length());


        return hostName;

    }

    /**
     * -- SR Softwares - Smart Database Explorer
     * <p/>
     * return system name, ip address and the domain
     *
     * @return an array of string whose <p/> 1st value : System's Name, <p/> 2nd Value : System's IP Address and <p/> 3rd Value : System's Domain
     *         <p/>
     *         -- Copyrights SR Softwares
     */
    public static String[] getSystemNameIPAndDomain() {
        String sysNameIPAndDomain[] = new String[3];

        InetAddress ownIP = null;
        try {
            ownIP = InetAddress.getLocalHost();
            String ip = ownIP.getHostAddress();
            String name = InetAddress.getByName(ip).getHostName();
            String domain = ip.substring(0, ip.lastIndexOf("."));
            sysNameIPAndDomain[0] = name;
            sysNameIPAndDomain[1] = ip;
            sysNameIPAndDomain[2] = domain;

        } catch (UnknownHostException e1) {
        }

        return sysNameIPAndDomain;

    }

    public static String getFileExtension(File f) {
        String ext = null;
        String s = f.getName();
        int i = s.lastIndexOf('.');

        if (i > 0 &&  i < s.length() - 1) {
            ext = s.substring(i+1).toLowerCase();
        }
        return ext;
    }


    public static void addCellHeaderRender(TableColumnModel columnModel, TableHeaderRender headerRender, TableRenderer tableRenderer) {
        for (int i = 0; i < columnModel.getColumnCount(); i++) {
            TableColumn tableColumn = columnModel.getColumn(i);
            if (headerRender != null) {
                tableColumn.setHeaderRenderer(headerRender);
            }
            if (tableRenderer != null) {
                tableColumn.setCellRenderer(tableRenderer);
            }
        }
    }

    public static int adjustTableColumnWidth(TableRenderer tabRender, int basicWidth) {
        JLabel label = tabRender;
        Font f = label.getFont();
        FontMetrics mat = label.getFontMetrics(f);
        int v = mat.stringWidth(getStringOfWidth(basicWidth));
        return v;
    }

    public static String getStringOfWidth(int n) {
        String str = "";
        for (int i = 0; i < n; i++) {
            str += "*";
        }
        return str;
    }

    public static String[] getAllSystemFonts() {
        GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();

        return env.getAvailableFontFamilyNames();
    }

    public static Integer[] getAllSystemFontSizes() {
        Integer[] fontSizes = new Integer[41];
        for (int i = 8; i <= 48; i++) {
            fontSizes[i - 8] = i;
        }
        return fontSizes;
    }

    public static String[] getAllFontStyles() {
        String[] fontStyles;
        fontStylesMap.put("Normal", Font.PLAIN);
        fontStylesMap.put("Bold", Font.BOLD);
        fontStylesMap.put("Italics", Font.ITALIC);
        fontStylesMap.put("Bold & Italics", Font.BOLD + Font.ITALIC);
        fontStyles = new String[]{"Normal", "Bold", "Italics", "Bold & Italics"};
        return fontStyles;
    }

    public static List<Integer> getMaxLengthsForColumns(Vector<Vector<Object>> records, Vector<String> columnNames) {
        List<Integer> list = new ArrayList<Integer>(columnNames.size());
        for (int i = 0; i < columnNames.size(); i++) {
            list.add(i, columnNames.get(i).length());
        }

        for (Vector<Object> record : records) {
            for (int i = 0; i < record.size(); i++) {
                Object o = record.get(i);
                if (o instanceof String) {
                    String str = o.toString().trim();
                    if (str.length() > list.get(i)) {
                        list.set(i, str.length());
                    }
                }
            }
        }
        return list;
    }

    public void lostOwnership(Clipboard clipboard, Transferable contents) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

     /**
     * -- SR Softwares - Smart Database Explorer
     * <p/>
     * Changes swing button look and feel with the icon supplied.
     * @param hoverInIconName   Name of the icon with extension(.jpg,.png) when the mouse will be entered in the button
     * @param hoverOutIconName  Name of the icon with extension(.jpg,.png) when the mouse will be exited from the button
     * @param toolTipText Tooltip text for the button
     *  <p/>
     *         -- Copyrights SR Softwares , 2012  www.srsoftwares.co.nr
     */
    public static void changeButtonLook(final JButton button, final String hoverInIconName,final String hoverOutIconName,final String toolTipText){
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setToolTipText(toolTipText);
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                try {
                    button.setIcon(new ImageIcon(ImageIO.read(this.getClass().getResource("images/"+hoverInIconName))));
                } catch (IOException e1) {
                    e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                try {
                    button.setIcon(new ImageIcon(ImageIO.read(this.getClass().getResource("images/"+hoverOutIconName))));
                } catch (IOException e1) {
                    e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
        });
    }
}
