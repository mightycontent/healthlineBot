# healthlineBot
This small grails app simulates the Healthline Tag API. Like most grails applications it can be
easily deployed as a war file.

When a POST call is made to the /tag method the system returns a doc_id.  A quartz job runs every 20 seconds and
looks for all indexing requests with a status of 'queued'. For each one it will make a GET call to the callback_url
provided in the /tag request. Whether successful or not, the system currently changes the status of the request to
'ok'.

The /tagstatus method was also implemented per the provided specification. This synchronous returns the status
for a given request.

The calls will always return the same set of tags. The JSON tag object is understood to be a list containing up
to three concepts. The tag object returned by this program is based on samples that were received in CSV format so
their final representation may change.

