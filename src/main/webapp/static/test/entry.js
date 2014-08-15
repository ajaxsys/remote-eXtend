QUnit.module( "Test cookies plugin" );

QUnit.test( "add & remove cookies", function( assert ) {
  Entry.addCookie("secure_code_1", "test_app_1");

  assert.equal( $.cookie("SECURE_KEY"), "secure_code_1", "equals with added secure code" );
  assert.equal( $.cookie("APP_ID"), "test_app_1", "equals with added app id" );
 
  Entry.removeCookie();

  assert.equal( $.cookie("SECURE_KEY"), null,  "empty because removed secure code" );
  assert.equal( $.cookie("APP_ID"), null,  "equals with added app id" );
 
 
});




// Global
var savedMsg;

QUnit.module( "Test DOM", {
  setup: function( assert ) {
    // Mock behaver of alert
    Entry.alert = function(msg) { savedMsg = msg; }
    // Mock `location` object that cant not be override in Browser
    Entry.reload = function() { savedMsg = "reload"; }

    // // Prepare DOM
    // var $fixture = $( "#qunit-fixture" );
    // $fixture.append( "<input type='text' id='secureCode' value='1fa4798d67e83ca12eee5cf6be2f2917' />" + 
    //   "<select id='appID'><option value=''>---Select One---</option><option value='FileDownloader'>FileDownloader</option><option value='H2Console'>H2Console</option></select>" +
    //   "<button id='entry'>エントリー</button>" );

    // // Regist event with context
    // Entry.init($fixture);
    // $('#entry', $fixture).click($.proxy(Entry.click, Entry));

    Entry.init();
    $('#entry').click($.proxy(Entry.click, Entry));

  }
  // , teardown: function( assert ) {
  //   // assert.ok( true, "and one extra assert after each test" );
  // }
});


QUnit.test( "Entry button click", function( assert ) {
  // var $fixture = $( "#qunit-fixture" );
  // var $entry = $('#entry', $fixture), $appID = $('#appID', $fixture);
  var $entry = $('#entry'), $appID = $('#appID');

  // Execute event
  $entry.trigger('click');
  assert.equal( savedMsg, "Please input all infomations.", "No input, alert message!" );
 
  $appID.val("FileDownloader");
  $entry.trigger('click');
  assert.equal( savedMsg, "reload", "All input, reload the web page!" );
 
});


QUnit.test( "validate", function( assert ) {
  assert.ok( Entry.validate("a","b") , "OK, all parameter is given");
  assert.ok( !Entry.validate("","b") , "NG, one of parameter is not given");
  assert.ok( !Entry.validate("a","") , "NG, one of parameter is not given");
  assert.ok( !Entry.validate("","") ,  "NG, both parameter are not given");
});