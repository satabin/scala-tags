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

case class Tag(name: String, file: String, line: String, tpe: TagType) {
  override def toString = name + "\t" + file + "\t/^" + line + "$/;\"\t" + tpe
}

// the tag type respects the format given at this address http://ctags.sourceforge.net/FORMAT

sealed trait TagType
case object ClassType extends TagType {
  override def toString = "c"
}
case object ObjectType extends TagType {
  override def toString = "o"
}
case object TraitType extends TagType {
  override def toString = "T"
}
case object DefType extends TagType {
  override def toString = "f"
}
case object ValType extends TagType {
  override def toString = "C" // "C" as in "Constant"
}
case object VarType extends TagType {
  override def toString = "v"
}
case object TypeType extends TagType {
  override def toString = "t" // use the typedef from C
}