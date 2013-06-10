Narayana
========

NOTE: PATCHED EMMA JAR
----------------------
This build uses a patch of the EMMA Jar, the source code for the patch is available from:

	https://svn.jboss.org/repos/labs/labs/jbosstm/workspace/emma

To install the patched Emma jar in your Maven repository, you need to have performed the following command (or run a complete build with the codeCoverage profile):

./build.sh -PcodeCoverage -pl ext


Requirements
------------
To build Narayana you should have installed:
Java 1.6.0 or greater

When building on Mac OS make sure that JAVA_HOME is set.

Building Naryana
----------------
To build Narayana you should call:

	./build.[sh|bat] <maven_goals>

To use this wrapper to build an individual module (say arjuna) you would type:

	./build.[sh|bat] clean install -pl :arjuna

If you are building the "community" profile and are using a different maven installation to the one provided in tools/maven you need to make sure you have the following options:

	-Dorson.jar.location=/full/path/to/checkout/location/ext/orson-0.5.0.jar

Code Coverage Testing
---------------------
  ./build.[sh|bat] -PcodeCoverage (the output is in ${project.build.directory}/coverage.html)

See the notes on installing the patched emma jar above if you have not installed it into your Maven repository yet.

Build QA
--------

	cd qa/
	ant -Ddriver.url=file:///home/hudson/dbdrivers get.drivers dist
	ant -f run-tests.xml ci-tests

Now The Gory Details.
---------------------
Each module contains a set of maven build scripts, which chiefly just inherits and selectively overrides the parent
 pom.xml  Understanding this approach requires some knowledge of maven's inheritance.

Top level maven builds always start from scratch. Individual module builds on the other hand are incremental,
such that you may rebuild a single module by traversing into its directory and running 'mvn', but only if you
have first built any pre-req modules e.g. via a parent build.

In addition to driving the build of individual modules, the build files in the bundles directories (ArjunaCore,
ArjunaJTA, ArjunaJTS) contain steps to assemble the release directory structure, including docs, scripts,
config files and other ancillaries. These call each other in some cases, as JTS is largely a superset of
JTA and JTA in turn a superset of Core.

3rd party dependency management is done via maven. Note that versions of most 3rd party components are resolved via the JBossAS component-matrix
pom.xml, even when building standalone releases. The version of JBossAS to use is determined by the top level pom.xml
You may need to set up maven to use the jboss.org repositories: http://community.jboss.org/wiki/MavenGettingStarted-Users

Maven is provided in the tools/maven section, though later versions of this may work. Download locations are:
http://www.oracle.com/technetwork/java/javase/downloads/index.html
http://maven.apache.org/


A handful of unit tests build and run as part of the normal build. Most test coverage is in the form of integration
tests which reside in the qa/ directory. These are built but not run automatically. See qa/README.txt for usage.


Developing Narayana
-------------------
Please see the following JIRA for details on how to configure your IDE for developing with the Narayana code styles:
	
	https://issues.jboss.org/browse/JBTM-989
