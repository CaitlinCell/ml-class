package so.modernized

import scala.xml.Elem
import scala.collection.JavaConverters._
import java.io.FileNotFoundException
import java.nio.file.{Files, Paths}
import java.util.zip.ZipFile

/**
 * @author John Sullivan
 */
object PatentPipeline {
  def fromDir(dir:String):Iterator[Elem] = Paths.get(dir) match {
    case path if Files.isDirectory(path) => Files.newDirectoryStream(path).iterator().asScala.flatMap{filePath =>
      val zipFile = new ZipFile(filePath.toFile)
      zipFile.entries().asScala.flatMap{zipEntry =>
        PatentReader(zipFile.getInputStream(zipEntry))
      }
    }
    case nonPath => throw new FileNotFoundException("%s is not a valid directory path" format nonPath.getFileName)
  }

  def apply(dir:String):Iterator[Patent] = PatentFilters(fromDir(dir)).map{Patent.fromXML}

  def main(args:Array[String]) {
    val patentsXML = PatentPipeline("data/")
    val x = patentsXML.next()


    //println(x \ "us-bibliographic-data-grant" \ "classifications-ipcr")
    //println(x \ "abstract")
    //println(x \ "claims")

    println(patentsXML.next())
  }
}
