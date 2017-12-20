Clone cTAKES and replace files in ctakes-gui and ctakes-distribution with the ones in this repo

cd ctakes-gui
docker-compose up -d
docker ps # note id of the mysql container
docker exec -it <mysql container id> bash
mysql -u root --password=root
CREATE SCHEMA umls;
exit
exit

mvn install

cd ../ctakes-distribution
mvn install

cd target/
unzip apache-ctakes-4.0.1-SNAPSHOT-bin.zip
./apache-ctakes-4.0.1-SNAPSHOT/bin/runDictionaryCreator.sh
