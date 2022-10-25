package fr.charlotte

import org.apache.commons.io.FileUtils

import java.io.{File, FileReader}
import scala.collection.mutable

object Config {
  var f: File = null;
  var config: ujson.Value.Value = null;

  def init(): Unit = {
    val homepage = System.getProperty("user.home")
    val delimiter = File.separator
    val url = homepage + delimiter + ".starfinder" + delimiter;
    val f = File(url)
    if (!f.exists())
      f.mkdirs()
    val file = File(url + "config.json")
    if (!file.exists())
      file.createNewFile()
    this.f = file
    val json_string = os.read(os.Path(file.getAbsolutePath))
    try {
     val _ = ujson.read(json_string)
    }catch {
      case _: ujson.IncompleteParseException => {
        os.write.append(os.Path(file.getAbsolutePath), "{\"defaultProvider\":\"STAR\"}")
      }
    }
  }

  def initConfig(): Unit = {
    if this.f == null then
      init()
    val s = os.read(os.Path(f.getAbsolutePath))
    val data = ujson.read(s)
    this.config = data
  }

  def updateConfiguration(newProvider: String): Unit = {
    this.config("defaultProvider") = newProvider
    saveConfig()
  }

  def saveConfig(): Unit = {
    os.write.over(os.Path(f.getAbsolutePath), config)
  }

}
