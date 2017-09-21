import json
import xmltodict
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
    self.unmapped_concept_mentions = []
    self.mapped_concept_mentions = {}
    self.flattened_mapped_concept_mentions = []
    self.CONCEPT_MENTION_TYPE = {
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
    self.process_and_output_all_concept_mentions()


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

    final_structure = {
      'partitioned': self.mapped_concept_mentions,
      'flattened': self.flattened_mapped_concept_mentions
    }
    file.write(json.dumps(final_structure))
    file.close()


  def extract_all_unmapped_concept_mentions(self):
    unmapped_concept_mentions = self.converted_data['xmi:XMI']['refsem:UmlsConcept']

    for concept_mention in unmapped_concept_mentions:
      nullable_code_name = 'unknown'
      if '@code' in concept_mention:
        nullable_code_name = concept_mention['@code']

      self.unmapped_concept_mentions.append({
        'scheme': concept_mention['@codingScheme'],
        'id'    : concept_mention['@xmi:id'],
        'text'  : concept_mention['@preferredText'],
        'code'  : nullable_code_name
      })


  def map_unmapped_concept_mentions_by_type(self, concept_mention_type):
    if ('textsem:' + concept_mention_type) not in self.converted_data['xmi:XMI']:
      self.log('Error: couldn\'t process concept mention type ' + concept_mention_type)
      return

    unmapped_concept_mentions_by_type = self.converted_data['xmi:XMI']['textsem:' + concept_mention_type]

    concept_mentions_for_mapping = []
    for unmapped_concept_mention_by_type in unmapped_concept_mentions_by_type:
      for concept_id in unmapped_concept_mention_by_type['@ontologyConceptArr'].split(' '):
        concept_mentions_for_mapping.append({
          'id':    concept_id,
          'begin': int(unmapped_concept_mention_by_type['@begin']),
          'end':   int(unmapped_concept_mention_by_type['@end'])
        })

    self.mapped_concept_mentions[concept_mention_type] = []
    for unmapped_concept_mention in self.unmapped_concept_mentions:
      for concept_mention_for_mapping in concept_mentions_for_mapping:
        if concept_mention_for_mapping['id'] == unmapped_concept_mention['id']:
          mapped_concept_mention = unmapped_concept_mention # start with known data
          mapped_concept_mention['begin'] = concept_mention_for_mapping['begin']
          mapped_concept_mention['end'] = concept_mention_for_mapping['end']

          # Build up a structure that is partitioned by concept type
          self.mapped_concept_mentions[concept_mention_type].append(mapped_concept_mention)

          # Also include the item in a flattened structure
          mapped_concept_mention['type'] = concept_mention_type
          self.flattened_mapped_concept_mentions.append(mapped_concept_mention)


  def process_and_output_all_concept_mentions(self):
    ret_code = self.convert_xml_to_dict()
    if ret_code == 0:
      self.log('Started processing')
      self.extract_all_unmapped_concept_mentions()
      for key, value in self.CONCEPT_MENTION_TYPE.items():
        self.map_unmapped_concept_mentions_by_type(value)

      self.write_dict_to_json()
      self.log('Completed processing')
    else:
      self.log('Error: couldn\'t convert XML to dict')
