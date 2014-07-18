var imageGrabber = {
	getImages: function(successCallback, errorCallback) {
		cordova.exec(successCallback, errorCallback, "ImageGrabber", "getImages", []);
	},
	
	getImageData: function(successCallback, errorCallback, imageUri) {
		console.log("imageUri: " + imageUri);
		console.log("successCallback: " + successCallback);
		console.log("errorCallback: " + errorCallback);
		cordova.exec(succesCallback, errorCallback, "ImageGrabber", "getImageData", [
			imageUri]);
	}
};

module.exports = imageGrabber;
