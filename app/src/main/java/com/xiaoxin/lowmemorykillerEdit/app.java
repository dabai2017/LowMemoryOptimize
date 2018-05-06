package com.xiaoxin.lowmemorykillerEdit;
import android.app.*;
import java.io.*;

public class app
{
    public static void writer(Activity mainActivity, String str, byte[] bArr, int i)
	{
        try
		{
            FileOutputStream openFileOutput = mainActivity.getApplicationContext().openFileOutput(str, i);
            openFileOutput.write(bArr);
            openFileOutput.close();
        }
		catch (Exception e)
		{
            e.printStackTrace();
        }
    }
    public static void remove(Activity mainActivity, String str)
	{
        try
		{
            mainActivity.getApplicationContext().deleteFile(str);
        }
		catch (Exception e)
		{
            e.printStackTrace();
        }
    }

    public static String[] getList(Activity mainActivity)
	{
        try
		{
            return mainActivity.getApplicationContext().fileList();
        }
		catch (Exception e)
		{
            e.printStackTrace();
            return null;
        }
    }

    public static String getPath(Activity mainActivity)
	{
        try
		{
            return mainActivity.getApplicationContext().getFilesDir().getAbsolutePath();
        }
		catch (Exception e)
		{
            e.printStackTrace();
			return "";
        }
    }

	
	public static File getFile(Activity mainActivity, String str)
	{
        try
		{
            return new File(mainActivity.getApplicationContext().getFilesDir(),str);
        }
		catch (Exception e)
		{
            return null;
        }
    }
    public static byte[] read(Activity mainActivity, String str)
	{
        try
		{
            FileInputStream openFileInput = mainActivity.getApplicationContext().openFileInput(str);
            byte[] bArr = new byte[openFileInput.available()];
            openFileInput.read(bArr);
            return bArr;
        }
		catch (Exception e)
		{
            e.printStackTrace();
            return new byte[0];
        }
    }
}
