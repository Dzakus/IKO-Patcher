package pl.patcher.iko;

import java.util.Arrays;

import android.os.Build;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class IKOPatcher implements IXposedHookLoadPackage {

	@Override
	public void handleLoadPackage(LoadPackageParam lpparam) throws Throwable {

		if (lpparam.packageName.equals("pl.pkobp.iko")
				|| lpparam.packageName.equals("pl.patcher.iko.tester")) {
			ClassLoader classLoader = lpparam.classLoader;
			if (BuildConfig.DEBUG)
				XposedBridge.log("Load: " + lpparam.packageName);

			// Replace command su to wu
			XposedHelpers.findAndHookMethod("java.lang.Runtime", classLoader,
					"exec", java.lang.String[].class, new XC_MethodHook() {
						protected void beforeHookedMethod(MethodHookParam param) {
							if (BuildConfig.DEBUG) {
								XposedBridge.log("exec:"
										+ Arrays.toString((String[]) param.args[0]));
								XposedBridge.log("exec:"
										+ ((String[]) param.args[0])[1]);
							}

							if (((String[]) param.args[0])[1].equals("su"))
								((String[]) param.args[0])[1] = "wu";
						}
					});
			// Disable Checking Debugger
			XposedHelpers.findAndHookMethod("android.os.Debug", // class
					classLoader, // classLoader
					"isDebuggerConnected", // function
					XC_MethodReplacement.returnConstant(false) // output
					);

			// Disable Checking test-keys
			if (!Build.TAGS.equals("release-keys")) {
				if (BuildConfig.DEBUG)
					XposedBridge.log("Tags:pre:" + Build.TAGS);

				XposedHelpers.setStaticObjectField(android.os.Build.class,
						"TAGS", "release-keys");

				if (BuildConfig.DEBUG)
					XposedBridge.log("Tags:post:" + Build.TAGS);
			}
		}
	}
}