copy ..\..\target\multiplayserver.war ROOT.war
java -jar ..\payara-micro-4.1.1.164.jar --deploy ROOT.war --outputUberJar multiplayserver.jar
