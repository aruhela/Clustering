/*
 * Replica Server Placement using KMeans Algorithm
 * Implemented by :	Amit Ruhela
 *
 */
package Clustering;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import Defination.CommonFunctions;
import Defination.globalConstants;

/*************************************************************************************
 * The Class implements clustering of geographical coordinates using K-Means algorithm.
*************************************************************************************/
public class GetGeoClusters
{

	/** Array of initial clusters. */
	public static Cluster[] gClustersArray = new Cluster[globalConstants.gClusterCount];

	/*************************************************************************************
	 * The Class DataPoint. : Belongs to Geo coordinates for each user
	 *************************************************************************************/
	public static class DataPoint
	{
		double mLatitude;
		double mLongitude;

		/** UserId. */
		int mId;

		/** The current cluster id. */
		int currentClusterId;

		/** The new cluster id. */
		int newClusterId;

		/**
		 * Instantiates a new data point.
		 *
		 * @param lat the Latitude
		 * @param lan the Longitude
		 * @param uid the UserID
		 */
		public DataPoint(double latitude, double longitude, int uid)
		{
			mLatitude = latitude;// Latitude of datapoint
			mLongitude = longitude;// Longitude of datapoint
			mId = uid;
		}
	}

	/*************************************************************************************
	 * The Class Cluster.
	 *************************************************************************************/
	public static class Cluster
	{

		/** Centroid : X coordinates ie latitude */
		double mCx;

		/** Centroid : Y coordinates i.e longitude*/
		double mCy;

		/** The Data points in a cluster. */
		List<DataPoint> mDataPoints;

		/**
		 * Instantiates a new cluster.
		 */
		public Cluster()
		{
			mDataPoints = new ArrayList<>();
		}

		/**
		 * Set the value of centroid
		 * @param cx the cx
		 * @param cy the cy
		 */
		public void SetCentroid(double Latitude, double Longitude)
		{
			mCx = Latitude;
			mCy = Longitude;
		}
	}

	/*************************************************************************************
	 * The main method.
	 *
	 * @param args the arguments
	 * @throws IOException Signals that an I/O exception has occurred.
	 *************************************************************************************/
	public static void main(String args[]) throws IOException
	{
		String myClassName = Thread.currentThread().getStackTrace()[1].getClassName();
		long startExecution = (new Long(System.currentTimeMillis())).longValue();
		CommonFunctions.InitializeFiles();
		CommonFunctions.logAndPrint("\n\n" + myClassName + " started at " + new Date().toString() + "\n");

		process();

		long endExecution = (new Long(System.currentTimeMillis())).longValue();
		long difference = (endExecution - startExecution) / 1000;
		CommonFunctions.logAndPrint("\n" + myClassName + " finished at " + new Date().toString() + ". The program has taken " + (difference / 60) + " minutes.");
		CommonFunctions.out_Log.close();
	}

	/*************************************************************************************
	 * Process the datapoints and assigns to various clusters.
	 *
	 * @throws NumberFormatException the number format exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 *************************************************************************************/
	private static void process() throws NumberFormatException, IOException
	{
		CommonFunctions.logAndPrint("Started GetGeoClusters.process()");
		// Create Clusters
		for (int i = 0; i < globalConstants.gClusterCount; i++)
		{
			gClustersArray[i] = new Cluster();
		}

		if (initialize())
		{
			startAnalysis();
			WriteOutput();
		}
	}

	/*************************************************************************************
	 * Initialize all the datapoints to the clusters uniformly
	 * Calculate the centroid based on initial assignment
	 * 
	 * @throws NumberFormatException the number format exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 *************************************************************************************/
	private static boolean initialize() throws NumberFormatException, IOException
	{
		CommonFunctions.logAndPrint("GetGeoClusters.initialize()");
		if (!addDataPointsUniformly())
			return false;
		UpdateCentroids();
		return true;
	}

