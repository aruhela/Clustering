package Defination;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class CommonFunctions
{
	public static BufferedWriter out_Log = null;

	/*************************************************************************
	 * File Filter
	 *************************************************************************/
	public static void log(String strLine)
	{
		try
		{
			out_Log.write(strLine + "\n");
		}
		catch (IOException e)
		{
			CommonFunctions.logAndPrint("Logging Error :" + strLine);
		}
		return;
	}

	/*************************************************************************
	 * File Filter
	 *************************************************************************/
	public static void logAndPrint(String strLine)
	{
		try
		{
			out_Log.write(strLine + "\n");
			System.out.println(strLine);
		}
		catch (IOException e)
		{
			System.out.println("Logging Error :" + strLine);
		}
		return;
	}

	/*************************************************************************
	 * File Filter
	 *************************************************************************/
	public static void logWithoutNewLine(String strLine)
	{
		try
		{
			out_Log.write(strLine);
		}
		catch (IOException e)
		{
			System.out.println("Logging Error :" + strLine);
		}
		return;
	}

	/*************************************************************************
	 * File Filter
	 *************************************************************************/
	public static void logAndFlush(String strLine)
	{
		try
		{
			out_Log.write(strLine + "\n");
			out_Log.flush();
		}
		catch (IOException e)
		{
			System.out.println("Logging Error :" + strLine);
		}
		return;
	}

	/*************************************************************************
	 * File Filter
	 *************************************************************************/
	public static void InitializeFiles()
	{
		try
		{
			String[] DirNames = { globalConstants.Name_OutputFolder, globalConstants.Name_TempFolder, globalConstants.Name_LogFolder };

			for (int i = 0; i < DirNames.length; i++)
			{
				System.out.println("!!! Going to empty directory " + DirNames[i]);
				EmptyDirectoryCompletely(new File(DirNames[i]));

				System.out.println("Creating new directory " + DirNames[i]);
				new File(DirNames[i]).mkdirs();
			}
			out_Log = new BufferedWriter(new FileWriter(globalConstants.Log_File, false));
		}
		catch (Exception e)
		{
			System.out.println("Exception !!!!!!!!!!!!!  in CommonFunctions.InitializeFiles()");
			e.printStackTrace();
		}
	}

	/*************************************************************************
	 * Recursively delete files from a folder. Keep the current folder.
	 * @throws FileNotFoundException 
	 *************************************************************************/
	public static void EmptyDirectory(File path) throws FileNotFoundException
	{
		File[] files = path.listFiles();
		if (files != null)
		{ //some JVMs return null for empty dirs
			for (File f : files)
			{
				if (f.isDirectory())
				{
					EmptyDirectory(f);
				}
				else
				{
					f.delete();
				}
			}
		}
		//path.delete();
	}

	/*************************************************************************
	 * Recursively delete files from a folder. Also delete the current folder.
	 * @throws FileNotFoundException 
	 *************************************************************************/
	public static void EmptyDirectoryCompletely(File path) throws FileNotFoundException
	{
		File[] files = path.listFiles();
		if (files != null)
		{
			//some JVMs return null for empty dirs
			for (File f : files)
			{
				if (f.isDirectory())
				{
					EmptyDirectory(f);
				}
				else
				{
					f.delete();
				}
			}
		}
		path.delete();
	}
}
