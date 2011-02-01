scala-tags
==========

Introduction
------------

scala-tags is a small [Scala][1] compiler plugin to generate a [tags][2] file for Scala source files.
This tags file may be then used by editors like [vim][3] or [emacs][4].

Usage
-----

scalac -Xplugin:/path/to/scala-tags.jar -P:scalatags:file=<tag-file> ...

No class files are generated when the plugin is enabled, only the tag file

[1]: http://www.scala-lang.org
[2]: http://ctags.sourceforge.net/
[3]: http://www.vim.org/
[4]: http://www.gnu.org/software/emacs/
