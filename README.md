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
* ConnectCore 6 SBC v3

License
-------

Copyright (c) 2014-2016, Digi International Inc. <support@digi.com>

Permission to use, copy, modify, and/or distribute this software for any
purpose with or without fee is hereby granted, provided that the above
copyright notice and this permission notice appear in all copies.

THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.