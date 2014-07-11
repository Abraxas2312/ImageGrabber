var path = {
	getContentPath: function(filePath, successCallback, errorCallback) {
		imagesViewController.showError("getting path");
		cordova.exec(successCallback, errorCallback, "Path", "getContentPath", [filePath]);
	}
};

module.exports = path;
