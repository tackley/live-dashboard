#!/usr/bin/env ruby 

require 'rubygems'
require 'zmq'

unless ARGV.length == 1
  puts "usage: local_pub.rb file"
  exit
end

file = ARGV[0]
port = 5100

puts "Publishing on port #{port}..."

context = ZMQ::Context.new(1)
publisher = context.socket(ZMQ::PUB)
publisher.setsockopt(ZMQ::HWM, 10);
publisher.bind("tcp://*:#{port}")

puts "Waiting a bit for 0mq to settle down..."
sleep 5


File.new(file, "r").each_line do |l|
  puts "sending: #{l}"
  publisher.send(l)
end

publisher.close

puts "All sent, waiting a bit more for 0mq to finish sending..."
sleep 5
puts "Bye!"

