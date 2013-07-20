#!/bin/bash

echo -ne "version number ?";
read version;
workspace=/Users/peter/workspace
current="$(PWD)"

if [ ! -d $workspace ]; then
	echo $workspace" not exist";
	exit;
fi

rm -fr pandora$version.zip
rm -fr lib
rm -fr *.jar

cd $workspace/tightvncpanel
mvn clean package install

cd $workspace/peter-swing
mvn clean package install

cd $workspace/c2-pandora-server
mvn clean install

cd $workspace/c2-pandora
mvn clean package

cd $current
cp -fr $workspace/c2-pandora-server/libInUse/hyperic-sigar-1.6.4/sigar-bin/lib .
cp $workspace/c2-pandora-server/target/*.jar .
cp $workspace/c2-pandora/target/*.jar .

#zip -r pandora$version.zip lib *.jar
tar cjvf pandora$version.tar.bz2 lib *.jar

rm -fr lib
rm -fr *.jar

