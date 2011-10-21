#!/bin/bash

ssh devsuprt@guweb51 "nice tail -f /apache2/logs/guardian-access_log" | ./publisher2.rb

