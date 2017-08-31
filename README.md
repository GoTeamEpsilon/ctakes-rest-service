# cTAKES Intelligent Chart Summarization Solution

_...This project is under active development..._

## Description and Goals

The goal of this project is to process unstructured clinical text through a smart natural language processing system in a fast, accurate, and easy to setup way. Although the target system is the popular OpenEMR EHR, this solution is generic so that any modern and customizable EMR can leverage this solution.

## Usage

1. Install the latest OpenEMR with the INTELLIGENT_CHART_SUMMARIZATION_FEATURE globals flag set to true
2. Get a UMLS account at https://uts.nlm.nih.gov/ (may take a couple of days)
3. (command to clone relevant repos)
4. (instructions on placing UMLS credentials in the right place)
5. (instructions on placing app secret in the right places)
6. (instructions on placing local host IP in the right place)
7. (instructions on installing Apache UIMA-AS and cTAKES with the proper environment variables)
8. (instructions on enabling the nodeidPipeline)
9. (command to build all images)
10. docker-compose up
11. (screenshots of how the feature in action)

## Systems Overview

1. Doctor logs into https://my-practice.org/openemr
2. A new patient 'John Doe' is created
3. Doctor enters in an (unstructured) encounter form for John Doe's visit
4. Encounter is automatically sent to https://my-practice.org/emr-communications/inbound/raw-clinical-text (which is the [cTAKES EMR Communications Layer](https://github.com/GoTeamEpsilon/cTAKES-EMR-Communications-Layer) container) with the following payload:

```
{
  "pid": 1,
  "eid": 1,
  "body": "<<clinical text>>",
  "app_secret": "<<app secret>>"
}
```
5. Container verifies the request and then writes the data to `PID1_EID1_(TIMESTAMP).txt` and places it in a shared volume for cTAKES processing.
6. [The cTAKES Docker Collection Reader](https://github.com/tmills/ctakes-docker#running-via-collection-reader) is kicked off, which queues up (ActiveMQ) any files in the shared directory for cTAKES processing
7. cTAKES processes the text files and returns which files were processed into respective XML files
8. Relevant text files are deleted
9. Processed XML files are sent to a shared volume for a special parsing step
10. [cTAKES Concept Mention Parser](https://github.com/GoTeamEpsilon/cTAKES-Concept-Mention-Parser) "listens" for new XML files to appear and concurrently processes each into a programmer-friendly JSON format:

```
{
  "MedicationMention": [{
    "begin": 242,
    "code": "84698008",
    "end": 253,
    "text": "Cholesterol",
    "scheme": "SNOMEDCT_US",
    "id": "6249"
  }, {
    ...and so on
  }],  
  "SignSymptomMention": [{
    "begin": 168,
    "code": "417662000",
    "end": 188,
    "text": "PMH - past medical history",
    "scheme": "SNOMEDCT_US",
    "id": "6609"
  }, {
    ...and so on
  }]
}
```

11. JSON is automatically sent to [cTAKES EMR Communications Layer](https://github.com/GoTeamEpsilon/cTAKES-EMR-Communications-Layer) container, whichs sends the data back to the EMR with the following payload:

```
{
  "pid": 1,
  "eid": 1,
  "body": "<<parsed clinical json>>",
  "app_secret": "<<app secret>>"
}
```

12. OpenEMR processes the new information and the doctor can almost instantly (via long polling) view the information in a friendly HTML/JS/CSS web viewer via the [cTAKES Friendly Web UI](https://github.com/GoTeamEpsilon/cTAKES-Friendly-Web-UI)

