// EXERCISE 36 - Reading XML with the DOM (reference solution)

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

public class XmlDom {

    public static void main(String[] args) {
        String path = args.length > 0 ? args[0] : "grades.xml";

        Document doc = null;
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            doc = db.parse(new File(path));
        } catch (Exception e) {
            System.err.println("Parsing failed: " + e);
            System.exit(1);
        }

        // Every <grade> element
        NodeList grades = doc.getElementsByTagName("grade");
        double sum = 0.0;
        for (int i = 0; i < grades.getLength(); i++) {
            String text = grades.item(i).getFirstChild().getNodeValue();  // the "9" etc.
            sum += Double.parseDouble(text);
        }
        System.out.println("Average grade: " + (sum / grades.getLength()));  // 8.25

        // BONUS: list each id with its grade (parallel tag lists)
        NodeList ids = doc.getElementsByTagName("id");
        System.out.println("\nPer student:");
        for (int i = 0; i < ids.getLength(); i++) {
            String id = ids.item(i).getFirstChild().getNodeValue();
            String grade = grades.item(i).getFirstChild().getNodeValue();
            System.out.println("  " + id + " -> " + grade);
        }
    }
}
