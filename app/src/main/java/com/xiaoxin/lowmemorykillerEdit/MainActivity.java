package com.xiaoxin.lowmemorykillerEdit;

import android.app.*;
import android.app.AlertDialog.*;
import android.content.*;
import android.net.*;
import android.os.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import java.io.*;
import java.util.*;

import android.view.View.OnClickListener;
import com.xiaoxin.util.*;
import com.xiaoxin.runtime.*;
public class MainActivity extends Activity 
{
	TextView support;
	TextView isModification ;
	Button button;
	Button reMButton;
	Button bakreturn;
	String links = "https://www.coolapk.com/apk/com.xiaoxin.lowmemorykillerEdit";
	Context context = null;
	boolean isRoot;
	/*
	 5120,7680,10240,12800,25600,32000
	 */

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		
		
		support = (TextView)findViewById(R.id.support);
		isModification = (TextView)findViewById(R.id.isModification);
		button = (Button)findViewById(R.id.modification);

		reMButton =  (Button)findViewById(R.id.mainButton2);
		bakreturn  = (Button)findViewById(R.id.mainButton1);
		
		context = getApplicationContext();

		handler.post(task);//立即调用
	
	}
	
	

	private Handler handler = new Handler();   
    private Runnable task = new Runnable() {  
        public void run() {   

			//写出busybox
			try
			{
				busyboxFilePath = File.separator + "system" + File.separator + "xbin" + File.separator + "busybox" + " ";
				if (new File(busyboxFilePath).exists() == false || new File(busyboxFilePath).length() == 0)
				{
					String abi = "arm";
					try
					{
						abi = getBuildProp().get("ro.product.cpu.abi").toString();
					}
					catch (Exception e)
					{
						toast(StackTraceToString(e));
					}
					boolean isX86 = "x86".equals(abi) || "x86_64".equals(abi);
					try
					{
						String tmpbusyboxFilePath = app.getPath(MainActivity.this) + File.separator + "busybox" + " ";
						app.writer(MainActivity.this, "busybox", readAssets(MainActivity.this, isX86 ?"busybox-arm": "busybox-arm"), Context.MODE_WORLD_READABLE + Context.MODE_WORLD_WRITEABLE);
						shellExecute("su", "chmod 0755 " + tmpbusyboxFilePath);
						shellExecute("su", tmpbusyboxFilePath + "mount -o rw,remount /system");
						shellExecute("su", tmpbusyboxFilePath + "cp " + tmpbusyboxFilePath + " " + busyboxFilePath);
						shellExecute("su", tmpbusyboxFilePath + "chmod 0755 " + busyboxFilePath);
						shellExecute("su", tmpbusyboxFilePath + "mount -o ro,remount /system");
					}
					catch (Exception e)
					{
						toast(MainActivity.this, StackTraceToString(e));
					}
				}
				if (new File(busyboxFilePath.trim()).exists() == false)
					toast(MainActivity.this, "Busybox 无法写出到 " + busyboxFilePath);
				try
				{
					app.remove(MainActivity.this, "busybox");
				}
				catch (Exception e)
				{
					//toast(StackTraceToString(e));
				}
			}
			catch (Exception e)
			{
				busyboxFilePath = "";
				toast(MainActivity.this, StackTraceToString(e));
			}


			//审查
			try
			{
				isRoot = isSu();
				if (isRoot == false)
					toast(MainActivity.this, "然而你并没有Root");
				else
				{
					mountSystem();
					chmod(post_bootFile, "0644");
				}
				check();
			}
			catch (Exception e)
			{
				toast(MainActivity.this, StackTraceToString(e));
			}
        }   
    };





	
	
	
	
	public void Modify(View v){
		try
		{
			if (isModification())
			{
				check();
				return;
			}
			if (Modification())
			{
				toast(getApplicationContext(), "修改成功，请重启手机");

				AlertDialog show = new Builder(MainActivity.this).setTitle("提示")
					.setMessage("立即重启？")
					.setPositiveButton("确定", new DialogInterface.OnClickListener(){
						@Override
						public void onClick(DialogInterface p1, int p2)
						{
							try
							{
								mountSystem();
								chmod(post_bootFile, "0644");
								mountSystemRo();
								shellExecute("su", "reboot");
							}
							catch (Exception e)
							{
								toast(getApplicationContext(), StackTraceToString(e));
							}
						}
					})
					.setNegativeButton("取消", new DialogInterface.OnClickListener(){
						@Override
						public void onClick(DialogInterface p1, int p2)
						{
							// TODO: Implement this method
						}
					})
					.show();
			}
			else
				toast(getApplicationContext(), "修改失败，原因未知");
			check();
		}
		catch (Exception e)
		{
			toast(getApplicationContext(), StackTraceToString(e));
		}
	}
	
	public void Update(View v){
		
		try
		{
			if (ghost())
			{
				Thread.sleep(500);
				if (Modification())
				{
					toast(getApplicationContext(), "修改成功，请重启手机");
					AlertDialog show = new Builder(MainActivity.this).setTitle("提示")
						.setMessage("立即重启？")
						.setPositiveButton("确定", new DialogInterface.OnClickListener(){
							@Override
							public void onClick(DialogInterface p1, int p2)
							{
								try
								{
									mountSystem();
									chmod(post_bootFile, "0644");
									mountSystemRo();
									shellExecute("su", "reboot");
								}
								catch (Exception e)
								{
									toast(getApplicationContext(), StackTraceToString(e));
								}
							}
						})
						.setNegativeButton("取消", new DialogInterface.OnClickListener(){
							@Override
							public void onClick(DialogInterface p1, int p2)
							{
								// TODO: Implement this method
							}
						})
						.show();

				}
				else
					toast(getApplicationContext(), "修改失败，原因未知");
			}

			check();
		}
		catch (Exception e)
		{
			toast(getApplicationContext(), StackTraceToString(e));
		}
		
		
		
	}
	
	
	public void Restore(View v){
		
		try
		{
			if (!isModification())
			{
				check();
				return;
			}
			if (ghost())
			{
				toast(getApplicationContext(), "修改成功，请重启手机");

				AlertDialog show = new Builder(MainActivity.this).setTitle("提示")
					.setMessage("立即重启？")
					.setPositiveButton("确定", new DialogInterface.OnClickListener(){
						@Override
						public void onClick(DialogInterface p1, int p2)
						{
							try
							{
								mountSystem();
								chmod(post_bootFile, "0644");
								mountSystemRo();
								shellExecute("su", "reboot");
							}
							catch (Exception e)
							{
								toast(getApplicationContext(), StackTraceToString(e));
							}
						}
					})
					.setNegativeButton("取消", new DialogInterface.OnClickListener(){
						@Override
						public void onClick(DialogInterface p1, int p2)
						{
							// TODO: Implement this method
						}
					})
					.show();
			}
			else
				toast(getApplicationContext(), "修改失败，原因未知");
			check();
		}
		catch (Exception e)
		{
			toast(getApplicationContext(), StackTraceToString(e));
		}
		
	}
	
	//菜单 
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}
	@Override
    public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case R.id.cool:
				try
				{
					openlink(MainActivity.this, links);
				}
				catch (Exception e)
				{
					toast(getApplicationContext(), StackTraceToString(e));
				}
				break;
        }
        return super.onOptionsItemSelected(item);
    }
	
	@Override
	protected void onDestroy()
	{
		// TODO: Implement this method
		try
		{
			mountSystemRo();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		super.onDestroy();
	}

	private static boolean support()
	{
		boolean Boolean = new File(post_bootFile).exists();
		return Boolean;
	}

	private static String post_bootFile = "/system/etc/init.qcom.post_boot.sh";
	private static String modificationlowmemorykillerS = "\n#lowmemorykillerEdit\n";
	private static String modificationlowmemorykiller = modificationlowmemorykillerS
	//+ "MemTotalStr=`cat /proc/meminfo | grep MemTotal`\nMemTotal=${MemTotalStr:16:8}\nMemTotalPg=$((MemTotal / 4))\n"
	+ "echo '" + "5120,7680,8192,10240,12800,19200" + "' > /sys/module/lowmemorykiller/parameters/minfree\n"
	+ "echo '" + "8192" + "' > /sys/module/lowmemorykiller/parameters/vmpressure_file_min\n" 
	+ "echo '" + "1" + "' > /sys/module/zcache/parameters/clear_percent\n"
	+ "echo '" + "0" + "' > /sys/module/lowmemorykiller/parameters/enable_adaptive_lmk\n"
	+ "echo '" + "0,64,128,196,764,1280" + "' > /sys/module/lowmemorykiller/parameters/adj\n"
	//+ "adjZeroMinFree=" + "5120\nclearPercent=$((((adjZeroMinFree * 100) / MemTotalPg) + 1))\necho $clearPercent > /sys/module/zcache/parameters/clear_percent\necho 30 >  /sys/module/zcache/parameters/max_pool_percent"
	+ modificationlowmemorykillerS;
	private static String storageDirectory = Environment.getExternalStorageDirectory().getPath();
	private static String busyboxFilePath = "";

	private void check() throws IOException, InterruptedException
	{
		boolean supportBoolean = support();
		chmod("/sys/module/lowmemorykiller/parameters/minfree", "0664");
		String minfree = cat("/sys/module/lowmemorykiller/parameters/minfree");
		setTitle(getText(R.string.app_name) + "(" + (supportBoolean ?"支持": "不支持") + ")");
		support.setText("当前数值:" + minfree);

		boolean isModificationBoolean = isModification();
		boolean isModification2Boolean = isModification2();
		isModification.setText("状态:" + (isModificationBoolean ?"已修改": "未修改") + "(" + ((!isModification2Boolean) ?"可更新修改": "不可更新修改") + ")");
		bakreturn.setClickable(isModificationBoolean);
		bakreturn.setVisibility(isModificationBoolean ?View.VISIBLE: View.INVISIBLE);
		
		button.setClickable(!isModificationBoolean);
		button.setVisibility((!isModificationBoolean) ?View.VISIBLE: View.INVISIBLE);
		
		reMButton.setClickable(!isModification2Boolean);
		reMButton.setVisibility((!isModification2Boolean) ?View.VISIBLE: View.INVISIBLE);
	}
	private static boolean isModification() throws FileNotFoundException, IOException
	{
		File File = new File(post_bootFile);
		if (!File.exists())
			return false;
		return new String(readFile(post_bootFile)).lastIndexOf(modificationlowmemorykillerS) > -1;
	}
	private static boolean isModification2() throws FileNotFoundException, IOException
	{
		File File = new File(post_bootFile);
		if (!File.exists())
			return false;
		return new String(readFile(post_bootFile)).lastIndexOf(modificationlowmemorykiller) > -1;
	}
	//修改
	private static boolean Modification() throws FileNotFoundException, IOException, InterruptedException
	{
		mountSystem();
		chmod(post_bootFile, "0644");

		File bak = new File(post_bootFile + ".bak_");
		//备份文件 如果备份文件不存在的话
		if (bak.exists() == false || bak.length() == 0)
			cp(post_bootFile, bak.getCanonicalPath());
		echo_appendFile(post_bootFile, modificationlowmemorykiller.getBytes());
		chmod(post_bootFile, "0644");
		return isModification();
	}
	//还原
	private static boolean ghost() throws FileNotFoundException, IOException, InterruptedException
	{
		mountSystem();
		chmod(post_bootFile, "0644");
		String post_bootContent = new String(readFile(post_bootFile));
		StringBuffer Result = new StringBuffer(post_bootContent);
		if (Result.toString().endsWith(modificationlowmemorykiller))
			Result.delete(Result.length() - modificationlowmemorykiller.length(), Result.length());
		int index = Result.indexOf(modificationlowmemorykillerS);
		if (index != -1)
		{
			int index2 = Result.lastIndexOf(modificationlowmemorykillerS);
			Result.delete(index, index2 == -1 ?Result.length(): index2 + modificationlowmemorykillerS.length());
		}
		Result = new StringBuffer(Result.toString().trim());
		File modificationlowmemorykillerFile = new File(storageDirectory + File.separator + "modificationlowmemorykiller.sh");
		new FileOutputStream(modificationlowmemorykillerFile).write(Result.toString().getBytes());

		cp(modificationlowmemorykillerFile.getAbsolutePath(), post_bootFile);
		chmod(post_bootFile, "0644");

		return !isModification();
	}


	public static String echo_appendFile(String file, byte[] bytes) throws InterruptedException, IOException
	{
		return shellExecute("su", busyboxFilePath + "echo -e -n " + "\"" + XRuntimeTool.toCommandHex(bytes) + "\"" + " >> " + "'" +  XEscape.escapeCommand(file) + "'");
	}
	public static String cat(String file) throws InterruptedException, IOException
	{
		return shellExecute("su", busyboxFilePath + "cat " + "'" +  XEscape.escapeCommand(file) + "'");
	}
	public static void cp(String file, String filenew) throws InterruptedException, IOException
	{
		shellExecute("su", busyboxFilePath + "cp -rf " + "'" + file + "'" + " " + "'" +  XEscape.escapeCommand(filenew) + "'");
	}
	public static void chmod(String file, String p) throws InterruptedException, IOException
	{
		p = p.trim();
		shellExecute("su", busyboxFilePath + "chmod " + p + " " + "'" +  XEscape.escapeCommand(file) + "'");
	}
	public static void mount(String device, String p) throws InterruptedException, IOException
	{
		p = p.trim();
		shellExecute("su", busyboxFilePath + "mount -o " + p + ",remount " + device);
	}
	public static String ls(String file) throws InterruptedException, IOException
	{
		return shellExecute("su", busyboxFilePath + "ls " +  "'" +  XEscape.escapeCommand(file) + "'");
	}


	public static void mountSystem() throws InterruptedException, IOException
	{
		mount("/system", "rw");
	}
	public static void mountSystemRo() throws InterruptedException, IOException
	{
		mount("/system", "ro");
	}
	private static boolean isSu() throws InterruptedException, IOException
	{
		return ls("/data").equals("") == false;
	}



	public static byte[] readAssets(Activity mainActivity, String str)
	{
        try
		{
            InputStream open = mainActivity.getAssets().open(str);
			ByteArrayOutputStream out = new ByteArrayOutputStream();
            writeStreamToFile(open, out);
			return out.toByteArray();
        }
		catch (IOException e)
		{
            e.printStackTrace();
            return null;
        }
    }
	public static boolean WriterAssets(Activity mainActivity, String str, OutputStream File2)
	{
        try
		{
            InputStream open = mainActivity.getAssets().open(str);
            return open != null && writeStreamToFile(open, File2);
        }
		catch (IOException e)
		{
            e.printStackTrace();
            return false;
        }
    }
	private static boolean writeStreamToFile(InputStream open, OutputStream file2) throws IOException
	{
		int buf = 8192;
		byte[] bufbytes = new byte[buf];
		int read;
		while ((read = open.read(bufbytes)) != -1)
			file2.write(bufbytes, 0, read);
		return true;
	}
	public static String StackTraceToString(Throwable th)
	{
		if (th == null)
		{
            return "";
        }
		Writer stringWriter = new StringWriter();
        PrintWriter printWriter2 = new PrintWriter(stringWriter);
        th.printStackTrace(printWriter2);
        return stringWriter.toString();
    }
	/*
	 arm64
	 armeabi

	 (0)、APP_ABI目前能取得值包括:
	 (1)、32位：armeabi、armeabi-v7a、x86、mips;
	 (2)、64位：arm64-v8a,x86_64, mips64;
	 */
	public static Properties getBuildProp()
	{
		try
		{
			InputStream in = new FileInputStream(new File("/system/build.prop"));
            Properties prop = new Properties();
            prop.load(in);
			in.close();
			return prop;
		}
		catch (Exception e)
		{
			return null;
		}
	}
	
	private void toast(Object str)
	{
		Toast.makeText(getApplicationContext(), str == null ?"null": str.toString(), Toast.LENGTH_SHORT).show();
	}
	
	
	private void toast(Context context, String str)
	{
		Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
	}
	private void openlink(Activity Super, String links)
	{
		Intent intent = new Intent();        
		intent.setAction("android.intent.action.VIEW");    
		Uri content_url = Uri.parse(links);   
		intent.setData(content_url);  
		Super.startActivity(intent);
	}
	
	private static void readFile(File file, OutputStream out) throws FileNotFoundException, IOException
	{
		InputStream in = new FileInputStream(file);
		try
		{
			int read = 0;
			byte bytes[] = new byte[8192];
			while ((read = in.read(bytes)) != -1)
				out.write(bytes, 0, read);
		}
		catch (Exception e)
		{
			return ;
		}
		finally
		{
			in.close();
		}
	}
	private static byte[] readFile(String file) throws FileNotFoundException, IOException
	{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		readFile(new File(file), out);
		return out.toByteArray();
	}
	private static String shellExecute(String user, String... str) throws IOException, InterruptedException
	{
		java.lang.Process proc = Runtime.getRuntime().exec(user);
		InputStream in = proc.getInputStream();
		OutputStream out = proc.getOutputStream();
		InputStream error = proc.getErrorStream();
		StringBuffer Result = new StringBuffer();
		for (int i = 0;i < str.length;i++)
		{
			out.write(new String((str[i] + "\n").getBytes(), "utf-8").getBytes());
			out.flush();
		}
		out.write("exit\n".getBytes());
		out.flush();
		proc.waitFor();
		int read = 0;
		byte bytes[] = new byte[8192];
		while ((read = in.read(bytes)) != -1)
			Result.append(new String(bytes, 0, read, "utf-8"));
		return Result.toString();
	}
}
