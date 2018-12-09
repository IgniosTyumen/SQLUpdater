
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.model.SharedStringsTable;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.InputStream;

/**
 * XSSF and SAX (Event API)
 */
public class ReadTarifsFromXLSM {

    static String ean = "";
    static Integer article = 0;
    static Double readDouble;
    static String cellAdr = "";
    static String rowAdr = "";

    public static String getCellAdr() {
        return cellAdr;
    }

    public static void setCellAdr(String cellAdr) {
        ReadTarifsFromXLSM.cellAdr = cellAdr;
    }

    public void processOneSheet(String filename) throws Exception {
        OPCPackage pkg = OPCPackage.open(filename);
        XSSFReader r = new XSSFReader(pkg);
        SharedStringsTable sst = r.getSharedStringsTable();

        XMLReader parser = fetchSheetParser(sst);

        // rId2 found by processing the Workbook
        // Seems to either be rId# or rSheet#
        InputStream sheet2 = r.getSheet("rId1");
        InputSource sheetSource = new InputSource(sheet2);
        parser.parse(sheetSource);
        sheet2.close();
    }


    public XMLReader fetchSheetParser(SharedStringsTable sst) throws SAXException {
        XMLReader parser =
                XMLReaderFactory.createXMLReader(
                        "org.apache.xerces.parsers.SAXParser"
                );
        ContentHandler handler = new SheetHandler(sst);
        parser.setContentHandler(handler);
        return parser;
    }

    /**
     * See org.xml.sax.helpers.DefaultHandler javadocs
     */
    private static class SheetHandler extends DefaultHandler {
        private SharedStringsTable sst;
        private String lastContents;
        private boolean nextIsString;
        private String adress;

        private SheetHandler(SharedStringsTable sst) {
            this.sst = sst;
        }

        private static void uploadTarifParts(String adressColumn, String meaning) {
            switch (adressColumn) {
                case ("A"): {
                    ean = meaning;
                    break;
                }
                case ("E"): {
                    article = Integer.parseInt(meaning);
                    Ean eanObj = new Ean(ean,article);
                    JDBCInitializer.checkifexists(eanObj);
                    break;
                }
            }
        }

        public void startElement(String uri, String localName, String name,
                                 Attributes attributes) throws SAXException {
            // c => cell
            if (name.equals("c")) {
                // Print the cell reference
                adress = attributes.getValue("r");

                String cellAdrs[] = CellSeparator.translateAdressToColAndRows(adress);
                cellAdr = cellAdrs[0];
                rowAdr = cellAdrs[1];
                // Figure out if the value is an index in the SST
                String cellType = attributes.getValue("t");
                if (cellType != null && cellType.equals("s")) {
                    nextIsString = true;
                } else {
                    nextIsString = false;
                }
            }
            // Clear contents cache
            lastContents = "";
        }

        public void endElement(String uri, String localName, String name)
                throws SAXException {
            // Process the last contents as required.
            // Do now, as characters() may be called more than once
            if (nextIsString && lastContents != null && !lastContents.equals("")) {

                int idx = Integer.parseInt(lastContents);
                lastContents = new XSSFRichTextString(sst.getEntryAt(idx)).toString();
            }

            // v => contents of a cell
            // Output after we've seen the string contents
            if (name.equals("v") && !rowAdr.equals("1") && !rowAdr.equals("2")){
                uploadTarifParts(ReadTarifsFromXLSM.getCellAdr(), lastContents);
            }
            lastContents = "";

        }

        public void characters(char[] ch, int start, int length)
                throws SAXException {
            lastContents += new String(ch, start, length);
        }
    }


}