/*
 * EXERCISE 37: Reading a NESTED XML tree  (DATA-REP rung 4.5 of 5)
 * ===============================================================
 * Rung 36 read a FLAT list: lots of identical <grade> tags, one query, done.
 * Real XML is a TREE: a parent element CONTAINS specific child elements, and
 * elements can carry ATTRIBUTES. This rung builds the missing skill — walking
 * INTO a parent to read its children — step by step. (This is the DOM version
 * of JSON's "object -> array" build-up.)
 *
 * The data (catalog.xml):
 *     <catalog>
 *       <book isbn="...">
 *         <title>...</title>
 *         <author>...</author>
 *         <year>...</year>
 *       </book>
 *       ... more books ...
 *     </catalog>
 *
 * THE SAME PARSE SETUP AS RUNG 36 (copy it, it's just plumbing):
 *     DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
 *     DocumentBuilder db = dbf.newDocumentBuilder();
 *     Document doc = db.parse(new File("exercises/37-xml-nested/catalog.xml"));
 *   (wrap in try { ... } catch (Exception e) { e.printStackTrace(); })
 *
 * THE NEW IDEA — go from "all tags named X" to "this parent's children":
 *   An Element is a Node you can query AGAIN, scoped to inside itself.
 *   So the pattern for nested reading is:
 *     1. get the list of <book> elements:   doc.getElementsByTagName("book")
 *     2. loop them; cast each item to an Element:
 *            Element book = (Element) books.item(i);
 *     3. reach INTO that book for its OWN children:
 *            String title = book.getElementsByTagName("title")
 *                               .item(0).getTextContent();
 *        getTextContent() is the easy way to get the text inside an element
 *        (nicer than getFirstChild().getNodeValue() from rung 36 — same result).
 *     4. read the ATTRIBUTE straight off the start tag:
 *            String isbn = book.getAttribute("isbn");
 *
 * Imports:
 *   import javax.xml.parsers.DocumentBuilder;
 *   import javax.xml.parsers.DocumentBuilderFactory;
 *   import org.w3c.dom.Document;
 *   import org.w3c.dom.Element;
 *   import org.w3c.dom.NodeList;
 *   import java.io.File;
 *
 * YOUR TASK — build it up in steps:
 * 1. Parse catalog.xml (copy the setup above).
 * 2. getElementsByTagName("book") -> NodeList. Print how many books (getLength).
 * 3. Loop the books. For each, cast to Element and read + print its:
 *      - title   (book.getElementsByTagName("title").item(0).getTextContent())
 *      - author  (same pattern)
 *      - year    (same pattern)
 *      - isbn    (book.getAttribute("isbn"))
 *    Print one tidy line per book.
 * 4. BONUS: parse year to an int and print only books published after 2004.
 *
 */
