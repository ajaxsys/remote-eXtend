

// Page initialed: focus, result hidden
// test( 'init test', function( assert ) {
// 	console.log($('#path').is(':focus'))
// 	assert.equal($('#path').is(':focus'), true, 'input is focused while initialed.')
// });

// Check protocal
if (window.location.protocol === 'file:') {
	alert('Please run this test case in server mode.e.g: http://localhost:9876/static/test/test_downloader.html');
}
// Real Ajax test need cookie info
$.cookie("SECURE_KEY", "efa4798d67e83ca12eee5cf6be2f2917", { path : '/' });
$.cookie("APP_ID", "FileDownloader", { path : '/' });

var DL = Downloader();

var singleResp = 'tmp/	8192	Mon Aug 11 15:50:07 JST 2014',
	singleFileResp = 'tree.txt	164633	Mon Aug 11 15:50:07 JST 2014',
	multiResp =
	'tmp1/	4096	Wed Aug 06 18:48:30 JST 2014\n' +
	'tree - abc.txt	5	Fri Aug 01 17:12:58 JST 2014\n' +
	'tree - あいうえ (16).txt	5	Fri Aug 01 17:12:58 JST 2014\n' +
	'tree - コピー.txt	5	Fri Aug 01 17:12:58 JST 2014\n' +
	'tree - 中国话 (17).txt	5	Fri Aug 01 17:12:58 JST 2014\n' +
	'tree - 日本語 (18).txt	5	Fri Aug 01 17:12:58 JST 2014\n' +
	'tree - 日本語中国话 (19).txt	5	Fri Aug 01 17:12:58 JST 2014\n' +
	'tree.log	5	Fri Aug 01 17:12:58 JST 2014\n' +
	'tree.txt	164633	Mon Aug 11 15:50:07 JST 2014\n' +
	'tree_result.txt	167766	Mon Aug 11 15:50:15 JST 2014\n'
	;

// Global mock for DL
var savedMsg;
DL._relocate = function mockRelocation(url){
	savedMsg = url;
};
















/****************************************************
 * Mocked AJAX
 ****************************************************/
module('Test by mocked Ajax');

var case_1_input = 'd:/tm';
$.mockjax({
  url: '/dspch',
  data: { filepath: case_1_input },
  responseTime: 1,
  responseText: singleResp,
});

asyncTest( 'DL.ls - Success, while press tab with parts of file path', function( assert ) {

	expect(2);

	var inputPath = case_1_input, autoCompletedPath = 'd:/tmp/';

	DL.ls(inputPath, function onSuccess(fileList){
		assert.equal(fileList, 'tmp/\t8192\tMon Aug 11 15:50:07 JST 2014', '1 file returned');
		start();

		var autoComplete = DL.autoCompleteCmd(fileList, inputPath);
		assert.equal(autoComplete, autoCompletedPath, 'File autocompleted, from ' + inputPath + ' to: ' + autoCompletedPath);

	});
});


var case_2_input = 'd:/tmp_not_exist';

$.mockjax({
  url: '/dspch',
  data: { filepath: case_2_input },
  status: 403,
  responseTime: 1,
  responseText: '',
});

asyncTest( 'DL.ls - Test Ajax Failed.', function( assert ) {
	expect(1);
	var inputPath = case_2_input;

	DL.ls(inputPath, function onSuccess(fileList){
		ok(false, 'Should not be success');
		start();
	}, function onFail(fileList){
		ok(true, 'Expected failed root.');
		start();
	});
});


var case_3_input = 'd:/tmp/';

$.mockjax({
  url: '/dspch',
  data: { filepath: case_3_input },
  responseTime: 1,
  responseText: multiResp,
});


asyncTest( 'DL.ls - Success, with file list in folder', function( assert ) {

	expect(2);

	var inputPath = case_3_input, autoCompletedPath = 'd:/tmp/t';

	DL.ls(inputPath, function onSuccess(fileList){
		assert.equal(fileList, multiResp, 'Multi line responsed');
		start();

		var autoComplete = DL.autoCompleteCmd(fileList, inputPath);
		assert.equal(autoComplete, autoCompletedPath, 'File autocompleted, from ' + inputPath + ' to: ' + autoCompletedPath);

	});
});


var case_4_input = 'd:/tmp/tree.txt';

$.mockjax({
  url: '/dspch',
  data: { filepath: case_4_input },
  responseTime: 1,
  responseText: singleFileResp,
});


asyncTest( 'DL.ls - Success, single file list in folder', function( assert ) {

	expect(2);

	var inputPath = case_4_input;

	DL.ls(inputPath, function onSuccess(fileList){
		assert.equal(fileList, singleFileResp, 'Multi line responsed');
		start();

		var autoComplete = DL.autoCompleteCmd(fileList, inputPath);
		assert.equal(autoComplete, case_4_input, 'File autocompleted, NO changed');

	});
});