	/*************************************************************************************
	 * Adds the data points uniformly to all clusters.
	 * The clusters centroids are also uniformly chosen to optimize the K-Means algorithm
	 *
	 * @throws NumberFormatException the number format exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 *************************************************************************************/
	private static boolean addDataPointsUniformly() throws NumberFormatException, IOException
	{
		String readString;
		BufferedReader bu_entities = new BufferedReader(new InputStreamReader(new FileInputStream(globalConstants.fin_UserLoc)));
		int usersCount = 0;

		//For finding range of latitudes and longitudes of users location
		/** The Min Lat. : Minimum X-Coordinate of user location */
		double Min_Lat = Double.MAX_VALUE;

		/** The Min Lan.  : Minimum Y-Coordinate of user location*/
		double Min_Lan = Double.MAX_VALUE;

		/** The Max Lat.  : Maximum X-Coordinate of user location*/
		double Max_Lat = Double.MIN_VALUE;

		/** The Max Lan.  : Maximum Y-Coordinate of user location*/
		double Max_Lan = Double.MIN_VALUE;

		while((readString = bu_entities.readLine()) != null)
		{
			usersCount++;
			int user_id = Integer.parseInt(readString.split("\t")[0]);
			double user_Lat = Double.parseDouble(readString.split("\t")[1]);// Latitude +-90
			double user_Lan = Double.parseDouble(readString.split("\t")[2]);// Longitude +-180

			int CId = usersCount % globalConstants.gClusterCount;
			DataPoint dp = new DataPoint(user_Lat, user_Lan, user_id);
			dp.currentClusterId = CId;
			dp.newClusterId = CId;
			gClustersArray[CId].mDataPoints.add(dp);

			//Verify bad coordinates
			if ((user_Lat < globalConstants.Earth_MinLat) && (user_Lat > globalConstants.Earth_MaxLat) && (user_Lan < globalConstants.Earth_MinLan)
					&& (user_Lan > globalConstants.Earth_MaxLan)) // For verification
			{
				CommonFunctions.logAndPrint("Invalid Location");
			}

			if (Max_Lat < user_Lat)
			{
				Max_Lat = user_Lat;
			}
			if (Max_Lan < user_Lan)
			{
				Max_Lan = user_Lan;
			}

			if (Min_Lat > user_Lat)
			{
				Min_Lat = user_Lat;
			}
			if (Min_Lan > user_Lat)
			{
				Min_Lan = user_Lan;
			}
		}
		bu_entities.close();
		if (usersCount < (globalConstants.gClusterCount * globalConstants.MinClusterSize))
		{
			CommonFunctions.logAndPrint("GetGeoClusters. The number of clusters with minimum datapoints criteria canot be met. Use appropriate gobal constants");
			CommonFunctions.logAndPrint("UsersCount=" + usersCount);
			CommonFunctions.logAndPrint("Clusters Requested=" + globalConstants.gClusterCount);
			CommonFunctions.logAndPrint("Minimum datapoints per cluster requested=" + globalConstants.MinClusterSize);

			return false;
		}

		CommonFunctions.logAndPrint("Min_X=" + Min_Lat + " Min_Y=" + Min_Lan + " Max_X=" + Max_Lat + " Max_Y=" + Max_Lan);
		CommonFunctions.logAndPrint("GetGeoClusters.addDataPoints() completed");

		// Find uniformly the initial position of all the Centroids. Optimization step
		double iLat = 0, iLan = 0;
		for (int n = 1; n <= globalConstants.gClusterCount; n++)
		{
			iLat = (((Max_Lat - Min_Lat) / (globalConstants.gClusterCount + 1)) * n) + Min_Lat;
			iLan = (((Max_Lan - Min_Lan) / (globalConstants.gClusterCount + 1)) * n) + Min_Lan;
			gClustersArray[n - 1].SetCentroid(iLat, iLan);
		}
		CommonFunctions.logAndPrint("\nGetGeoClusters.setInitialCentroids() completed");
		return true;
	}

	/*************************************************************************************
	 * Start analysis.
	 * Assign each datapoint to closest cluster
	 * If all datapoints are intact, the algorithms is converge
	 * If the datapoints moves to other clusters, then remove bad clusters and update the clusters centroids.
	 *
	 * @throws NumberFormatException the number format exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 *************************************************************************************/
	private static void startAnalysis() throws NumberFormatException, IOException
	{
		for (int i = 0; i < globalConstants.gIterCount; i++)
		{
			CommonFunctions.logAndPrint("\nIteration = " + i);

			findBestClusters();
			boolean changed = UpdateClusters();
			if (changed == false)
			{
				CommonFunctions.logAndPrint("Algorithm conerged in " + i + " iterations");
				break;
			}
			UpdateEmptyCluster();
			UpdateCentroids();
		}
	}

