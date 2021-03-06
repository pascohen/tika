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

import scala.language.postfixOps

import scala.collection.mutable.Set
import scala.collection.mutable.ArrayBuffer


class Handler extends ContentHandler {
  var values = "" //ArrayBuffer[String]() //Set[String]()

  def characters(ch: Array[Char], start: Int, length: Int) {
    values = values + " " + new String(ch)
  }

  def endDocument = {}
  def endElement(uri: String, localName: String, qName: String) = {}
  def endPrefixMapping(prefix: String) = {}
  def ignorableWhitespace(ch: Array[Char], start: Int, length: Int) = {}
  def processingInstruction(target: String, data: String) = {}
  def setDocumentLocator(locator: Locator) = {}
  def skippedEntity(name: String) = {}
  def startDocument = {}
  def startElement(uri: String, localName: String, qName: String, atts: Attributes) = {}
  def startPrefixMapping(prefix: String, uri: String) = {}
  def getValues = values
}

object Tika extends Controller {

  implicit val reads = (
    (__ \ 'fileName).read[String] and
    (__ \ 'url).read[String]) tupled

  def listWrites(l: List[String]): JsValue = {
    new JsArray(l.map { it => new JsString(it) })
  }

  def metadataWrites(l: List[(String, List[String])]): JsValue = {
    new JsObject(l.map { it => (it._1, listWrites(it._2)) })
  }

  def parseContent = Action(parse.json) { implicit request =>
    request.body.validate[(String, String)].map {
      case (fileName, url) =>
        println("fileName = " + fileName + " and url = " + url)
        val parser = new AutoDetectParser

        val stream = new URL(url).openStream
        val metadata = new Metadata

        val h = new Handler
        parser.parse(stream, h, metadata)
        stream.close()

        val names = metadata.names
        val pairs = names.map { n => (n, metadata.getValues(n).toList) }.toList

        Ok(Json.obj("status" -> "Ok", "content" -> h.getValues, "metadata" -> metadataWrites(pairs)))
    }.recoverTotal {
      e => BadRequest(Json.obj("status" -> "KO", "message" -> JsError.toFlatJson(e)))
    }
  }

  def parseRawContent = Action(parse.raw) { implicit request =>
    request.body.asBytes().map { bytes =>
      val parser = new AutoDetectParser

      val stream = new ByteArrayInputStream(bytes)
      val metadata = new Metadata

      val h = new Handler
      parser.parse(stream, h, metadata)

      val names = metadata.names
      val pairs = names.map { n => (n, metadata.getValues(n).toList) }.toList

      Ok(Json.obj("status" -> "Ok", "content" -> h.getValues, "metadata" -> metadataWrites(pairs)))
    }.getOrElse {
      BadRequest(Json.obj("status" -> "KO","request.body" -> request.body.toString))
    }
  }

  def parseRawContent2 = Action(parse.multipartFormData) { implicit request =>
    request.body.file("filedata").map { picture =>
      val filename = picture.ref

      val parser = new AutoDetectParser

      val stream = new FileInputStream(filename.file)
      val metadata = new Metadata

      val h = new Handler
      parser.parse(stream, h, metadata)
      stream.close()

      val names = metadata.names
      val pairs = names.map { n => (n, metadata.getValues(n).toList) }.toList

      Ok(Json.obj("status" -> "Ok", "content" -> h.getValues, "metadata" -> metadataWrites(pairs)))
    }.getOrElse {
      BadRequest(Json.obj("status" -> "KO","request.body" -> request.body.toString))
    }
  }
}