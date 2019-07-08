# wdogs
Following environment parameters expected:
- DOGS_DATABASE_HOST - database host
- DOGS_DATABASE_PORT - database port
- DOGS_DATABASE_USER - database user
- DOGS_DATABASE_PWD  - database password

Building project: <code>mvn clean install</code>. This will create docker image as a part of the build.

Using maven in dogs module directory:<br/>
<code>mvn -DDOGS_DATABASE_HOST=<host> -DDOGS_DATABASE_PORT=<port> -DDOGS_DATABASE_USER=<user> -DDOGS_DATABASE_PWD=<pwd> -DDOGS_DATABASE_NAME=<database_name> spring-boot:run</code>

Using docker (after image created):<br/>
<code>docker run --expose=8080 --env DOGS_DATABASE_HOST=<host> --env DOGS_DATABASE_PORT=<port> --env DOGS_DATABASE_USER=<user> --env DOGS_DATABASE_PWD=<pwd> --env DOGS_DATABASE_NAME=<database_name> dzcs/dogs
</code>

Dummy Installation with embedded H2 db:<br/>
<code>docker run --expose=8080 --env spring.profiles.active=dummy dzcs/dogs</code>
