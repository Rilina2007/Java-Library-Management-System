#!/bin/bash
# Start the local Spring Boot web interface (needs JDK 17 or newer).
project_dir="$(cd "$(dirname "$0")" && pwd)" || exit 1
exec "$project_dir/web/mvnw" -f "$project_dir/web/pom.xml" \
  "-Dspring-boot.run.workingDirectory=$project_dir" spring-boot:run
