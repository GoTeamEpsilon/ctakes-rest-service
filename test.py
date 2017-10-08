import pytest
import unittest
from parser import *
import sys,os


###
### Unit Tests ###

class TestCodeExtractor(unittest.TestCase):
    # CodeExtractor.__init__ CHECK FOR STATE AT CREATION ACROSS VARIOUS SELF VARIABLES
    def setUp(self):
        sys.path.append(os.path.realpath('..'))
        self.CE = CodeExtractor('samples/data.xml')

    def testCodeExtractorInit(self):
        self.assertEqual(self.CE.CONCEPT_MENTION_TYPE['MEDICATION_MENTION'], 'MedicationMention')

    def testCodeExtractorLog(self):
        value = self.CE.log('Help I am in trouble!')
        print("this is: " + value)
        self.assertTrue(type(value) == str)
        self.assertFalse(len(value) > 10)

    def testWriteDictToJsonPass(self):
        x = 1

    def testWriteDictToJsonFail(self):
        x = 1

# CodeExtractor.convert_xml_to_dict RAISE ERROR
##pass in a sample corrupt file, perhaps in /sample or just make a whole test folder?
# CodeExtractor.convert_xml_to_dict RETURN DICTIONARY
## normal test, does it return 0?

# CodeExtractor.write_dict_to_json INSPECT FINAL_STRUCTURE VARIABLE FOR JSON-NESS
## can you get a JSON verifyer or something?

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
