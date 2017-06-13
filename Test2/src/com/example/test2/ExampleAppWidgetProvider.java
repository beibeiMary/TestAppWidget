package com.example.test2;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

public class ExampleAppWidgetProvider extends AppWidgetProvider {
	private static final String TAG = "zhsy";
	private boolean DEBUG = false;
	// ����ExampleAppWidgetService�����Ӧ��action
	private final Intent EXAMPLE_SERVICE_INTENT = new Intent("android.appwidget.action.EXAMPLE_APP_WIDGET_SERVICE");
	// ���� widget �Ĺ㲥��Ӧ��action
	private final String ACTION_UPDATE_ALL = "com.example.test2.UPDATE_ALL";
	// ���� widget ��id��HashSet��ÿ�½�һ�� widget ����Ϊ�� widget ����һ�� id��
	private static Set idsSet = new HashSet();
	// ��ť��Ϣ
	private static final int BUTTON_SHOW = 1;
	// ͼƬ����
	private static final int[] ARR_IMAGES = { R.drawable.sample_0, R.drawable.sample_1, R.drawable.sample_2,
			R.drawable.sample_3, R.drawable.sample_4, R.drawable.sample_5, };

	// onUpdate() �ڸ��� widget ʱ����ִ�У�
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		Log.d(TAG, "onUpdate(): appWidgetIds.length=" + appWidgetIds.length);

		// ÿ�� widget ������ʱ����Ӧ�Ľ�widget��id��ӵ�set��
		for (int appWidgetId : appWidgetIds) {
			idsSet.add(Integer.valueOf(appWidgetId));
		}
		prtSet();
	}

	// �� widget ��������� ���� �� widget �Ĵ�С���ı�ʱ��������
	@SuppressLint("NewApi")
	@Override
	public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId,
			Bundle newOptions) {
		Log.d(TAG, "onAppWidgetOptionsChanged");
		super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
	}

	// widget��ɾ��ʱ����
	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		Log.d(TAG, "onDeleted(): appWidgetIds.length=" + appWidgetIds.length);

		// �� widget ��ɾ��ʱ����Ӧ��ɾ��set�б����widget��id
		for (int appWidgetId : appWidgetIds) {
			idsSet.remove(Integer.valueOf(appWidgetId));
		}
		prtSet();

		super.onDeleted(context, appWidgetIds);
	}

	// ��һ��widget������ʱ����
	@Override
	public void onEnabled(Context context) {
		Log.d(TAG, "onEnabled");
		// �ڵ�һ�� widget ������ʱ����������
		context.startService(EXAMPLE_SERVICE_INTENT);

		super.onEnabled(context);
	}

	// ���һ��widget��ɾ��ʱ����
	@Override
	public void onDisabled(Context context) {
		Log.d(TAG, "onDisabled");

		// �����һ�� widget ��ɾ��ʱ����ֹ����
		context.stopService(EXAMPLE_SERVICE_INTENT);

		super.onDisabled(context);
	}

	// ���չ㲥�Ļص�����
	@Override
	public void onReceive(Context context, Intent intent) {

		final String action = intent.getAction();
		Log.d(TAG, "OnReceive:Action: " + action);
		if (ACTION_UPDATE_ALL.equals(action)) {
			// �����¡��㲥
			updateAllAppWidgets(context, AppWidgetManager.getInstance(context), idsSet);
		} else if (intent.hasCategory(Intent.CATEGORY_ALTERNATIVE)) {
			// ����ť������㲥
			Uri data = intent.getData();
			int buttonId = Integer.parseInt(data.getSchemeSpecificPart());
			if (buttonId == BUTTON_SHOW) {
				Log.d(TAG, "Button wifi clicked");
				Toast.makeText(context, "Button Clicked", Toast.LENGTH_SHORT).show();
			}
		}

		super.onReceive(context, intent);
	}

	// �������е� widget
	private void updateAllAppWidgets(Context context, AppWidgetManager appWidgetManager, Set set) {

		Log.d(TAG, "updateAllAppWidgets(): size=" + set.size());

		// widget ��id
		int appID;
		// �����������ڱ������б����widget��id
		Iterator it = set.iterator();

		while (it.hasNext()) {
			appID = ((Integer) it.next()).intValue();
			// �����ȡһ��ͼƬ
			int index = (new java.util.Random().nextInt(ARR_IMAGES.length));

			if (DEBUG)
				Log.d(TAG, "onUpdate(): index=" + index);
			// ��ȡ example_appwidget.xml ��Ӧ��RemoteViews
			RemoteViews remoteView = new RemoteViews(context.getPackageName(), R.layout.example_appwidget);

			// ������ʾͼƬ
			remoteView.setImageViewResource(R.id.iv_show, ARR_IMAGES[index]);

			// ���õ����ť��Ӧ��PendingIntent���������ťʱ�����͹㲥��
			remoteView.setOnClickPendingIntent(R.id.btn_show, getPendingIntent(context, BUTTON_SHOW));

			// ���� widget
			appWidgetManager.updateAppWidget(appID, remoteView);
		}
	}

	private PendingIntent getPendingIntent(Context context, int buttonId) {
		Intent intent = new Intent();
		intent.setClass(context, ExampleAppWidgetProvider.class);
		intent.addCategory(Intent.CATEGORY_ALTERNATIVE);
		intent.setData(Uri.parse("custom:" + buttonId));
		PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
		return pi;
	}

	// �����ã�����set
	private void prtSet() {
		if (DEBUG) {
			int index = 0;
			int size = idsSet.size();
			Iterator it = idsSet.iterator();
			Log.d(TAG, "total:" + size);
			while (it.hasNext()) {
				Log.d(TAG, index + " -- " + ((Integer) it.next()).intValue());
			}
		}
	}
}
