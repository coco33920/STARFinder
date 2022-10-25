package fr.charlotte
import scala.io.StdIn.readLine
import fr.charlotte.ast.Ast.Tree.*
import fr.charlotte.ast.Parameter.Type.*
import fr.charlotte.ast.Parser
import fr.charlotte.lexing.{Lexer, Token}
import fr.charlotte.providers.STARProvider
import fr.charlotte.ast.Translator
import cats.implicits.*
import com.monovore.decline.*


private var defaultProvider: Provider = STARProvider()
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
    val infoOpt = Opts.flag("info","Prints information about the current version").orFalse
    val updateOpt = Opts.flag("update", "Update your current provider (not implemented yet)").orFalse
    val verboseOpt = Opts.flag("verbose", "Toggle verbose in the REPL").orFalse

    (providerOpt,infoOpt,updateOpt,verboseOpt).mapN {
      (provider,info,update,verbose) =>
        var repl = false
        if(update)
          println("Not implemented yet")
          repl = false
        if(info)
          println("Version 1.0-Snapshot, Made by Charlotte Thomas @ISTIC Univ-Rennes1 pour apprendre le Scala, backend actuel STAR-Rennes")
          repl = false
        provider.trim().toLowerCase() match
          case "star" => repl = true
          case "rennes" => repl = true
          case _ => println("Backend currently supported : STAR/Rennes")
        if(repl)
          REPL(getDefaultProvider,verbose).main
    }
  },

  version = "1.0-Snapshot"
)