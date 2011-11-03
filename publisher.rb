#!/usr/bin/env ruby 

require 'rubygems'
require 'zmq'

unless ARGV.length == 1
  puts "usage: publisher.rb port"
  exit
end

port = ARGV[0]

puts "Publishing on port #{port}..."

context = ZMQ::Context.new(1)
publisher = context.socket(ZMQ::PUB)
publisher.setsockopt(ZMQ::HWM, 10);
publisher.bind("tcp://*:#{port}")

STDIN.each_line do |l|
  publisher.send(l)
end

