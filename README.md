_(NOTE: This is alpha software... this project is under active development!)_

# cTAKES Rest Service

The goal of this solution is to provide a JSON-based REST service to process unstructured clinical text through a smart natural language processing system in a fast, accurate, and easy to setup way.

_This software provided to the open source healthcare community by:_

- Gandhirajan N (Technical Architect)
- Sandeep B G (Solutions Architect)
- Daniel E (Solutions Engineer)
- Matthew V (Software Engineer)

_Thanks to the following people for support and guidance:_

- Timothy M, PhD (Scientific Researcher)
- Sean F (Senior Software Developer)
- Pei S (Bioinformatics Programmer)

## Setup

### Docker

Install [Docker](https://www.docker.com/) for your platform (Windows/Linux/Mac).

```
docker-compose up
```

### Linux/MacOS

_TODO: details here_

### Windows

_TODO: details here_

## Deployment

You may use docker-compose or do a Maven build to get the `war` file for your custom purposes.

## Endpoint

HTTP POST `data` to `http://localhost:8080/ctakes-nlp-service/ctakesnlp/analyze`

## Custom Dictionaries

To use a custom dictionary, adjust `customDictionary.xml` to meet your needs and replace _(TODO: instructions here)_ file in `Dockerfile` to have the build convert the contents to MySQL tables.

## Project Management

### Remaining Version 1 Work

- [Project Board](https://github.com/GoTeamEpsilon/ctakes-rest-service/projects/1)

### Version 2 Work

- [Project Board](https://github.com/GoTeamEpsilon/ctakes-rest-service/projects/2)

## License

Apache License, Version 2.0
