# MySQL Dictionary GUI

Tweaks to get MySQL supported out of the box for the GUI tool. Ideally this code will be committed into cTAKES proper.

## Setup

1. Run `CREATE SCHEMA umls;` on your MySQL database
2. `cd` to the root directory of this project run `./install-ctakes-gui.sh`
3. `cd ctakes-codebase-area/ctakes-distribution/target/`
4. `unzip apache-ctakes-4.0.1-SNAPSHOT-bin.zip`
5. `./apache-ctakes-4.0.1-SNAPSHOT/bin/runDictionaryCreator.sh`

## Todo

- Fix error `Can''t call commit when autocommit=true`
- Test cTAKES with MySQL xml configuration
- Setup xml configuration / env for MySQL credentials
- Work with cTAKES core team to see how to deal with MySQL licensing issues
