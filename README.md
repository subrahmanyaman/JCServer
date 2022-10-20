# TestingTools
JCServer is a testing tool, which provides a way to communicate with 
JCardSimulator/JCOPSimulator from android emulator/device.
It basically opens a socket connection on the port(8080)
and listens for the incomming data on this port. This tool uses apduio and JCardsim jars
to validate and transmit the APDUs to the Keymaster Applet. It also uses OpenCard Framework
to test with JCOP simulator.

### Build
Import JCServer server application either in Eclipse or IntelliJ. Add the provided jars inside
lib/ directory and also add precompiled applets .cap and *jar file for jcop and 
jcarsim respectively inside ExtBinaries/ directory

### Program Arguments
Program takes two arguments 
- Simulator type either 'jcop' or 'jcardsim'
- Packages to install either single or multiple 

Example to install multiple packages
<pre>
jcop
keymaster,weaver,fira
</pre>
Example to install single package
<pre>
jcop
keymaster
</pre>
