tika
====

Project to interact with tika thorugh a REST Api

Prrequisites: at least Java (OpenJDK7) and PlayFramework (2.2.1 - latest)

Install play framework and start it into the restapp directory with play run

Then try some post requests with curl:

curl -H "Content-Type:text/json" -X POST -d "{\"fileName\":\"test\", \"url\":\"http://www.onlydoo.com/creer-un-site/documents/Powerpoint-Onlydoo-pont.pptx\"}" localhost:9000/parse

curl -H "Content-Type:text/json" -X POST -d "{\"fileName\":\"test\", \"url\":\"file:///home/pcohen/Downloads/check.pp\"}" localhost:9000/parse

With data binary:
curl -i -X POST --data-binary "@Downloads/check.pp"  localhost:9000/parseraw

