#!/bin/bash

while true; do
    foreman start
    echo "Foreman exited with code $?.  Respawning... (hit ^C again now to stop!)" >&2
    sleep 5
done

