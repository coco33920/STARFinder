package fr.charlotte

import scala.io.StdIn.readLine
import fr.charlotte.ast.Ast.Tree.*
import fr.charlotte.ast.Parameter.Type.*
import fr.charlotte.ast.Parser
import fr.charlotte.lexing.{Lexer, Token}
import fr.charlotte.providers.*
import cats.implicits.*
import com.monovore.decline.*
import fr.charlotte.config.{Config, Utils}
import fr.charlotte.runtime.{REPL, Translator}

/*
Command architecture
--provider "provider" => change provider "only star is configured atm
--info => show the config, version, current provider etc.
--update => update the current provider
--verbose => toggle the verbose
=> send to the REPL
*/

object STARFinder extends CommandApp(
  name = "star-finder", header = "Find your bus/metro/tram stops with logic!",
  main = {
    val providerOpt: Opts[String] = Opts.option[String]("provider", help = "Which provider to choose from").withDefault("default")
    val infoOpt = Opts.flag("info", "Prints information about the current version").orFalse
    val updateOpt = Opts.flag("update", "Update your current provider (not implemented yet)").orFalse
    val verboseOpt = Opts.flag("verbose", "Toggle verbose in the REPL").orFalse

    (providerOpt, infoOpt, updateOpt, verboseOpt).mapN {
      (provider, info, update, verbose) =>
        var repl = false
        Config.init()
        Config.initConfig()
        if (!provider.trim.toLowerCase().equalsIgnoreCase(Config.config("defaultProvider").str.trim) && !provider.equalsIgnoreCase("default")) then
          if verbose then
            println("Provider differs... updating configuration accordingly")
          Config.updateConfiguration(provider.trim)
        provider.trim().toLowerCase() match
          case "star" => repl = true
          case "rennes" => repl = true
          case "nantes" => repl = true
          case "tan" => repl = true
          case "tcl" => repl = true
          case "lyon" => repl = true
          case "default" => repl = true
          case _ => if verbose then println("Backend currently supported : STAR/Rennes TAN/Nantes") else ()

        Config.config("defaultProvider").str.toUpperCase.trim match
          case "STAR" => Utils.setDefaultProvider(STARProvider.getInstance(verbose))
          case "RENNES" => Utils.setDefaultProvider(STARProvider.getInstance(verbose))
          case "TAN" => Utils.setDefaultProvider(TANProvider.getInstance(verbose))
          case "NANTES" => Utils.setDefaultProvider(TANProvider.getInstance(verbose))
          case "TCL" => Utils.setDefaultProvider(TCLProvider.getInstance(verbose))
          case "LYON" => Utils.setDefaultProvider(TCLProvider.getInstance(verbose))
          case e => {
            if verbose then
              println("The backend for " + e + " is not implemented yet, defaulting to STAR")
            Utils.setDefaultProvider(STARProvider.getInstance(verbose))
          }
        if (update)
          println("Please wait, this may take up to a few minutes.")
          val updated = Utils.getDefaultProvider.update();
          println(updated)
          repl = false
        if (info)
          println(s"STARFinder version ${Utils.VERSION}, Made by Charlotte Thomas @ISTIC Univ-Rennes1 to learn Scala, backend for ${Utils.getDefaultProvider.implementationName()}-${Utils.getDefaultProvider.townName()}")
          repl = false
        if (repl)
          REPL(Utils.getDefaultProvider, verbose).main()
    }
  },

  version = Utils.VERSION
)

