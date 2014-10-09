$(function(){
    var DL = Downloader();
    DL.init();
});


function Downloader(){
    if (! (this instanceof Downloader) )
        return new Downloader();

    var DL = this;
    // public method
    DL.init = init;
    registTestHook();

    var $path = $('#path'), $msg = $('#showMessageBar'), $form = $('#cmdForm'), $result = $('#showResult'),
        $tableTmplt = $('#fileListTemplate'), $rowTmplt = $('#fileListRowTemplate'), $goParent = $('#goParent');
    var DEFAULT_ACTION = '/dspch';
    var THIS_URL = SERVER_URL = (location.protocol + "//" + location.host + location.pathname);
    var req, _lastReqResult;

    // For test by direct link
    if (SERVER_URL.endsWith('\.html')){
        SERVER_URL = DEFAULT_ACTION;
    }








    function init(){
        $path.focus();

        bindEvent();
        // Do first hash change events
        doHashChanged();
    }



    function bindEvent(){
        $path.keydown(doKeyDown);

        $form.submit(doSubmit);

        $goParent.click(doGoParent);

        // Enable set path from URL
        $(window).on('hashchange', doHashChanged);

    }

    function doKeyDown(e) {
        switch (e.keyCode) {
        case 9:// Tab
            // Allow auto complete only when page first load.
            $path.attr('autocomplete', 'off');
            // Only autocomplete while press tab key
            $path.data('enableAutoComplete', 'auto');

            var subUrl = '#' + encodeURIComponent($path.val());
            if (window.location.href === THIS_URL + subUrl){
                showLsFileResult();
            } else {
                // show ls command in hash changed event
                pushHistory($path.val());
            }

            // Stop original tab event
            return false;
        case 13:// Enter
            $form.submit();
            return false;
        }
    }

    function doSubmit() {
        if (isFolder($path.val())) {
            alert('Invalid File Path.');
            return false;
        }

        DL.dl($path.val());

        return false;
    }

    function doGoParent() {
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
    }


    function doHashChanged(href) {
        // param is for test
        href = (typeof href === "string") ? href : window.location.href;
        var paths = href.split('#');

        if (paths.length > 1) {
            var path = decodeURIComponent(paths[1]);
            $path.val(path);
            ls(path, showLsFileResult, showLsError);
        } else {
            $path.val('');
            $result.hide();
            $path.attr('autocomplete', 'on');
        }
    }

    function showLsFileResult(text){
        // For last result cache
        text = text || _lastReqResult;
        _lastReqResult = text;

        var thisPath = $path.val();
        formatLsFileList(text, thisPath);
        if ($path.data('enableAutoComplete')){
            doAutoComplete(text, thisPath);
            $path.data('enableAutoComplete', null);
        }
    }

    function doAutoComplete(text, thisPath){
        if (!text || !thisPath){
            return;
        }

        var newPath = autoCompleteCmd(text, thisPath);

        if (thisPath!==newPath){
            $path.val(newPath);
            showMsg(thisPath);
        }

    }


    function showLsError() {
        $result.text('Error: can NOT get file list!');
    }

    function showMsg(msg) {
        $msg.text(msg).show().fadeOut(2000);
    }














    function ls(path, onSuccess, onFail){
        if (!path)
            return;
        if (req){
            req.abort();
        }
        req = $.ajax({
            url : SERVER_URL,
            type : 'GET',
            data : {
                'filepath' : path
            },
            dataType : 'text',
            async: DL._async===false ? false : true,
        });
        if (onSuccess){
            req.done(onSuccess);
        }
        if (onFail){
            req.fail(onFail);
        }
    }

    // text format: [name]\t[size]\t[timestamp]
    function autoCompleteCmd(text, inputPath){
        var rows = text.split('\n');
        // For autocomplete
        var pathPrefix;

        for ( var i in rows) {
            var row = rows[i];
            var cols = row.split('\t');
            if (cols.length != 3)
                continue;

            var filePath = getFullPath(cols[0], inputPath);
            pathPrefix = getAutoCompletePath(pathPrefix, filePath);
        }
//        return pathPrefix;
        return pathPrefix ? pathPrefix : inputPath;

    }

    function dl(path, onStart, onFail) {
        var opt = {
            httpMethod : "POST",
            // data: {
            // 'filepath' : path,
            // },
        };
        if (onStart) opt.prepareCallback = onStart;
        if (onFail) opt.failCallback = onFail;

        $.fileDownload(SERVER_URL + '?filepath=' + encodeURIComponent(path), opt)
    }

    function formatLsFileList(text, inputPath) {
        if (!text) {
            return null;
        }

        var rows = text.split('\n');
        // For autocomplete
        var result = '', $table = $tableTmplt.clone();

        for ( var i in rows) {
            var row = rows[i];
            var cols = row.split('\t');
            if (cols.length != 3)
                continue;

            var $row=$rowTmplt.clone().removeAttr('id');
            var filePath = getFullPath(cols[0], inputPath);

            $('.fileName', $row).text(cols[0])
                .attr('id', 'file_' + i)
                .attr('href', filePath)
                .click(   doTriggerTab    );

            $('.fileSize', $row).text(cols[1]);
            $('.fileLastModified', $row).text(cols[2]);
            $table.append($row.show());
        }

        $result.empty().append($table.show()).show();

        return $table;
    }

    function doTriggerTab() {
        var href = $(this).attr('href');

        // `$path.val()` will be changed in doHashChanged
        if (isFolder(href)) {
            pushHistory(href);
        } else {
            // file get it's path
            $path.val(href);
        }
        return false;
    }

    function isFolder(filePath) {
        return filePath.endsWith('/');
    }

    function pushHistory(href) {
        // Must call from DL!
        // [NG]this.relocate: this==link obj
        // [NG]relocate: always referenct to original relocate, because it's a closure
        DL._relocate( '#' + encodeURIComponent(href) );
    }

    function relocate(subUrl){
        window.location.href = THIS_URL + subUrl;
    }

    /*private functions*/
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

    function getFullPath(fileName, inputPath) {
        // 'd:\tm' --> 'd:/' + 'tmp/'
        return getPresentFolder(inputPath) + fileName;
    }

    function getPresentFolder(path) {
        // 'd:\' --> 'd:/'
        var path = path.replace('\\', '/');
        // 'd:/abc/def' --> 'd:/abc/'
        return path.substring(0, path.lastIndexOf('/') + 1);
    }

    function registTestHook(){
        if (!window.__ENABLE_TEST__)
            return;
        // (regist for test only) option OR methods
        DL._async = true;
        DL._relocate = relocate;
        DL.ls = ls;
        DL.dl = dl;
        DL.autoCompleteCmd = autoCompleteCmd;
        DL.bindEvent = bindEvent;
        DL.ui = {
            doHashChanged: doHashChanged,
            doTriggerTab : doTriggerTab,
            doKeyDown: doKeyDown,
            doGoParent: doGoParent,
            doSubmit: doSubmit,
            doAutoComplete: doAutoComplete,
        }
        DL.ui.util = {
            formatLsFileList : formatLsFileList,
        }
    }

}















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






