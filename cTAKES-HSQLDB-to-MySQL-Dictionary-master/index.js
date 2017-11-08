var lineReader = require('line-reader');
var fs = require('fs');
var lazyList = require('lazy');

// Adds time stamps to console.log
require('console-stamp')(console, '[HH:MM:ss.l]');

// Your hsqldb script file
var file = process.argv[2] || 'sno_rx_16ab.script';

// Tracks where the INSERTs start and end because we known they are clumped together
// in the middle of the file.
var range = { start: 0, end: 0 };

// Tracks what tables are in the hsqldb script in case the user needs to add additional
// tables to the schema.sql file. Using an object so that the key will be the table name
// to "cache" the values.
var discoveredTables = {};

// Line number tracker.
var lineIter = 0;

console.log('Processing ' + file);
lineReader.eachLine(file, function(line, isLast) {
  lineIter++;

  // Only focus on INSERT lines... can skip BLOCKS table because it is not
  // clumped with the rest of the INSERTs and is not important.
  if (line.indexOf('INSERT INTO') > -1 && line.split(' ')[2] !== 'BLOCKS') {
    if (range.start === 0) {
      range.start = lineIter;
    } else {
      range.end = lineIter;
    }

    // This stores the table name as a key to cache the value.
    discoveredTables[line.split(' ')[2]] = '';
  }

  if (isLast) {
    console.log('Discovered tables (must have all): ' + Object.keys(discoveredTables).join(','));
    var readStream = fs.createReadStream(file);

    var newFileName = file + '.sql';
    var writeStream = fs.createWriteStream(newFileName, { flags: 'w' });

    // Reading in script file
    console.log('Creating new MySQL file ' + newFileName);
    lazyList(readStream)
      .lines
      .skip(range.start - 1)
      .take((range.end - range.start) + 1)
      // Writing new sql file
      .forEach(function(line) { writeStream.write(line.toString('utf-8') + ';'); });

    console.log('Complete');

    // Signal that we are done. Can exit the outer callback.
    return false;
  }
});
