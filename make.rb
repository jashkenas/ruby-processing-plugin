#! /usr/bin/env ruby

todo = []
folder = "RubyProcessingPlugin"
tool_path = "~/Documents/Processing/tools/#{folder}"

todo << "mkdir -p #{folder}/tool"
todo << "mkdir -p #{tool_path}"
todo << 'javac -classpath pde.jar:core.jar:jruby-complete.jar -d . src/*.java'
todo << 'cp -r images processing/app/tools'
todo << 'jar -cf ruby-processing-plugin.jar processing ruby-processing/lib/ruby-processing ruby-processing/lib/ruby-processing.rb'
todo << "mv ruby-processing-plugin.jar #{folder}/tool/ruby-processing-plugin.jar"
# todo << "cp jruby-complete.jar #{folder}/tool/jruby-complete.jar"
todo << "rm -r #{tool_path}"
todo << "cp -r #{folder} #{tool_path}"
todo << "open /Applications/Processing.app"

exec todo.join(' && ')