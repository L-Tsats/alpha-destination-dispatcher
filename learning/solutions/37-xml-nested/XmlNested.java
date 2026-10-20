// EXERCISE 37 - Reading a nested XML tree (reference solution)

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import java.io.File;

public class XmlNested {

    public static void main(String[] args) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new File("exercises/37-xml-nested/catalog.xml"));

            NodeList books = doc.getElementsByTagName("book");
            System.out.println("Books in catalog: " + books.getLength());

            for (int i = 0; i < books.getLength(); i++) {
                Element book = (Element) books.item(i);

                String title  = book.getElementsByTagName("title").item(0).getTextContent();
                String author = book.getElementsByTagName("author").item(0).getTextContent();
                String year   = book.getElementsByTagName("year").item(0).getTextContent();
                String isbn   = book.getAttribute("isbn");

                System.out.printf("- %s by %s (%s) [%s]%n", title, author, year, isbn);
            }

            // BONUS: only books after 2004
            System.out.println("\nPublished after 2004:");
            for (int i = 0; i < books.getLength(); i++) {
                Element book = (Element) books.item(i);
                int year = Integer.parseInt(
                        book.getElementsByTagName("year").item(0).getTextContent());
                if (year > 2004) {
                    String title = book.getElementsByTagName("title").item(0).getTextContent();
                    System.out.println("  " + title + " (" + year + ")");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
