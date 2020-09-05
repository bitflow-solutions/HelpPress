var editor, selectedGroupId, selectedContentId, selectedContentTitle;
var SOURCE = [];
const URL_UPDATE_NODE = "/api/v1/ecm/node";

function initTree() {
  $('#tree').fancytree({
    extensions: ["edit"],
    checkbox: false,
    selectMode: 3,
    source: SOURCE,
    nodata: '도움말이 없습니다',
    edit: {
      triggerStart: ["f2"],
      adjustWidthOfs: 4,   // null: don't adjust input size to content
      inputCss: { minWidth: "120px" },
      beforeEdit: function(e, data){
      },
      edit: function(e, data){
      },
      save: function(e, data) {
      	console.log('save');
        return renameTitle(e, data);
      },
      beforeClose: function(e, data){
      },
      close: function(e, data){
    	// initEvents();
      }
    },
    activate: function(e, data){
	  console.log("activate");
	  var node = data.node;
	  if (!node.folder || node.folder===false) {
	  	// 도움말 표시
	  	loadPage(node.key);
	  }
	},
	click: function(e, data){
		console.log("click");
    },
    lazyLoad: function (event, data) {},
  })
}

function initEvents() {
	$(".btn-modify").click(function(e) {
		// 도움말 수정완료 버튼 클릭
		$(".btn-modify").hide();
		$(".spinner-border").show();
		var url = "/api/v1/ecm/content/" + selectedContentId;
		$.ajax({
			url: url,
			method: "PUT",
			data: { 
				title: selectedContentTitle,
				content: editor.getPublishingHtml(),
			}
		})
		.done(function(msg) {
		  var key = msg.result.key;
		  console.log('key ' + key);
		  if (key && key.length>0) {
			loadPage(key);
		  }
		  alert('도움말을 수정하였습니다');
		})
		.always(function() {
			setTimeout(function() {
				$(".spinner-border").hide();
			}, 500);
	    });
	});
	$("#btn-expand-all").click(function(e) {
		expandAll();
	});
	$("#btn-collapse-all").click(function(e) {
		collapseAll();
	});
	// (1) 파일 타입
	$.contextMenu({
	    selector: ".fancytree-ico-c > .fancytree-title",
	    callback: function(key, options) {
        },
	    items: {
	        rename: {name: "제목 변경", callback: editTitle },
	        modify: {name: "도움말 수정", callback: editContent },
	        deletecontent: {name: "도움말 삭제", callback: deleteContent },
	        downloadcontent: {name: "다운로드", callback: downloadContent }
	    },
	    events: {
			show : function(options){
			  var key = $(this).attr('key');
	    	  var _tree = $.ui.fancytree.getTree();
			  var node = _tree.getNodeByKey(key);
			  node.setActive();
			  console.log('file ' + key);
	        }           
		}
	});
	// (2) 폴더 타입
	$.contextMenu({
	    selector: ".fancytree-folder > .fancytree-title",
	    callback: function(key, options) {
        },
	    items: {
	        rename: {name: "제목 변경", callback: editTitle },
	        newfolder: {name: "새 폴더", callback: appendChildFolder },
	        newcontent: {name: "새 도움말", callback: appendChildContent },
	        deletefolder: {name: "폴더 삭제", callback: deleteFolder }
	    },
	    events: {
			show : function(options){
			  var key = $(this).attr('key');
	    	  var _tree = $.ui.fancytree.getTree();
			  var node = _tree.getNodeByKey(key);
			  node.setActive();
			  console.log('folder ' + key);
	        }           
		}
	});
	$.contextMenu({
		selector: "#tree",
		items: {
		  newfolder:  {name: "새 폴더",  callback: appendRootFolder },
		  newcontent: {name: "새 도움말", callback: appendRootContent }
		}
	});
}

