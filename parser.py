import json
import xmltodict
import sys
#import HTMLParser
from html.parser import HTMLParser
import os
import time
import datetime
from watchdog.observers import Observer
from watchdog.events import FileSystemEventHandler
from threading import Thread

class Watcher:
  DIRECTORY_TO_WATCH = './data'

  def __init__(self):
    self.observer = Observer()

  def run(self):
    event_handler = Handler()
    self.observer.schedule(event_handler, self.DIRECTORY_TO_WATCH, recursive=True)
    self.observer.start()
    try:
      while True:
        time.sleep(5)
    except Exception as err:
      self.observer.stop()
      print(err, 'An error happened')

    self.observer.join()

class Handler(FileSystemEventHandler):
  @staticmethod
  def on_any_event(event):
    if event.is_directory:
      return None
    elif event.event_type == 'created' and 'xml' in event.src_path:
      file_name_without_extension = event.src_path.rsplit('.', 1)[0]
      c = CodeExtractor(file_name_without_extension)
      c.start()


class CodeExtractor(Thread):
  DIRECTORY_TO_WATCH = './samples'

  def __init__(self, file):
    Thread.__init__(self)
    self.file = file
    self.converted_data = {}
    self.concepts = []
    self.mentions = {}
    self.MENTION_TYPE = {
      'MEDICATION_MENTION':       'MedicationMention',
      'DISEASE_DISORDER_MENTION': 'DiseaseDisorderMention',
      'PROCEDURE_MENTION':        'ProcedureMention',
      'SIGN_SYMPTOM_MENTION':     'SignSymptomMention',
      'ANATOMICAL_SITE_MENTION':  'AnatomicalSiteMention'

      # TODO: handle these addition mentions
      # 'MEDICATION_EVENT_MENTION': 'MedicationEventMention',
      # 'LAB_MENTION':              'LabMention'
    }

  # TODO: Bring in actual logger
  def log(self, msg):
    print('[' + self.getName() + '] ' + str(datetime.datetime.utcnow()) + ' ' + msg)


  def run(self):
    self.process_and_output_all_mentions()


  def convert_xml_to_dict(self):
    try:
      file_handler = open(self.file + '.xml', 'r')
      self.converted_data = xmltodict.parse(file_handler.read())
      file_handler.close()
      return 0
    except IOError as ioerr:
      self.log('Error: File ' + self.file + ' does not appear to exist')
      print(ioerr, 'IOError occured')
      return 1


  def write_dict_to_json(self):
    file = open(self.file + '.json', 'w')
    file.write(json.dumps(self.mentions))
    file.close()


  def extract_all_concepts(self):
    raw_concepts = self.converted_data['xmi:XMI']['refsem:UmlsConcept']

    for concept in raw_concepts:
      nullable_code_name = 'unknown'
      if concept.in('@code'):
        nullable_code_name = concept['@code']

      self.concepts.append({
        'scheme': concept['@codingScheme'],
        'id'    : concept['@xmi:id'],
        'text'  : concept['@preferredText'],
        'code'  : nullable_code_name
      })


  def map_specific_concept_mention(self, mention_type):
    if not self.converted_data['xmi:XMI'].in('textsem:' + mention_type):
      self.log('Error: couldn\'t process mention_type ' + mention_type)
      return

    raw_concept_mentions = self.converted_data['xmi:XMI']['textsem:' + mention_type]

    selected_concept_mentions = []
    for concept_mention in raw_concept_mentions:
      for concept_id in concept_mention['@ontologyConceptArr'].split(' '):
        selected_concept_mentions.append({
          'id':    concept_id,
          'begin': int(concept_mention['@begin']),
          'end':   int(concept_mention['@end'])
        })

    self.mentions[mention_type] = []
    for concept in self.concepts:
      for selected_concept_mention in selected_concept_mentions:
        if concept['id'] == selected_concept_mention['id']:
          concept['begin'] = selected_concept_mention['begin']
          concept['end'] = selected_concept_mention['end']
          self.mentions[mention_type].append(concept)


  def process_mention(self, mention_type, should_write):
    self.map_specific_concept_mention(mention_type)

    if should_write:
      self.write_dict_to_json()


  def process_and_output_all_mentions(self):
    ret_code = self.convert_xml_to_dict()
    if ret_code == 0:
      self.log('Started processing')
      self.extract_all_concepts()
      for key, value in self.MENTION_TYPE.iteritems():
        self.process_mention(value, False)

      self.write_dict_to_json()
      self.log('Completed processing')
    else:
      self.log('Error: couldn\'t convert XML to dict')


w = Watcher()
w.run()
