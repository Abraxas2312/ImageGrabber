package de.fb08.hsnr.path;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

public class Path extends CordovaPlugin {

	@Override
	public boolean execute(String action, JSONArray args,
			CallbackContext callbackContext) throws JSONException {
		if (action.equals("getContentPaths")) {
			final String contentPath = getContentPaths(true, cordova
					.getActivity().getContentResolver())
					+ "#"
					+ getContentPaths(false, cordova.getActivity()
							.getContentResolver());
			if (contentPath != null) {
				callbackContext.sendPluginResult(new PluginResult(
						PluginResult.Status.OK, contentPath));
				return true;
			}

		}
		return false;
	}

	private String getContentPaths(final boolean internal,
			final ContentResolver contentResolver) {
		try {
			final Uri imagesUri;
			if (internal) {
				// should be something like
				// content://media/internal/images/media/
				imagesUri = MediaStore.Images.Media.getContentUri("internal");
			} else {
				// should be something like
				// content://media/external/images/media/
				imagesUri = MediaStore.Images.Media.getContentUri("external");
			}
			// ID column
			final String[] projection = { MediaStore.Images.ImageColumns._ID };
			// Get all images ids from the library
			final Cursor cursor = contentResolver.query(imagesUri, projection,
					null, null, null);
			final StringBuilder stringBuilder = new StringBuilder("");
			cursor.moveToFirst();
			for (int i = 0, len = cursor.getCount(); i < len; i++) {
				stringBuilder.append(imagesUri.toString() + "/"
						+ cursor.getLong(0) + "#");
				cursor.moveToNext();
			}
			// clean up last #
			stringBuilder.deleteCharAt(stringBuilder.lastIndexOf("#"));
			cursor.close();
			return stringBuilder.toString();
		} catch (final Exception e) {
			return null;
		}
	}

}