function initEditor() {
  editor = new SynapEditor('synapEditor', synapEditorConfig);
  editor.setEventListener('beforeUploadImage', function (e) {
    e.addParameter('key', selectedContentId);
	console.log("beforeUploadImage " + JSON.stringify(e));
  });
  editor.setEventListener('afterUploadImage', function (e) {
    console.log('afterUploadImage ' + JSON.stringify(e));
    var fileType = e.fileType;
    var uploadPath = e.path;
    // console.log('filetype ' + fileType + ' uploadpath ' + uploadPath);
    e.editor.addUploadPath(fileType, uploadPath);
  });
  editor.setEventListener('beforeOpenDocument', function (e) {
	e.addParameter('key', selectedContentId);
	console.log("beforeOpenDocument " + JSON.stringify(e));
  });
}

function saveTree() {
  console.log('saveTree');
  var _tree = $.ui.fancytree.getTree();
  var tree = _tree.toDict(true);
  $(".spinner-border").show();
  $.ajax({
	  url: "/api/v1/ecm/group/" + selectedGroupId,
	  method: "PUT",
		data: {  tree: JSON.stringify(tree.children) }
  	})
	.done(function(msg) {
	  _tree.reload(JSON.parse(msg.result.tree));
	})
	.fail(function() {
	})
	.always(function() {
		setTimeout(function() {
			$(".spinner-border").hide();
		}, 300);
	});
}

function editContent() {
  console.log("editContent");
  var _tree = $.ui.fancytree.getTree();
  var node = _tree.getActiveNode();
  selectedContentId = node.key;
  selectedContentTitle = node.title;
  console.log('selectedContentTitle' + selectedContentTitle);
  var url = "/api/v1/ecm/content/" + node.key;
  $.ajax({
	url: url,
	method: "GET"
  })
  .done(function(msg) {
    editor.openHTML(msg.result.contents);
    $("#contents-detail").hide();
	$("#editor-wrapper").show();
    $("#btn-edit").show();
  });
}

function loadPage(key) {
  console.log("loadPage " + key);
  $("#editor-wrapper").hide();
  $("#btn-edit").hide();
  $("#contents-detail").attr("src", key + ".html");
  $("#contents-detail").show();
}

function renameTitle(e, data) {
    var title = data.input.val();
    console.log('renameTitle ' + title);
    var node = data.node;
    var data = { groupId: selectedGroupId, key: node.key, title: title };
    if (node.folder && node.folder===true) {
    	data.folder = true;
    }
    $(".spinner-border").show();
	$.ajax({
		url: URL_UPDATE_NODE,
		method: "PUT",
		data: data
	})
	.done(function(msg) {
	  console.log('status ' + msg.status);
	  if (msg.status==401) {
	  	location.href = "/logout";
	  } else {
	  	// node.setTitle(title);
        saveTree();
	  }
	})
	.always(function() {
		setTimeout(function() {
			$(".spinner-border").hide();
		}, 500);
    });
}

function appendRootFolder() {
  console.log("appendRootFolder");
  var _tree = $.ui.fancytree.getTree();
  var parent = _tree.getRootNode();
  $.ajax({
	  url: URL_UPDATE_NODE,
	  method: "POST",
	  data: { groupId: selectedGroupId, parentKey: parent.key, folder: true }
  	})
	.done(function(msg) {
	  if (msg.status==401) {
	  	location.href = "/logout";
	  } else {
	    console.log('appendRootFolder - parent ' + parent.key + ' child ' + msg.result.key);
    	var existingNode = _tree.getNodeByKey(msg.result.key);
    	if (!existingNode) {
			var child = { key: msg.result.key, title: msg.result.title, folder: true };
			parent.addNode(child, 'child');
    	}
        saveTree();
	  }
	})
	.fail(function() {
	})
	.always(function() {
  });
}

