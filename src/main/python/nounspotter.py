import spacy
import json 

nlp = spacy.load("en_core_web_lg")


#txt = "When George Washington crossed the Delaware River, he had Leukemia"
#txt2 = "Sir Francis Drake was an English explorer and privateer best known for his circumnavigation of the world in a single expedition"
# Call this with a JSON message, e.g.
# {'text': some text}
# returns {'data':an array of results}
def handleGet(json):
  txt = json['text']
  doc = nlp(txt)
  ml = []
  for chunk in doc.noun_chunks:
    ml.append(chunk.text)

  print('BAR', ml)
  return {'data':ml}
