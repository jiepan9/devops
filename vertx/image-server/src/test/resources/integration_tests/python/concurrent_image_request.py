#!/usr/bin/python
# Copyright (c) 2009 Denis Bilenko. See LICENSE for details.

"""Spawn multiple workers and wait for them to complete"""
from __future__ import print_function
import sys

#url = 'http://localhost:8081/2015-02-Test-WebP/text_black.jpg'
url = 'http://localhost:8081/metrics'
urls =[]
for i in range(5000):
	urls.append(url)

import gevent
from gevent import monkey

# patches stdlib (including socket and ssl modules) to cooperate with other greenlets
monkey.patch_all()


if sys.version_info[0] == 3:
    from urllib.request import urlopen
else:
    from urllib2 import urlopen


def print_head(url):
    print('Starting %s' % url)
    data = urlopen(url, timeout=10).read()
    print('%s: %s bytes: %r' % (url, len(data), data[:50]))

jobs = [gevent.spawn(print_head, url) for url in urls]

gevent.wait(jobs)
