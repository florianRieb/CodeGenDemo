
#java --module-path proxygen/target/classes -m proxygen/com.gen.ProxyGenMain

# if [ -d ] = file is a directory?

for D in generatedFiles/java/*/*; do
    if [ -d "${D}" ]; then
        echo "${D}"   # your processing here


    fi
done