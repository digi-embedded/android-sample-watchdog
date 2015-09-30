Watchdog Sample Application
===========================

This example demonstrates the usage of the Watchdog API. Application allows to 
configure the hardware watchdog timeout and start it. User is able also to 
subscribe the application to the watchdog service choosing between the hardware 
watchdog or the software watchdog. Finally, user is able to report application 
failure to the watchdog service anytime using a button.

Demo requeriments
-----------------

To run this example you will need:
    - One compatible device to host the application.
    - Network connection between the device and the host PC in order to
      transfer and launch the application.
    - Establish remote target connection to your Digi hardware before running
      this application.

Demo setup
----------

Make sure the hardware is set up correctly:
    - The device is powered on.
    - The device is connected directly to the PC or to the Local
      Area Network (LAN) by the Ethernet cable.

Demo run
--------

The example is already configured, so all you need to do is to build and launch 
the project.

Once application starts, you will be able to configure the hardware watchdog 
timeout time and start it.
  
Below the hardware watchdog section there is a section that allows you to 
subscribe the application either to the hardware watchdog or to the software 
watchdog service. You can configure the time interval at which the watchdog 
service will ask application about state. Once the application is subscribed, 
the watchdog service will ask application for state once every configured 
interval time. By default, application always returns a "true" state. You can 
modify this return value to "false" by clicking on the "Report Failure" button. 
At this point, the application will report a negative value the next time 
watchdog service asks for status. Depending on the subscribed watchdog type, 
the application will behave in the following way:

    - If the application was subscribed to the hardware watchdog and a negative 
	  state (failure) is reported, the system will perform a reboot as soon as 
	  the configured hardware watchdog timeout elapses.
    - If the application was subscribed to the software watchdog and a negative 
	  state (failure) is reported, the system will stop the application. At 
	  this point, if the "Restart Application" button was checked, the 
	  application will automatically be started again, if not, you will need to 
	  restart the application manually.

You can unsubscribe application from watchdog service anytime by clicking on 
the "Unsubscribe" button.

Finally, you can click on the multiple question mark buttons near each section 
to obtain more information regarding the watchdog service.

Tested on
---------

ConnectCore 6 SBC
ConenctCore 6 SBC v2