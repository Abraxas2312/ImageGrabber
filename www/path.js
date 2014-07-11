cordova.define("de.fb08.hsnr.path.Path", function(require, exports, module) {
	var exec = require('cordova/exec');
	
	var path = {
		getContentPath: function(filePath, successCallback, errorCallback) {
			imagesViewController.showError("getting path");
			exec(successCallback, errorCallback, "Path", "getContentPath", [filePath]);
		}
	};

	module.exports = path;	
});
