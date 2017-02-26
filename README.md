# Replica Server Placement using KMeans Algorithm
===============================================

The standard K-Means algorithm implemented in Matlab is based on Euclidean distance between datapoints. The Matlab algorithm doesn't suit to cluster geographic coordinates as earth is spherical and not flat. We therefore need a modified version of k-Means algorithm which works correctly for data points specified in format of Latitude and Longitude. I have implemented one such version of KMeans whose pseudo-code is given below. 
 
## Inputs: 
1. Datapoints        : Locations of the user in Latitude and Longitude
2. ClusterCount  	  : How many clusters you want 
3. IterationsCount   : The number of iterations for which the algorithm will iterate 
4. ClusterThreshold  : Minimum number of datapoints that should be present in a cluster 
5. Centroid  	      : Initial Position of Cluster centroids: Optional 
 
```
K-Means(DataPoints X, ClusterCount K, IterationsCount I, ClusterThreshold T, Centroid C )
{ 
- Uniformly choose K datapoints as centroids on the earth if initial centroids are not specified. 
- Assign datapoints to their nearest centroid
- Loop until all iterations are completed or No change observed from the previous iteration in clusters configuration
- LoopStart 
  1. Compute centroid of last generated/updated clusters 
  2. Update the association with cluster for all the datapoints. ie if a datapoints becomes closer to centroid of some other cluster, then remove the datapoint from current cluster and assign the datapoint to the cluster whose centroid is nearest to it right now. 
  3. If no modification is observed in clusters configuration, then exit from the loop. 
  4. During updation of datapoints, following situations can happen. Both cases described below.
     Case 1) A cluster now has lesser datapoints than Cluster-Threshold
     Case 2) A cluster becomes empty. 
		
     Case 1) : Remove all the datapoints from the current cluster and assign to that cluster which is nearest to them. This means we merge the datapoints of this cluster with all other clusters. 
     Case 2) : Find a cluster which is worst. Divide this worst cluster in two parts and assign half of its elements to the empty cluster. A cluster is called worst if its cost (= sum of distances of all datapoints from present centroid) is highest. Greedily, we cut the worst cluster along that direction which is longest. 
- LoopEnd	
- Store the output of this program in files 
- Some of the centroid may appear on the sea where we expect none of the users exist. We therefore replace such centroid with the location of the nearest data point from them. 
} 
``` 
Note : Nearest Centroid is determined on basis of Haversine Great-Circle Formula. Further whenever a worst cluster is divided, we cut the cluster along that direction (along Latitude or Longitude) which results in maximum benefit. i.e. the two cluster collectively have least cost after the division. 
 
# Contributions of the algorithm 
-------------------------------
	- The algorithm works well for spherical datapoints 
	- Converges in less than 30 iterations 
	- Chooses centroid reasonably well based on Haversine formula 
	- Handles situation when clusters becomes empty.s 
	- Handles situation when clusters becomes very small. 
	- Chooses worst cluster for division and division is made on longest axes. 

# Limitation  
-------------------------------
	- The algorithm still requires following input parameter  
		. ClusterCount 
		. IterationsCount 
	- The efficiency of the program depends upon initial chosen centroid. 

# Observations/Results
-------------------------------
- With above algorithm, it was observed that if the initial centroids are selected uniformly on the earth, then a benefit of 10% is seen compared to case when initial centroids are placed at the locations where top tier CDN providers have placed their servers. For all comparisons, we kept the value of K same. 
 
- With this approach, when centroid were placed uniformly on earth at K=53 locations, the distance of users from all the surrogate servers on average was 295.778 KM. The program completed in 24 iterations. 

- Without this algorithm and using standard K-Means algorithm that use euclidean distanace, when centroid were placed at the locations dictated by the CDN providers, the users were 409.8 Km distant from surrogate servers on an average.

- Using this algorithm, when centroid were placed at the locations dictated by the CDN providers, the users were 329.5 Km distant from surrogate servers on an average. The program completed in 17 iterations. 
 
 
 
