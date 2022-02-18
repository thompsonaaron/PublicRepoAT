This project was used to understand the basic time efficiency of various looping protocols in Java (for loop, enhanced for loop, streams, parallel streams). Interestingly, because functional programming is not native to Java, the streams API is noticeably slower in all facets compared to a traditional for loop; however, the one exception is for CPU intensive tasks, in which parallel streams are faster. I believe the equation N * Q > 1000 where N is the number of processes and Q is the number of steps per process is the guide for when to use parallel streams.

The setup of this project services as a template for future benchmarking tests in Java