	/*************************************************************************************
	 * For each datapoint, find which cluster is closest.
	 *************************************************************************************/
	private static void findBestClusters()
	{
		long TotalDistanceofAllDps = 0;
		int DP_Count = 0;
		for (int i = 0; i < globalConstants.gClusterCount; i++)
		{
			for (DataPoint dp : gClustersArray[i].mDataPoints)
			{
				DP_Count++;
				double closestDistance = Double.MAX_VALUE;
				double currentDist = 0.0;
				int currentBestCluster = -1;

				for (int j = 0; j < globalConstants.gClusterCount; j++)
				{
					if (GetClusterElementCount(j) == 0)
					{
						continue;
					}

					currentDist = GreatCircleDistUsingHaverSine(dp.mLatitude, dp.mLongitude, gClustersArray[j].mCx, gClustersArray[j].mCy);
					if (currentDist < closestDistance)
					{
						closestDistance = currentDist;
						currentBestCluster = j;
					}
				}

				dp.newClusterId = currentBestCluster;
				TotalDistanceofAllDps += closestDistance;
			}
		}
		CommonFunctions.logAndPrint("GetGeoClusters.findBestClusters() " + ((1.0 * TotalDistanceofAllDps) / DP_Count));

	}

	/*************************************************************************************
	 * Update clusters.	
	 * If any datapoint is closest to differnt cluster than remove it from current cluster and assign to the closest cluster.
	 *  
	 * @return true, if successful
	 * if all datapoints are assigned to the best cluster already, then return false
	 *************************************************************************************/
	private static boolean UpdateClusters()
	{
		boolean changed = false;
		for (int i = 0; i < globalConstants.gClusterCount; i++)
		{
			for (Iterator<DataPoint> k = gClustersArray[i].mDataPoints.iterator(); k.hasNext();)
			{
				DataPoint dp = k.next();
				if (dp.currentClusterId != dp.newClusterId)
				{
					changed = true;
					k.remove();
					dp.currentClusterId = dp.newClusterId;
					gClustersArray[dp.newClusterId].mDataPoints.add(dp);
				}
			}
		}
		CommonFunctions.logAndPrint("GetGeoClusters.UpdateClusters()");
		return changed;
	}

	/*************************************************************************************
	 * Update clusters which contains very few datapoints or have become empty
	 *
	 * @throws NumberFormatException the number format exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 *************************************************************************************/
	private static void UpdateEmptyCluster() throws NumberFormatException, IOException
	{
		for (int i = 0; i < globalConstants.gClusterCount; i++)
		{
			// When a cluster contains very few elements, then each elements of this cluster is merged to the cluster which is closest to it.
			if (GetClusterElementCount(i) < globalConstants.MinClusterSize)
			{
				CommonFunctions.logAndPrint("Merging Cluster " + i + " which has " + GetClusterElementCount(i) + " elements");
				MergeCluster(i);
			}

			// When a cluster becomes empty, then the biggest cluster is split into two sub-parts and half of its elements are
			// assigned to an empty cluster
			if (GetClusterElementCount(i) == 0)
			{
				int WorstCid = FindWorstCluster();
				CommonFunctions.logAndPrint("Empty Cluster Found " + i + ": Dividing Worst Cluster " + WorstCid);
				SplitWorstCluster(WorstCid, i);
			}
		}
	}

	/*************************************************************************************
	 * Merge cluster.
	 *
	 * @param IdClusterTobeMerged the id cluster to be merged
	 *************************************************************************************/
	private static void MergeCluster(int IdClusterTobeMerged)
	{

		for (Iterator<DataPoint> k = gClustersArray[IdClusterTobeMerged].mDataPoints.iterator(); k.hasNext();)
		{
			DataPoint dp = k.next();
			k.remove();

			double CurrDist = Double.MAX_VALUE;
			double NewDist = 0.0;
			int NewClusterId = -1;

			// Assign Element to that cluster which has least distance from the Datapoint
			for (int j = 0; j < globalConstants.gClusterCount; j++)
			{
				if (GetClusterElementCount(j) < globalConstants.MinClusterSize) // Ignore empty cluster or the cluster that has few datapoints
				{
					continue;
				}

				if (j == IdClusterTobeMerged) // Ignore Cluster to which the Datapoint was earlier associated
				{
					continue;
				}

				NewDist = GreatCircleDistUsingHaverSine(dp.mLatitude, dp.mLongitude, gClustersArray[j].mCx, gClustersArray[j].mCy);
				if (NewDist < CurrDist)
				{
					CurrDist = NewDist;
					NewClusterId = j;
				}
			}

			dp.currentClusterId = NewClusterId;
			dp.newClusterId = NewClusterId;
			if (NewClusterId == -1)
			{
				CommonFunctions.logAndPrint("Error : Canot merge clusters with given inputs. Suggestion : Decrease UserThreshold.");
				System.exit(0);
			}
			gClustersArray[NewClusterId].mDataPoints.add(dp);
		}
	}

