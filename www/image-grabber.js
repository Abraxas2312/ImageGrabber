var imageGrabber = {
	getImages: function(successCallback, errorCallback) {
		cordova.exec(successCallback, errorCallback, "ImageGrabber", "getImages", []);
	}
	
	getImageData: function(id, successCallback, errorCallback) {
		cordova.exec(succesCallback, errorCallback, "ImageGrabber", "getImageData", [id])
	}
};

module.exports = imageGrabber;
