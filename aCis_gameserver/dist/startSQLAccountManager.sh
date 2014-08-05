#!/bin/sh
java -Djava.util.logging.config.file=config/console.cfg -cp ./libs/*:l2jserver.jar:mysql-connector-java-5.1.14-bin.jar net.sf.l2j.accountmanager.SQLAccountManager
