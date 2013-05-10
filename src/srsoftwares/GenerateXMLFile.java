package srsoftwares;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.dom.DOMSource;
import java.io.File;
import java.io.PrintWriter;
import java.util.List;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: Admin
 * Date: May 2, 2011
 * Time: 2:01:54 PM
 * To change this template use File | Settings | File Templates.
 */
public class GenerateXMLFile {

    private static File file;
    private static String schemaName;
    private static List<String> allSelectedTables;

    public static void createXMLFile(File inFile, String inSchemaName, List<String> inAllSelectedTables) {
        file = inFile;
        schemaName = inSchemaName;
        allSelectedTables = inAllSelectedTables;
        try {
            PrintWriter printWriter = new PrintWriter(file);
            printWriter.flush();
            DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
            Document doc = docBuilder.newDocument();
            Element schemaElement = doc.createElement("Schema");
            schemaElement.setAttribute("Name", schemaName);

            for (String tables : allSelectedTables) {
                Element tableElement = doc.createElement("Table");
                tableElement.setAttribute("Name", tables);
                Vector<String> tableColumnDetails = DataBaseUtility.getColumnNames(schemaName, tables);
                // ENTRY OF DATA FOR EVERY TABLE COLUMN IN A SCHEMA
                for (String tableColumnDetail : tableColumnDetails) {
                    Element columnElement = doc.createElement("COLUMN");
                    columnElement.setAttribute("Name", tableColumnDetail);
                    columnElement.setAttribute("DataType", DataBaseUtility.getColumnType(schemaName, tables, tableColumnDetail));
                    columnElement.setAttribute("Size", String.valueOf(DataBaseUtility.getColumnSize(schemaName, tables, tableColumnDetail)));
                    columnElement.setAttribute("IsPrimaryKey", String.valueOf(DataBaseUtility.checkPrimary(schemaName, tables, tableColumnDetail)));
                    columnElement.setAttribute("IsPrimaryKey", String.valueOf(DataBaseUtility.checkNullable(schemaName, tables, tableColumnDetail)));
                    tableElement.appendChild(columnElement);
                }

                schemaElement.appendChild(tableElement);
            }

            doc.appendChild(schemaElement);
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            transformer.transform(source, new StreamResult(printWriter));
            printWriter.close();

        } catch (Exception e1) {
            e1.printStackTrace();
        }

    }
}
