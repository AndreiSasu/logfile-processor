Batch Log Processing Demo application

### The general approach: 

1. loop through all files in the folder
2. parse and load the content of each file in a separate table, using a thread pool
3. process all loaded events and load them in a second table, using a thread pool

### Large file support
Split file into chunks, either by line, or by size: 

``split -l 200 large_file.log``

``split -b 500MB large_file.log``

### How to run the application: 
1. From the IDE, make sure "annotation processing is enabled", because Lombok is being used.
[https://www.jetbrains.com/help/idea/configuring-annotation-processing.html](https://www.jetbrains.com/help/idea/configuring-annotation-processing.html)

2. Run DemoApplication.java with a folder name parameter containing log files. 
   Ex:
   ``/home/andrei/Projects/logfile-processor``

### Production support
This is only a demo application. 
For real world production use a streaming solution is preferred to a batch processing once since it's more suitable for logs.

1. AWS Kinesis + ElasticSearch:
   Ex: https://aws.amazon.com/getting-started/projects/build-log-analytics-solution/

2. Stream logs to Kafka and process with Apache Spark

3. Any other form of distributed cache(like Redis) + Hadoop MapReduce

3. Spring Batch in parallel mode with file chunking.


### Todo
The application can be extended to become a lightweight log streaming agent
