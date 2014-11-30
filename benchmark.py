import string
import sys
#import datetime
from subprocess import PIPE, Popen


JAR_PATH = "./search.jar"

CORPUS_PATH = "./test_08n0147.xml"

# the results will be written to this file
OUT_FILE = ""

# the following queries will be performed
QUERIES = [
    "dicyclocarbodimide",
    "reduce the appeal of",
    "experiment",
    ["protein", "complex"],
    "duchenne\'s muscular",
    ["duchenne's", "disease"],
]


def run_query(query):

    time = ["/usr/bin/time", "-f", "time: %E"]

    java = ["java", "-jar", JAR_PATH]

    if type(query) is str:
        query = [query]

    proc = Popen(time + java + query, stdout = PIPE)

    return string.join(proc.stdout.readlines(), "\n")


for query in QUERIES:
    print
    print '======================='

    if type(query) is str:
        print 'phrase: %s' % query
    elif type(query) is list:
        print 'query terms: %s' % string.join(query, ', ')
    else:
        raise Exception('unknown query type')

    print run_query(query)

