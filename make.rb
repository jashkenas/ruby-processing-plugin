#! /usr/bin/env ruby

todo = []
folder = "RubyProcessingPlugin"
tool_path = "~/Documents/Processing/tools/#{folder}"

todo << "mkdir -p #{folder}/tool"
todo << "mkdir -p #{tool_path}"
todo << 'javac -classpath pde.jar:core.jar -d . src/*.java'
todo << 'cp -r images processing/app/tools'
todo << 'jar -cf ruby-processing-plugin.jar processing'
todo << "mv ruby-processing-plugin.jar #{folder}/tool/ruby-processing-plugin.jar"
todo << "rm -r #{tool_path}"
todo << "cp -r #{folder} #{tool_path}"

exec todo.join(' && ')