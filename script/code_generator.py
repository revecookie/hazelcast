#!/usr/bin/python

import os, os.path, sys, io, string, re

tab1 = ' ' * 4
tab2 = tab1 * 2
tab3 = tab1 * 3
tab4 = tab1 * 4


def create_write_data (variables):
  __prefix = '\n' + \
             tab1 + '@Override' + '\n' + \
             tab1 + 'public void writeData(ObjectDataOutput objectDataOutput) throws IOException {' + '\n'
  
  __central = ''.join(map(lambda v: tab2 + 'objectDataOutput.writeObject(this.' + v + ');' + '\n', variables))
  __suffix = tab1 + '}' + '\n'
  
  return __prefix + __central + __suffix


def create_read_data (variables):
  __prefix = '\n' + \
             tab1 + '@Override' + '\n' + \
             tab1 + 'public void readData(ObjectDataInput objectDataInput) throws IOException {' + '\n'
  
  __central = ''.join(map(lambda v: tab2 + 'this.' + v + ' = objectDataInput.readObject();' + '\n', variables))
  __suffix = tab1 + '}' + '\n'
  
  return __prefix + __central + __suffix


def extract_class_name (source):
  __file_name = source.split('/')[-1]
  return __file_name.split('.')[0]


def create_to_string (source, variables):
  __prefix = '\n' + \
             tab1 + '@Override' + '\n' + \
             tab1 + 'public String toString()' + '\n' + \
             tab1 + '{' + '\n' + \
             tab2 + 'return' + '\n' + \
             tab3 + '"' + extract_class_name(source) + '{" +' + '\n'
  
  __central_raw = ''.join(map(lambda v: tab3 + '", ' + v + '=" + ' + v + ' +' + '\n', variables))
  __first_comma = __central_raw.find(',')
  __central = __central_raw[0: __first_comma] + __central_raw[(__first_comma + 2):]
  
  __suffix = tab3 + '"}";' + '\n' + \
             tab1 + '}' + '\n'

  return __prefix + __central + __suffix


def create_output (source, variables):
  return create_write_data(variables) + create_read_data(variables) + create_to_string(source, variables)


def overwrite_file (dest, lines):
  f = open(dest, 'w')
  
  for ln in lines:
    f.write(ln)
  
  f.flush()
  f.close()


def write_output (source, lines, source_code):
  __last_curly = -1;
  __counter = 0
  
  for ln in lines:
    if re.sub('\s+', '', ln) == '}':
      __last_curly = __counter
      
    __counter += 1

  __new_lines = lines[0:__last_curly] + [source_code] + lines[__last_curly:]
  overwrite_file(source, __new_lines)


def process_file (source):
  __lines = [ ln for ln in io.open(source, 'r') ]
  __field_lines = [string.replace(ln.strip(), ';', '').strip() for ln in __lines if re.match('^public.+;$', ln.strip()) ]
  
  __tokens = map( lambda ln : ln.split()[-1], __field_lines)
  __source_code = create_output(source, __tokens)
  
  write_output(source, __lines, __source_code)

  
def help_debug (items):
  for i in items:
    print i


def process_files (files):
  for f in files:
    print 'Will create methods writeData(.), readData(.), toString()'
    print '  in class: ', extract_class_name(f)
    process_file(f)
    print


if __name__ == "__main__":
  process_files(sys.argv[1:])

