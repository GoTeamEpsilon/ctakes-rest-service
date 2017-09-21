# cTAKES Concept Mention Parser

This is a child component of https://github.com/GoTeamEpsilon/cTAKES-Intelligent-Chart-Summarization-Solution, the user-friendly, native EMR NLP solution.

## Responsibilites

This container, when given an XMI result from cTAKES, puts together a nice JSON representation of the concept mentions with medical codes and text. When the processing is done, the JSON is sent to a container that communicates back to the EMR.



## Install

_(Note that this will be taken care of in the parent via `docker-compose`)_

```
$ docker build -t ctakes-concept-mention-parser .
$ docker run -d -v ~/cTAKES-Concept-Mention-Parser/data:/usr/src/app/data -t ctakes-concept-mention-parser
```

## Demonstration

![gif](https://github.com/MatthewVita/cTAKES-Concept-Mention-Parser/blob/master/demo.gif?raw=true)

As an example, some fake data (credit: https://www.med.unc.edu/medselect/resources/sample-notes/sample-initial-visit-note-1) has been checked into the samples folder to get and idea of the output quality:

> Mr. Smith is a 56-year-old gentleman formally followed at Carolina Premier who presents to obtain a new primary care physician secondary to insurance changes. He has a past medical history significant for a myocardial infarction in 1994. His cholesterol has been fine. Catheterization showed a possible "kink" in one of his vessels and it was thought that he had a possible "eddy" of current which led to a clot. He has been on Coumadin since then as well as a calcium channel blocker with the thought that there may have been a superimposed spasm. He has had several unremarkable stress tests since then. He works out on a Nordic-Track three times a week without any chest pain or shortness of breath. He has a history of possible peptic ulcer disease in 1981. He was treated with H2 blockers and his symptoms resolved. He has never had any bleeding to his knowledge. He had a hernia repair bilaterally in 1989 and surgery for a right knee cyst in 1999, all of which went well. He also has about a year long history of right buttock pain. This happens only when he is sitting for some time and does not change position. It does not happen when he is walking or exercising. He wonders if it might be pyriformis syndrome. If he changes positions frequently or stretches his legs, this seems to help. He has no acute complaints today and is here to get plugged into the system. He does wonder if there is anything else that can be done about his buttock pain.

`$ vi samples/data.json` to see

## TODOs

- Make the lookup O(1) by indexing on XML ID
- Unit tests

## LICENSE

MIT


# ctakes-rest-service

## Description

_(NOTE: This is alpha software... this project is under active development!)_

The goal of this solution is to provide a JSON-based REST service to process unstructured clinical text through a smart natural language processing system in a fast, accurate, and easy to setup way.

This software provided to the open source healthcare community by:

- Gandhirajan N (Technical Architect)
- Sandeep B G (Solutions Architect)
- Matthew V (Software Engineer)

Thanks to the following people for support and guidance:

- Timothy M, PhD (Scientific Researcher)
- Sean F (Senior Software Developer)

## Setup

Install [Docker](https://www.docker.com/) for your platform (Windows/Linux/Mac)..

```
docker-compose up
```

## Endpoint

HTTP POST `data` to `http://localhost:8080/ctakes-nlp-service/ctakesnlp/analyze`

## Custom Dictionaries

To use a custom dictionary, adjust `customDictionary.xml` to meet your needs and replace (instructions here) file in `Dockerfile` to have the build convert the contents to MySQL tables.

## Remaining Version 1 Work

1. Setup docker-compose with multiple containers
2. Write CtakesHsqlToMysql.java
3. Use our current solutions to finalize CtakesConceptMentionParser.java
4. Make CtakesHsqlToMysql.java parallel
5. Custom dictionary testing
6. Support UMLS credentials
7. Fix unit tests
8. Setup basic auth Spring Security
9. Test with https://github.com/GoTeamEpsilon/cTAKES-Friendly-Web-UI
10. Work with Sean and Tim to do SVN merge
11. Write press release
12. test with ctakes-friendly-web-vierwer
13. cTAKES Friendly Web UI

## License

Apache License, Version 2.0



