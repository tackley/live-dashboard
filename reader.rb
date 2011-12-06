#!/usr/bin/env ruby 

require 'rubygems'
require 'zmq'

context = ZMQ::Context.new(1)
sub = context.socket(ZMQ::SUB)
sub.setsockopt(ZMQ::HWM, 10);
sub.setsockopt(ZMQ::SUBSCRIBE, "")
sub.connect("tcp://localhost:5100")

for i in 1..1000 do
  msg = sub.recv(0)
  puts "received: " + msg
end

