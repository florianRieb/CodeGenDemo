
find /home/riebdt/workspace/CodeGenDemo//generatedFiles/java/service.a_client -name "*.java" > client_source.txt
javac -d /home/riebdt/workspace/CodeGenDemo//generatedFiles/classes/service.a_client --module-path /home/riebdt/workspace/CodeGenDemo//mlib/  @client_source.txt
jar cf /home/riebdt/workspace/CodeGenDemo//generatedFiles/mods/client/service.a_client.jar -C /home/riebdt/workspace/CodeGenDemo//generatedFiles/classes/service.a_client/ .

find /home/riebdt/workspace/CodeGenDemo//generatedFiles/java/service.b_client -name "*.java" > client_source.txt
javac -d /home/riebdt/workspace/CodeGenDemo//generatedFiles/classes/service.b_client --module-path /home/riebdt/workspace/CodeGenDemo//mlib/  @client_source.txt
jar cf /home/riebdt/workspace/CodeGenDemo//generatedFiles/mods/client/service.b_client.jar -C /home/riebdt/workspace/CodeGenDemo//generatedFiles/classes/service.b_client/ .

find /home/riebdt/workspace/CodeGenDemo//generatedFiles/java/ENVb -name "*.java" > server_source.txt
javac -d /home/riebdt/workspace/CodeGenDemo//generatedFiles/classes/ENVb --module-path /home/riebdt/workspace/CodeGenDemo//mlib/ @server_source.txt
jar cf /home/riebdt/workspace/CodeGenDemo//generatedFiles/mods/ENVb.jar -C /home/riebdt/workspace/CodeGenDemo//generatedFiles/classes/ENVb/ .

rm client_source.txt
rm server_source.txt