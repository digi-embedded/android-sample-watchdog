Watchdog Sample Application
===========================

This example demonstrates the usage of the Watchdog API. The sample allows
you to interact with the watchdog service by registering the application either 
to the system or the application watchdog services and report application failure
at any time.

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

While it is running, a form is displayed to register the application
either to the system watchdog or to the application watchdog service.
From this form you can also configure the timeout of the watchdog service.

Click **Register** to register the application to the selected watchdog service.

Once the application is registered, it will automatically refresh the watchdog 
periodically to avoid the watchdog timeout to expire.

Click **Report Failure** to stop refreshing the watchdog and let the service 
execute the corresponding actions:

* If the application was registered to the system watchdog service and a failure 
  is reported, the system reboots as soon as the configured timeout elapses.
* If the application was registered to the application watchdog service and a 
  failure is reported, the system stops the application as soon as the configured
  timeout elapses. At this point, if **Restart Application** option was selected,
  the application automatically starts again, if not, you have to restart the 
  application manually.

Click **Unregister** to unregister application from the application watchdog service.
It is not possible to unregister the application from the system watchdog service 
as once it starts, it cannot be stopped.

Finally, click on any question mark near each section to obtain more information
regarding the watchdog service.

Compatible with
---------------

* ConnectCore 6 SBC
* ConnectCore 6 SBC v3
* ConnectCore 8X SBC Pro

License
-------

Copyright (c) 2014-2021, Digi International Inc. <support@digi.com>

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