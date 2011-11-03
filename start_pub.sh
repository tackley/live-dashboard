#!/bin/bash

SERVER=$1
PORT=${PORT:-5100}

echo "Connecting to server $SERVER, republishing on port $PORT"

ssh devsuprt@$SERVER "nice tail -f /apache2/logs/guardian-access_log" | ./publisher.rb $PORT

