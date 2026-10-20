/*
 * EXERCISE 36: Reading XML with the DOM  (DATA-REP rung 4 of 4)
 * ============================================================
 * XML represents data as a TREE of nested "elements":
 *       <result_list>
 *         <result> <id>809678</id> <grade>9</grade> </result>
 *         <result> <id>809630</id> <grade>8</grade> </result>
 *       </result_list>
 * Each element has a start tag <x>, content (text or more elements), end tag </x>.
 *
 * READING XML IN JAVA — the DOM (Document Object Model). You ask a parser to
 * turn the file into an in-memory tree you can walk. Unlike JSON (rung 35) this
 * is BUILT IN to Java — no external library.
 *
 * THE RECIPE:
 *   DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
 *   DocumentBuilder db = dbf.newDocumentBuilder();
 *   Document doc = db.parse(new File("data.xml"));   // the tree
 *
 * Then query the tree. The simplest, most useful query:
 *   NodeList nodes = doc.getElementsByTagName("grade");  // every <grade> element
 *   nodes.getLength();        // how many
 *   nodes.item(i);            // the i-th one (a Node)
 *
 * GETTING THE TEXT INSIDE AN ELEMENT (the one fiddly bit):
 *   For <grade>9</grade>, the "9" is actually a TEXT child node of the <grade>
 *   element. So you reach it with:
 *       nodes.item(i).getFirstChild().getNodeValue()   // -> "9" (a String)
 *   Convert to a number with Integer.parseInt(...) / Double.parseDouble(...).
 *
 * EXCEPTIONS: db.parse(...) can throw several checked exceptions
 * (parsing/IO/config). Simplest for an exercise is to wrap the setup in
 * try { ... } catch (Exception e) { ... }  as the Spinellis Average example does.
 *
 * Imports:
 *   import javax.xml.parsers.DocumentBuilder;
 *   import javax.xml.parsers.DocumentBuilderFactory;
 *   import org.w3c.dom.Document;
 *   import org.w3c.dom.NodeList;
 *   import java.io.File;
 *
 * SET-UP: this module ships with grades.xml at its root (a <result_list> of
 * <result> entries, each with an <id> and a <grade>).
 *
 * YOUR TASK — compute the average grade (the Spinellis "Average" exercise):
 * 1. Build the DocumentBuilderFactory -> DocumentBuilder -> parse grades.xml
 *    into a Document (inside a try/catch).
 * 2. getElementsByTagName("grade") to get every <grade> element.
 * 3. Loop 0..getLength()-1; for each, read item(i).getFirstChild().getNodeValue(),
 *    parse it to a number, and add to a running sum.
 * 4. Print sum / count  (the average). With the shipped file it should be 8.25.
 * 5. BONUS: also list each <id> with its <grade> by looping both tag lists.
 *
 */
