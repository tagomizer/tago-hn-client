# Python NLP server

## Installation
### Vertual Environment
cd into /python directory 

pip install virtualenv

python3 -m venv hnnlp

source hnnlp/bin/activate

### Dependencies
pip install spacy

python -m spacy download en_core_web_lg

### Building
pip install .

## Running
uvicorn --port 7777 app:app or ./spacy.sh