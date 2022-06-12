package com.fxyublib.android.baseutillib.Activity;

import android.app.ActivityManager;
import android.content.*;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
import android.widget.LinearLayout;
import android.widget.EditText;
import android.view.ViewGroup;
import android.view.View;
import android.view.KeyEvent;
import android.view.inputmethod.InputMethodManager;
import android.text.InputFilter;
import android.text.InputType;
import android.graphics.Color;
import java.util.*;

public class ActivityUtils
{
	private static String Tag = "ActivityUtils";
	public static PackageManager mPackageManager;
	public static Activity mActivity = null;

	/**
	 * 启动组件
	 *
	 * @param componentName 组件名
	 */
	private static void enableComponent(ComponentName componentName) {
		//此方法用以启用和禁用组件，会覆盖Androidmanifest文件下定义的属性
		mPackageManager.setComponentEnabledSetting(componentName,
				PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
				PackageManager.DONT_KILL_APP);
	}

	/**
	 * 禁用组件
	 *
	 * @param componentName 组件名
	 */
	private static void disableComponent(ComponentName componentName) {
		mPackageManager.setComponentEnabledSetting(componentName,
				PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
				PackageManager.DONT_KILL_APP);
	}

	/**
	 * 当前组件的状态,判断当前enable状态
	 * 即使xml里面设置enable=false 标志位第一次获取时 还是COMPONENT_ENABLED_STATE_DEFAULT
	 * 所以这里判断是否为enable
	 *
	 * @param componentName return true 未被应用为可显示
	 */
	private static boolean isComponentState(ComponentName componentName, String defaultIcon) {
		//默认图标且为默认状态则返回false
		return !(defaultIcon.equals(componentName.getClassName())
				&& mPackageManager.getComponentEnabledSetting(componentName) == PackageManager.COMPONENT_ENABLED_STATE_DEFAULT)
				&& mPackageManager.getComponentEnabledSetting(componentName) != PackageManager.COMPONENT_ENABLED_STATE_ENABLED;
	}

	/**
	 * 更换app 图标
	 *
	 * @param context
	 * @param currentIcon
	 * @param changeName
	 */
	public static void changeIconState(Context context, ComponentName currentIcon, String changeName) {
		ComponentName otherIcon = new ComponentName(context, changeName);
		if (!currentIcon.getClassName().equals(changeName)) {
			enableComponent(otherIcon);
			disableComponent(currentIcon);
		}
	}

	public static  void toastMessage(String msg) {
		Toast.makeText(mActivity, msg, Toast.LENGTH_SHORT).show();
	}

	public static void showToast(String text) {
        Toast toast = Toast.makeText(mActivity,text,Toast.LENGTH_SHORT);
        toast.setGravity(android.view.Gravity.CENTER,0,0);
        toast.show();
    }

