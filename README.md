# coalescent.dk

This contains everything needed to build the docker image that coalescent.dk (or birc-services.au.dk/coalescentdk) is running.

`simulate` is a bash script that works as a CGI script.
It calls one of the simulation programs in the `server-side-` folders based on parameters passed from the java program defined in `hudson_gui`.

`dumb-init.c` and `VERSION.h` are taken from the [dumb-init](https://github.com/Yelp/dumb-init/tree/4d3debba4d355f8ae93010f558f4cd3fdf00b2b9) project and is covered by the MIT license.
