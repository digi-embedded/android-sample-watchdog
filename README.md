Watchdog Sample Application
===========================

This example demonstrates the usage of the Watchdog API. This application allows
the configuration of the hardware watchdog timeout and start it. Users can
subscribe the application to the watchdog service choosing between the hardware 
watchdog or the software watchdog. Finally, users can report application
failures to the watchdog service anytime using a button.

Demo requirements
-----------------

To run this example you need:

* One compatible device to host the application.
* A USB connection between the device and the host PC in order to transfer and
  launch the application.
* Establish remote target connection to your Digi hardware before running this
  application.

Demo setup
----------

Make sure the hardware is set up correctly:

1. The device is powered on.
2. The device is connected directly to the PC by the micro USB cable.

Demo run
--------

The example is already configured, so all you need to do is to build and launch 
the project.

Once application starts, configure the hardware watchdog timeout time and then
start it.
  
Below the hardware watchdog section there is a section to subscribe the
application either to the hardware watchdog or to the software watchdog service.
Configure the time interval at which the watchdog service asks the application
about state.

Once the application is subscribed, the watchdog service asks application for
state once every configured interval time.

By default, application always returns a _true_ state. Click **Report Failure**
to return a _false_ state. At this point, the application reports a negative
value the next time the watchdog service asks for status.

Depending on the subscribed watchdog type, the application behaves in the
following way:

* If the application was subscribed to the hardware watchdog and a negative
  state (failure) is reported, the system reboots as soon as the configured
  hardware watchdog timeout elapses.
* If the application was subscribed to the software watchdog and a negative
  state (failure) is reported, the system stops the application. At this point,
  if **Restart Application** option is selected, the application automatically
  starts again, if not, you have to restart the application manually.

Click **Unsubscribe** to unsubscribe application from watchdog service anytime.

Finally, click on any question mark near each section to obtain more information
regarding the watchdog service.

Compatible with
---------------

* ConnectCore 6 SBC
* ConnectCore 6 SBC v2

License
-------

This software is open-source software. Copyright Digi International, 2014-2015.

This Source Code Form is subject to the terms of the Mozilla Public License,
v. 2.0. If a copy of the MPL was not distributed with this file, you can obtain
one at http://mozilla.org/MPL/2.0/.