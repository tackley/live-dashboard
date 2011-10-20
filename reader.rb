#!/usr/bin/env ruby 

require 'rubygems'
require 'zmq'

context = ZMQ::Context.new(1)
sub = context.socket(ZMQ::SUB)
sub.setsockopt(ZMQ::HWM, 10);
sub.setsockopt(ZMQ::SUBSCRIBE, "")
sub.connect("tcp://gnmfasteragain.int.gnl:5536")

for i in 1..10 do
  msg = sub.recv(0)
  puts "received: " + msg
end

