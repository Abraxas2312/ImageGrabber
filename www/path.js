var path = {
	getContentPaths: function(successCallback, errorCallback) {
		cordova.exec(successCallback, errorCallback, "Path", "getContentPaths", []);
	}
};

module.exports = path;