	/*************************************************************************************
	 * Find cluster whose datapoints are farthest from its centroid. 
	 *
	 * @return index of worst cluster
	 * @throws NumberFormatException the number format exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 *************************************************************************************/
	private static int FindWorstCluster() throws NumberFormatException, IOException
	{
		int lWorstClusterIndex = 0;
		double lWorstCumDistance = 0;
		for (int i = 0; i < globalConstants.gClusterCount; i++)
		{
			double thisClusterDist = GetClusterTotalDistance(i);
			if (lWorstCumDistance < thisClusterDist)
			{
				lWorstClusterIndex = i;
				lWorstCumDistance = thisClusterDist;
			}
		}
		return lWorstClusterIndex;
	}

	/*************************************************************************************
	 * Split worst cluster.
	 *
	 * @param bigCid the big cid
	 * @param emptyClusterId the empty cluster id
	 *************************************************************************************/
	private static void SplitWorstCluster(int bigCid, int emptyClusterId)
	{
		double lLat_Min = Double.MAX_VALUE;
		double lLat_Max = Double.MIN_VALUE;

		double lLan_Min = Double.MAX_VALUE;
		double lLan_Max = Double.MIN_VALUE;

		// Divide along Longitude

		double SumSin = 0.0;
		double SumCos = 0.0;
		double Sum_X = 0.0;
		for (DataPoint dp : gClustersArray[bigCid].mDataPoints)
		{
			if (dp.mLatitude < lLat_Min)
			{
				lLat_Min = dp.mLatitude;
			}
			if (dp.mLatitude > lLat_Max)
			{
				lLat_Max = dp.mLatitude;
			}
			Sum_X += dp.mLatitude;

			// Longitude
			if (dp.mLongitude < lLan_Min)
			{
				lLan_Min = dp.mLongitude;
			}
			if (dp.mLongitude > lLan_Max)
			{
				lLan_Max = dp.mLongitude;
			}
			SumSin += Math.sin(Math.toRadians(dp.mLongitude));
			SumCos += Math.cos(Math.toRadians(dp.mLongitude));
		}

		boolean Split_X = false;
		if (GreatCircleDistUsingHaverSine(lLat_Min, 0, lLat_Max, 0) > GreatCircleDistUsingHaverSine(0, lLan_Min, 0, lLan_Max)) // Longer along Latitude
		{
			Split_X = true;
		}
		CommonFunctions.logAndPrint(lLat_Min + "\t" + lLat_Max + "\t, \t" + lLan_Min + "\t" + lLan_Max + "\t" + "Split_X=" + Split_X);

		double AvgAngle_X = Sum_X / GetClusterElementCount(bigCid);

		double ComputedAngle_Y = Math.toDegrees(Math.atan2(SumSin, SumCos));
		double MinAngle_Y = 0.0;
		double MaxAngle_Y = 0.0;
		if (ComputedAngle_Y < 0)
		{
			MinAngle_Y = (ComputedAngle_Y + 180) % 360;
			MaxAngle_Y = (ComputedAngle_Y + 360) % 360;
		}
		else
		{
			MinAngle_Y = (ComputedAngle_Y + 360) % 360;
			MaxAngle_Y = (ComputedAngle_Y + 180) % 360;
		}
		CommonFunctions.logAndPrint("AvgAngle_X=" + AvgAngle_X + "\t MinAngle_Y=" + MinAngle_Y + "X MaxAngle_Y=" + MaxAngle_Y);

		for (Iterator<DataPoint> k = gClustersArray[bigCid].mDataPoints.iterator(); k.hasNext();)
		{
			DataPoint dp = k.next();
			double insideAngle = (dp.mLongitude + 360) % 360;
			if (!Split_X)
			{
				// longitude
				if ((MinAngle_Y < insideAngle) && (insideAngle <= MaxAngle_Y))
				{
					k.remove();
					dp.currentClusterId = emptyClusterId;
					dp.newClusterId = emptyClusterId;
					gClustersArray[emptyClusterId].mDataPoints.add(dp);
				}
			}
			else if (Split_X)
			{
				if (dp.mLatitude < AvgAngle_X)
				{
					k.remove();
					dp.currentClusterId = emptyClusterId;
					dp.newClusterId = emptyClusterId;
					gClustersArray[emptyClusterId].mDataPoints.add(dp);
				}
			}
		}
		CommonFunctions.logAndPrint("Worst has now " + GetClusterElementCount(bigCid) + " and Empty has " + GetClusterElementCount(emptyClusterId) + " elements");

	}

