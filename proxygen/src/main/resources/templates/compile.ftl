

<#list modules as module>

find ${projDir}/generatedFiles/java/${module}/_client -name "*.java" > client_source.txt
javac -d ${projDir}/generatedFiles/classes/${module}/_client --module-path ${projDir}/mlib/ @client_source.txt
echo 'compiled classes'
cat client_source.txt
jar cf ${projDir}/generatedFiles/mods/client/${module}_client.jar -C ${projDir}/generatedFiles/classes/${module}/_client/ .

find ${projDir}/generatedFiles/java/${module}/_server -name "*.java" > server_source.txt
javac -d ${projDir}/generatedFiles/classes/${module}/_server --module-path ${projDir}/mlib/ @server_source.txt
echo 'compiled classes'
cat server_source.txt
jar cf ${projDir}/generatedFiles/mods/server/${module}_server.jar -C ${projDir}/generatedFiles/classes/${module}/_server/ .
</#list>
rm client_source.txt
rm server_source.txt