/****************************************************
 * Test Real AJAX
 ****************************************************/
module('Test with real Ajax (Make sure http server is on, and cookies be set on entry.html)');


asyncTest( 'DL.ls - Success, while press tab with file path auto complete', function( assert ) {

	expect(2);

	var inputPath = './src/test/resources/list/', autoCompletedPath = './src/test/resources/list/foo'; //./src/test/resources/

	DL.ls(inputPath, function onSuccess(fileList){
		// -1 because contains a new line mark at end of file)
		assert.equal(fileList.split('\n').length - 1, 11,  '11 file returned');
		start();

		var autoComplete = DL.autoCompleteCmd(fileList, inputPath);
		assert.equal(autoComplete, autoCompletedPath, 'File autocompleted, from ' + inputPath + ' to: ' + autoCompletedPath);

	});
});











/****************************************************
 * Test download (Cant be mock, must use real server mode!)
 ****************************************************/
module('Test download with real Ajax (Make sure http server is on)');

var case_10_input = './src/test/resources/bar.log_ng'

asyncTest( 'DL.dl - NG: while press download button or press enterkey.', function( assert ) {
	expect(2);

	DL.dl(case_10_input, function onStart(){
		ok(true, 'Start process must be execute');
	}, function (){
		ok(true, 'Expected failed root.');
		start();
	});


});


var case_11_input = './src/test/resources/bar.log'


asyncTest( 'DL.dl - OK: while press download button or press enterkey.', function( assert ) {
	expect(1);

	DL.dl(case_11_input, function onStart(){
		ok(true, 'Start process must be execute');
		start();
	}, function (){
		ok(false, 'Should not be failed.Expected download OK.');
		start();
	});


});







/****************************************************
 * Test UI
 ****************************************************/



module('Test UI', {
  setup: function( assert ) {
  	DL._async = false;
  }, teardown: function( assert ) {
  	DL._async = true;
    // assert.ok( true, "and one extra assert after each test" );
  }
});


testStart(clear);
testDone(clear);

function clear(){
	$('#showResult').empty().hide();
	$('#path').val('');
}



test( 'DL.ui.util.formatLsFileList - show file list & test link event', function( assert ) {

	var $table1 = DL.ui.util.formatLsFileList(singleResp, 'd:/tm');

	ok($table1 instanceof jQuery, 'table is a jQuery object');
	equal( $table1.find('tr').length - 2 , 1 , 'table is should be append 1 row');
	equal( $table1.find('a#file_0').attr('href')  , 'd:/tmp/' , 'table is should be append 1 row');


	var $table2 = DL.ui.util.formatLsFileList(multiResp, 'd:/tmp/');

	ok($table2 instanceof jQuery, 'table is a jQuery object');
	equal( $table2.find('tr').length - 2 , 10 , 'table is should be append 9 row');
	equal( $table2.find('a#file_0').attr('href')  , 'd:/tmp/tmp1/' , 'test 1st link');
	equal( $table2.find('a#file_9').attr('href')  , 'd:/tmp/tree_result.txt' , 'test 10th link');


	equal(null , DL.ui.util.formatLsFileList('', case_1_input), 'return null while no response text');


	// test click then url changed.
	$table2.find('a#file_0').trigger('click');
	equal( savedMsg  , '#d%3A%2Ftmp%2Ftmp1%2F' , 'url be changed.');
	savedMsg = null;

	$table2.find('a#file_9').trigger('click');
	equal( savedMsg  , null , 'url NOT be changed, because it is NOT a folder');

});


test( 'DL.ui.doHashChanged - ui changed when url changed', function( assert ) {

	// var $main = $('#main').clone().removeAttr('id');
	// var $fixture = $( "#qunit-fixture" );
	// $fixture.append($main);

	// NOTICE: use mock still must test in async mode!


	// this event trigger by patterns bellow:
	// - Folder with like `d:/tmp` is clicked
	// - URL with `d:/tmp` param is opened
	// - User input `d:/tmp`, then press tab key
	DL.ui.doHashChanged('http://localhost#d%3A%2Ftmp%2F'); // d:/tmp/  == #d%3A%2Ftmp%2F, use mock above

	// NOTICE: test in sync mode!
	equal($('#path').val(), 'd:/tmp/', '[sync mode]Input form should be set to same with URL params');
	ok($('#showResult').is(':visible'), '[sync mode]Result should be show');
	equal($('#showResult table tr').length - 2, 10, '[sync mode]Should contains 10 records')

});

// Bind events to
DL.bindEvent();


// Send tab key
// Send enter key
// click download button
	// Show `download start...`
	// Show `download failed`

// click go parent button

