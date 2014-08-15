var Entry = {
	'SERVER_URL' : location.origin + location.pathname,
	'SECURE_KEY': 'SECURE_KEY',
	'APP_ID': 'APP_ID',

	// Support context for test 
	'init' : function(context){
		if (this._isInit) { // Only one
			return;
		}

		context = context || document;

		this.$appId = $('#appID', context);
		this.$secureCode = $('#secureCode', context);
		this._isInit = true;
	},

	// DOM event. NOTICE: `this` keyword refereced to DOM object, so must use $.proxy() to redefine `this`.
	'click' : function() {
		var E = this;
		E.init();

		var code = E.$secureCode.val(), appId = E.$appId.val();

		if (E.validate(code, appId)){
			E.removeCookie();
			E.addCookie(code, appId);
			E.reload();
		} else {
			E.alert('Please input all infomations.');
		}
	},
	'validate': function(code, appId){
		if (code === '' || appId === '') {
			return false;
		}
		return true;
	},

	// Cookie operation
	removeCookie : function(){
		$.removeCookie(this.SECURE_KEY, { path: '/' });
		$.removeCookie(this.APP_ID, { path: '/' });
	},
	addCookie : function(code, appId){
		// Without `expires: 365`, the cookie becomes a session cookie.
		$.cookie(this.SECURE_KEY, code, { path : '/' });
		$.cookie(this.APP_ID, appId, { path : '/' });
	},

	// Seperate dom event for test(mock)
	'alert' :  function(msg){
		window.alert(msg);
	},
	'reload' : function(){
		window.location.reload();
	},
}


$(function() {
	$("#entry").click($.proxy(Entry.click, Entry));
}); // End dom ready