	public static void setLinearLayout(View view, int w, int h) {
		LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
				w == 0 ? ViewGroup.LayoutParams.MATCH_PARENT : w, h == 0 ? ViewGroup.LayoutParams.WRAP_CONTENT : h);
		param.setMargins(0, 0, 0, 0);
		view.setLayoutParams(param);
	}

	public static void setLinearLayout(View view, int w, int h, int gravity) {
		LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
				w == 0 ? ViewGroup.LayoutParams.MATCH_PARENT : w, h == 0 ? ViewGroup.LayoutParams.WRAP_CONTENT : h);
		param.gravity = gravity;
		param.setMargins(0, 0, 0, 0);
		view.setLayoutParams(param);
	}

	public static void setLinearLayout(View view, int left, int top, int right, int bottom) {
		LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		param.setMargins(left, top, right, bottom);
		view.setLayoutParams(param);
	}

	public static void setEtCoustomLength(EditText ev, int length) {
		if (length > 0) {
			ev.setFilters(new InputFilter[] { new InputFilter.LengthFilter(length) });
		}
	}

	public static void setEditTextHint(final EditText view, String text) {
		view.setHint(text);
		view.setHintTextColor(Color.LTGRAY);
		view.setOnClickListener(new android.view.View.OnClickListener() {
			public void onClick(View v) {
				view.setHint(null);
			}
		});
	}

	public static void setEditTextReadOnly(EditText view, Boolean isEnableSelected) {
		//view.setTextColor(Color.GRAY);
		view.setKeyListener(null);
		view.setTextIsSelectable(true);
		view.setInputType(InputType.TYPE_NULL);
	}

	public static void setEditTextReadOnly(EditText view, int c) {
		view.setTextColor(c);
		view.setOnKeyListener(new android.view.View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				return true;
			}
		}); //consume key input
		view.setInputType(InputType.TYPE_NULL);
	}

	public static void setEditTextReadOnly(EditText view) {
		view.setTextColor(Color.GRAY);
		if (view instanceof android.widget.EditText) {
			view.setCursorVisible(false);
			view.setFocusable(false);
			view.setFocusableInTouchMode(false);
		}
	}

	public static boolean copyToClipboard(Activity act, String copyStr) {
		try {
			ClipboardManager cm = (ClipboardManager) act.getSystemService(Context.CLIPBOARD_SERVICE);
			ClipData mClipData = ClipData.newPlainText("Label", copyStr);
			cm.setPrimaryClip(mClipData);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
   	public static void copyToClipboard(Context context, String text) {
		try {
			ClipboardManager cmb = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
			cmb.setText(text.trim());
		} catch (Exception e) {
		}
	}

   	public static String copyFromClipboard(Context context) {
		try {
			ClipboardManager cmb = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
			return cmb.getText().toString().trim();
		} catch (Exception e) {
			return "";
		}
	}

    public static void showInput(Context context, final EditText et) {
        et.requestFocus();
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(et, InputMethodManager.SHOW_IMPLICIT);
    }

    public static void hideInput(Context context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        //View v = getWindow().peekDecorView();
		View v = mActivity.getCurrentFocus();
        if (null != v) {
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }

    public static void hideSoftKeyboard(Activity activity) {
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public static void hideSoftKeyboard(Context context, List<View> viewList) {
        if (viewList == null) return;

        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);

        for (View v : viewList) {
            inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

	//防止重复点击 事件间隔，在这里我定义的是1000毫秒
	private static long lastClickTime = 0;
	public static boolean isFastDoubleClick() {
		long time = System.currentTimeMillis();
		long timeD = time - lastClickTime;

		if (timeD >= 0 && timeD <= 500) {
			return true;
		} else {
			lastClickTime = time;
			return false;
		}
	}

	public static void setViewMargins (View view, int left, int top, int right, int bottom) {
		if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
			ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
			p.setMargins(left, top, right, bottom);
			view.requestLayout();
		}
	}

	//dip转像素
	public static int DipToPixels(Context context,int dip) {
		final float SCALE = context.getResources().getDisplayMetrics().density;
		float valueDips =  dip;
		int valuePixels = (int)(valueDips * SCALE + 0.5f);
		return valuePixels;

	}


	//像素转dip
	public static float PixelsToDip(Context context,int Pixels) {
		final float SCALE = context.getResources().getDisplayMetrics().density;
		float dips =Pixels / SCALE ;
		return dips;

	}

	/**
	 * 判断某个界面是否在前台
	 *
	 * @param activity 要判断的Activity
	 * @return 是否在前台显示
	 */
	public static boolean isForeground(Activity activity) {
		return isForeground(activity, activity.getClass().getName());
	}

	/**
	 * 判断某个界面是否在前台
	 *
	 * @param context   Context
	 * @param className 界面的类名
	 * @return 是否在前台显示
	 */
	public static boolean isForeground(Context context, String className) {
		if (context == null || TextUtils.isEmpty(className))
			return false;
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(1);
		if (list != null && list.size() > 0) {
			ComponentName cpn = list.get(0).topActivity;
			if (className.equals(cpn.getClassName()))
				return true;
		}
		return false;
	}
}
