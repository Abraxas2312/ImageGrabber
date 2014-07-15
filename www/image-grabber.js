var imageGrabber = {
	getImages: function(successCallback, errorCallback) {
		cordova.exec(successCallback, errorCallback, "Path", "getImages", []);
	}
};

module.exports = path;
