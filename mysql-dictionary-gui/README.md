# MySQL Dictionary GUI

Tweaks to get MySQL supported out of the box for the GUI tool. Ideally this code will be committed into cTAKES proper.

## Setup

1. Clone cTAKES via `svn ls 'https://svn.apache.org/repos/asf/ctakes/branches/ctakes-4.0.0/' | parallel svn export 'https://svn.apache.org/repos/asf/ctakes/branches/ctakes-4.0.0/'{}`
2. Replace files in `ctakes-gui` and `ctakes-distribution` with the ones in this repo
3. Place the `ctakes-gui/docker-compose.yml` somewhere and run `docker-compose up` 
4. `docker ps` and docker the `mysql` `CONTAINER_ID`
5. `docker exec -it <container id> mysql -u root --password=root`
6. `> CREATE SCHEMA umls;`
7. Open new terminal tab
8. cd `ctakes-gui`
9. `mvn install`
10. `cd ../ctakes-distribution`
11. `mvn install`
12. `cd target`
13. `unzip apache-ctakes-4.0.1-SNAPSHOT-bin.zip`
14. `./apache-ctakes-4.0.1-SNAPSHOT/bin/runDictionaryCreator.sh`

## Todo

- Fix error `Can''t call commit when autocommit=true`
- Test cTAKES with MySQL xml configuration
- Setup xml configuration / env for MySQL credentials
- Work with cTAKES core team to see how to deal with MySQL licensing issues 