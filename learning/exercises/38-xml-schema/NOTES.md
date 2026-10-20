# EXERCISE 38: Writing XSD (XML Schema) — by hand, one concept at a time

No Java here. XSD is its own little language. We learn it like we learned Java:
introduce ONE new piece of vocabulary per step, read an example, then you write
one. By the end you'll be able to read any schema (including your auto-generated
course one) and write your own.

An XSD is a document that describes the SHAPE of a valid XML document. It is
itself written in XML. Every XSD starts and ends the same way:

    <?xml version="1.0" encoding="UTF-8"?>
    <xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema">
       ... your declarations go here ...
    </xs:schema>

(The `xs:` prefix just means "this is XML-Schema vocabulary." Your course file
might use a different prefix like `xsd:` or `xcd:` — same thing, cosmetic.)

The example files in this folder go from simplest to richest:
  step1.xsd  - a single text element
  step2.xsd  - an element with a number type
  step3.xsd  - an element that CONTAINS other elements (complexType + sequence)
  step4.xsd  - adding an attribute
  step5.xsd  - optional and repeated elements (minOccurs / maxOccurs)
  step6.xsd  - restricted values (enumeration)
Read each one with the matching section below. Then do YOUR TASK at the end.

------------------------------------------------------------------------------
STEP 1 — an element that holds text
------------------------------------------------------------------------------
Vocabulary: `<xs:element>`, `type="xs:string"`.

    <xs:element name="title" type="xs:string"/>

Reads as: "there is an element called <title>, and whatever goes inside it is
text." It describes this kind of XML:   <title>Anything here</title>

`xs:element` = "declare an element (a tag)."
`name=` = what the tag is called.
`type="xs:string"` = its content is text.

------------------------------------------------------------------------------
STEP 2 — other simple types
------------------------------------------------------------------------------
Vocabulary: the built-in types.
    xs:string   text              <name>Anna</name>
    xs:int      whole number      <age>25</age>
    xs:decimal  number w/ decimals<price>19.99</price>
    xs:boolean  true / false      <active>true</active>
    xs:date     YYYY-MM-DD        <born>2001-04-09</born>

    <xs:element name="age" type="xs:int"/>

Only the type changes; the shape is the same.

------------------------------------------------------------------------------
STEP 3 — an element that CONTAINS other elements
------------------------------------------------------------------------------
This is the big one. An element with children is a `complexType`. The children
go in a `sequence`, which also fixes their ORDER.

    <xs:element name="person">
      <xs:complexType>
        <xs:sequence>
          <xs:element name="name" type="xs:string"/>
          <xs:element name="age"  type="xs:int"/>
        </xs:sequence>
      </xs:complexType>
    </xs:element>

Reads as: "<person> contains a <name> then an <age>, in that order." Describes:
    <person>
      <name>Anna</name>
      <age>25</age>
    </person>

`complexType` = "this element has structure inside it (not just text)."
`sequence`    = "these children, in exactly this order."

------------------------------------------------------------------------------
STEP 4 — an attribute
------------------------------------------------------------------------------
Vocabulary: `<xs:attribute>`, `use="required"`. Attributes live in the START
TAG, not as children. They are declared INSIDE the complexType, AFTER the
sequence.

    <xs:element name="person">
      <xs:complexType>
        <xs:sequence>
          <xs:element name="name" type="xs:string"/>
        </xs:sequence>
        <xs:attribute name="id" type="xs:int" use="required"/>
      </xs:complexType>
    </xs:element>

Describes:   <person id="7"><name>Anna</name></person>

`use="required"` makes the attribute mandatory; leave it out and the attribute
is optional.

------------------------------------------------------------------------------
STEP 5 — how MANY: minOccurs / maxOccurs
------------------------------------------------------------------------------
By default an element appears EXACTLY ONCE. Change that with:
    minOccurs="0"            optional (0 or 1)
    maxOccurs="unbounded"    may repeat any number of times
    minOccurs="2" maxOccurs="5"   between 2 and 5

    <xs:element name="author"   type="xs:string" maxOccurs="unbounded"/>
    <xs:element name="nickname" type="xs:string" minOccurs="0"/>

First line: one or more <author> tags allowed. Second: <nickname> may be absent.

------------------------------------------------------------------------------
STEP 6 — restricting the allowed values (enumeration)
------------------------------------------------------------------------------
A `simpleType` with a `restriction` limits what text is allowed. An
`enumeration` lists the only permitted values.

    <xs:simpleType name="GenreType">
      <xs:restriction base="xs:string">
        <xs:enumeration value="FICTION"/>
        <xs:enumeration value="SCIENCE"/>
        <xs:enumeration value="HISTORY"/>
      </xs:restriction>
    </xs:simpleType>

Now an element of `type="GenreType"` may only contain one of those three words.
`base="xs:string"` = "start from text, then restrict it."

------------------------------------------------------------------------------
YOUR TASK  (write it in  mine.xsd  in this folder — NO Java, just XSD)
------------------------------------------------------------------------------
Describe a <song> element. Build it up using the steps above:
  1. <song> is a complexType containing, IN ORDER:
       - <title>  text                                   (step 1)
       - <artist> text, ONE OR MORE allowed               (step 5)
       - <lengthSeconds> a whole number                   (step 2)
       - <mood> restricted to CALM, HAPPY, or SAD         (step 6)
       - <remix> text, OPTIONAL                           (step 5)
  2. <song> has a required attribute  id  that is an int  (step 4)

Write it by hand. When you think it's right, open  library.xsd  (the fully
worked richer example) and compare the shapes. There's also a reference answer
for the song task in  _solutions/java/38-xml-schema/mine.xsd  (peek AFTER trying).

Tip to feel the structure: an XSD nests exactly like the XML it describes.
Every <xs:element> you write = a tag that will exist in the document.
