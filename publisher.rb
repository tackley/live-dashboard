#!/usr/bin/env ruby 

require 'rubygems'
require 'zmq'

context = ZMQ::Context.new(1)
publisher = context.socket(ZMQ::PUB)
publisher.setsockopt(ZMQ::HWM, 10);
publisher.bind("tcp://*:5536")

ARGF.each_line do |l|
  publisher.send(l)
end

