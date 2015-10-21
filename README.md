# jConstraints-Coral #
This plugin adds support for the meta-heuristic constraint solver [Coral](http://pan.cin.ufpe.br/coral/index.html) in [jConstraints](https://bitbucket.org/psycopaths/jconstraints). Consult the Coral website for more details.

## Building and Installing ##
First, install [jConstraints](https://bitbucket.org/psycopaths/jconstraints) by following the instructions on the project website.

### Prerequisites ###
jConstraints-Coral relies on the coral library in turn relying on a number of libraries. Please use the following installation instructions:

#### CORAL ####
1. Download [CORAL 0.7 latest release](http://pan.cin.ufpe.br/coral/Download.html)
2. Go to the directory where you saved `coral.jar` and run `mvn install:install-file -Dfile=coral.jar -DgroupId=br.ufpe.cin.pan -DartifactId=coral -Dversion=0.7 -Dpackaging=jar`

#### Opt4j ####
1. Download [opt4j 2.2](http://sourceforge.net/projects/opt4j/files/opt4j.jar/)
2. Go to the directory where you saved `opt4j-2.2.jar` and run `mvn install:install-file -Dfile=opt4j-2.2.jar -DgroupId=org.opt4j -DartifactId=opt4j -Dversion=2.2 -Dpackaging=jar`

### Installing ###

* Go to the *jConstraints-coral* folder and run ``` mvn install ```. It should run a lot of test cases - hopefully everything works.

* If the compilation was successful, the jConstraints-coral library can be found in the JAR file target/jConstraints-coral[VERSION].jar

* jConstraints loads extensions automatically from the ~/.jconstraints/extensions folder in a users home directory. Create this directory and copy coral.jar (the jar installed to your Maven repository) and jConstraints-coral-[version].jar into this folder.

### Using Interval Solvers ###
jConstraints-coral supports the interval solvers of Coral (RealPaver and ICOS). The original version of RealPaver 0.4 does not support Mac OS X. To use it, build and install the Mac OS X port, [RealPaver-Mac](https://bitbucket.org/luckow/realpaver-mac).

## Usage and Configuration ##
To use coral, simply put the following in your .jpf file.

```text
symbolic.dp=coral
```

While Coral supports many options, the following are currently allowed in the .jpf configuration file. Note that all are optional. RealPaver is the preferred interval solver.

```text
coral.seed = [:number:]
coral.iterations = [:number:]
coral.solver = AVM | GA_OPT4J | PSO_OPT4J | RANDOM | DE_OPT4J (Default: PSO_OPT4J)
coral.optimize = true | false (Default: true)
coral.interval_solver = ICOS | REALPAVER | NONE (Default: NONE)
coral.interval_solver.path = /path/to/either/realpaver/or/icos
```

### Limitations and Known Issues ###
* FILTERED_PSO and REVERSE_PSO are documented as solvers, but they do not work in coral 0.7
* Logical NOT throws an exception (coral 0.7)
* Coral 0.7 cannot handle constraints with multiple nested casts, e.g., x == (double)((int) 2.0). In the coral 0.7 language: DEQ(DVAR(ID_1), ASDOUBLE(ASINT(DCONS(2.0))))
* bitshift operations (also allowed by the coral 0.8 API) are not supported
* The coral engine in general has many bugs; jconstraints-coral returns DONT_KNOW in these cases
* **Possibly fixed as of coral 0.8** API allows float and long relations (<, <=, ==, >=, >), but coral 0.7 throws exceptions when they are used
* **Possibly fixed as of coral 0.8** Float and long arithmetic are superfluous (API allows constructing them)
* **Possibly fixed as of coral 0.8** For above reasons, float constants are currently converted to doubles in jConstraints-coral