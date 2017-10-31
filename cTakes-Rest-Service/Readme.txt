Steps to be followed before running this web app:

1) Make the code changes available under https://github.com/gandhirajan/cTAKES/tree/master/SpringUpgrade/ctakes-SVN-src to upgrades
   cTAKES to Spring 4x as it currently uses Spring 3x
2) Build all cTAKES modules and copy all ctakes module JARS to the repo folder in the webapp
3) Change your respective DB settings in the following file(customDictionary.xml) available in   https://github.com/gandhirajan/cTAKES/blob/master/cTakes-Rest-Service/src/main/resources/org/apache/ctakes/dictionary/lookup/fast/customDictionary.xml
3) Build the webapp project that should give you a WAR file that can be deployed.
4) Once the webapp is deployed, you can test the rest service using any REST client or using index.html where you can fire sample rest call
5) REST URL to test - "http://<server_ip>:<port>/ctakes-nlp-service/ctakesnlp/analyze". It takes clinical text as input in request body 
   and produces JSON ouput with identified mentions and values
   
   Big thanks to Pei for setting up the basic foundation of cTAKES as webapp under,
   https://github.com/healthnlp/examples/tree/master/ctakes-temporal-demo
   
   
   Please leave your suggestions for further improvements.
