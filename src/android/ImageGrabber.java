package de.fb08.hsnr.imageGrabber;

import java.io.FileNotFoundException;
import java.io.IOException;
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

// TODO: Code should be optimized
public class ImageGrabber extends CordovaPlugin {
	@Override
	public boolean execute(String action, JSONArray args,
			CallbackContext callbackContext) {
		try {
			if (action.equals("getImages")) {
				final JSONArray images = new JSONArray();
				final ContentResolver contentResolver = cordova.getActivity()
						.getContentResolver();
				getImages(true, contentResolver, images);
				getImages(false, contentResolver, images);
				callbackContext.sendPluginResult(new PluginResult(
						PluginResult.Status.OK, images));
				return true;
			} else if (action.equals("getImageData")) {
				final String imageUri = args.getString(0);
				final ContentResolver contentResolver = cordova.getActivity()
						.getContentResolver();
				JSONObject image = getImageData(true, contentResolver, imageUri);
				if (image == null) {
					image = getImageData(false, contentResolver, imageUri);
				}
				if (image == null) {
					callbackContext.sendPluginResult(new PluginResult(
							PluginResult.Status.ERROR, "No Data found"));
				} else {
					callbackContext.sendPluginResult(new PluginResult(
							PluginResult.Status.OK, image));
				}
				return true;
			} else {
				return false;
			}
		} catch (final Exception e) {
			return false;
		}
	}

	private void getImages(final boolean internal,
			final ContentResolver contentResolver, final JSONArray images)
			throws JSONException, FileNotFoundException, IOException {
		final Uri imagesUri;
		final Uri thumbnailsUri;
		if (internal) {
			// content://media/internal/images/media/
			imagesUri = MediaStore.Images.Media.INTERNAL_CONTENT_URI;
			// content://media/internal/images/thumbnails/
			thumbnailsUri = MediaStore.Images.Thumbnails.INTERNAL_CONTENT_URI;
		} else {
			// content://media/external/images/media/
			imagesUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
			// content://media/external/images/thumbnails/
			thumbnailsUri = MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI;
		}
		// Columns for images
		final String[] imagesProjection = { MediaStore.Images.ImageColumns._ID,
				MediaStore.Images.ImageColumns.TITLE,
				MediaStore.Images.ImageColumns.LATITUDE,
				MediaStore.Images.ImageColumns.LONGITUDE,
				MediaStore.Images.ImageColumns.DATE_TAKEN };
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
			final String title = imagesCursor.getString(1);
			final String latitude = imagesCursor.getString(2);
			final String longitude = imagesCursor.getString(3);
			final SimpleDateFormat dateFormat = new SimpleDateFormat(
					"dd.MM.yyyy - hh:mm:ss", Locale.GERMANY);
			final String date = dateFormat.format(new Date(new Timestamp(
					imagesCursor.getLong(4)).getTime()));
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
			image.put("contentPath", imagesUri.toString());
			image.put("title", title);
			image.put("latitude", latitude);
			image.put("longitude", longitude);
			image.put("date", date);
			image.put("miniId", miniId);
			image.put("microId", microId);
			image.put("thumbnailPath", thumbnailsUri.toString());
			images.put(image);
			imagesCursor.moveToNext();
		}
	}

	private JSONObject getImageData(final boolean internal,
			final ContentResolver contentResolver, final String imageUri)
			throws JSONException {
		final Uri imagesUri;
		final Uri thumbnailsUri;
		if (internal) {
			// content://media/internal/images/media/
			imagesUri = MediaStore.Images.Media.INTERNAL_CONTENT_URI;
			// content://media/internal/images/thumbnails/
			thumbnailsUri = MediaStore.Images.Thumbnails.INTERNAL_CONTENT_URI;
		} else {
			// content://media/external/images/media/
			imagesUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
			// content://media/external/images/thumbnails/
			thumbnailsUri = MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI;
		}
		final String filePath = imageUri.substring("file://".length());
		final String[] imageProjection = { MediaStore.Images.ImageColumns._ID,
				MediaStore.Images.ImageColumns.TITLE,
				MediaStore.Images.ImageColumns.LATITUDE,
				MediaStore.Images.ImageColumns.LONGITUDE,
				MediaStore.Images.ImageColumns.DATE_TAKEN };
		Cursor cursor = contentResolver.query(imagesUri, imageProjection,
				MediaStore.Images.Media.DATA + "=? ",
				new String[] { filePath }, null);
		if (cursor.getCount() <= 0) {
			return null;
		}
		cursor.moveToFirst();
		final long imageId = cursor.getLong(0);
		final String title = cursor.getString(1);
		final String latitude = cursor.getString(2);
		final String longitude = cursor.getString(3);
		final SimpleDateFormat dateFormat = new SimpleDateFormat(
				"dd.MM.yyyy - hh:mm:ss", Locale.GERMANY);
		final String date = dateFormat.format(new Date(new Timestamp(cursor
				.getLong(4)).getTime()));
		final String[] thumbnailsProjection = {
				MediaStore.Images.Thumbnails._ID,
				MediaStore.Images.Thumbnails.KIND };
		cursor = contentResolver.query(thumbnailsUri, thumbnailsProjection,
				MediaStore.Images.Thumbnails.IMAGE_ID + "=? ",
				new String[] { String.valueOf(imageId) }, null);
		Long miniId = null;
		Long microId = null;
		for (int j = 0, jLen = cursor.getCount(); j < jLen; j++) {
			final long thumbnailImageId = cursor.getLong(1);
			if (thumbnailImageId == imageId) {
				final int kind = cursor.getInt(2);
				if (kind == MediaStore.Images.Thumbnails.MINI_KIND) {
					miniId = cursor.getLong(0);
				} else if (kind == MediaStore.Images.Thumbnails.MICRO_KIND) {
					microId = cursor.getLong(0);
				}
				break;
			}
			cursor.moveToNext();
		}
		final JSONObject image = new JSONObject();
		image.put("id", imageId);
		image.put("contentPath", imagesUri.toString());
		image.put("title", title);
		image.put("latitude", latitude);
		image.put("longitude", longitude);
		image.put("date", date);
		image.put("miniId", miniId);
		image.put("microId", microId);
		image.put("thumbnailPath", thumbnailsUri.toString());
		return image;
	}
}
