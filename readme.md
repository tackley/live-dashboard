Live Dashboard Hack
===================

This is a simple hack that take a feed from one or more apache log files,
and displays the 10 most accessed pages over the last 15 minutes.

To start
--------

This uses [ZeroMQ](http://www.zeromq.org/), which is a native library you need to install.

On Ubuntu do this with:

    sudo apt-get install libzmq0 libzmq-dev

The publisher bit uses ruby, so you'll also need to install the gem:

    sudo gem install zmq

Then start the thingy that tails the log and publishes to ZeroMQ:

    ./start_pub.sh
    ./start_pub2.sh

(The first tails guweb01, the second guweb51.)

Finally, start the webapp with:

   cd dashboard
   ../sbt "container:start" "shell"

Now go to http://localhost:8080 and enjoy.



