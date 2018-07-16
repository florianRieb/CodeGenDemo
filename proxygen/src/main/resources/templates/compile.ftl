

<#list modules as module>

find java/${module}/_client -name "*.java" > client_source.txt
javac -d classes/${module}/_client --module-path ~/workspace/CodeGenDemo/mlib/ @client_source.txt
echo 'compiled classes'
cat client_source.txt
jar cf mods/${module}client.jar -C classes/${module}/_client/ .
find java/${module}/_server -name "*.java" > server_source.txt
javac -d classes/${module}/_server --module-path ~/workspace/CodeGenDemo/mlib/ @server_source.txt
echo 'compiled classes'
cat server_source.txt
jar cf mods/${module}server.jar -C classes/${module}/_server/ .

</#list>