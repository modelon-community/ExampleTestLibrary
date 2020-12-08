# ExampleTestLibrary
This repository contains an example library with Modelica model regression tests.  

Some of the tests reference external data that is stored together with the library (in this case Resources/Data).  The Modelica models are structured in a main "MainLibrary" with component and test case models.  A second library named "LocalTests" contains the running simulation models.  These would contain any specific configurations for the running simulations.  The third library named "RegressionTests" contains the regression test models that extend from test cases in "LocalTests" and also extend from the "Regression Test Library".

The LibraryConfig folder contains a default groovy script for execution in Jenkins and a yaml config file for setting up the library test.
