# Voting service

Design and implement a JSON API using Hibernate/Spring/SpringMVC without frontend.

The task is:

Build a voting system for deciding where to have lunch.

2 types of users: admin and regular users
Admin can input a restaurant and it's lunch menu of the day (2-5 items usually, just a dish name and price)
Users can vote on which restaurantUri they want to have lunch at
Only one vote counted per user
If user votes again the same day:
If it is before 11:00 we assume that he changed his mind.
If it is after 11:00 then it is too late, vote can't be changed
Each restaurant provides new menu each day.

As a result, provide a link to github repository. It should contain the code, README.md with API documentation and couple curl commands to test it.

P.S.: you can use a project seed you find where all technologies are already pre-configured.

# Design considerations
Current solution is prototype therefore speed of development is of high priority.
Designed rest service is backed by a spring-boot stack.
Basic security is provided by code configuration.
In-memory h2 database is chosen as data storage.
Mockito/TestRestTemplate for testing.
Maven as a build tool.

# Build service
Execute in shell: mvn clean package

# Run service
Java 8 is required.
Execute in shell: java -jar target/voting-service-1.0-SNAPSHOT.jar

# Test service
Follow the instruction located in static/doc/getting-started-guide.html

## Verify db state
Open in browser: http://localhost:8080/console/
Set connection to: jdbc:h2:mem:testdb and connect.
Investigate state of tables.