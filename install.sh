#!/bin/bash

apt-get update -y
apt-get install -y software-properties-common
add-apt-repository -y "deb http://ppa.launchpad.net/webupd8team/java/ubuntu xenial main"
apt-get update -y
echo oracle-java8-installer shared/accepted-oracle-license-v1-1 select true | \
/usr/bin/debconf-set-selections
echo "oracle-java8-installer shared/accepted-oracle-license-v1-1 select true" | debconf-set-selections
echo "oracle-java8-installer shared/accepted-oracle-license-v1-1 seen true" | debconf-set-selections
apt-get install -y maven vim subversion parallel openjdk-7-jdk oracle-java8-installer
export JAVA_HOME="/usr/lib/jvm/java-8-oracle"
apt-get install -y oracle-java8-set-default
svn ls 'https://svn.apache.org/repos/asf/ctakes/branches/ctakes-4.0.0/' | parallel svn export 'https://svn.apache.org/repos/asf/ctakes/branches/ctakes-4.0.0/'{}

cp SpringUpgrade/ctakes-dictionary-lookup-fast/src/main/java/org/apache/ctakes/dictionary/lookup2/ae/JCasTermAnnotator.java ctakes-4.0.0/ctakes-dictionary-lookup-fast/src/main/java/org/apache/ctakes/dictionary/lookup2/ae/
cp SpringUpgrade/ctakes-dictionary-lookup-fast/desc/analysis_engine/UmlsLookupAnnotator.xml ctakes-4.0.0/ctakes-dictionary-lookup-fast/desc/
cp SpringUpgrade/ctakes-dictionary-lookup-fast/desc/analysis_engine/UmlsOverlapLookupAnnotator.xml ctakes-4.0.0/ctakes-dictionary-lookup-fast/desc/
cp SpringUpgrade/ctakes-assertion/build.xml ctakes-4.0.0/ctakes-assertion/
cp SpringUpgrade/ctakes-assertion/pom.xml ctakes-4.0.0/ctakes-assertion/
cp SpringUpgrade/ctakes-type-system/pom.xml ctakes-4.0.0/ctakes-type-system/
cp SpringUpgrade/ctakes-ytex/src/main/java/org/apache/ctakes/ytex/kernel/evaluator/CorpusKernelEvaluatorImpl.java ctakes-4.0.0/ctakes-ytex/src/main/java/org/apache/ctakes/ytex/kernel/evaluator/
cp SpringUpgrade/ctakes-ytex/src/main/java/org/apache/ctakes/ytex/kernel/tree/InstanceTreeBuilderImpl.java ctakes-4.0.0/ctakes-ytex/src/main/java/org/apache/ctakes/ytex/kernel/tree/
cp SpringUpgrade/ctakes-ytex/src/main/java/org/apache/ctakes/ytex/kernel/AbstractBagOfWordsExporter.java ctakes-4.0.0/ctakes-ytex/src/main/java/org/apache/ctakes/ytex/kernel/
cp SpringUpgrade/ctakes-ytex/src/main/java/org/apache/ctakes/ytex/kernel/SparseDataExporterImpl.java ctakes-4.0.0/ctakes-ytex/src/main/java/org/apache/ctakes/ytex/kernel/
cp SpringUpgrade/ctakes-ytex/pom.xml ctakes-4.0.0/ctakes-ytex/
cp SpringUpgrade/ctakes-ytex-uima/src/main/java/org/apache/ctakes/ytex/uima/DBCollectionReader.java ctakes-4.0.0/ctakes-ytex-uima/src/main/java/org/apache/ctakes/ytex/uima/
cp SpringUpgrade/ctakes-ytex-web/src/main/java/org/apache/ctakes/ytex/web/search/ConceptSearchServiceImpl.java ctakes-4.0.0/ctakes-ytex-web/src/main/java/org/apache/ctakes/ytex/web/search/
cp SpringUpgrade/ctakes-ytex-web/src/main/java/org/apache/ctakes/ytex/web/search/DocumentSearchServiceImpl.java ctakes-4.0.0/ctakes-ytex-web/src/main/java/org/apache/ctakes/ytex/web/search/
cp SpringUpgrade/ctakes-ytex-web/src/main/java/org/apache/ctakes/ytex/web/search/DocumentViewBean.java ctakes-4.0.0/ctakes-ytex-web/src/main/java/org/apache/ctakes/ytex/web/search/
cp SpringUpgrade/ctakes-ytex-web/pom.xml ctakes-4.0.0/ctakes-ytex-web/
cp SpringUpgrade/pom.xml ctakes-4.0.0/
cd ctakes-4.0.0
mvn clean install -Dmaven.test.skip=true
cd ..
tar -xvf ctakes-4.0.0/ctakes-distribution/target/apache-ctakes-4.0.1-SNAPSHOT-bin.tar.gz
cp ctakes-4.0.0/ctakes-distribution/target/apache-ctakes-4.0.1-SNAPSHOT/lib/* ctakes-rest-service/repo
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
