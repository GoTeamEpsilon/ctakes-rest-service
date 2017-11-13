#!/bin/bash

apt-get update -y
apt-get install -y software-properties-common
add-apt-repository "deb http://http.debian.net/debian jessie-backports main"
apt-get update -y
apt-get install -y -t jessie-backports openjdk-8-jre-headless
/usr/sbin/update-java-alternatives -s java-1.8.0-openjdk-amd64
apt-get install -y openjdk-8-jdk openjdk-8-jre
apt-get install -y maven vim subversion

# TODO: Don't point at trunk
svn checkout https://svn.apache.org/repos/asf/ctakes/trunk

cp SpringUpgrade/ctakes-dictionary-lookup-fast/src/main/java/org/apache/ctakes/dictionary/lookup2/ae/JCasTermAnnotator.java trunk/ctakes-dictionary-lookup-fast/src/main/java/org/apache/ctakes/dictionary/lookup2/ae/
cp SpringUpgrade/ctakes-dictionary-lookup-fast/desc/analysis_engine/UmlsLookupAnnotator.xml trunk/ctakes-dictionary-lookup-fast/desc
cp SpringUpgrade/ctakes-dictionary-lookup-fast/desc/analysis_engine/UmlsOverlapLookupAnnotator.xml trunk/ctakes-dictionary-lookup-fast/desc
cp SpringUpgrade/ctakes-assertion/build.xml trunk/ctakes-assertion
cp SpringUpgrade/ctakes-assertion/pom.xml trunk/ctakes-assertion
cp SpringUpgrade/ctakes-type-system/pom.xml trunk/ctakes-type-system
cp SpringUpgrade/ctakes-ytex/src/main/java/org/apache/ctakes/ytex/kernel/evaluator/CorpusKernelEvaluatorImpl.java trunk/ctakes-ytex/src/main/java/org/apache/ctakes/ytex/kernel/evaluator
cp SpringUpgrade/ctakes-ytex/src/main/java/org/apache/ctakes/ytex/kernel/tree/InstanceTreeBuilderImpl.java trunk/ctakes-ytex/src/main/java/org/apache/ctakes/ytex/kernel/tree
cp SpringUpgrade/ctakes-ytex/src/main/java/org/apache/ctakes/ytex/kernel/AbstractBagOfWordsExporter.java trunk/ctakes-ytex/src/main/java/org/apache/ctakes/ytex/kernel/
cp SpringUpgrade/ctakes-ytex/src/main/java/org/apache/ctakes/ytex/kernel/SparseDataExporterImpl.java trunk/ctakes-ytex/src/main/java/org/apache/ctakes/ytex/kernel/
cp SpringUpgrade/ctakes-ytex/pom.xml trunk/ctakes-ytex/
cp SpringUpgrade/ctakes-ytex-uima/src/main/java/org/apache/ctakes/ytex/uima/DBCollectionReader.java trunk/ctakes-ytex-uima/src/main/java/org/apache/ctakes/ytex/uima
cp SpringUpgrade/ctakes-ytex-web/src/main/java/org/apache/ctakes/ytex/web/search/ConceptSearchServiceImpl.java trunk/ctakes-ytex-web/src/main/java/org/apache/ctakes/ytex/web/search
cp SpringUpgrade/ctakes-ytex-web/src/main/java/org/apache/ctakes/ytex/web/search/DocumentSearchServiceImpl.java trunk/ctakes-ytex-web/src/main/java/org/apache/ctakes/ytex/web/search
cp SpringUpgrade/ctakes-ytex-web/src/main/java/org/apache/ctakes/ytex/web/search/DocumentViewBean.java trunk/ctakes-ytex-web/src/main/java/org/apache/ctakes/ytex/web/search
cp SpringUpgrade/ctakes-ytex-web/pom.xml trunk/ctakes-ytex-web/
cp SpringUpgrade/pom.xml trunk/
cd trunk
mvn clean install -Dmaven.test.skip=true
cd ..
tar -xvf trunk/ctakes-distribution/target/apache-ctakes-4.0.1-SNAPSHOT-bin.tar.gz
cp trunk/ctakes-distribution/target/apache-ctakes-4.0.1-SNAPSHOT/lib/* ctakes-rest-service/repo
mvn clean install -Dmaven.test.skip=true
cd ..
echo 'TODO: com.ctakes.nlp.data.service.CtakesHsqlToMysql.java'
cp ../cTAKES/ctakes-rest-service/src/main/resources/org/apache/ctakes/dictionary/lookup/fast/sno_rx_16ab/sno_rx_16ab.script .
cd ..
echo 'TODO: umls credentials'
echo 'TODO: put inside tomcat'
cd /usr/local/tomcat/webapps
wget https://tomcat.apache.org/tomcat-6.0-doc/appdev/sample/sample.war
unzip sample.war
