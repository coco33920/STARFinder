package fr.charlotte

import scala.io.StdIn.readLine
import fr.charlotte.ast.Ast.Tree.*
import fr.charlotte.ast.Parameter.Type.*
import fr.charlotte.ast.Parser
import fr.charlotte.lexing.{Lexer, Token}
import fr.charlotte.providers.STARProvider
import cats.implicits.*
import com.monovore.decline.*
import fr.charlotte.config.{Config, Utils}
import fr.charlotte.runtime.{REPL, Translator}

/*
Command architecture
--provider "provider" => change provider "only star is configured atm
--info => show the config, version, current provider etc.
--update => update the current provider (todo later)
--verbose => toggle the verbose
=> send to the REPL
*/

object STARFinder extends CommandApp(
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
          case "STAR" => Utils.setDefaultProvider(STARProvider())
          case e => {
            if verbose then
              println("The backend for " + e + " is not implemented yet, defaulting to STAR")
            Utils.setDefaultProvider(STARProvider())
          }
        if (update)
          println("Not implemented yet")
          repl = false
        if (info)
          println(s"STARFinder version ${Utils.VERSION}, Made by Charlotte Thomas @ISTIC Univ-Rennes1 to learn Scala, backend for ${Utils.getDefaultProvider.implementationName()}-${Utils.getDefaultProvider.townName()}")
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
          REPL(Utils.getDefaultProvider, verbose).main()
    }
  },

  version = Utils.VERSION
)

