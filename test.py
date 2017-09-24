import pytest
from parser import *
import os

###
### Unit Tests ###

# Test individual parts of parser
# Handler class
# CodeExtractor class


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
