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

On my version on Ubuntu (11.10) I get an error message like:

    Invalid gemspec in [/var/lib/gems/1.8/specifications/foreman-0.25.0.gemspec]: invalid date format in specification: "2011-10-17 00:00:00.000000000Z"

In which case you need to follow the instructions at on [Stack Overflow](http://stackoverflow.com/questions/5771758/invalid-gemspec-because-of-the-date-format-in-specification):

    sudo gem install rubygems-update
    sudo update_rubygems

Note this will effectively uninstall all your gems, so you'll need to repeat the `gem install` command above.

We use [Foreman](https://github.com/ddollar/foreman) to startup all the apps, so install that with:

    sudo gem install foreman

Then startup with:

    foreman start

Now go to http://localhost:8080 and enjoy. (Note I do development on port 8081, so depending on how I checked stuff in
you might need to go to that port instead.)




