package de.fb08.hsnr.path;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

public class ImageGrabber extends CordovaPlugin {

	@Override
	public boolean execute(String action, JSONArray args,
			CallbackContext callbackContext) throws JSONException {
		if (action.equals("getImages")) {
			final JSONArray images = new JSONArray();
			getImages(true, cordova.getActivity().getContentResolver(),
					images);
			getImages(false, cordova.getActivity().getContentResolver(),
					images);
			callbackContext.sendPluginResult(new PluginResult(
					PluginResult.Status.OK, images));
		}
		return false;
	}

	private void getImages(final boolean internal,
			final ContentResolver contentResolver, final JSONArray images)
			throws JSONException {
		final Uri imagesUri;
		final Uri thumbnailsUri;
		if (internal) {
			// content://media/internal/images/media/
			imagesUri = MediaStore.Images.Media.getContentUri("internal");
			// content://media/internal/images/thumbnails/
			thumbnailsUri = MediaStore.Images.Thumbnails
					.getContentUri("internal");
		} else {
			// content://media/external/images/media/
			imagesUri = MediaStore.Images.Media.getContentUri("external");
			// content://media/external/images/thumbnails/
			thumbnailsUri = MediaStore.Images.Thumbnails
					.getContentUri("external");
		}
		// Columns for images
		final String[] imagesProjection = { MediaStore.Images.ImageColumns._ID,
				MediaStore.Images.ImageColumns.DATA,
				MediaStore.Images.ImageColumns.TITLE,
				MediaStore.Images.ImageColumns.LATITUDE,
				MediaStore.Images.ImageColumns.LONGITUDE,
				MediaStore.Images.ImageColumns.DATE_TAKEN,
				MediaStore.Images.ImageColumns.ORIENTATION };
		// Images query
		final Cursor imagesCursor = contentResolver.query(imagesUri,
				imagesProjection, null, null, null);
		imagesCursor.moveToFirst();
		// Columns for thumbnails
		final String[] thumbnailsProjection = {
				MediaStore.Images.Thumbnails._ID,
				MediaStore.Images.Thumbnails.IMAGE_ID,
				MediaStore.Images.Thumbnails.KIND };
		// Thumbnails query
		final Cursor thumbnailsCursor = contentResolver.query(thumbnailsUri,
				thumbnailsProjection, null, null, null);
		thumbnailsCursor.moveToFirst();
		for (int i = 0, iLen = imagesCursor.getCount(); i < iLen; i++) {
			final long imageId = imagesCursor.getLong(0);
			final String dataPath = imagesCursor.getString(1);
			final String title = imagesCursor.getString(2);
			final String latitude = imagesCursor.getString(3);
			final String longitude = imagesCursor.getString(4);
			final SimpleDateFormat dateFormat = new SimpleDateFormat(
					"dd.MM.yyyy - hh:mm:ss", Locale.GERMANY);
			final String date = dateFormat.format(new Date(new Timestamp(
					imagesCursor.getLong(5)).getTime()));
			final int orientation = imagesCursor.getInt(6);
			Long miniId = null;
			Long microId = null;
			for (int j = 0, jLen = thumbnailsCursor.getCount(); j < jLen; j++) {
				final long thumbnailImageId = thumbnailsCursor.getLong(1);
				if (thumbnailImageId == imageId) {
					final int kind = thumbnailsCursor.getInt(2);
					if (kind == MediaStore.Images.Thumbnails.MINI_KIND) {
						miniId = thumbnailsCursor.getLong(0);
					} else if (kind == MediaStore.Images.Thumbnails.MICRO_KIND) {
						microId = thumbnailsCursor.getLong(0);
					}
					break;
				}
				thumbnailsCursor.moveToNext();
			}
			thumbnailsCursor.moveToFirst();
			final JSONObject image = new JSONObject();
			image.put("id", imageId);
			image.put("contentPath", imagesUri.toString() + imageId);
			image.put("dataPath", dataPath);
			image.put("title", title);
			image.put("latitude", latitude);
			image.put("longitude", longitude);
			image.put("date", date);
			image.put("orientation", orientation);
			image.put("miniId", miniId);
			image.put("microId", microId);
			image.put("thumbnailPath", thumbnailsUri.toString());
			images.put(image);
			imagesCursor.moveToNext();
		}
	}
}
