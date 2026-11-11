package com.forddrive.litek2001n;

import android.app.*;
import android.os.*;
import android.content.*;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.provider.Settings;
import android.view.*;
import android.widget.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class MainActivity extends Activity {
  LinearLayout base, toolbar, actions; TextView clock, date; Handler handler=new Handler();
  Runnable tick; int bg=Color.rgb(9,18,33), panel=Color.rgb(21,38,59), cyan=Color.rgb(91,200,245);
  int dp(float n){return (int)(n*getResources().getDisplayMetrics().density+.5f);}
  GradientDrawable shape(int color){GradientDrawable g=new GradientDrawable();g.setColor(color);g.setCornerRadius(dp(17));g.setStroke(dp(1),Color.rgb(54,80,106));return g;}
  TextView text(String s,int size,int color){TextView t=new TextView(this);t.setText(s);t.setTextSize(size);t.setTextColor(color);t.setGravity(Gravity.CENTER);t.setTypeface(Typeface.DEFAULT,Typeface.BOLD);return t;}
  @Override public void onCreate(Bundle b){super.onCreate(b);getWindow().getDecorView().setSystemUiVisibility(5894|1024|512);render();}
  void render(){
    base=new LinearLayout(this);base.setOrientation(LinearLayout.VERTICAL);base.setBackgroundColor(bg);base.setPadding(dp(22),dp(12),dp(22),dp(12));setContentView(base);
    toolbar=new LinearLayout(this);toolbar.setGravity(Gravity.CENTER_VERTICAL);base.addView(toolbar,new LinearLayout.LayoutParams(-1,dp(53)));
    TextView title=text("FORD  •  DRIVE LITE",21,cyan);toolbar.addView(title,new LinearLayout.LayoutParams(0,-1,1));
    clock=text("",25,Color.WHITE);toolbar.addView(clock,new LinearLayout.LayoutParams(dp(145),-1));
    date=text("",13,Color.LTGRAY);toolbar.addView(date,new LinearLayout.LayoutParams(dp(185),-1));
    LinearLayout center=new LinearLayout(this);center.setGravity(Gravity.CENTER);base.addView(center,new LinearLayout.LayoutParams(-1,0,1));
    ImageView car=new ImageView(this);car.setImageResource(R.drawable.car);car.setScaleType(ImageView.ScaleType.FIT_CENTER);center.addView(car,new LinearLayout.LayoutParams(-1,-1));
    actions=new LinearLayout(this);actions.setGravity(Gravity.CENTER);base.addView(actions,new LinearLayout.LayoutParams(-1,dp(94)));
    addButton("التطبيقات",()->apps());addButton("الملاحة",()->launchMap());addButton("الموسيقى",()->launchCategory("android.intent.category.APP_MUSIC"));addButton("البلوتوث",()->openSettings(Settings.ACTION_BLUETOOTH_SETTINGS));addButton("الشبكة",()->openSettings(Settings.ACTION_WIFI_SETTINGS));addButton("الإعدادات",()->settings());
    updateClock();
  }
  void addButton(String s,Runnable action){TextView b=text(s,17,Color.WHITE);b.setBackground(shape(panel));b.setFocusable(true);b.setClickable(true);b.setOnClickListener(v->action.run());b.setOnFocusChangeListener((v,f)->{b.setBackground(shape(f?Color.rgb(32,96,132):panel));b.setTextColor(f?Color.WHITE:Color.LTGRAY);});LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(0,dp(68),1);p.setMargins(dp(5),0,dp(5),0);actions.addView(b,p);}
  void updateClock(){Calendar now=Calendar.getInstance();Locale ar=new Locale("ar","SA");clock.setText(new SimpleDateFormat("hh:mm a",ar).format(now.getTime()));date.setText(new SimpleDateFormat("EEEE  yyyy/MM/dd",ar).format(now.getTime()));}
  @Override protected void onResume(){super.onResume();if(tick!=null)handler.removeCallbacks(tick);tick=new Runnable(){public void run(){updateClock();handler.postDelayed(this,60000);}};handler.post(tick);}
  @Override protected void onPause(){if(tick!=null)handler.removeCallbacks(tick);super.onPause();}
  @Override protected void onDestroy(){if(tick!=null)handler.removeCallbacks(tick);super.onDestroy();}
  void openSettings(String a){try{startActivity(new Intent(a));}catch(Exception e){toast("الإعداد غير متاح على هذا الجهاز");}}
  void launchMap(){Intent i=new Intent(Intent.ACTION_VIEW,android.net.Uri.parse("geo:0,0?q="));try{startActivity(i);}catch(Exception e){toast("لا يوجد تطبيق ملاحة متوافق");}}
  void launchCategory(String cat){Intent i=new Intent(Intent.ACTION_MAIN);i.addCategory(cat);try{startActivity(i);}catch(Exception e){apps();}}
  void toast(String s){Toast.makeText(this,s,Toast.LENGTH_SHORT).show();}
  void apps(){
    Intent query=new Intent(Intent.ACTION_MAIN);query.addCategory(Intent.CATEGORY_LAUNCHER);
    java.util.List<android.content.pm.ResolveInfo> all=getPackageManager().queryIntentActivities(query,0);
    Collections.sort(all,new android.content.pm.ResolveInfo.DisplayNameComparator(getPackageManager()));
    ArrayList<String> labels=new ArrayList<>();ArrayList<Intent> intents=new ArrayList<>();
    for(android.content.pm.ResolveInfo r:all){String pkg=r.activityInfo.packageName;if(pkg.equals(getPackageName()))continue;labels.add(r.loadLabel(getPackageManager()).toString());Intent x=new Intent(Intent.ACTION_MAIN);x.addCategory(Intent.CATEGORY_LAUNCHER);x.setClassName(pkg,r.activityInfo.name);intents.add(x);}
    if(labels.isEmpty()){toast("لا توجد تطبيقات متاحة");return;}
    new AlertDialog.Builder(this).setTitle("التطبيقات المثبتة").setItems(labels.toArray(new String[0]),(d,which)->{try{startActivity(intents.get(which));}catch(Exception e){toast("تعذر فتح التطبيق");}}).setNegativeButton("إغلاق",null).show();
  }
  void settings(){String[] items={"إعدادات أندرويد","اختيار الواجهة الرئيسية","إعادة تشغيل الواجهة","معلومات النسخة"};new AlertDialog.Builder(this).setTitle("إعدادات Ford Drive Lite").setItems(items,(d,n)->{switch(n){case 0:openSettings(Settings.ACTION_SETTINGS);break;case 1:openSettings(Settings.ACTION_HOME_SETTINGS);break;case 2:render();break;default:new AlertDialog.Builder(this).setMessage("نسخة تجريبية 1.0 • K2001N • Android 10 • بدون WebView").setPositiveButton("حسنًا",null).show();}}).setNegativeButton("رجوع",null).show();}
  @Override public void onBackPressed(){new AlertDialog.Builder(this).setMessage("فتح إعدادات اختيار الواجهة الرئيسية؟").setPositiveButton("فتح",(d,w)->openSettings(Settings.ACTION_HOME_SETTINGS)).setNegativeButton("إلغاء",null).show();}
}