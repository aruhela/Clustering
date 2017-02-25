/*
 * Implemented by :	Amit Ruhela 
 * This class describes the global constants used in various files.
 */

package Defination;

public class globalConstants
{
	private globalConstants()
	{
		// Prevents instantiation
	}

	//Main Folder
	public static final String Name_Directory = "L:/Simulator/";

	//sub Folders
	public static final String Name_InputFolder = Name_Directory + "Input/";
	public static final String Name_OutputFolder = Name_Directory + "Output/";
	public static final String Name_TempFolder = Name_Directory + "Temp/";
	public static final String Name_LogFolder = Name_Directory + "Log/";


	/** Input file containing location of users */
	public static String fin_UserLoc = Name_InputFolder + "Sample.txt";
//	public static String fin_UserLoc = Name_InputFolder + "UserLocation.txt";
	
	/** Output file containing id of cluster which user belongs to */
	public static String Output_UsersFile = Name_OutputFolder + "UserClusters.txt";

	/** The Output clusters file. */
	public static String Output_ClustersFile = Name_OutputFolder + "ClustersLocation.txt";
	
	/** The file for logging. */
	public static String Log_File = Name_LogFolder + "Log.txt";
	
	// Logging Levels
	public final static int LL_INFO = 1;
	public final static int LL_ERROR = 1;
	public final static int LL_DEBUG = 1;
	public final static int LogLevel[] = { 1, 1, 1 };
	
	public final static int gClusterCount = 9; // No of Clusters
	public final static int gIterCount = 100; // No of iteration to run

	public final static int MinClusterSize = 5; // Minimum number of data-points in a cluster.
	
	
	public final static int Earth_MinLat = -90;
	public final static int Earth_MinLan = +90;
	public final static int Earth_MaxLat = -180;
	public final static int Earth_MaxLan = +180;
}

