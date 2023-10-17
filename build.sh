#!/usr/bin/env bash
set -euo pipefail

rm -rfv base64

curl --silent --create-dirs -O --output-dir base64/src/sun/misc/ \
  https://raw.githubusercontent.com/JetBrains/jdk8u_jdk/94318f9185757cc33d2b8d527d36be26ac6b7582/src/share/classes/sun/misc/CharacterEncoder.java
curl --silent --create-dirs -O --output-dir base64/src/sun/misc/ \
  https://raw.githubusercontent.com/JetBrains/jdk8u_jdk/94318f9185757cc33d2b8d527d36be26ac6b7582/src/share/classes/sun/misc/BASE64Encoder.java

mkdir -p base64/build
javac --patch-module jdk.unsupported=base64/src/ -d base64/build/ base64/src/sun/misc/*java
jar --create --file base64.jar -C base64/build/ .

mvn -B org.apache.maven.plugins:maven-install-plugin:2.4:install-file -Dfile=base64.jar \
                         -DgroupId=sun.misc \
                         -DartifactId=base64 \
                         -Dversion=1 \
                         -Dpackaging=jar \
                         -DcreateChecksum=true \
                         -DlocalRepositoryPath=lib

curl --silent -O https://faturas.portaldasfinancas.gov.pt/factemipf_static/java/FACTEMICLI-2.8.0-50828-cmdClient.jar

mvn -B org.apache.maven.plugins:maven-install-plugin:2.4:install-file -Dfile=FACTEMICLI-2.8.0-50828-cmdClient.jar \
                         -DgroupId=pt.gov.portaldasfinancas \
                         -DartifactId=FACTEMICLI \
                         -Dversion=2.6.2-46755 \
                         -Dpackaging=jar \
                         -DcreateChecksum=true \
                         -DlocalRepositoryPath=lib

mvn -B clean package

## Copy dependencies
mvn -B dependency:copy-dependencies
cp -v target/saft-sender-*.jar target/dependency/saft-sender.jar

## Create Native Java App Installers
jpackage \
  --java-options "--patch-module=jdk.unsupported=\$APPDIR/base64-1.jar" \
  --input ./target/dependency \
  --main-jar saft-sender.jar \
  --main-class com.premiumminds.saftsender.gui.Launcher \
  --name saft-sender \
  --dest ./target/jpackage/

### Install locally
#sudo dpkg -i ./target/jpackage/linux/saft-sender_1.0-1_amd64.deb
#
### Run locally
#/opt/saft-sender/bin/saft-sender
