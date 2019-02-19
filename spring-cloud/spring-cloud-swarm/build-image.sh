      #!/bin/bash


      # if you have engine project . plz add
      #cd ${your engine path}
      #mvn clean install
      cd eureka-server

      mvn clean install -Dmaven.test.skip=true docker:build

      cd ..

      cd zuul-gateway

      mvn clean package -DskipTests=true docker:build

      cd ..

      cd config-server

      mvn clean package -DskipTests=true docker:build

      cd ..

      cd client-server1

      mvn clean package -DskipTests=true docker:build

      cd ..

      cd client-server2

      mvn clean package -DskipTests=true docker:build

      cd ..

      cd ..





