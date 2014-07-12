var path = {
	getContentPaths: function(successCallback, errorCallback) {
		cordova.exec(successCallback, errorCallback, "Path", "getContentPath", []);
	}
};

module.exports = path;
