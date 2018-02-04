cd ctakes-codebase-area
svn ls 'https://svn.apache.org/repos/asf/ctakes/branches/ctakes-4.0.0/' | parallel svn export 'https://svn.apache.org/repos/asf/ctakes/branches/ctakes-4.0.0/'{}

cp ../mysql-dictionary-gui/ctakes-distribution/src/main/assembly/bin.xml ctakes-distribution/src/main/assembly/bin.xml
cp ../mysql-dictionary-gui/ctakes-gui/src/main/java/org/apache/ctakes/gui/dictionary/util/*.java ctakes-gui/src/main/java/org/apache/ctakes/gui/dictionary/util/
cp ../mysql-dictionary-gui/ctakes-gui/src/main/java/org/apache/ctakes/gui/dictionary/*.java ctakes-gui/src/main/java/org/apache/ctakes/gui/dictionary/
cp ../mysql-dictionary-gui/ctakes-gui/pom.xml ctakes-gui/
mvn install -DskipTests
