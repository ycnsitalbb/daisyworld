# mcss_assignment2_daisyworld
This program is a java implementation of the daisy world model in NetLogo. Apart from implementing all the functionalities the original one exhibits, our replicated version also adds an extension that introduce a disease mechanism to explore whether the daisy world can remain the same behavior under the pressure of being infected.
## How to run experiments using our model
1. Open *src* folder in the command line.
2. Edit the *config.properties file* to configure the parameters.
3. Type _javac *.java_ to compile all the .java files into .class files.
4. Type *java App* to run the program.
5. The results of experiment will be generated as a *daisyworld.csv* under the root folder.

## How to configure model parameters to reproduce experiments results
1. Open *config.properties file* under *src* folder.
2. To reproduce the experiments with constant solar luminosity, change the value of SOLAR_LUMINOSITY and SURFACE_ALBEDO according to the parameters in our report.
3. To reproduce the experiments in the ramp-up-ramp-down scenario, change the value of MODE to *RAMP_UP_RAMP_DOWN*.
4. To turn on the extension, change the value of EXTENSION to *True*.
