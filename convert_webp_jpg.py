#!/usr/bin/python

'''
1 convert oignal jpg file to webp file with compressing factor 85 and 80 using libwebp (google)
cwebp
2 convert webp file to png file using libwebp
dwebp
3 convert png file to jpg using ImageMagick
convert


'''


import os
import sys

root=sys.argv[1]

files = [f for f in os.listdir(root) if os.path.isfile(f)]
for f in files:
	print f



