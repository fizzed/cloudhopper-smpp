Sserializable Cloudhopper SMPP (forked from Fizzed)
 ================================================

[![Build Status](https://api.travis-ci.org/aasaru/serializable-cloudhopper-smpp.png?branch=master)](http://travis-ci.org/aasaru/serializable-cloudhopper-smpp)

Overview
------------------------

This is a copy of Cloudhopper SMPP library (https://github.com/fizzed/cloudhopper-smpp) with PDU-s made Serializable.
You can switch to this library from the original but this might break backwards compatibility (setReferenceObject method of any Pdu now requires the parameter to be Serializable).

The following is complete list of changes compared to original library

Package / Class | Original code | Modified code
------------ | ------------ | -------------
com.cloudhopper.smpp.type.Address | public class Address  { | public class Address implements Serializable {
com.cloudhopper.smpp.tlv.Tlv | public class Tlv { | public class Tlv implements Serializable {
com.cloudhopper.smpp.pdu.Pdu | public abstract class Pdu { | public abstract class Pdu implements Serializable {
com.cloudhopper.smpp.pdu.Pdu | public void setReferenceObject(Object value) { | public <T extends Serializable> void setReferenceObject(T value) {
com.cloudhopper.smpp.pdu.* | | private static final long serialVersionUID = 1L;

License (same as original)
--------------------------

Copyright (C) 2015+ Fizzed, Inc.
Copyright (C) 2009-2015 Twitter, Inc.
Copyright (C) 2008-2009 Cloudhopper, Inc.

This work is licensed under the Apache License, Version 2.0. See LICENSE for details.
