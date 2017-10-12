import pytest
import unittest
from parser import *
import sys,os


###
### Unit Tests ###

class TestCodeExtractor(unittest.TestCase):

    def setUp(self):
        sys.path.append(os.path.realpath('..'))
        self.CE = CodeExtractor('samples/data')

    def testCodeExtractorInit(self):
        self.assertEqual(self.CE.CONCEPT_MENTION_TYPE['MEDICATION_MENTION'], 'MedicationMention')

    def testCodeExtractorLog(self):
        value = self.CE.log('Help I am in trouble!')
        print("this is: " + value) # debug
        self.assertTrue(type(value) == str)
        self.assertTrue(len(value) > 10)

    def testConvertXMLToDict(self):
        value = self.CE.convert_xml_to_dict()
        if isinstance(self.CE.converted_data, dict):
            print("passes")
            self.assertEqual(value,0)

    ### edit self.file so
      ### convertxmltodict fails...then check for exception?

    def testWriteDictToJsonPass(self):
        # validate JSON with this https://stackoverflow.com/questions/23344948/python-validate-and-format-json-files
        x = 1

    def testWriteDictToJsonFail(self):
        x = 1

    ### TEAR DOWN - delete all of the extra test files after running

    # CodeExtractor.extract_all_unmapped_concept_mentions ???
    # CodeExtractor.map_unmapped_concept_mentions_by_type ???
    # CodeExtractor.process_and_output_all_concept_mentions ???

if __name__ == '__main__':
    unittest.main()
###
### Integration Tests ###
###
# def testParser():
#   # set up paths you need
#   currentPath = os.path.abspath(os.curdir)
#   currentLocation = currentPath + "/samples/data.xml"
#   newLocation = currentPath + "/data/data.xml"
#   print(currentLocation)
#   print(newLocation)

#   # move file and run full program
#   w = Watcher()
#   w.run()
#   os.rename(currentLocation, newLocation)
#   ##### # HOW DO I BREAK OUT OF THIS LOOP ^^^^^^

#   # now check to see if the program converted the file
#   finalResult = currentPath + "/data/data.json"
#   try:
#     assert os.path.isfile(finalResult) == True
#     print("Yay runParser() passed")
#   except AssertionError:
#     print("Sorry assertion error for runParser()")
###
### Run Tests ###
###
#testParser()
