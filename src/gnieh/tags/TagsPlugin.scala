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

import scala.tools.nsc
import nsc.Global
import nsc.Phase
import nsc.plugins.Plugin
import nsc.plugins.PluginComponent

class TagsPlugin(val global: Global) extends Plugin {

  val name = "scalatags"
  val description = "generates a tags file"

  val phase = new TagsTraverser() {
    val global = TagsPlugin.this.global
    val runsAfter = List("typer")
    override val runsRightAfter = Some("typer")
  }

  val components = List[PluginComponent](phase)
  
  override def processOptions(options: List[String], error: String => Unit) {
    for (option <- options) {
      option match {
        case tagFile(file) =>
          global.settings.stop.value = List("superaccessors")
          phase.outputFile = file
        case _ => error("Option not understood: " + option)
      }
    }
  }
  
  override val optionsHelp: Option[String] = 
    Some("  -P:scalatags:file=<path>        generate the tag file only (no class file generated)")
  
  object tagFile {
    def unapply(option: String): Option[String] = {
      if(option.startsWith("file="))
        Some(option.substring(5))
      else
        None
    }
  }

}