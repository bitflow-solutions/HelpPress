var editor, selectedGroupId, selectedContentId;
var SOURCE = [];

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
        const title = data.input.val();
        console.log('saving ' + title);
        var node = data.node;
        node.setTitle(title);
        saveTree();
        return true;
      },
      beforeClose: function(e, data){
      },
      activate: function(e, data){
  	    console.log("activate");
	    // var node = data.node;
	  },
	  click: function(e, data){
	    console.log("click");
	  },
      close: function(e, data){
    	initEvents();
      }
    },
    activate: function (event, data) {},
    lazyLoad: function (event, data) {},
  })
}

function initEvents() {
	$(".btn-modify").click(function(e) {
		// 도움망 수정완료 버튼 클릭
		var url = "/api/v1/ecm/content/" + selectedContentId;
		$.ajax({
			url: url,
			method: "PUT",
			data: { 
				title: $("#bf-subject-input").val(),
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
		});
	});
	$(".fancytree-ico-c .fancytree-title").click(function(e) {
		try {
			var node = $.ui.fancytree.getNode(e);
			loadPage(node.key);
		} catch (ex) { console.log('err ' + ex.message); }
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
		  newfolder: {name: "새 폴더", callback: appendChildFolder },
		  newcontent: {name: "새 도움말", callback: appendChildContent }
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
    // editor.getUploadedFiles()를 통해 업로드된 파일을 확인하려면 반드시 사용
    console.log('filetype ' + fileType + ' uploadpath ' + uploadPath);
    e.editor.addUploadPath(fileType, uploadPath);
  });
  editor.setEventListener('beforeOpenDocument', function (e) {
	e.addParameter('key', selectedContentId);
	console.log("beforeOpenDocument " + JSON.stringify(e));
  });
}

function editContent() {
  console.log("editContent");
  var _tree = $.ui.fancytree.getTree();
  var node = _tree.getActiveNode();
  selectedContentId = node.key;
  var url = "/api/v1/ecm/content/" + node.key;
  $.ajax({
	url: url,
	method: "GET"
  })
  .done(function(msg) {
    editor.openHTML(msg.result.contents);
    $("#contents-detail").hide();
	$("#editor-wrapper").show();
  });
}

function loadPage(key) {
	$("#editor-wrapper").hide();
	$("#contents-detail").attr("src", key + ".html");
	$("#contents-detail").show();
}

function appendChildFolder() {
  console.log("appendChildFolder");
  var _tree = $.ui.fancytree.getTree();
  var node = _tree.getActiveNode();
  if( !node ) {
    node = _tree.getRootNode();
  }
  $.ajax({
	  url: "/api/v1/ecm/content/folder",
	  method: "POST",
	  data: { folder: true }
  	})
	.done(function(msg) {
	  console.log('msg ' + JSON.stringify(msg));
	  node.editCreateNode('child', {
	    title: "새 폴더",
	    folder: true,
	    key: msg.result.key
	  })
	  saveTree();
	})
	.fail(function() {
	})
	.always(function() {
  });
}

function appendChildContent() {
  var _tree = $.ui.fancytree.getTree();
  var node = _tree.getActiveNode();
  if( !node ) {
    node = _tree.getRootNode();
  }
  var title = "새 도움말";
  $.ajax({
	  url: "/api/v1/ecm/content",
	  method: "POST",
	  data: { title: title }
  	})
	.done(function(msg) {
	  node.editCreateNode('child', {
	    title: msg.result.title,
	    key: msg.result.key
	  })
	  saveTree();
	})
	.fail(function() {
	})
	.always(function() {
  });  

}

function deleteContent() {
  var _tree = $.ui.fancytree.getTree();
  var node = _tree.getActiveNode();
  if( !node ) {
    alert("부모 노드를 선택해주세요");
    return;
  }
  node.remove();
  saveTree();
}

function downloadContent() {
  var _tree = $.ui.fancytree.getTree();
  var node = _tree.getActiveNode();
  if( !node ) {
    alert("부모 노드를 선택해주세요");
    return;
  }
  var key = node.key;
  $(".spinner-border").show();
  $("#ifrm").attr("src", "/api/v1/ecm/release/" + key);
  setTimeout(function() {
	$(".spinner-border").hide();
  }, 6000);
}

function deleteFolder() {
  var _tree = $.ui.fancytree.getTree();
  var node = _tree.getActiveNode();
  if( !node ) {
    alert("부모 노드를 선택해주세요");
    return;
  }
  node.remove();
  saveTree();
}

function editTitle() {
  console.log('editTitle');
  var _tree = $.ui.fancytree.getTree();
  var node = _tree.getActiveNode();
  if( !node ) {
    alert("부모 노드를 선택해주세요");
    return;
  }
  node.editStart();
}

function saveTree() {
  var _tree = $.ui.fancytree.getTree();
  const tree = _tree.toDict(true);
  console.log('saveTree ' + JSON.stringify(tree));
  $.ajax({
	  url: "/api/v1/ecm/group/" + selectedGroupId,
	  method: "PUT",
		data: { 
		  tree: JSON.stringify(tree.children)
		}
  	})
	.done(function(msg) {
	  _tree.reload(JSON.parse(msg.result.tree));
	  initEvents();
	})
	.fail(function() {
	})
	.always(function() {
  });
}

function onSelectChanged(select) {
  selectedGroupId = select.options[select.selectedIndex].value;
  if (!selectedGroupId && selectedGroupId.length<1) {
	  $("#tree").hide();
	  $("#contents-detail").attr("src", "/empty-content.html");
  } else {
	$.ajax({
	  url: "/api/v1/ecm/group/" + selectedGroupId,
	  method: "GET"
	})
	.done(function(msg) {
	  console.log('tree ' + msg.result.tree);
	  var _tree = $.ui.fancytree.getTree();
	  _tree.reload(JSON.parse(msg.result.tree));
	  initEvents();
	  $("#tree").show();
	})
	.fail(function() {
	})
	.always(function() {
	});
  }
}

function expandAll() {
  $.ui.fancytree.getTree().expandAll();
}

function collapseAll() {
  $.ui.fancytree.getTree().expandAll(false);
}

$(function() {
	initTree();
	initEvents();
	initEditor();
});

/*
function refreshTreeNodeListener() {
  $(".fancytree-folder > .fancytree-title").contextmenu(function(e) {
    let node = $.ui.fancytree.getNode(e)
    node.setFocus(true)
    e.preventDefault()
    e.stopPropagation()
  })

  $(".fancytree-ico-c > .fancytree-title").contextmenu(function(e) {
    let node = $.ui.fancytree.getNode(e)
    node.setFocus(true)
    e.preventDefault()
    e.stopPropagation()
  })

  $(".ui-fancytree").contextmenu(function(e) {
    e.preventDefault()
    e.stopPropagation()
  })
}
*/