	/*************************************************************************************
	 * Update centroid of a cluster
	 * When assignment of datapoints in clusters is changed, the centroids are recalculated
	 *************************************************************************************/
	private static void UpdateCentroids()
	{
		for (int i = 0; i < globalConstants.gClusterCount; i++)
		{
			double tempX = 0;
			double SumSin = 0;
			double SumCos = 0;
			for (DataPoint dp : gClustersArray[i].mDataPoints)
			{
				tempX += dp.mLatitude;

				SumSin += Math.sin(Math.toRadians(dp.mLongitude));
				SumCos += Math.cos(Math.toRadians(dp.mLongitude));
			}
			gClustersArray[i].mCx = tempX / GetClusterElementCount(i);
			gClustersArray[i].mCy = Math.toDegrees(Math.atan2(SumSin, SumCos));
		}
		CommonFunctions.logAndPrint("GetGeoClusters.UpdateCentroids() completed");
		WriteClusersInformation();
	}

	/*************************************************************************************
	 * Great circle distance using haversine formula between two datapoints.
	 * http://introcs.cs.princeton.edu/java/12types/GreatCircle.java.html
	 * @param Lat1 the lat 1 of datapoint1
	 * @param Lan1 the lan 1 of datapoint1
	 * @param Lat2 the lat 2 of datapoint2
	 * @param Lan2 the lan 2 of datapoint2
	 * @return the geographical distance between two datapoints in Kilometers
	 *************************************************************************************/
	public static double GreatCircleDistUsingHaverSine(double Lat1, double Lan1, double Lat2, double Lan2)
	{
		double x1 = Math.toRadians(Lat1);
		double x2 = Math.toRadians(Lat2);

		double y1 = Math.toRadians(Lan1);
		double y2 = Math.toRadians(Lan2);

		double a = Math.pow(Math.sin((x2 - x1) / 2), 2) + (Math.cos(x1) * Math.cos(x2) * Math.pow(Math.sin((y2 - y1) / 2), 2));

		// great circle distance in radians
		double angle = 2 * Math.asin(Math.min(1, Math.sqrt(a)));
		double distance = angle * 6372.8; //Km
		return distance;
	}

