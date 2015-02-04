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
import shutil

root=sys.argv[1]
files = [f for f in os.listdir(root) if not os.path.isdir(root+'/' +f)]
for f in files:
	if f[-4:].lower() == '.jpg':
		print '--------------' + f + '----------------'
		print '\n'
		path = root + f.replace('.', '_').replace(' ', '_')
		if not os.path.exists(path):
			os.mkdir(path, 0755)
		shutil.copy(root +'/' + f, path)
		print 'FACTOR 80'

		wbp80f = f[:-4] + '_80' + '.webp'
		cmd = 'cwebp -q 80 '+ path + '/' + f + ' -o ' + path + '/' + wbp80f
		print 'RUN ' + cmd 
		os.system(cmd)

		png80f = wbp80f[:-5] + '.png'
		cmd = 'dwebp ' + path + '/' + wbp80f + ' -o ' + path + '/' + png80f
		print 'RUN ' + cmd
		os.system(cmd)
		
		cmd = 'convert ' + path + '/' + png80f + ' ' + path + '/' + png80f[:-4] + '.jpg'
		print 'RUN ' + cmd
		os.system(cmd)

		print '\n'
		print 'FACTOR 85'
		wbp85f = f[:-4] + '_85' + '.webp'
		cmd = 'cwebp -q 85 '+ path + '/' + f + ' -o ' + path + '/' + wbp85f
		print 'RUN ' + cmd
		os.system(cmd)
		
		png85f = wbp85f[:-5] + '.png'
		cmd = 'dwebp ' + path + '/' + wbp85f + ' -o ' + path + '/' + png85f
		print 'RUN ' + cmd
		os.system(cmd)
		
		cmd = 'convert ' + path + '/' + png85f + ' ' + path + '/' + png85f[:-4] + '.jpg'
		print 'RUN ' + cmd
		os.system(cmd)
		print '-----------------END---------------\n'



