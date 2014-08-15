var DEFAULT_ACTION = '/dspch';

$(function() {
	var SERVER_URL = (location.origin + location.pathname).endsWith('.html') ? DEFAULT_ACTION
			: (location.origin + location.pathname);

	var $path = $('#path'), $msg = $('#showMessageBar'), $form = $('#cmdForm'), $result = $('#showResult'), $tableTmplt = $('#fileListTemplate'), $rowTmplt = $('#fileListRowTemplate'), $goParent = $('#goParent');
	// Allow auto complete when first page load.
	$path.focus();

	var API = {
		ls : function(path) {
			if (!path)
				return;
			$.ajax({
				url : SERVER_URL,
				type : 'GET',
				data : {
					'filepath' : path
				},
				dataType : 'text',
			}).done(showFileLists).fail(showLsError);
		},
		downFile : function(path) {
			var opt = {
				httpMethod : "POST",
				// data: {
				// 'filepath' : path,
				// },
				prepareCallback : function() {
					showMsg('Download start...');
				},
				failCallback : function() {
					showMsg('Download failed!');
				},
			};

			$.fileDownload(
					SERVER_URL + '?filepath=' + encodeURIComponent(path), opt)
		}

	};

	$path.keydown(function(e) {
		switch (e.keyCode) {
		case 9:// Tab
			// Allow auto complete only when page first load.
			$path.attr('autocomplete', 'off');
			// Only autocomplete while press tab key
			$path.data('enableAutoComplete', 'auto');
			// show ls command in hash changed event
			pushHistory($path.val());

			// Stop original tab event
			return false;
		case 13:// Enter
			$form.submit();
			return false;
		}
	});

	$form.submit(function() {
		if (isFolder($path.val())) {
			alert('Invalid File Path.');
			return false;
		}

		API.downFile($path.val());

		return false;
	});

	$goParent.click(function() {
		var path = $path.val();
		if (path.endsWith('/')) {
			// d:/tmp/ --> d:/tmp
			path = path.removeLastNChar(1);
		}
		// Not the last
		if (path.indexOf('/') > 0) {
			// if path contains `/` > 2, then remove one `/`
			pushHistory(getPresentFolder(path));
		}
		return false;
	});

	// Enable set path from URL
	$(window).on('hashchange', doURIChanged)
	doURIChanged();

	/** ********Private functions*********** */
	function isFolder(filePath) {
		return filePath.endsWith('/');
	}

	function showFileLists(text) {
		if (!text) {
			$result.hide();
			return;
		}

		var $table = $tableTmplt.clone();

		var rows = text.split('\n');
		// For autocomplete
		var pathPrefix;

		for ( var i in rows) {
			var row = rows[i];
			var cols = row.split('\t');
			if (cols.length != 3)
				continue;
			var $row = $rowTmplt.clone();

			var filePath = getFullPath(cols[0]);
			pathPrefix = getAutoCompletePath(pathPrefix, filePath);

			$('.fileName', $row).text(cols[0]).attr('href', filePath).click(
					triggerTab);
			$('.fileSize', $row).text(cols[1]);
			$('.fileLastModified', $row).text(cols[2]);
			$table.append($row.show());

		}

		$result.empty().append($table.show()).show();
		// $result.html(text.replace(/\n/g,'<br>').replace(/\t/g,'&nbsp;&nbsp;&nbsp;&nbsp;'));

		// Set auto complete
		if ($path.data('enableAutoComplete') && pathPrefix && pathPrefix !== $path.val()) {
			showMsg($path.val());
			$path.val(pathPrefix);
			$path.data('enableAutoComplete', null);
		}
	}

	function getAutoCompletePath(pathPrefix, filePath) {
		// First Load
		if (pathPrefix === undefined || pathPrefix === filePath) {
			return filePath;
		}

		// After second load, ignore no matches
		if (!pathPrefix || filePath.startsWith(pathPrefix)) {
			return pathPrefix;
		}

		// return the same prefix
		for ( var i = pathPrefix.length - 1; i > 0; i--) {
			var toTest = pathPrefix.substring(0, i);
			if (filePath.startsWith(toTest)) {
				return toTest;
			}
		}
		return "";
	}

	function showMsg(msg) {
		$msg.text(msg).show().fadeOut(2000);
	}

	function setFilePath() {
		$path.val($(this).attr('href'));
		return false;
	}

	function triggerTab() {
		var href = $(this).attr('href');
		$path.val(href);
		if (isFolder(href)) {
			// trigger tab (API.ls) event
			pushHistory(href);
		}
		$path.focus();
		// API.ls($path.val(href));
		return false;
	}

	function pushHistory(href) {
		window.location.href = window.location.href.split('#')[0] + '#'
				+ encodeURIComponent(href);
	}

	function doURIChanged() {
		var paths = window.location.href.split('#');

		if (paths.length > 1) {
			var path = decodeURIComponent(paths[1]);
			$path.val(path);
			API.ls(path);
		} else {
			$path.val('');
			$result.hide();
			$path.attr('autocomplete', 'on');
		}
	}

	function getFullPath(fileName) {
		// 'd:\tm' --> 'd:/' + 'tmp/'
		return getPresentFolder($path.val()) + fileName;
	}

	function getPresentFolder(path) {
		// 'd:\' --> 'd:/'
		var path = path.replace('\\', '/');
		// 'd:/abc/def' --> 'd:/abc/'
		return path.substring(0, path.lastIndexOf('/') + 1);
	}

	function showLsError(msg) {
		$result.text('Error: can NOT get file list!');
	}

}); // End dom ready


/** ***********PROTOTYPE************ */
String.prototype.startsWith = function(prefix) {
	return this.indexOf(prefix) === 0;
}

String.prototype.endsWith = function(suffix) {
	return this.match(suffix + "$") == suffix;
};

String.prototype.removeLastNChar = function(charLen) {
	if (this.length <= charLen) {
		return '';
	}
	return this.substring(0, this.length - charLen);
};
