package sbaz.clui
import java.io.{File,FileInputStream}

// Global settings for the command-line UI
class Settings {
  // the name of the directory that is being managed
  var dirname = new File(System.getProperty("scala.home", "."))

  // a ManagedDirectory opened on the same
  var dir:ManagedDirectory = null 

  // whether to actually do the requested work, or to
  // just print out what would be done
  var dryrun = false

  // Whether to print out extra information about what
  // the tool is doing
  var verbose = false

  // The location of the miscellaneous helper files
  // needed by a ManagedDirectory.  Normally these
  // are taken from within the managed directory, but
  // developers of sbaz itself may wish to use different
  // versions.
  var miscdirname: File =
    { val str = System.getProperty("sbaz.miscdirhack")
      if(str == null)
	null
     else
       new File(str)
   }


  // XXX bogusly choose a simple universe to connect to
  def chooseSimple = {
    dir.universe.simpleUniverses.reverse(0)
  }

  // Parse global options from the beginning of a command-line.
  // Returns the portion of the command line that was not
  // consumed.
  def parseOptions(args: List[String]): List[String]  = {
    args match {
      case ("-n" | "--dryrun") :: rest => {
	dryrun = true
	parseOptions(rest)
      }

      case ("-v" | "--verbose") :: rest => {
	verbose = true
	parseOptions(rest)
      }

      case "-d" :: dirname :: rest => {
	this.dirname = new File(dirname)
	parseOptions(rest)
      }

      case "-d" :: Nil => {
	throw new Error("-d requires an argument")
      }

      case _ => args
    }
  }

  // describe the global options
  val fullHelp = (
    "Global options:\n" +
    "\n" +
    "   -d dir      Operate on dir as the local managed directory.\n" +
    "   -n          Do not actually do anything.  Only print out what\n" +
    "               tool would normally do with the following arguments.\n")
}


object Settings {

  // load system properties from scala.home/settings/sbaz.properties,
  // if that file is present.
  def loadSystemProperties:Unit = {
    val home = System.getProperty("scala.home", ".")
    val propFile = new File(new File(new File(home), "config"), "sbaz.properties")

    if(propFile.exists) {
      val reader = new FileInputStream(propFile)
      System.getProperties.load(reader)
      reader.close
    }
  }
}