	/*************************************************************************************
	 * Write cluster information in files
	 * Write cluster assignment of users in a file
	 * Computer total and average distance of users from centroid of clusters
	 *
	 * @throws NumberFormatException the number format exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 *************************************************************************************/
	@SuppressWarnings("boxing")
	private static void WriteOutput() throws NumberFormatException, IOException
	{
		//WriteClusersInformation();

		BufferedWriter out_u = new BufferedWriter(new FileWriter(globalConstants.Output_UsersFile)); // Output file containing users information
		BufferedWriter out_c = new BufferedWriter(new FileWriter(globalConstants.Output_ClustersFile)); // Output file containing clusters information

		out_u.write("#UserId" + "\t" + "Latitude" + "\t" + "Longitude" + "\t" + "NearestCluster" + "\t" + "ClusterDistance" + "\n");
		out_c.write("#ClusterId" + "\t" + "Latitude" + "\t" + "Longitude" + "\n");

		//Format double values
		NumberFormat formatter = new DecimalFormat("#0.00");

		double TotalDistance = 0;
		int UsersCount = 0;

		for (int i = 0; i < globalConstants.gClusterCount; i++)
		{
			DataPoint dpNearest = findNearestDatPoint(i);
			out_c.write(i + "\t" + formatter.format(dpNearest.mLatitude) + "\t" + formatter.format(dpNearest.mLongitude) + "\n");
			for (DataPoint dp : gClustersArray[i].mDataPoints)
			{
				Double Distance = GreatCircleDistUsingHaverSine(dp.mLatitude, dp.mLongitude, dpNearest.mLatitude, dpNearest.mLongitude);

				UsersCount++;
				TotalDistance += Distance;
				out_u.write(dp.mId + "\t" + formatter.format(dp.mLatitude) + "\t" + formatter.format(dp.mLongitude) + "\t" + dp.currentClusterId + "\t"
						+ formatter.format(Distance) + "\n");
			}
		}
		CommonFunctions.logAndPrint("\nTotal Datapoints = " + UsersCount);
		CommonFunctions.logAndPrint("Total Distance of datapoints= " + formatter.format(TotalDistance) + " Kilometer");
		CommonFunctions.logAndPrint("Average Distance of a datapoint= " + formatter.format(TotalDistance / UsersCount) + " Kilometer");

		out_c.write("\n\nAverage Distance of a datapoint = " + formatter.format(TotalDistance / UsersCount) + " Kilometer");
		out_c.write("\nTotal Distance of datapoints = " + formatter.format(TotalDistance) + " Kilometer");

		out_u.close();
		out_c.close();

	}

	//
	/*************************************************************************************
	 *  It may happen the centroid of the cluster lies in the sea, therefore choose that point which lie on the land. We choose the nearest datapoint from the centroid as the new centroid
	 *  Return the location of datapoint which is closest to the centroid of the cluster
	 *
	 * @param i : cluster index
	 * @return Location of closest datapoint
	 *************************************************************************************/
	@SuppressWarnings("boxing")
	private static DataPoint findNearestDatPoint(int clusterid)
	{
		// Get centroid of cluster
		double CentroidX = gClustersArray[clusterid].mCx;
		double CentroidY = gClustersArray[clusterid].mCy;

		//Find closest datapoint to the centroid of cluster
		double loopDistance = Double.MAX_VALUE;
		DataPoint ret_Dp = new DataPoint(0, 0, 0);
		for (DataPoint dp : gClustersArray[clusterid].mDataPoints)
		{
			Double Distance = GreatCircleDistUsingHaverSine(dp.mLatitude, dp.mLongitude, CentroidX, CentroidY);
			if (loopDistance > Distance)
			{
				loopDistance = Distance;
				ret_Dp = dp;
			}
		}
		return ret_Dp;
	}

	/*************************************************************************************
	 * Find sum of distance of all datapoints from the centroid of a cluster
	 *
	 * @param ClusterId the cluster id
	 * @return the average distance
	 * @throws NumberFormatException the number format exception
	 *************************************************************************************/
	private static double GetClusterTotalDistance(int ClusterId) throws NumberFormatException
	{
		double TotalDistance = 0.0;
		int noOfElements = GetClusterElementCount(ClusterId);
		if (noOfElements == 0)
		{
			return 0;
		}

		for (DataPoint dp : gClustersArray[ClusterId].mDataPoints)
		{
			TotalDistance += GreatCircleDistUsingHaverSine(dp.mLatitude, dp.mLongitude, gClustersArray[ClusterId].mCx, gClustersArray[ClusterId].mCy);
		}
		return TotalDistance;
	}

	/*************************************************************************************
	 * Write clusters information to a file
	 *************************************************************************************/
	private static void WriteClusersInformation()
	{
		CommonFunctions.logAndPrint("");
		for (int i = 0; i < globalConstants.gClusterCount; i++)
		{
			int usersCount = GetClusterElementCount(i);
			CommonFunctions.logAndPrint("Cluster " + i + " has " + usersCount + " datapoints");
		}
	}

	/*************************************************************************************
	 * Gets number of datapoints in a cluster.
	 *
	 * @param index : index of cluster
	 * @return number of datapoints in a cluster
	 *************************************************************************************/
	public static int GetClusterElementCount(int index)
	{
		return gClustersArray[index].mDataPoints.size();
	}
}
