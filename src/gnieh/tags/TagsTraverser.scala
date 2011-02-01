package gnieh.tags

import scala.collection.mutable.ListBuffer

import scala.tools.nsc._
import scala.tools.nsc.plugins.PluginComponent

import java.io.{ File, FileWriter }

abstract class TagsTraverser extends PluginComponent {

  import global._
  import TagsTraverser._

  val phaseName: String = "scalatags"

  val tags = new ListBuffer[Tag]
  var currentUnit: CompilationUnit = null
  var outputFile: String = null

  def newPhase(prev: Phase): Phase = new StdPhase(prev) {
    def apply(unit: CompilationUnit) {
      currentUnit = unit
      tagsCollecter.traverse(unit.body)
    }

    override def run {
      
      // only runs if an output tags file was given
      if(outputFile == null)
        return
      
      // run the phase
      super.run
      // generate the tag file
      val file = new File(outputFile)
      if (file.exists)
        file.delete
      file.createNewFile
      val writer = new FileWriter(file)
      try {
        // first, write the headers
        headers.foreach { line =>
          writer.write(line)
          writer.write("\n")
          writer.flush
        }
        // then all the tags (sorted by name)
        val sorted = tags.sortWith {
          case (Tag(_, name1, _, _), Tag(_, name2, _, _)) => name1 < name2
        }
        sorted.foreach { tag =>
          writer.write(tag.toString)
          writer.write("\n")
          writer.flush
        }
      } finally {
        writer.close
      }
    }
  }

  object tagsCollecter extends Traverser {
    override def traverse(t: Tree) = {
      val sym = t.symbol
      val path = currentUnit.source.path
      t match {
        case ClassDef(_, name, _, _) if sym.isTrait =>
          tags += Tag(name.decode, path, sym.pos.lineContent, TraitType)
        case ClassDef(_, name, _, _) =>
          tags += Tag(name.decode, path, sym.pos.lineContent, ClassType)
        case ModuleDef(_, name, _) =>
          tags += Tag(name.decode, path, sym.pos.lineContent, ObjectType)
        case DefDef(_, name, _, _, _, _) =>
          tags += Tag(name.decode, path, sym.pos.lineContent, DefType)
        case ValDef(_, name, _, _) if sym.isMutable =>
          tags += Tag(name.decode, path, sym.pos.lineContent, VarType)
        case ValDef(_, name, _, _) =>
          tags += Tag(name.decode, path, sym.pos.lineContent, ValType)
        case _ => // do nothing
      }
      super.traverse(t)
    }
  }

}

object TagsTraverser {
  val headers = List(
    "!_TAG_FILE_FORMAT\t2\t/extended format; --format=1 will not append ;\" to lines/",
    "!_TAG_FILE_SORTED\t1\t/0=unsorted, 1=sorted, 2=foldcase/",
    "!_TAG_PROGRAM_AUTHOR\tLucas Satabin\t/lucassat@n7mm.org/",
    "!_TAG_PROGRAM_NAME\tscala-ctags\t//",
    "!_TAG_PROGRAM_URL\thttps://www.github.com/gnieh/scala-tags\t/official site/",
    "!_TAG_PROGRAM_VERSION\t0.1\t//")
}