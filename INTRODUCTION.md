# 1. General introduction and motivation
  
## Overview of Hadoop 

The Apache Hadoop is a framework for the distributed processing of large scale of data sets across computers clusters by using simple programming models. It is designed to scale up from single servers to thousands of machines, each offering local computation and storage. All the modules in Hadoop are designed with a fundamental assumption that hardware failures are common and should be automatically handled by the framework. [Hadoop]

## Overview of Pig Latin1

Apache Pig is a platform for analyzing large data sets that consists of a high-level language for expressing data analysis programs, coupled with infrastructure for evaluating these programs. The salient property of Pig programs is that their structure is amenable to substantial parallelization, which in turns enables them to handle very large data sets. [PigLatin1]

## Overview of Apache Spark

A fast and general compute engine for Hadoop data. Spark provides a simple and expressive programming model that supports a wide range of applications, including high-level APIs in Java, Scala, Python and R, and an optimized engine that supports general execution graphs. It also supports a rich set of higher-level tools including Spark SQL for SQL and structured data processing, MLlib for machine learning, GraphX for graph processing, and Spark Streaming. [Spark]

## Overview of Pig on Spark ( Spork (Pig-on-Spark) project )

Developing of Spork was initiated by Mayer Rustagi (Sigmoid Analytics). Spork project focused to
	• Make data processing more powerful
	• Make data processing more simple
	• Make data processing 100x faster than before
by combine the power of and simplicity of Apache Pig on Apache Spark, increasing ETL process faster than before. For it DataDoctor operator toolkit is used. DataDoctor is a high-level operator on top of Spark - framework for joins, sorting, grouping, which hide a lot of complexity of data operators for simple implementing data operators in Pig and Apache Hive on Spark. [Spork]

# 2. State of the art
- *What have others tried to solve the problem?*
- *Why are existing solutions not sufficient?*

## CUBE operator on Spark

### Naive CUBE operator on Pig Latin1
Current realization of CUBE operator is presenting  syntactic sugar over UDF's and different union of combination of grouping set operators. Each grouping are compute independently of each other without using result of previous computations. 

## MRCube for Pig Latin1
Now, developers of Pig Latin1 are implementing efficient algorithm of computing CUBE operator, based on approaches described  research results in [MRCube] paper. In this paper considered several issues: 
The first is efficient distribution data for computing result of algebraic measures. Operation is algebraic if result of operation over the group can be computed as union of result, which computed over sub group. 
The second issue is how to effectively distribute the computation such that we strike a good balance between the amount of intermediate data being produced and the pruning of unnecessary data.

### Group by operator on Spark as “implementation” CUBE operator
According the task [Spark-2663] current implementation of  CUBE operator presents syntactic shugar over group by operation. In general, CUBE operator can be implemented by using different combination of attributes with UNION operaton.  Because group by's are executing separately from each other, it is easy, but not efficient way of implementation CUBE operator.   

# 3. Your task in this thesis

- *What is your task in this thesis?*
- *In how far will this go beyond what has been done before and/or in how far will a different approach be taken than what has been tried before?*

In this research I will be implementing efficient way of computing CUBE operation and evaluate effectiveness by  comparing performance current implementation CUBE operation over group by operation with new implementation.

## PipeSort

PipeSort Algorithm is improvement of GBLP algorithm [PipeSort]. PipeSort algorithm try to minimize the total computation cost of a data cube. PipeSort operate with two costs: the first is Sxy cost - it is cost of computing Nx node by using Ny if Nx already sorted and the second is Axy - it is computing cost of Ny node, if Nx is not sorted. PipeSort determine sorting order for each node in each level, starting from root of tree. If order of Ny is a prefix of parent node Nx then computation of Ny can be obtained without any extra sorting. And Nxy edge marks with Axy cost (Otherwise Nxy will be marked as Sxy). In general, always one of Edge can be marked as Axy. PipeSort uses a local optimization technique based on weighted bipartite matching (minimum weight matching problem) [Combinatory]. Then PipeSort adds a node that corresponds to the original fact table. Edge between original table and root marked as S edge. 

# 4. Approach taken in this thesis

- *How do you plan to solve the problem in this thesis?*
- *If your approach is different than other existing approaches: 
  why is there good reason to assume that your approach will lead to a better solution?*

In this paper will be considered implementation PipeSort algorithm on Apache Spark for efficient computation of CUBE operation.

# 5 Evaluation

   - *How do you plan to evaluate your approach?*
   - *What are good criteria for assessing the quality of your approach?*
   - *What are suitable metrics to measure and quantify its performance characteristics?*
   - *Do you plan to prototypically implement the approach and perform experiments/measurements or simulation experiments?*
   - *Do you plan to mathematically prove certain properties of your approach?*
   - *Please formulate 3 to 5 research questions that you plan to answer with the evaluation of your approach.*

Evaluation is comparing my implementation with naive cubing (Groupping set).
Evaluation with comparing mrcube implementation on pig latin1 is not possible, because mrcube realised for Hadoop.

# 6 Work plan:

   - *Which work steps are necessary to work on your task?*
   - *When will the individual work steps be ready?*
   - *When will you write the text for which chapter of your thesis?*

1. Learn Apache Spark/Scala
2. Simple implementaton of PipeSort
3. Overview of Spark optimization information on Spark
4. How can I user optimization information for my efficient implementation
5. Implementation of approach
6. Prepare large dataset for testing and comparing
7. Make meashures for comparing efficient of implementation
8. Write report.

# Refferences: 

	1. http://hadoop.apache.org/ [Hadoop]
	2. http://pig.apache.org/docs/r0.8.1/piglatin_ref1.html#Overview [PigLatin1]
	3. http://blog.cloudera.com/blog/2014/09/pig-is-flying-apache-pig-on-apache-spark/ [Spork]
	4. http://spark.apache.org/docs/latest/index.html [Spark]
	5. https://issues.apache.org/jira/browse/SPARK-2663 [Spark-2663]
	6. K.MORFONIOS, S.KONAKAS, Y.IOANNIDIS, N.KOTSIS ROLAP Implementations of the Data Cube [PipeSort]
	7. Bernhard Korte, Jens Vygen Combinatorial Optimization [Combinatory]
