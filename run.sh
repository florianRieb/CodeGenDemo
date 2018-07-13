
java --module-path proxygen/target/classes -m proxygen/com.gen.ProxyGenMain



for D in *; do
    if [ -d "${D}" ]; then
        echo "${D}"   # your processing here
    fi
done