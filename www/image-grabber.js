var imageGrabber = {
	getImages: function(successCallback, errorCallback) {
		cordova.exec(successCallback, errorCallback, "ImageGrabber", "getImages", []);
	}
	
	getImageData: function(imageUri, successCallback, errorCallback) {
		cordova.exec(succesCallback, errorCallback, "ImageGrabber", "getImageData", [
			imageUri]);
	}
};

module.exports = imageGrabber;
