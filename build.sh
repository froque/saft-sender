#!/usr/bin/env bash
set -euo pipefail

rm -rfv base64
wget --no-verbose --directory-prefix=base64/src/sun/misc/ \
  https://raw.githubusercontent.com/JetBrains/jdk8u_jdk/master/src/share/classes/sun/misc/CharacterEncoder.java \
  https://raw.githubusercontent.com/JetBrains/jdk8u_jdk/master/src/share/classes/sun/misc/BASE64Encoder.java

mkdir -p base64/build
javac --patch-module jdk.unsupported=base64/src/ -d base64/build/ base64/src/sun/misc/*java
jar --create --file base64.jar -C base64/build/ .

mvn -B install:install-file -Dfile=base64.jar \
                         -DgroupId=sun.misc \
                         -DartifactId=base64 \
                         -Dversion=1 \
                         -Dpackaging=jar \
                         -DlocalRepositoryPath=lib


wget --no-verbose https://faturas.portaldasfinancas.gov.pt/factemipf_static/java/FACTEMICLI-2.5.16-9655-cmdClient.jar

mvn -B install:install-file -Dfile=FACTEMICLI-2.5.16-9655-cmdClient.jar \
                         -DgroupId=pt.gov.portaldasfinancas \
                         -DartifactId=FACTEMICLI \
                         -Dversion=2.5.16-9655 \
                         -Dpackaging=jar \
                         -DlocalRepositoryPath=lib

mvn -B clean package

## Copy dependencies
mvn -B dependency:copy-dependencies
cp -v target/saft-sender-1.0-SNAPSHOT.jar target/dependency

## Create Native Java App Installers
jpackage \
  --java-options "--patch-module=jdk.unsupported=\$APPDIR/base64-1.jar" \
  --input ./target/dependency \
  --main-jar saft-sender-1.0-SNAPSHOT.jar \
  --main-class com.premiumminds.saftsender.gui.Launcher \
  --name saft-sender \
  --dest ./target/jpackage/

### Install locally
#sudo dpkg -i ./target/jpackage/linux/saft-sender_1.0-1_amd64.deb
#
### Run locally
#/opt/saft-sender/bin/saft-sender