function appendChildFolder() {
  console.log("appendChildFolder");
  var _tree = $.ui.fancytree.getTree();
  var parent = _tree.getActiveNode();
  if( !parent ) {
    parent = _tree.getRootNode();
  }
  $.ajax({
	  url: URL_UPDATE_NODE,
	  method: "POST",
	  data: { groupId: selectedGroupId, parentKey: parent.key, folder: true }
  	})
	.done(function(msg) {
	  if (msg.status==401) {
	  	location.href = "/logout";
	  } else {
	    console.log('appendChildFolder - parent ' + parent.key + ' child ' + msg.result.key);
    	var existingNode = _tree.getNodeByKey(msg.result.key);
    	if (!existingNode) {
			var child = { key: msg.result.key, title: msg.result.title, folder: true };
			parent.addNode(child, 'child');
    	}
        saveTree();
	  }
	})
	.fail(function() {
	})
	.always(function() {
  });
}

function appendChildContent() {
  var _tree = $.ui.fancytree.getTree();
  var parent = _tree.getActiveNode();
  if( !parent ) {
    parent = _tree.getRootNode();
  }
  $.ajax({
	  url: URL_UPDATE_NODE,
	  method: "POST",
	  data: { groupId: selectedGroupId, parentKey: parent.key }
  	})
	.done(function(msg) {
	  if (msg.status==401) {
	  	location.href = "/logout";
	  } else {
	    console.log('appendChildFolder - parent ' + parent.key + ' child ' + msg.result.key);
    	var existingNode = _tree.getNodeByKey(msg.result.key);
    	if (!existingNode) {
			var child = { key: msg.result.key, title: msg.result.title };
			parent.addNode(child, 'child');
    	}
        saveTree();
	  }
	})
	.fail(function() {
	})
	.always(function() {
  });  
}

function appendRootContent() {
  var _tree = $.ui.fancytree.getTree();
  var parent = _tree.getRootNode();
  $.ajax({
	  url: URL_UPDATE_NODE,
	  method: "POST",
	  data: { groupId: selectedGroupId, parentKey: parent.key }
  	})
	.done(function(msg) {
	  if (msg.status==401) {
	  	location.href = "/logout";
	  } else {
	    console.log('appendRootFolder - parent ' + parent.key + ' child ' + msg.result.key);
    	var existingNode = _tree.getNodeByKey(msg.result.key);
    	if (!existingNode) {
			var child = { key: msg.result.key, title: msg.result.title };
			parent.addNode(child, 'child');
    	}
        saveTree();
	  }
	})
	.fail(function() {
	})
	.always(function() {
  });
}

function deleteContent() {
  var _tree = $.ui.fancytree.getTree();
  var node = _tree.getActiveNode();
  $.ajax({
	  url: URL_UPDATE_NODE,
	  method: "DELETE",
	  data: { groupId: selectedGroupId, key: node.key, title: node.title }
  	})
	.done(function(msg) {
	  if (msg.status==401) {
	  	location.href = "/logout";
	  } else {
	  	node.remove();
        saveTree();
	  }
	})
	.fail(function() {
	})
	.always(function() {
  });
}

/**
 * 폴더 삭제
 */
function deleteFolder() {
  var _tree = $.ui.fancytree.getTree();
  var node = _tree.getActiveNode();
  // recursive) node.getChildren();
  var childContentsArr = [];
  getChildrenRecursive(node, childContentsArr);
  if (childContentsArr) {
  	console.log('childContentsArr ' + JSON.stringify(childContentsArr));
  }
  $.ajax({
	  url: URL_UPDATE_NODE,
	  method: "DELETE",
	  data: { groupId: selectedGroupId, key: node.key, child: childContentsArr, folder: true, title:  node.title }
  	})
	.done(function(msg) {
	  if (msg.status==401) {
	  	location.href = "/logout";
	  } else {
	    node.remove();
        saveTree();
	  }
	})
	.fail(function() {
	})
	.always(function() {
  });
}

function getChildrenRecursive(node, arr) {
    console.log('[' + node.key + '] folder ' + node.folder);
	if (node.folder && node.folder===true) {
	    // 1) if node is folder
		if (node.hasChildren()) {
			var children = node.getChildren();
			for (var i=0; i<children.length; i++) {
			  getChildrenRecursive(children[i], arr);
			}
		}
		return;
	} else {
		// 2) if node is content
		arr.push(node.key);
		return;
	}
}

