# Elevator Control Center

![GitHub Workflow Status](https://img.shields.io/github/workflow/status/fhhagenberg-sqe-esd-ws20/elevator-control-center-team-a/Java%20CI%20with%20Maven)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=fhhagenberg-sqe-esd-ws20_elevator-control-center-team-a&metric=alert_status)](https://sonarcloud.io/dashboard?id=fhhagenberg-sqe-esd-ws20_elevator-control-center-team-a)

## Running the application

Requirement: Java 13 (or higher)

1. Download the `ecc-team-a.jar` binary from the latest [release](https://github.com/fhhagenberg-sqe-esd-ws20/elevator-control-center-team-a/releases).
2. Execute it by either
   - Double clicking the *.jar*-file
   - Launching it on the command line
     
     ```
     java -jar ecc-team-a.jar
     ```
	
## Development

### Prerequisites

- [x] Java 13 SDK (e.g. Oracle or OpenJDK).
- [x] Maven 3. (If you use an IDE like Eclipse or IntelliJ, Maven is **already included** :sunglasses:.)
	- see http://maven.apache.org/install.html
- [x] An IDE or code editor of your choice.

> Confirm your installation with `mvn -v` in a new shell. The result should be similar to:

```
$ mvn -v
Apache Maven 3.6.2 (40f52333136460af0dc0d7232c0dc0bcf0d9e117; 2019-08-27T17:06:16+02:00)
Maven home: C:\Users\winterer\.tools\apache-maven-3.6.2
Java version: 13, vendor: Oracle Corporation, runtime: C:\Program Files\Java\jdk-13
Default locale: en_GB, platform encoding: Cp1252
OS name: "windows 10", version: "10.0", arch: "amd64", family: "windows"
```

### Instructions

This maven project is already set up for JavaFx based GUI applications. It also contains a small example application - `App.java`.

1. Import this git repository into your favourite IDE.

1. Make sure, you can run the sample application without errors.
	- Either run it in your IDE
	- Via command line, run it with `mvn clean javafx:run`.

You can build your project with maven with

```
mvn clean package
```

The resulting `ecc-team-a.jar` file is in the `target` directory.


## Test Concept
For the test concept, an attempt was made to try out the techniques learned in class. Accordingly, various approaches were used to increase the code quality and to test it accordingly. The following approaches were taken into account:

1. Test-separation : The basic principles were used for the test separation. Accordingly, the tests were separated based on their classes and packages.
2. Usage of Test Automation Pattern: Special care was taken to cover the model with unit tests as far as possible. Furthermore, attention was paid to also cover the given interfaces and the connection as well as connection interruptions with appropriate tests. The function of the GUI was also checked by automatic tests. Finally, manual tests were also carried out for special cases in order to achieve an appropriate balance between automation of the test cases and development time.
3. GUI End-to-End tests: Two information channels were examined separately from each other. First, it was checked whether the information comes from the model to the GUI. Accordingly, parameters were changed in the model and finders were used to check whether the display in the GUI changed accordingly. In the second step, the opposite flow of information was examined. Here, selections were made in the GUI and then the model was verified to see whether the selection in the GUI also changed the model accordingly. 
4. Page Object Pattern: In order to increase the readability of the GUI end-to-end tests on the one hand, and on the other hand to ensure appropriate maintainability in the case of GUI changes, the page object pattern was used. The GUI interaction was abstracted into a separate class. The GUI interactions by means of robots and finders were thus encapsulated in individual methods to enable the tests to interact with the GUI in an appropriately abstracted manner. 
5. White Box Techniques: Appropriate tools (Jacococ, SonarCloud) were used to generate coverage data. Based on this data, the current tests were analyzed and new tests were derived to further increase the coverage. 
6. Black Box Techniques: Care was taken to also check for the relevant boundary cases.
