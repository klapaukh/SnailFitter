# SnailFitter

This project contains two curve fitting AIs which attempt to match coiling as 
described in David Raup's 1966 paper. As the AI search progresses the current
solution is displayed. It has the following github projects as dependencies:

   * klapaukh/fgpj
   * yannrichet/jmathio
   * yannrichet/jmathplot
   
It has been developed under Java 8 with JUnit 4.

If you use this project in your work please cite it. 

## Running the project

There are two main entry points - one for each of the AIs. SnailMain is the main
entry point of the genetic programming AI. SimulatedAnnealing is the entry point
for the SimulatedAnnealing search. 

At the moment there are no command line arguments and all settings must be set
from inside the program. The expected input format is a space separated table
(tsv) file (with no column headings or row numbers). The columns are cylindrical
coordinates, one per row. The order of the columns is theta, r, y. 