function downloadContent() {
  var _tree = $.ui.fancytree.getTree();
  var node = _tree.getActiveNode();
  if( !node ) {
    alert("도움말을 선택해주세요");
    return;
  }
  var key = node.key;
  $(".spinner-border").show();
  $("#ifrm").attr("src", "/api/v1/ecm/release/" + key);
  setTimeout(function() {
	$(".spinner-border").hide();
  }, 6000);
}

function editTitle() {
  console.log('editTitle');
  var _tree = $.ui.fancytree.getTree();
  var node = _tree.getActiveNode();
  if( !node ) {
    alert("도움말을 선택해주세요");
    return;
  }
  node.editStart();
}

function onSelectChanged(select) {
  selectedGroupId = select.options[select.selectedIndex].value;
  if (!selectedGroupId || selectedGroupId.length<1) {
	  $("#tree").hide();
	  $("#contents-detail").attr("src", "/empty-content.html");
	  location.href = "#";
  } else {
	loadTree(selectedGroupId);
  }
}

function loadTree(groupId) {
	$.ajax({
	  url: "/api/v1/ecm/group/" + groupId,
	  method: "GET"
	})
	.done(function(msg) {
	console.log('msg ' + JSON.stringify(msg));
	  var _tree = $.ui.fancytree.getTree();
	  $("#tree").show();
	  if (msg && msg.result) {
	  	_tree.reload(JSON.parse(msg.result.tree));
	  }
	  location.href = "#" + groupId;
	})
	.fail(function() {
	})
	.always(function() {
	});
}

function expandAll() {
  $.ui.fancytree.getTree().expandAll();
}

function collapseAll() {
  $.ui.fancytree.getTree().expandAll(false);
}

function initSocket() {
	var socket = new SockJS('ws');
	var stompClient = Stomp.over(socket);
	stompClient.debug = null;
	stompClient.connect({}, function (frame) {
        // console.log('Connected: ' + frame);
        stompClient.subscribe('/group', function (msg) {
          console.log("groupId " + JSON.parse(msg.body).groupId);
          if (selectedGroupId===JSON.parse(msg.body).groupId) {
            console.log("reloading group " + JSON.parse(msg.body).groupId);
	        // console.log("msg " + JSON.parse(msg.body).tree);
	        var _tree = $.ui.fancytree.getTree();
	        // msg.body.groupId , msg.body.tree
	        _tree.reload(JSON.parse(JSON.parse(msg.body).tree));
	  	  }
        });
        stompClient.subscribe('/node', function (rawmsg) {
          console.log("/node: msg " + rawmsg.body);
          var msg = JSON.parse(rawmsg.body);
          if (selectedGroupId===msg.groupId) {
            console.log("reloading group " + msg.groupId);
            var _tree = $.ui.fancytree.getTree();
            if (msg.method=="ADD") {
            	var existingNode = _tree.getNodeByKey(msg.key);
            	console.log('existingNode ' + existingNode);
            	if (existingNode) {
            	  return;
            	}
            	var parent = _tree.getNodeByKey(msg.parentKey);
            	var child = { key: msg.key, title: msg.title };
            	if (msg.folder!==null) {
            		child["folder"] = msg.folder;
            	} 
            	parent.addNode(child, 'child');
            }else if (msg.method=="DEL") {
			  var node = _tree.getNodeByKey(msg.key);
			  node.remove();
			}else if (msg.method=="REN") {
			  var node = _tree.getNodeByKey(msg.key);
			  node.setTitle(msg.title);
            }
	  	  }
        });
    });
}

$(function() {
    // 서버쪽의 NumberFormatException: For input string: "" <- 우회를 위한 방어코드
	jQuery.ajaxSettings.traditional = true;
	initTree();
	initEvents();
	initEditor();
	initSocket();
	var hash = window.location.hash;
	console.log('hash ' + hash);
	if (hash.length>1) {
		$("#sel_category").val(hash.substring(1));
		onSelectChanged($("#sel_category").get(0));
		// loadTree(hash.substring(1));
	}
});
