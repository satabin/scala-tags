/* This file is part of scala-tags.
*
* See the NOTICE file distributed with this work for copyright information.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package gnieh.tags

import scala.collection.mutable.ListBuffer

import scala.tools.nsc._
import scala.tools.nsc.plugins.PluginComponent
import scala.tools.nsc.symtab.Flags

import java.io.{ File, FileWriter }

abstract class TagsTraverser extends PluginComponent {

  import global._
  import TagsTraverser._
  import Flags._

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
      if (outputFile == null)
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
        val sorted = tags.sortWith (_.name.toLowerCase < _.name.toLowerCase)
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
      val path = currentUnit.source.path
      t match {
        case ClassDef(mods, name, _, _)
              if mods.hasFlag(TRAIT) && !mods.hasFlag(SYNTHETIC) =>
          tags += Tag(name.decode, path, t.pos.lineContent, TraitType)
          super.traverse(t)
          
        case ClassDef(mods, name, _, _)
              if !mods.hasFlag(SYNTHETIC) =>
          tags += Tag(name.decode, path, t.pos.lineContent, ClassType)
          super.traverse(t)
          
        case ModuleDef(mods, name, _)
              if !mods.hasFlag(SYNTHETIC) =>
          tags += Tag(name.decode, path, t.pos.lineContent, ObjectType)
          super.traverse(t)
          
        case DefDef(_, _, _, _, _, body) if t.symbol.isPrimaryConstructor  =>
          // do not generate tags for primary constructor, simply traverse the body
          traverse(body)
          
        case DefDef(_, _, _, _, _, body) if t.symbol.isGetterOrSetter =>
          // do not generate tags for accessor, simply traverse the body
          traverse(body)

        case DefDef(mods, name, _, _, _, _)
              if !mods.hasFlag(SYNTHETIC) =>
          tags += Tag(name.decode, path, t.pos.lineContent, DefType)
          super.traverse(t)
          
        case ValDef(mods, name, _, _)
              if mods.hasFlag(MUTABLE) && !mods.hasFlag(SYNTHETIC) =>
          tags += Tag(name.decode, path, t.pos.lineContent, VarType)
          super.traverse(t)
          
        case ValDef(mods, name, _, _)
              if !mods.hasFlag(SYNTHETIC) =>
          tags += Tag(name.decode, path, t.pos.lineContent, ValType)
          super.traverse(t)
          
        case TypeDef(mods, name, _, _)
              if !mods.hasFlag(SYNTHETIC) =>
          tags += Tag(name.decode, path, t.pos.lineContent, TypeType)
          super.traverse(t)
          
        case _ =>
          super.traverse(t)
      }
    }
  }

}

object TagsTraverser {
  val headers = List(
    "!_TAG_FILE_FORMAT\t2\t/extended format;/",
    "!_TAG_FILE_SORTED\t1\t/0=unsorted, 1=sorted, 2=foldcase/",
    "!_TAG_PROGRAM_AUTHOR\tLucas Satabin\t/lucassat@n7mm.org/",
    "!_TAG_PROGRAM_NAME\tscala-ctags\t//",
    "!_TAG_PROGRAM_URL\thttps://www.github.com/gnieh/scala-tags\t/official site/",
    "!_TAG_PROGRAM_VERSION\t0.1\t//")
}