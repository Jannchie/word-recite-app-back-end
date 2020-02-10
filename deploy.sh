#!/usr/bin/env bash
pkill -9 java
source /etc/profile
nohup java -jar ./word-backend/word*.jar 1>log.out 2>&1 &
