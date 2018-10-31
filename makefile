# -----------------------------------------------------------------------------
# @Author:    Guillermo Rodriguez
# @Created:   10/02/2018
# @Purpose:   Automate the build file process for a Java application.
# ------------------------------------------------------------------------------

# Java Compiler
JC = javac

# Turn Debugging On
JFLAG = -g

.SUFFIXES: .java .class

.java.class:
	$(JC) $(JFLAG) $*.java

CLASSES = \
	  client.class \
	  server.class

default: classes

classes: $(CLASSES:.java=.class)

clean:
	$(RM) *.class
