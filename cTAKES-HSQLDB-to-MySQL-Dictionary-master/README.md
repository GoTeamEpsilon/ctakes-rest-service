# cTAKES HSQLDB to MySQL Dictionary

This is a child component of https://github.com/GoTeamEpsilon/cTAKES-Intelligent-Chart-Summarization-Solution, the user-friendly, native EMR NLP solution.

## Purpose

If you have a non-trivial dictionary, chances are you will exhaust HSQLDB's capabilities. By using this solution, you will have a MySQL schema filled up with what would have been the HSQLDB data.

This solution uses lazy lists and streams to keep memory usage low when the script files are huge.

## Usage

_(This is alpha software)_

```
$ # move your_file.script into this directory
$ vim your_file.script # See if there are other tables outside of CUI_TERMS, TUI, PREFTERM, RXNORM, SNOMEDCT_US... if so, include the new table definitions. BLOCK can be ignored.
$ mysql -u your_user -p < schema.sql
$ npm install
$ node index.js your_file.script
$ mysql -u your_user -p CTAKES_DATA < your_file.script.sql # This will take a long time
$ # edit your dictionary jdbc configs
```

## LICENSE

MIT
