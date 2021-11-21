window.app = {
	/**
	 * http服务器
	 */
	serverUrl: "http://192.168.43.15:8080",

	/**
	 * 图片服务器
	 */
	imgServerUrl: "http://192.168.43.15:88",

	/**
	 * websocket 服务器
	 */
	wsServerUrl: "ws://192.168.43.15:8000/ws",

	/**
	 * 获取缓存中的用户信息
	 */
	getUserInfo: function() {
		var userInfoStr = plus.storage.getItem("userInfo");
		return JSON.parse(userInfoStr);
	},

	/**
	 * 设置用户信息
	 * @param {Object} userInfo
	 */
	setUserInfo: function(userInfo) {
		var userInfoStr = JSON.stringify(userInfo);
		plus.storage.setItem("userInfo", userInfoStr);
	},
	syncAjax: function(url) {
		var result = null;
		var xhr = new XMLHttpRequest();
		xhr.addEventListener("load", function() {
			result = this.responseText;
		});
		xhr.open("GET", url, false);
		xhr.send()
		return result;
	},
	/**
	 * 清除用户信息
	 */
	clearUserInfo: function() {
		plus.storage.removeItem("userInfo");
	},

	/**
	 * 表单验证
	 */
	isUsernameLegal: function(username) {
		var regex = /^[0-9a-zA-Z]+$/;
		return regex.test(username);
	},
	isPasswordLegal: function(password) {
		var regex = /(?=.*?[a-z])(?=.*?[A-Z])(?=.*?[0-9]).{8,}/;
		return regex.test(password);
	},

	/**
	 * 封装消息提示框
	 */
	showToast: function(msg, type) {
		plus.nativeUI.toast(msg, {
			icon: "../picture/" + type + ".png",
			verticalAlign: "center"
		}, )
	},

	/**
	 * 设置状态栏样式
	 */
	setStatusBar: function() {
		plus.navigator.setStatusBarStyle("light");
		plus.navigator.setStatusBarBackground("#f0ad4e");
	},

	isNull: function(str) {
		if (str == null || str == '' || str == undefined) {
			return true;
		}
		return false;
	},
	/**
	 * 错误响应统一处理
	 * @param {Object} xhr
	 * @param {Object} type
	 * @param {Object} errorThrown
	 */
	handleError: function(xhr, type, errorThrown) {
		console.log("错误类型: " + type);
		console.log(errorThrown);
		if (type == "timeout") {
			mui.toast("连接超时");
		} else if (type = "error") {
			mui.toast("连接出错");
		}
	},
	/**
	 * 构造消息对象
	 * @param {Object} senderId
	 * @param {Object} receiverId
	 * @param {Object} content
	 */
	ChatMsg: function(senderId, receiverId, content) {
		this.senderId = senderId;
		this.receiverId = receiverId;
		this.content = content;
	},
	/**
	 * 构造数据容器对象
	 * @param {Object} action --- CONNECT=1 CHAT=2
	 * @param {Object} chatMsg
	 */
	Envelope: function(action, chatMsg) {
		this.action = action;
		this.chatMsg = chatMsg;
	},
	/**
	 * 构造聊天快照对象
	 * @param {Object} senderId
	 * @param {Object} chatMsg
	 * @param {Object} unreadNum
	 */
	ChatSnapshot: function(senderId, chatMsg, unreadNum) {
		this.senderId = senderId;
		this.chatMsg = chatMsg;
		this.unreadNum = unreadNum;
	},
	/**
	 * map 转 json
	 * @param {Object} map
	 */
	map2Json: function(map) {
		var obj = {};
		for (var [k, v] of map) {
			obj[k] = v;
		}
		return JSON.stringify(obj);
	},
	/**
	 * json 转 map
	 * @param {Object} json
	 */
	json2Map: function(json) {
		var obj = JSON.parse(json);
		var map = new Map();
		for (var k of Object.keys(obj)) {
			map.set(k, obj[k]);
		}
		return map;
	},
	/**
	 * 保存聊天记录
	 * @param {Object} userId
	 * @param {Object} friendId
	 * @param {Object} chatMsg
	 */
	saveChatMsg: function(userId, friendId, chatMsg) {
		var chatRecords = null;
		var chatRecordsStr = plus.storage.getItem("chatRecords-" + userId);
		if (chatRecordsStr == null) {
			chatRecords = new Map();
			chatRecords.set(friendId, [chatMsg])
		} else {
			chatRecords = app.json2Map(chatRecordsStr);
			var records = chatRecords.get(friendId);
			if (records == null) {
				chatRecords.set(friendId, [chatMsg]);
			} else {
				records.push(chatMsg);
			}
		}
		plus.storage.setItem("chatRecords-" + userId, app.map2Json(chatRecords));
	},
	/**
	 * 获得聊天记录
	 * @param {Object} userId
	 * @param {Object} friendId
	 */
	getChatRecords: function(userId, friendId) {
		var chatRecords = null;
		var chatRecordsStr = plus.storage.getItem("chatRecords-" + userId);
		if (chatRecordsStr == null) {
			return null;
		} else {
			chatRecords = app.json2Map(chatRecordsStr);
			return chatRecords.get(friendId);
		}
	},
	delChatRecords: function(userId, friendId) {
		var chatRecords = null;
		var chatRecordsStr = plus.storage.getItem("chatRecords-" + userId);
		if (chatRecordsStr == null) {
			return;
		} else {
			chatRecords = app.json2Map(chatRecordsStr);
			chatRecords.delete(friendId);
			plus.storage.setItem("chatRecords-" + userId, app.map2Json(chatRecords));
		}
	},
	/**
	 * 保存聊天快照
	 */
	saveChatSnapshot: function(userId, friendId, snapshot) {
		var snapshotsStr = plus.storage.getItem("chatSnapshot-" + userId);
		var map = null;
		if (snapshotsStr == null) {
			map = new Map();
		} else {
			map = app.json2Map(snapshotsStr);
		}
		map.set(friendId, snapshot);
		plus.storage.setItem("chatSnapshot-" + userId, app.map2Json(map));
	},
	/**
	 * 获得所有聊天快照
	 * @param {Object} userId
	 */
	getAllChatSnapshot: function(userId) {
		var snapshotsStr = plus.storage.getItem("chatSnapshot-" + userId);
		if (snapshotsStr == null) {
			return null;
		} else {
			return app.json2Map(snapshotsStr);
		}
	},
	/**
	 * 获得指定朋友的聊天快照
	 * @param {Object} userId
	 * @param {Object} friendId
	 */
	getChatSnapshot: function(userId, friendId) {
		var snapshotsStr = plus.storage.getItem("chatSnapshot-" + userId);
		if (snapshotsStr == null) {
			return null;
		} else {
			var map = app.json2Map(snapshotsStr);
			return map.get(friendId);
		}
	},
	/**
	 * 删除指定朋友的聊天快照
	 * @param {Object} useId
	 * @param {Object} friendId
	 */
	delChatSnapshot: function(userId, friendId) {
		var snapshotsStr = plus.storage.getItem("chatSnapshot-" + userId);
		if (snapshotsStr == null) {
			return null;
		} else {
			var map = app.json2Map(snapshotsStr);
			map.delete(friendId);
			plus.storage.setItem("chatSnapshot-" + userId, app.map2Json(map));
		}
	},
	/**
	 * 保存所有的好友到本地缓存
	 * @param {Object} userId
	 * @param {Object} friends
	 */
	saveAllFriends: function(userId, friends) {
		var map = new Map();
		for (var friend of friends) {
			map.set(friend.id, friend);
		}
		plus.storage.setItem("friendsInfo-" + userId, app.map2Json(map));
	},
	/**
	 * 获得本地缓存的所有好友
	 * @param {Object} userId
	 */
	getAllLocalFriends: function(userId) {
		var friendsStr = plus.storage.getItem("friendsInfo-" + userId);
		if (friendsStr == null) {
			return null;
		}
		var map = app.json2Map(friendsStr);
		var friends = [];
		for (var [k, v] of map) {
			friends.push(v);
		}
		return friends;
	},
	/**
	 * 获得本地缓存中的好友信息
	 * @param {Object} userId
	 * @param {Object} friendId
	 */
	getLocalFriend: function(userId, friendId) {
		var friendsStr = plus.storage.getItem("friendsInfo-" + userId);
		if (friendsStr == null) {
			return null;
		}
		var map = app.json2Map(friendsStr);
		return map.get(friendId);
	},
	delLocalFriend: function(userId, friendId) {
		var friendsStr = plus.storage.getItem("friendsInfo-" + userId);
		if (friendsStr == null) {
			return null;
		}
		var map = app.json2Map(friendsStr);
		map.delete(friendId);
		plus.storage.setItem("friendsInfo-" + userId, app.map2Json(map));
		app.delChatSnapshot(userId, friendId);
		app.delChatRecords(userId, friendId);
	},
	delAllChatSnapshot: function(userId) {
		plus.storage.removeItem("chatSnapshot-" + userId)
	}
}
