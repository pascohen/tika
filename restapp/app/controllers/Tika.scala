package controllers

import play.api._
import play.api.mvc._

import java.io._
import java.net.URL

import play.api.libs.json._
// you need this import to have combinators
import play.api.libs.functional.syntax._

import org.apache.tika.parser.pdf._
import org.apache.tika.metadata._
import org.apache.tika.parser._
import org.xml.sax._
 
object Handler extends ContentHandler {
	def characters(ch : Array[Char], start: Int, length: Int) {
		println("<<"+new String(ch)+">>")
	}
 
	def endDocument() {
	}
 
	def endElement(uri: String, localName: String, qName: String) {
	}
 
	def endPrefixMapping(prefix: String) {
	}
 
	def ignorableWhitespace(ch: Array[Char], start: Int, length: Int) {
	}
 
	def processingInstruction(target: String, data: String) {
	}
 
	def setDocumentLocator(locator: Locator) {
	}
 
	def skippedEntity(name: String) {
	}
 
	def startDocument() {
	}
 
	def startElement(uri: String, localName: String, qName: String, atts: Attributes) {
	}
 
	def startPrefixMapping(prefix: String, uri: String) {
	}
}
 

object Tika extends Controller {

  implicit val reads = (
    (__ \ 'fileName).read[String] and
    (__ \ 'url).read[String]
  ) tupled

  def parseContent = Action(parse.json) { implicit request =>
    request.body.validate[(String, String)].map{
      case (fileName, url) => println("fileName = "+fileName+" and url = "+url) 
        //val file = "/home/pcohen/Downloads/pdf-test.pdf"     
  	//val pdf = new PDFParser()
        val parser = new AutoDetectParser
        
	val stream = new URL(url).openStream
	val metadata = new Metadata

	parser.parse(stream, Handler, metadata)
        stream.close()
    }
    Ok
  }
}