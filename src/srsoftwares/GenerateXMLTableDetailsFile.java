package srsoftwares;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import srsoftwares.DataBaseUtility;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.dom.DOMSource;
import javax.swing.*;
import java.io.File;
import java.io.PrintWriter;
import java.util.List;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: Admin
 * Date: May 2, 2011
 * Time: 4:34:07 PM
 * To change this template use File | Settings | File Templates.
 */
public class GenerateXMLTableDetailsFile {
    private static File file;
    private static String schemaName;
    private static String tableName;

    public static void createXMLTableDetails(File f, String schema, String table) {
        file = f;
        schemaName = schema;
        tableName = table;
        try {
            PrintWriter printWriter = new PrintWriter(file);
            printWriter.flush();
            DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
            Document doc = docBuilder.newDocument();
            Element tableElement = doc.createElement("Table");
            tableElement.setAttribute("Name", tableName);


            Vector<String> columnNames = DataBaseUtility.getColumnNames(schemaName, tableName);
            Vector<Vector<Object>> allRecords = DataBaseUtility.fillTable(schemaName, tableName);


            JTable recordsTable = new JTable(allRecords, columnNames);

            int rowNo = recordsTable.getRowCount();
            int colNo = recordsTable.getColumnCount();

            for (int i = 0; i < rowNo; i++) {
                Element rowElement = doc.createElement("Row");
                int j = 0;
                for (String columnName : columnNames) {

                    Element columnElement = doc.createElement("COLUMN");

                    String name = String.valueOf(recordsTable.getValueAt(i, j));
                    j++;
                    columnElement.setAttribute("Name", columnName);
                 columnElement.setTextContent(name);
                 //   columnElement.setPrefix(name);
                 //   columnElement.setNodeValue(name); 
                    rowElement.appendChild(columnElement);
                  }
                tableElement.appendChild(rowElement);
            }
            doc.appendChild(tableElement);
            TransformerFactory transformerFactory =TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            transformer.transform(source, new StreamResult(printWriter));
            printWriter.close();

        } catch (Exception e1) {
            e1.printStackTrace();
        }

    }

}
