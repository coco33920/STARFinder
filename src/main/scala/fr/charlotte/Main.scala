package fr.charlotte

import scala.io.StdIn.readLine
import fr.charlotte.ast.Ast.Tree.*
import fr.charlotte.ast.Parameter.Type.*
import fr.charlotte.ast.Parser
import fr.charlotte.lexing.{Lexer, Token}
import fr.charlotte.providers.STARProvider
import cats.implicits.*
import com.monovore.decline.*
import fr.charlotte.runtime.Translator


private var defaultProvider: Provider = null
def getDefaultProvider: Provider = defaultProvider

/*
Command architecture
--provider "provider" => change provider "only star is configured atm
--info => show the config, version, current provider etc.
--update => update the current provider (todo later)
--verbose => toggle the verbose
=> send to the REPL
*/

object Main extends CommandApp(
  name = "star-finder", header = "Find your bus/metro/tram stops with logic!",
  main = {
    val providerOpt: Opts[String] = Opts.option[String]("provider", help = "Which provider to choose from").withDefault("star")
    val infoOpt = Opts.flag("info", "Prints information about the current version").orFalse
    val updateOpt = Opts.flag("update", "Update your current provider (not implemented yet)").orFalse
    val verboseOpt = Opts.flag("verbose", "Toggle verbose in the REPL").orFalse

    (providerOpt, infoOpt, updateOpt, verboseOpt).mapN {
      (provider, info, update, verbose) =>
        var repl = false
        Config.init()
        Config.initConfig()
        Config.config("defaultProvider").str.trim match
          case "STAR" => defaultProvider = STARProvider()
          case e => {
            if verbose then
              println("The backend for " + e + " is not implemented yet, defaulting to STAR")
            defaultProvider = STARProvider()
          }
        if (update)
          println("Not implemented yet")
          repl = false
        if (info)
          println(s"STARFinder version ${Version.VERSION}, Made by Charlotte Thomas @ISTIC Univ-Rennes1 to learn Scala, backend for ${defaultProvider.implementationName()}-${defaultProvider.townName()}")
          repl = false
        if (!provider.trim.toLowerCase().equalsIgnoreCase(Config.config("defaultProvider").str.trim)) then
          if verbose then
            println("Provider differs... updating configuration accordingly")
          Config.updateConfiguration(provider.trim)
        provider.trim().toLowerCase() match
          case "star" => repl = true
          case "rennes" => repl = true
          case _ => if verbose then println("Backend currently supported : STAR/Rennes") else ()
        if (repl)
          REPL(getDefaultProvider, verbose).main()
    }
  },

  version = Version.VERSION
)

object Version {
  val VERSION = "1.2.0-SNAPSHOT"
}

