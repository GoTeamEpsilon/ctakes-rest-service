#!/bin/bash
apt-get update -y
apt-get install -y default-jre wget curl build-essential maven vim openjdk-8-jdk openjdk-8-jre git-core subversion
debconf-set-selections <<< 'mysql-server mysql-server/root_password password root'
debconf-set-selections <<< 'mysql-server mysql-server/root_password_again password root'
apt-get install -y mysql-server mysql-client
service mysql start
curl -sL https://deb.nodesource.com/setup_6.x -o nodesource_setup.sh
bash nodesource_setup.sh
apt-get install -y nodejs
git clone https://github.com/MatthewVita/cTAKES.git
cd cTAKES
svn checkout https://svn.apache.org/repos/asf/ctakes/trunk
cp SpringUpgrade/ctakes-SVN-src/ctakes-dictionary-lookup-fast/src/main/java/org/apache/ctakes/dictionary/lookup2/ae/JCasTermAnnotator.java trunk/ctakes-dictionary-lookup-fast/src/main/java/org/apache/ctakes/dictionary/lookup2/ae/ # cp SpringUpgrade/ctakes-SVN-src/ctakes-dictionary-lookup-fast/desc/analysis_engine/UmlsLookupAnnotator.xml trunk/ctakes-dictionary-lookup-fast/desc
cp SpringUpgrade/ctakes-SVN-src/ctakes-dictionary-lookup-fast/desc/analysis_engine/UmlsOverlapLookupAnnotator.xml trunk/ctakes-dictionary-lookup-fast/desc
cp SpringUpgrade/ctakes-SVN-src/ctakes-assertion/build.xml trunk/ctakes-assertion
cp SpringUpgrade/ctakes-SVN-src/ctakes-assertion/pom.xml trunk/ctakes-assertion
cp SpringUpgrade/ctakes-SVN-src/ctakes-type-system/pom.xml trunk/ctakes-type-system
cp SpringUpgrade/ctakes-SVN-src/ctakes-ytex/src/main/java/org/apache/ctakes/ytex/kernel/evaluator/CorpusKernelEvaluatorImpl.java trunk/ctakes-ytex/src/main/java/org/apache/ctakes/ytex/kernel/evaluator
cp SpringUpgrade/ctakes-SVN-src/ctakes-ytex/src/main/java/org/apache/ctakes/ytex/kernel/tree/InstanceTreeBuilderImpl.java trunk/ctakes-ytex/src/main/java/org/apache/ctakes/ytex/kernel/tree
cp SpringUpgrade/ctakes-SVN-src/ctakes-ytex/src/main/java/org/apache/ctakes/ytex/kernel/AbstractBagOfWordsExporter.java trunk/ctakes-ytex/src/main/java/org/apache/ctakes/ytex/kernel/
cp SpringUpgrade/ctakes-SVN-src/ctakes-ytex/src/main/java/org/apache/ctakes/ytex/kernel/SparseDataExporterImpl.java trunk/ctakes-ytex/src/main/java/org/apache/ctakes/ytex/kernel/
cp SpringUpgrade/ctakes-SVN-src/ctakes-ytex/pom.xml trunk/ctakes-ytex/
cp SpringUpgrade/ctakes-SVN-src/ctakes-ytex-uima/src/main/java/org/apache/ctakes/ytex/uima/DBCollectionReader.java trunk/ctakes-ytex-uima/src/main/java/org/apache/ctakes/ytex/uima
cp SpringUpgrade/ctakes-SVN-src/ctakes-ytex-web/src/main/java/org/apache/ctakes/ytex/web/search/ConceptSearchServiceImpl.java trunk/ctakes-ytex-web/src/main/java/org/apache/ctakes/ytex/web/search
cp SpringUpgrade/ctakes-SVN-src/ctakes-ytex-web/src/main/java/org/apache/ctakes/ytex/web/search/DocumentSearchServiceImpl.java trunk/ctakes-ytex-web/src/main/java/org/apache/ctakes/ytex/web/search
cp SpringUpgrade/ctakes-SVN-src/ctakes-ytex-web/src/main/java/org/apache/ctakes/ytex/web/search/DocumentViewBean.java trunk/ctakes-ytex-web/src/main/java/org/apache/ctakes/ytex/web/search
cp SpringUpgrade/ctakes-SVN-src/ctakes-ytex-web/pom.xml trunk/ctakes-ytex-web/
cp SpringUpgrade/ctakes-SVN-src/pom.xml trunk/
cd trunk
mvn clean install -Dmaven.test.skip=true
cd ..
tar -xvf trunk/ctakes-distribution/target/apache-ctakes-4.0.1-SNAPSHOT-bin.tar.gz
cp trunk/ctakes-distribution/target/apache-ctakes-4.0.1-SNAPSHOT/lib/* cTakes-Rest-Service/repo
mvn clean install -Dmaven.test.skip=true
cd ..
git clone https://github.com/GoTeamEpsilon/cTAKES-HSQLDB-to-MySQL-Dictionary.git
cd cTAKES-HSQLDB-to-MySQL-Dictionary
npm install
cp ../cTAKES/cTakes-Rest-Service/src/main/resources/org/apache/ctakes/dictionary/lookup/fast/sno_rx_16ab/sno_rx_16ab.script .
mysql -u root --password=root < schema.sql
node index.js sno_rx_16ab.script
echo 'TODO: change table case'
mysql -u root --password=root umls < sno_rx_16ab.script.sql
cd ..
echo 'TODO: umls credentials'
echo 'TODO: tomcat'
