Log Processing Demo application

### The general approach: 

1. loop through all files in the folder
2. parse and load the content of each file in a separate table, using a thread pool
3. process all loaded events and load them in a second table, using a thread pool

### How to run the application: 
1. From the IDE, make sure "annotation processing is enabled", because Lombok is being used.
[https://www.jetbrains.com/help/idea/configuring-annotation-processing.html](https://www.jetbrains.com/help/idea/configuring-annotation-processing.html)

2. Run DemoApplication.java with a folder name parameter with log a folder containing log files as parameter. 