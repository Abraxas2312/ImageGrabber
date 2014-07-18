var imageGrabber = {
	getImages: function(successCallback, errorCallback) {
		cordova.exec(successCallback, errorCallback, "ImageGrabber", "getImages", []);
	}
};

module.exports = imageGrabber;
