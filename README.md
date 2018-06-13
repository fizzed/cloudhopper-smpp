[Try Restcomm Cloud NOW for FREE!](https://www.restcomm.com/sign-up/) Zero download and install required.
=======
Cloudhopper SMPP by Fizzed (forked from Twitter) 
================================================

[![Build Status](https://secure.travis-ci.org/fizzed/cloudhopper-smpp.png?branch=master)](http://travis-ci.org/fizzed/cloudhopper-smpp)

Sponsored by
------------

Cloudhopper-SMPP is proudly sponsored by <a href="https://www.greenback.com">Greenback</a>.  We love the service and think you would too.

<a href="https://www.greenback.com?utm_source=github.com&utm_medium=sponsorship&utm_campaign=fizzed-cloudhopper-smpp" title="Greenback - Expenses made simple"><img src="https://www.greenback.com/assets/images/logo-greenback.png" height="48" width="166" alt="Greenback"></a>

<a href="https://www.greenback.com?utm_source=github.com&utm_medium=sponsorship&utm_campaign=fizzed-cloudhopper-smpp" title="Greenback - Expenses made simple">More engineering. Less paperwork. Expenses made simple.</a>

Overview
------------------------

Efficient, scalable, rock-solid, and flexible Java implementation of the Short
Messaging Peer to Peer Protocol (SMPP).

Twitter exclusively relies on this library for its global SMS infrastructure.

 * Real-world used library by Twitter across nearly every SMSC vendor and
   mobile operator around the world.  We've seen almost every variance in the
   SMPP protocol and this library handles it flawlessly.
 * Rigorous unit testing
 * Support for SMPP protocol:
    * Version 3.3
    * Version 3.4
    * Most of version 5.0
 * Uses non-blocking (NIO) sockets (via underlying Netty dependency, one thread
   can support 1 or more SMPP sessions)
 * Can support thousands of binds/connections using minimal resources and threads
 * Supports both client and server modes of the SMPP protocol (yes you can
   write your own SMPP server using this library as well as be a client to one)
 * Supports synchronous request mode (send request and block until response
   received)
 * Supports asynchronous request mode (send request, get a future response,
   and then decide when you'd like to wait/get a response)
 * Advanced support for SMPP "windowing":
    * Configurable window size per session
    * Waiting for a window slot to open up
    * Get a list of unacknowledged/in-flight PDUs if session disconnects
 * SSL/TLS support for clients and servers
 * Configurable support for expiry of unacknowledged PDUs
 * Configurable counter metrics per client-session, server-session, or server.
 * Support for sniffing/logging/discarding of PDUs before normal processing

The library has been tested and certified with hundreds of mobile operators
and suppliers around the world.  It's effective at being flexible with SMPP
specifications that aren't truly compliant.

Background and Contributors
---------------------------

This library was originally developed by Cloudhopper, Inc. in 2008. Cloudhopper
was acquired by Twitter in April 2010. The main author of this library,
Joe Lauer, left Twitter in April 2013 to found Fizzed, Inc.  As of Nov 2015,
[Fizzed, Inc](http://fizzed.com) is the official maintainer of the library.
If you're looking for commercial support, please contact [Fizzed](http://fizzed.com).

- Joe Lauer (Twitter: [@jjlauer](http://twitter.com/jjlauer))

Installation
------------

Library is available via maven central.  For netty-3 and a stable version:


[![Build Status](https://travis-ci.org/RestComm/cloudhopper-smpp.svg?branch=master)](https://travis-ci.org/RestComm/cloudhopper-smpp)

News
------------------------

**Nov 2016**

While Fizzed, Inc. took over from Twitter for a while, there is now an apparent lack of commits on the original repository for almost a year and PR pending reviews for more than 6 months.
The project was forked here and will be updated regularly as it is being used in Restcomm SMSC

