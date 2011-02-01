package gnieh.tags

case class Tag(name: String, file: String, line: String, tpe: TagType) {
  // TODO add tagfield
  override def toString = name + "\t" + file + "\t/^" + line + "$/" //;\"  " + tpe
}

sealed trait TagType
case object ClassType extends TagType {
  override def toString = "c"
}
case object ObjectType extends TagType {
  override def toString = "o"
}
case object TraitType extends TagType {
  override def toString = "t"
}
case object DefType extends TagType {
  override def toString = "m"
}
case object ValType extends TagType {
  override def toString = "C"
}
case object VarType extends TagType {
  override def toString = "v"
}
case object TypeType extends TagType {
  override def toString = "T"
}