package de.fb08.hsnr.path;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;

import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

public class Path extends CordovaPlugin {
	@Override
	public boolean execute(String action, JSONArray args,
			CallbackContext callbackContext) throws JSONException {
		if (action.equals("getContentPath")) {
			final String filePath = args.getString(0);
			final String contentPath = getContentPath(filePath, cordova);
			callbackContext.sendPluginResult(new PluginResult(
					PluginResult.Status.OK, contentPath));
			if (contentPath != null) {
				return true;
			}
		}
		return false;
	}

	public String getContentPath(String filePath, CordovaInterface cordova) {
		String contentPath = null;
		Cursor cursor = cordova
				.getActivity()
				.getContentResolver()
				.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
						new String[] { MediaStore.Images.Media._ID },
						MediaStore.Images.Media.DATA + "=? ",
						new String[] { filePath }, null);
		if (cursor != null && cursor.moveToFirst()) {
			int id = cursor.getInt(cursor
					.getColumnIndex(MediaStore.MediaColumns._ID));
			Uri baseUri = Uri.parse("content://media/external/images/media");
			Uri uri = Uri.withAppendedPath(baseUri, "" + id);
			contentPath = uri.toString();
		}
		return contentPath;
	}
}
