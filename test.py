import pytest
import unittest
from parser import *
import os

###
### Unit Tests ###

## CodeExtractor class

# CodeExtractor.__init__ CHECK FOR STATE AT CREATION ACROSS VARIOUS SELF VARIABLES

# CodeExtractor.log INSPECT COMPLETE MESSAGE VARIABLE FOR EXISTENCE

# CodeExtractor.convert_xml_to_dict RAISE ERROR
# CodeExtractor.convert_xml_to_dict RETURN DICTIONARY

# CodeExtractor.write_dict_to_json INSPECT FINAL_STRUCTURE VARIABLE FOR JSON-NESS

# CodeExtractor.extract_all_unmapped_concept_mentions ???
# CodeExtractor.map_unmapped_concept_mentions_by_type ???

###
### Integration Tests ###
###
def testParser():
  # set up paths you need
  currentPath = os.path.abspath(os.curdir)
  currentLocation = currentPath + "/samples/data.xml"
  newLocation = currentPath + "/data/data.xml"
  print(currentLocation)
  print(newLocation)

  # move file and run full program
  w = Watcher()
  w.run()
  os.rename(currentLocation, newLocation)
  ##### # HOW DO I BREAK OUT OF THIS LOOP ^^^^^^
 
  # now check to see if the program converted the file
  finalResult = currentPath + "/data/data.json"
  try:
    assert os.path.isfile(finalResult) == True
    print("Yay runParser() passed")
  except AssertionError:
    print("Sorry assertion error for runParser()")


###
### Run Tests ###
###

testParser()
