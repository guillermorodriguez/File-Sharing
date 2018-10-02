# --------------------------------------------------------------------------------
# @Author:    Guillermo Rodriguez
# @Created:   10/02/2018
# @Purpose:   Automate the build file process for a Java application.
# --------------------------------------------------------------------------------

# Java Compiler
JC = javac

# Turn Debugging On
JFLAG = -g

default: server.class client.class

server.class: server.java
  $(JC) $(JFLAG) server.java

client.class: client.java
  $(JC) $(JFLAG) client.java
  
clean:
  $(RM) *.class