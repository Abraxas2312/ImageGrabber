var imageGrabber = {
	getImages: function(successCallback, errorCallback) {
		cordova.exec(successCallback, errorCallback, "ImageGrabber", "getImages", []);
	},
	
	getImageData: function(successCallback, errorCallback, imageUri) {
		cordova.exec(successCallback, errorCallback, "ImageGrabber", "getImageData", [
			imageUri]);
	}
};

module.exports = imageGrabber;
