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

