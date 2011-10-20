#!/bin/bash

ssh devsuprt@guweb01 "nice tail -f /apache2/logs/guardian-access_log" | ./publisher.rb

