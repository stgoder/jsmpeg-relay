#!/bin/bash
pid=`ps -ef | grep "jsmpeg-relay-1.0-SNAPSHOT.jar" | grep -v grep | awk '{print $2}'`
echo $pid
kill -9 $pid
